
package report;

import model.Customer;
import model.Product;
import model.Transaction;
import model.TransactionItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    private ArrayList<Transaction> transactionList;
    private manager.CustomerManager customerManager;
    private manager.ProductManager productManager;
    private manager.TransactionManager transactionManager;

    public ReportService(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public ReportService(manager.CustomerManager customerManager, manager.ProductManager productManager, manager.TransactionManager transactionManager) {
        this.customerManager = customerManager;
        this.productManager = productManager;
        this.transactionManager = transactionManager;
        this.transactionList = transactionManager.getTransactionList();
    }

    // Daily Report — date format: dd/MM/yyyy
    public double generateDailyReport(String date) {
        double revenue = 0;
        for (Transaction t : transactionList) {
            if (t.isConfirmed() && t.getDate().equalsIgnoreCase(date)) {
                revenue += t.calculateTotal();
            }
        }
        return revenue;
    }

    // Monthly Report — date format: dd/MM/yyyy
    public double generateMonthlyReport(int month, int year) {
        double revenue = 0;
        for (Transaction t : transactionList) {
            String[] parts = t.getDate().split("/");
            if (parts.length == 3) {
                int transMonth = Integer.parseInt(parts[1]);
                int transYear  = Integer.parseInt(parts[2]);
                if (t.isConfirmed() && transMonth == month && transYear == year) {
                    revenue += t.calculateTotal();
                }
            }
        }
        return revenue;
    }

    // Best Selling Products — BR10
    public void getBestSellingProducts() {
        HashMap<String, Integer> productSales = new HashMap<>();

        for (Transaction t : transactionList) {
            if (!t.isConfirmed()) continue;
            for (TransactionItem item : t.getItems()) {
                String name = item.getProduct().getProductName();
                productSales.put(name, productSales.getOrDefault(name, 0) + item.getQuantity());
            }
        }

        ArrayList<Map.Entry<String, Integer>> ranking = new ArrayList<>(productSales.entrySet());
        ranking.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        System.out.println("\n=== BEST SELLING PRODUCTS ===");
        for (Map.Entry<String, Integer> entry : ranking) {
            System.out.println(entry.getKey() + " - Qty Sold: " + entry.getValue());
        }
    }

    // Top Customers — BR11
    public void getTopCustomers() {
        HashMap<Customer, Double> customerSpending = new HashMap<>();

        for (Transaction t : transactionList) {
            if (!t.isConfirmed()) continue;
            Customer customer = t.getCustomer();
            customerSpending.put(customer, customerSpending.getOrDefault(customer, 0.0) + t.calculateTotal());
        }

        ArrayList<Map.Entry<Customer, Double>> ranking = new ArrayList<>(customerSpending.entrySet());
        ranking.sort(Comparator.comparing(Map.Entry<Customer, Double>::getValue).reversed());

        System.out.println("\n=== TOP CUSTOMERS ===");
        for (Map.Entry<Customer, Double> entry : ranking) {
            System.out.printf("%s - Total Purchase: %.0f%n",
                    entry.getKey().getName(), entry.getValue());
        }
    }

    // R1: VIP Customer List
    public void printVIPCustomerList() {
        if (customerManager == null) {
            System.out.println("Error: CustomerManager is not initialized.");
            return;
        }
        System.out.println("\n=== VIP CUSTOMER LIST ===");
        System.out.printf("%-8s %-20s %-15s %-15s %-10s%n", "ID", "Name", "Phone", "Tier", "Discount%");
        for (Customer c : customerManager.getAllCustomers()) {
            if (c instanceof model.VIPCustomer) {
                model.VIPCustomer vip = (model.VIPCustomer) c;
                System.out.printf("%-8s %-20s %-15s %-15s %.0f%%%n",
                        vip.getId(), vip.getName(), vip.getPhone(),
                        vip.getTier().name(), vip.getDiscountRate() * 100);
            }
        }
    }

    // R2: Revenue After Discount
    public void printRevenueAfterDiscount() {
        System.out.println("\n=== REVENUE AFTER DISCOUNT ===");
        System.out.printf("%-15s %-20s %-15s%n", "Transaction ID", "Customer", "Final Total");
        double totalRevenue = 0;
        for (Transaction t : transactionList) {
            if (t.isConfirmed()) {
                double total = t.calculateTotal(); // Polymorphism
                System.out.printf("%-15s %-20s %.0f%n", t.getTransactionId(), t.getCustomer().getName(), total);
                totalRevenue += total;
            }
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("Total Revenue: %.0f%n", totalRevenue);
    }

    // R10: VIP Tier Distribution
    public void printVIPTierDistribution() {
        if (customerManager == null) {
            System.out.println("Error: CustomerManager is not initialized.");
            return;
        }
        
        int silverCount = 0;
        int goldCount = 0;
        int platinumCount = 0;
        
        for (Customer c : customerManager.getAllCustomers()) {
            if (c instanceof model.VIPCustomer) {
                model.VIPCustomer vip = (model.VIPCustomer) c;
                if (vip.getTier() != null) {
                    switch (vip.getTier()) {
                        case SILVER: silverCount++; break;
                        case GOLD: goldCount++; break;
                        case PLATINUM: platinumCount++; break;
                    }
                }
            }
        }
        
        System.out.println("\n=== VIP TIER DISTRIBUTION ===");
        System.out.printf("%-10s %5s%n", "Tier", "Count");
        System.out.printf("%-10s %5d%n", "PLATINUM", platinumCount);
        System.out.printf("%-10s %5d%n", "GOLD", goldCount);
        System.out.printf("%-10s %5d%n", "SILVER", silverCount);
    }

    // R5: Low Stock Products
    public void printLowStockProducts() {
        if (productManager == null) {
            System.out.println("Error: ProductManager is not initialized.");
            return;
        }

        ArrayList<Product> lowStock = productManager.getLowStockProducts();
        System.out.println("\n=== LOW STOCK PRODUCTS ===");
        
        if (lowStock.isEmpty()) {
            System.out.println("No low stock products found.");
            return;
        }

        System.out.printf("%-8s %-20s %-8s %-10s%n", "ID", "Name", "Stock", "Status");
        for (Product p : lowStock) {
            String status = p.isActive() ? "Active" : "Inactive";
            System.out.printf("%-8s %-20s %-8d %-10s%n", p.getProductId(), p.getProductName(), p.getStockQuantity(), status);
        }
    }

    // R6: Active Products
    public void printActiveProducts() {
        if (productManager == null) {
            System.out.println("Error: ProductManager is not initialized.");
            return;
        }

        ArrayList<Product> activeProducts = productManager.getActiveProducts();
        System.out.println("\n=== ACTIVE PRODUCTS ===");
        
        if (activeProducts.isEmpty()) {
            System.out.println("No active products found.");
            return;
        }

        System.out.printf("%-8s %-20s %-15s %10s %8s%n", "ID", "Name", "Category", "Price", "Stock");
        for (Product p : activeProducts) {
            p.displayInfo();
        }
    }

    // R7: Inactive Products
    public void printInactiveProducts() {
        if (productManager == null) {
            System.out.println("Error: ProductManager is not initialized.");
            return;
        }

        ArrayList<Product> inactiveProducts = productManager.getInactiveProducts();
        System.out.println("\n=== INACTIVE PRODUCTS ===");
        
        if (inactiveProducts.isEmpty()) {
            System.out.println("No inactive products found.");
            return;
        }

        System.out.printf("%-8s %-20s %-15s %10s%n", "ID", "Name", "Category", "Last Price");
        for (Product p : inactiveProducts) {
            System.out.printf("%-8s %-20s %-15s %10.0f%n", p.getProductId(), p.getProductName(), p.getCategory(), p.getPrice());
        }
    }

    // R8: Product Price History
    public void printProductPriceHistory() {
        if (productManager == null) {
            System.out.println("Error: ProductManager is not initialized.");
            return;
        }

        ArrayList<Product> allProducts = productManager.getAllProducts();
        System.out.println("\n=== PRODUCT PRICE HISTORY ===");
        
        if (allProducts.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (Product p : allProducts) {
            System.out.println("Product ID: " + p.getProductId());
            System.out.println("Product Name: " + p.getProductName());
            System.out.printf("Current Price: %.0f%n", p.getPrice());
            
            ArrayList<Double> history = p.getPriceHistory();
            System.out.println("Historical Prices:");
            if (history == null || history.isEmpty()) {
                System.out.println("No price changes recorded");
            } else {
                for (int i = 0; i < history.size(); i++) {
                    System.out.printf("%d. %.0f%n", i + 1, history.get(i));
                }
            }
            System.out.println("-------------------------");
        }
    }

    // R3: Revenue by Customer Type
    public void printRevenueByCustomerType() {
        java.util.HashSet<Customer> regularCustomers = new java.util.HashSet<>();
        java.util.HashSet<Customer> vipCustomers = new java.util.HashSet<>();
        
        double regularRevenue = 0;
        double vipRevenue = 0;

        for (Transaction t : transactionList) {
            if (t.isConfirmed()) {
                double total = t.calculateTotal(); // Calculate once
                Customer customer = t.getCustomer();
                
                if (customer instanceof model.VIPCustomer) {
                    vipCustomers.add(customer);
                    vipRevenue += total;
                } else {
                    regularCustomers.add(customer);
                    regularRevenue += total;
                }
            }
        }

        double totalRevenue = regularRevenue + vipRevenue;
        
        System.out.println("\n=== REVENUE BY CUSTOMER TYPE ===");
        if (totalRevenue == 0) {
            System.out.println("No confirmed revenue recorded.");
            return;
        }

        double regularPct = (regularRevenue / totalRevenue) * 100;
        double vipPct = (vipRevenue / totalRevenue) * 100;

        System.out.println("--- Regular Customers ---");
        System.out.printf("Count (unique buyers): %d%n", regularCustomers.size());
        System.out.printf("Revenue: %.0f%n", regularRevenue);
        System.out.printf("Percentage: %.2f%%%n", regularPct);

        System.out.println("--- VIP Customers ---");
        System.out.printf("Count (unique buyers): %d%n", vipCustomers.size());
        System.out.printf("Revenue: %.0f%n", vipRevenue);
        System.out.printf("Percentage: %.2f%%%n", vipPct);
        
        System.out.println("-------------------------");
        System.out.printf("Total Revenue: %.0f%n", totalRevenue);
    }

    // R4: Total Discount Given
    public void printTotalDiscountGiven() {
        double totalSubtotal = 0;
        double totalDiscountAmount = 0;
        double totalNetRevenue = 0;

        for (Transaction t : transactionList) {
            if (t.isConfirmed() && t instanceof model.InStoreTransaction) {
                // Cast only when necessary to access specific specialized properties
                model.InStoreTransaction ist = (model.InStoreTransaction) t;
                
                double subtotal = 0;
                for (TransactionItem item : ist.getItems()) {
                    subtotal += item.getLineTotal();
                }
                
                // Polymorphic net revenue calculation
                double netRevenue = ist.calculateTotal();
                
                double discountRate = ist.getDiscountRate();
                double discountAmount = subtotal * discountRate;
                
                totalSubtotal += subtotal;
                totalDiscountAmount += discountAmount;
                totalNetRevenue += netRevenue;
            }
        }
        
        System.out.println("\n=== TOTAL DISCOUNT GIVEN (IN-STORE) ===");
        System.out.printf("Total Subtotal: %.0f%n", totalSubtotal);
        System.out.printf("Total Discount Amount: %.0f%n", totalDiscountAmount);
        System.out.printf("Total Net Revenue: %.0f%n", totalNetRevenue);
    }
}

