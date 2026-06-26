package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import model.Customer;
import model.RegularCustomer;
import model.VIPCustomer;
import model.VIPTier;

public class CustomerFileHandler {

    private static final String FILE_PATH = "customers.txt";
    private static final String DELIMITER = "\\|";
    private static final String HEADER = "#customerType|customerId|name|phone|address|tier|active";
    private final java.util.concurrent.locks.ReentrantReadWriteLock rwLock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public CustomerFileHandler() {
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

    public boolean saveCustomers(List<Customer> customers) {
        if (customers == null) {
            System.err.println("Error: Customer list is null. Cannot save.");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(FILE_PATH, false), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();

            for (Customer customer : customers) {
                String customerType;
                String tierStr = "N/A";

                if (customer instanceof VIPCustomer) {
                    customerType = "VIP";
                    tierStr = ((VIPCustomer) customer).getTier().name();
                } else {
                    customerType = "Regular";
                }

                String line = String.format("%s|%s|%s|%s|%s|%s|%s",
                        customerType,
                        customer.getId(),
                        customer.getName(),
                        customer.getPhone(),
                        customer.getAddress(),
                        tierStr,
                        customer.isActive());
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving customers to file: " + e.getMessage());
            return false;
        }
    }

    public boolean appendCustomer(Customer customer) {
        if (customer == null) {
            System.err.println("Error: Customer is null. Cannot append.");
            return false;
        }

        Path path = Paths.get(FILE_PATH);
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
             FileLock lock = channel.lock();
             BufferedWriter writer = new BufferedWriter(java.nio.channels.Channels.newWriter(channel, StandardCharsets.UTF_8.name()))) {
            
            String customerType;
            String tierStr = "N/A";

            if (customer instanceof VIPCustomer) {
                customerType = "VIP";
                tierStr = ((VIPCustomer) customer).getTier().name();
            } else {
                customerType = "Regular";
            }

            String line = String.format("%s|%s|%s|%s|%s|%s|%s",
                    customerType,
                    customer.getId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getAddress(),
                    tierStr,
                    customer.isActive());
            writer.write(line);
            writer.newLine();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error appending customer to file: " + e.getMessage());
            return false;
        }
    }

    public List<Customer> loadCustomers() {
        List<Customer> customers = new java.util.ArrayList<>();
        Set<String> loadedIds = new java.util.HashSet<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return customers;
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

                Customer parsedCustomer = parseCustomer(line, lineNumber, loadedIds);
                if (parsedCustomer != null) {
                    int existingIndex = -1;
                    for (int i = 0; i < customers.size(); i++) {
                        if (customers.get(i).getId().equalsIgnoreCase(parsedCustomer.getId())) {
                            existingIndex = i;
                            break;
                        }
                    }
                    if (existingIndex != -1) {
                        customers.set(existingIndex, parsedCustomer);
                    } else {
                        customers.add(parsedCustomer);
                    }
                    loadedIds.add(parsedCustomer.getId().toUpperCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers from file: " + e.getMessage());
        }

        return customers;
    }

    private Customer parseCustomer(String line, int lineNumber, Set<String> loadedIds) {
        String[] tokens = line.split(DELIMITER, -1);

        if (!validateCustomerLine(tokens, lineNumber)) {
            return null;
        }

        String type = tokens[0].trim();
        String id = tokens[1].trim();
        String name = tokens[2].trim();
        String phone = tokens[3].trim();
        String address = tokens[4].trim();
        String tierStr = tokens[5].trim();

        // In append-only log, duplicates indicate an update
        // We no longer skip them.

        boolean active = true;
        if (tokens.length == 7) {
            active = Boolean.parseBoolean(tokens[6].trim());
        }

        try {
            Customer customer = null;
            if ("VIP".equalsIgnoreCase(type)) {
                VIPTier tier = validateVIPTier(tierStr, lineNumber);
                if (tier == null) return null;
                customer = new VIPCustomer(id, name, phone, address, tier);
                
            } else if ("Regular".equalsIgnoreCase(type)) {
                customer = new RegularCustomer(id, name, phone, address);
                
            } else {
                System.err.println("Warning [Line " + lineNumber + "]: Unknown customer type '" + type + "'. Skipping.");
                return null;
            }
            if (!active) customer.deactivate();
            return customer;
        } catch (IllegalArgumentException e) {
            System.err.println("Warning [Line " + lineNumber + "]: Error creating customer. Details: " + e.getMessage());
            return null;
        }
    }

    private boolean validateCustomerLine(String[] tokens, int lineNumber) {
        if (tokens.length < 6 || tokens.length > 7) {
            System.err.println("Warning [Line " + lineNumber + "]: Invalid column count (Expected 6 or 7). Skipping.");
            return false;
        }

        for (int i = 0; i < 5; i++) {
            if (tokens[i].trim().isEmpty()) {
                System.err.println("Warning [Line " + lineNumber + "]: Missing required field. Skipping.");
                return false;
            }
        }
        return true;
    }

    private VIPTier validateVIPTier(String tierStr, int lineNumber) {
        try {
            return VIPTier.valueOf(tierStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Warning [Line " + lineNumber + "]: Invalid VIP Tier '" + tierStr + "'. Skipping.");
            return null;
        }
    }

    private boolean isDuplicateCustomerId(String id, Set<String> loadedIds) {
        return loadedIds.contains(id.toUpperCase());
    }
}
