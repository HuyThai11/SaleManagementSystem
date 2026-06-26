package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import manager.ProductManager;
import manager.CustomerManager;
import model.Customer;
import model.InStoreTransaction;
import model.OnlineTransaction;
import model.Product;
import model.Transaction;
import model.TransactionItem;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionFileHandler {

    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String ITEMS_FILE = "transaction_items.txt";
    private static final String DELIMITER = "\\|";
    private static final String HEADER_TRANSACTIONS = "#transactionId|transactionType|customerId|date|confirmed|cancelled|discountRate|approvedBy|shippingAddress|shippingFee";
    private static final String HEADER_ITEMS = "#transactionId|productId|quantity|unitPrice";

    private final ProductManager productManager;
    private final CustomerManager customerManager;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public TransactionFileHandler(ProductManager productManager, CustomerManager customerManager) {
        if (productManager == null || customerManager == null) {
            throw new IllegalArgumentException("ProductManager and CustomerManager must not be null.");
        }
        this.productManager = productManager;
        this.customerManager = customerManager;
        createFilesIfMissing();
    }

    private void createFilesIfMissing() {
        try {
            Path transPath = Paths.get(TRANSACTIONS_FILE);
            if (!Files.exists(transPath)) {
                Files.createFile(transPath);
                System.out.println("System info: Created new file " + TRANSACTIONS_FILE);
            }

            Path itemsPath = Paths.get(ITEMS_FILE);
            if (!Files.exists(itemsPath)) {
                Files.createFile(itemsPath);
                System.out.println("System info: Created new file " + ITEMS_FILE);
            }
        } catch (IOException e) {
            System.err.println("Critical Error: Cannot create transaction files. " + e.getMessage());
        }
    }

    public boolean saveTransactions(List<Transaction> transactions) {
        if (transactions == null) {
            System.err.println("Error: Transaction list is null. Cannot save.");
            return false;
        }

        boolean success = true;
        rwLock.writeLock().lock();

        // Save Headers
        try (FileChannel transChannel = FileChannel.open(Paths.get(TRANSACTIONS_FILE), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
             FileLock transLock = transChannel.lock();
             BufferedWriter writer = new BufferedWriter(java.nio.channels.Channels.newWriter(transChannel, StandardCharsets.UTF_8.name()))) {
            
            transChannel.truncate(0);
            writer.write(HEADER_TRANSACTIONS);
            writer.newLine();

            for (Transaction t : transactions) {
                String type;
                double discountRate = 0.0;
                String approvedBy = "N/A";
                String shippingAddress = "N/A";
                double shippingFee = 0.0;

                if (t instanceof InStoreTransaction) {
                    type = "InStore";
                    InStoreTransaction ist = (InStoreTransaction) t;
                    discountRate = ist.getDiscountRate();
                    approvedBy = ist.getApprovedBy();
                } else if (t instanceof OnlineTransaction) {
                    type = "Online";
                    OnlineTransaction ot = (OnlineTransaction) t;
                    shippingAddress = ot.getShippingAddress();
                    shippingFee = ot.getShippingFee();
                } else {
                    continue; // Unknown type
                }

                String line = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                        t.getTransactionId(), type, t.getCustomer().getId(), t.getDate(),
                        t.isConfirmed(), t.isCancelled(), discountRate, approvedBy,
                        shippingAddress, shippingFee);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving transaction headers: " + e.getMessage());
            success = false;
        }

        // Save Items
        try (FileChannel itemsChannel = FileChannel.open(Paths.get(ITEMS_FILE), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
             FileLock itemsLock = itemsChannel.lock();
             BufferedWriter writer = new BufferedWriter(java.nio.channels.Channels.newWriter(itemsChannel, StandardCharsets.UTF_8.name()))) {
            
            itemsChannel.truncate(0);
            writer.write(HEADER_ITEMS);
            writer.newLine();

            for (Transaction t : transactions) {
                for (TransactionItem item : t.getItems()) {
                    String line = String.format("%s|%s|%s|%s",
                            t.getTransactionId(), item.getProduct().getProductId(),
                            item.getQuantity(), item.getUnitPrice());
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving transaction items: " + e.getMessage());
            success = false;
        } finally {
            rwLock.writeLock().unlock();
        }
        return success;
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        Set<String> loadedIds = new HashSet<>();

        rwLock.readLock().lock();
        try {
            // 1. Load Headers
            File transFile = new File(TRANSACTIONS_FILE);
            if (transFile.exists() && transFile.length() > 0) {
                try (FileChannel transChannel = FileChannel.open(Paths.get(TRANSACTIONS_FILE), StandardOpenOption.READ);
                     FileLock lock = transChannel.lock(0L, Long.MAX_VALUE, true);
                     BufferedReader reader = new BufferedReader(java.nio.channels.Channels.newReader(transChannel, StandardCharsets.UTF_8.name()))) {
                    String line;
                    int lineNumber = 0;
                    while ((line = reader.readLine()) != null) {
                        lineNumber++;
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;

                        Transaction parsedTx = parseTransaction(line, lineNumber, loadedIds);
                        if (parsedTx != null) {
                            int existingIndex = -1;
                            for (int i = 0; i < transactions.size(); i++) {
                                if (transactions.get(i).getTransactionId().equalsIgnoreCase(parsedTx.getTransactionId())) {
                                    existingIndex = i;
                                    break;
                                }
                            }
                            if (existingIndex != -1) {
                                transactions.set(existingIndex, parsedTx);
                            } else {
                                transactions.add(parsedTx);
                            }
                            loadedIds.add(parsedTx.getTransactionId().toUpperCase());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error loading transactions: " + e.getMessage());
                }
            }

            // 2. Load Items and attach to Headers
            loadTransactionItems(transactions);
        } finally {
            rwLock.readLock().unlock();
        }

        return transactions;
    }

    private void loadTransactionItems(List<Transaction> transactions) {
        File itemsFile = new File(ITEMS_FILE);
        if (!itemsFile.exists() || itemsFile.length() == 0) return;

        try (FileChannel itemsChannel = FileChannel.open(Paths.get(ITEMS_FILE), StandardOpenOption.READ);
             FileLock lock = itemsChannel.lock(0L, Long.MAX_VALUE, true);
             BufferedReader reader = new BufferedReader(java.nio.channels.Channels.newReader(itemsChannel, StandardCharsets.UTF_8.name()))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                parseTransactionItem(line, lineNumber, transactions);
            }
        } catch (IOException e) {
            System.err.println("Error loading transaction items: " + e.getMessage());
        }
    }

    private Transaction parseTransaction(String line, int lineNumber, Set<String> loadedIds) {
        String[] tokens = line.split(DELIMITER, -1);
        if (!validateTransactionLine(tokens, lineNumber)) return null;

        String id = tokens[0].trim();
        String type = tokens[1].trim();
        String customerId = tokens[2].trim();
        String date = tokens[3].trim();
        
        // In append-only log, duplicates indicate an update
        // We no longer skip them

        Customer customer = resolveCustomerReference(customerId, lineNumber);
        if (customer == null) return null;

        try {
            boolean confirmed = Boolean.parseBoolean(tokens[4].trim());
            boolean cancelled = Boolean.parseBoolean(tokens[5].trim());
            
            Transaction transaction = null;

            if ("InStore".equalsIgnoreCase(type)) {
                double discountRate = Double.parseDouble(tokens[6].trim());
                String approvedBy = tokens[7].trim();
                transaction = new InStoreTransaction(id, customer, date, discountRate, approvedBy);
            } else if ("Online".equalsIgnoreCase(type)) {
                String shippingAddress = tokens[8].trim();
                double shippingFee = Double.parseDouble(tokens[9].trim());
                transaction = new OnlineTransaction(id, customer, date, shippingAddress, shippingFee);
            } else {
                System.err.println("Warning [Header Line " + lineNumber + "]: Unknown transaction type. Skipping.");
                return null;
            }

            // Sử dụng Java Reflection để vượt qua Business Logic lúc Load (tránh hàm confirm() làm trừ sai số lượng tồn kho)
            setInternalState(transaction, "confirmed", confirmed);
            setInternalState(transaction, "cancelled", cancelled);

            return transaction;
        } catch (Exception e) {
            System.err.println("Warning [Header Line " + lineNumber + "]: Parse error. Details: " + e.getMessage());
            return null;
        }
    }

    private void parseTransactionItem(String line, int lineNumber, List<Transaction> transactions) {
        String[] tokens = line.split(DELIMITER, -1);
        if (!validateTransactionItemLine(tokens, lineNumber)) return;

        String transId = tokens[0].trim();
        String prodId = tokens[1].trim();

        Transaction transaction = findTransactionById(transId, transactions);
        if (transaction == null) {
            System.err.println("Warning [Item Line " + lineNumber + "]: Transaction ID not found. Orphan item skipped.");
            return;
        }

        Product product = resolveProductReference(prodId, lineNumber);
        if (product == null) return;

        try {
            int quantity = Integer.parseInt(tokens[2].trim());
            double unitPrice = Double.parseDouble(tokens[3].trim());

            if (quantity <= 0 || unitPrice < 0) {
                System.err.println("Warning [Item Line " + lineNumber + "]: Invalid quantity or price. Skipped.");
                return;
            }

            // Gắn trực tiếp vào mảng để vượt qua Business Logic
            TransactionItem item = new TransactionItem(product, quantity);
            
            // Ép gán lại unitPrice lịch sử bằng Reflection
            Field priceField = TransactionItem.class.getDeclaredField("unitPrice");
            priceField.setAccessible(true);
            priceField.setDouble(item, unitPrice);

            transaction.getItems().add(item);
            
            if (!transaction.isConfirmed() && !transaction.isCancelled()) {
                product.reserveStock(quantity);
            }
        } catch (Exception e) {
            System.err.println("Warning [Item Line " + lineNumber + "]: Parse error. Details: " + e.getMessage());
        }
    }

    private boolean validateTransactionLine(String[] tokens, int lineNumber) {
        if (tokens.length != 10) {
            System.err.println("Warning [Header Line " + lineNumber + "]: Invalid column count. Skipping.");
            return false;
        }
        return true;
    }

    private boolean validateTransactionItemLine(String[] tokens, int lineNumber) {
        if (tokens.length != 4) {
            System.err.println("Warning [Item Line " + lineNumber + "]: Invalid column count. Skipping.");
            return false;
        }
        return true;
    }

    private Customer resolveCustomerReference(String customerId, int lineNumber) {
        Customer customer = customerManager.findById(customerId);
        if (customer == null) {
            System.err.println("Warning [Header Line " + lineNumber + "]: Customer " + customerId + " not found. Skipping transaction.");
        }
        return customer;
    }

    private Product resolveProductReference(String productId, int lineNumber) {
        Product product = productManager.findById(productId);
        if (product == null) {
            System.err.println("Warning [Item Line " + lineNumber + "]: Product " + productId + " not found. Skipping item.");
        }
        return product;
    }

    private Transaction findTransactionById(String id, List<Transaction> transactions) {
        for (Transaction t : transactions) {
            if (t.getTransactionId().equalsIgnoreCase(id)) return t;
        }
        return null;
    }

    private void setInternalState(Transaction transaction, String fieldName, boolean value) {
        try {
            Field field = Transaction.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(transaction, value);
        } catch (Exception e) {
            System.err.println("Error setting internal state via reflection: " + e.getMessage());
        }
    }
}
