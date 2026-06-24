package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Product;

public class ProductFileHandler {

    private static final String FILE_PATH = "products.txt";
    private static final String DELIMITER = "\\|";
    private static final String HEADER = "#productId|productName|category|price|stockQuantity|active";

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

        try (BufferedWriter writer = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(FILE_PATH, false), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();

            for (Product product : products) {
                String line = String.format("%s|%s|%s|%s|%s|%s",
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.isActive());
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving products to file: " + e.getMessage());
            return false;
        }
    }

    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        Set<String> loadedIds = new HashSet<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return products;
        }

        try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(FILE_PATH), StandardCharsets.UTF_8))) {
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
                    products.add(parsedProduct);
                    loadedIds.add(parsedProduct.getProductId().toUpperCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products from file: " + e.getMessage());
        }

        return products;
    }

    private Product parseProduct(String line, int lineNumber, Set<String> loadedIds) {
        String[] tokens = line.split(DELIMITER, -1);

        if (!validateProductLine(tokens, lineNumber)) {
            return null;
        }

        String id = tokens[0].trim();
        String name = tokens[1].trim();
        String category = tokens[2].trim();

        if (isDuplicateId(id, loadedIds)) {
            System.err.println("Warning [Line " + lineNumber + "]: Duplicate Product ID '" + id + "'. Skipping.");
            return null;
        }

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
            return product;

        } catch (NumberFormatException e) {
            System.err.println("Warning [Line " + lineNumber + "]: Number format error. Skipping. Details: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Warning [Line " + lineNumber + "]: Validation error. Skipping. Details: " + e.getMessage());
            return null;
        }
    }

    private boolean validateProductLine(String[] tokens, int lineNumber) {
        if (tokens.length != 6) {
            System.err.println("Warning [Line " + lineNumber + "]: Invalid column count (Expected 6, got " + tokens.length + "). Skipping.");
            return false;
        }

        if (tokens[0].trim().isEmpty() || tokens[1].trim().isEmpty() || tokens[2].trim().isEmpty()) {
            System.err.println("Warning [Line " + lineNumber + "]: Missing required string fields (ID, Name, or Category). Skipping.");
            return false;
        }

        return true;
    }

    private boolean isDuplicateId(String id, Set<String> loadedIds) {
        return loadedIds.contains(id.toUpperCase());
    }
}
