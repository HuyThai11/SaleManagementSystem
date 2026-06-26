package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import model.Product;

public class ProductFileHandler {

    private static final String FILE_PATH = "products.txt";
    private static final String DELIMITER = "\\|";
    private static final String HEADER = "#productId|productName|category|price|stockQuantity|active|priceHistory";
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ProductFileHandler() {
        createFileIfMissing();
    }

    private void createFileIfMissing() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
                System.out.println("System info: Created new file " + FILE_PATH);
            }
        } catch (IOException e) {
            System.err.println("Critical Error: Cannot create file " + FILE_PATH + ". " + e.getMessage());
        }
    }

    public boolean saveProducts(List<Product> products) {
        if (products == null) {
            System.err.println("Error: Product list is null. Cannot save.");
            return false;
        }

        rwLock.writeLock().lock();
        Path path = Paths.get(FILE_PATH);
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
             FileLock lock = channel.lock();
             BufferedWriter writer = new BufferedWriter(java.nio.channels.Channels.newWriter(channel, StandardCharsets.UTF_8.name()))) {
            
            channel.truncate(0);
            
            writer.write(HEADER);
            writer.newLine();

            for (Product product : products) {
                String priceHistoryStr = "";
                if (product.getPriceHistory() != null && !product.getPriceHistory().isEmpty()) {
                    List<String> histStr = new ArrayList<>();
                    for (Double h : product.getPriceHistory()) {
                        histStr.add(String.valueOf(h));
                    }
                    priceHistoryStr = String.join(",", histStr);
                }

                String line = String.format("%s|%s|%s|%s|%s|%s|%s",
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.isActive(),
                        priceHistoryStr);
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving products to file: " + e.getMessage());
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean appendProduct(Product product) {
        if (product == null) {
            System.err.println("Error: Product is null. Cannot append.");
            return false;
        }

        rwLock.writeLock().lock();
        Path path = Paths.get(FILE_PATH);
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
             FileLock lock = channel.lock();
             BufferedWriter writer = new BufferedWriter(java.nio.channels.Channels.newWriter(channel, StandardCharsets.UTF_8.name()))) {
            
            String priceHistoryStr = "";
            if (product.getPriceHistory() != null && !product.getPriceHistory().isEmpty()) {
                List<String> histStr = new ArrayList<>();
                for (Double h : product.getPriceHistory()) {
                    histStr.add(String.valueOf(h));
                }
                priceHistoryStr = String.join(",", histStr);
            }

            String line = String.format("%s|%s|%s|%s|%s|%s|%s",
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.isActive(),
                    priceHistoryStr);
            writer.write(line);
            writer.newLine();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error appending product to file: " + e.getMessage());
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        Set<String> loadedIds = new HashSet<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return products;
        }

        rwLock.readLock().lock();
        Path path = Paths.get(FILE_PATH);
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
             FileLock lock = channel.lock(0L, Long.MAX_VALUE, true);
             BufferedReader reader = new BufferedReader(java.nio.channels.Channels.newReader(channel, StandardCharsets.UTF_8.name()))) {
            
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                Product parsedProduct = parseProduct(line, lineNumber, loadedIds);
                if (parsedProduct != null) {
                    // Find if the product is already in the list
                    int existingIndex = -1;
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getProductId().equalsIgnoreCase(parsedProduct.getProductId())) {
                            existingIndex = i;
                            break;
                        }
                    }
                    if (existingIndex != -1) {
                        products.set(existingIndex, parsedProduct); // Overwrite older version
                    } else {
                        products.add(parsedProduct);
                    }
                    loadedIds.add(parsedProduct.getProductId().toUpperCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products from file: " + e.getMessage());
        } finally {
            rwLock.readLock().unlock();
        }

        return products;
    }

    private Product parseProduct(String line, int lineNumber, Set<String> loadedIds) {
        String[] tokens = line.split(DELIMITER, -1);

        if (tokens.length < 6 || tokens.length > 7) {
            System.err.println("Warning [Line " + lineNumber + "]: Invalid column count (Expected 6-7, got " + tokens.length + "). Skipping.");
            return null;
        }

        if (tokens[0].trim().isEmpty() || tokens[1].trim().isEmpty() || tokens[2].trim().isEmpty()) {
            System.err.println("Warning [Line " + lineNumber + "]: Missing required string fields (ID, Name, or Category). Skipping.");
            return null;
        }

        String id = tokens[0].trim();
        String name = tokens[1].trim();
        String category = tokens[2].trim();

        // In append-only log, duplicates mean an update, so we don't skip them anymore.
        // We still track loadedIds for other purposes if needed.

        try {
            double price = Double.parseDouble(tokens[3].trim());
            int stock = Integer.parseInt(tokens[4].trim());
            boolean active = Boolean.parseBoolean(tokens[5].trim());

            if (price <= 0 || stock < 0) {
                System.err.println("Warning [Line " + lineNumber + "]: Invalid price or stock values. Skipping.");
                return null;
            }

            Product product = new Product(id, name, category, price, stock);
            if (!active) {
                product.deactivate();
            }
            
            if (tokens.length == 7) {
                String historyStr = tokens[6].trim();
                if (!historyStr.isEmpty()) {
                    ArrayList<Double> history = new ArrayList<>();
                    String[] hTokens = historyStr.split(",");
                    for (String ht : hTokens) {
                        history.add(Double.parseDouble(ht.trim()));
                    }
                    product.setPriceHistory(history);
                }
            }
            
            return product;

        } catch (NumberFormatException e) {
            System.err.println("Warning [Line " + lineNumber + "]: Number format error. Skipping. Details: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Warning [Line " + lineNumber + "]: Validation error. Skipping. Details: " + e.getMessage());
            return null;
        }
    }
}
