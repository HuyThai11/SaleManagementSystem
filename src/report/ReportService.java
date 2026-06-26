package report;

import model.Customer;
import model.Product;
import model.Transaction;
import model.TransactionItem;
import model.VIPCustomer;
import model.VIPTier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private ArrayList<Transaction> transactionList;
    private manager.CustomerManager customerManager;
    private manager.ProductManager productManager;

    public ReportService(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public ReportService(manager.CustomerManager customerManager, manager.ProductManager productManager, manager.TransactionManager transactionManager) {
        this.customerManager = customerManager;
        this.productManager = productManager;
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

    public List<Map.Entry<String, Integer>> getBestSellingProducts() {
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
        return ranking;
    }

    public List<Map.Entry<Customer, Double>> getTopCustomers() {
        HashMap<Customer, Double> customerSpending = new HashMap<>();

        for (Transaction t : transactionList) {
            if (!t.isConfirmed()) continue;
            Customer customer = t.getCustomer();
            customerSpending.put(customer, customerSpending.getOrDefault(customer, 0.0) + t.calculateTotal());
        }

        ArrayList<Map.Entry<Customer, Double>> ranking = new ArrayList<>(customerSpending.entrySet());
        ranking.sort(Comparator.comparing(Map.Entry<Customer, Double>::getValue).reversed());
        return ranking;
    }

    public List<VIPCustomer> getVIPCustomerList() {
        List<VIPCustomer> vips = new ArrayList<>();
        if (customerManager == null) return vips;
        
        for (Customer c : customerManager.getAllCustomers()) {
            if (c instanceof VIPCustomer) {
                vips.add((VIPCustomer) c);
            }
        }
        return vips;
    }

    public Map<Transaction, Double> getRevenueAfterDiscount() {
        Map<Transaction, Double> result = new HashMap<>();
        for (Transaction t : transactionList) {
            if (t.isConfirmed()) {
                result.put(t, t.calculateTotal());
            }
        }
        return result;
    }

    public Map<VIPTier, Integer> getVIPTierDistribution() {
        Map<VIPTier, Integer> distribution = new HashMap<>();
        distribution.put(VIPTier.SILVER, 0);
        distribution.put(VIPTier.GOLD, 0);
        distribution.put(VIPTier.PLATINUM, 0);
        
        if (customerManager == null) return distribution;
        
        for (Customer c : customerManager.getAllCustomers()) {
            if (c instanceof VIPCustomer) {
                VIPCustomer vip = (VIPCustomer) c;
                if (vip.getTier() != null) {
                    distribution.put(vip.getTier(), distribution.get(vip.getTier()) + 1);
                }
            }
        }
        return distribution;
    }

    public ArrayList<Product> getLowStockProducts() {
        if (productManager == null) return new ArrayList<>();
        return productManager.getLowStockProducts();
    }

    public ArrayList<Product> getActiveProducts() {
        if (productManager == null) return new ArrayList<>();
        return productManager.getActiveProducts();
    }

    public ArrayList<Product> getInactiveProducts() {
        if (productManager == null) return new ArrayList<>();
        return productManager.getInactiveProducts();
    }

    public ArrayList<Product> getProductPriceHistory() {
        if (productManager == null) return new ArrayList<>();
        return productManager.getAllProducts();
    }

    public double[] getRevenueByCustomerType() {
        java.util.HashSet<Customer> regularCustomers = new java.util.HashSet<>();
        java.util.HashSet<Customer> vipCustomers = new java.util.HashSet<>();
        
        double regularRevenue = 0;
        double vipRevenue = 0;

        for (Transaction t : transactionList) {
            if (t.isConfirmed()) {
                double total = t.calculateTotal();
                Customer customer = t.getCustomer();
                
                if (customer instanceof VIPCustomer) {
                    vipCustomers.add(customer);
                    vipRevenue += total;
                } else {
                    regularCustomers.add(customer);
                    regularRevenue += total;
                }
            }
        }
        
        return new double[] {
            regularCustomers.size(), regularRevenue,
            vipCustomers.size(), vipRevenue
        };
    }

    public double[] getTotalDiscountGiven() {
        double totalSubtotal = 0;
        double totalDiscountAmount = 0;
        double totalNetRevenue = 0;

        for (Transaction t : transactionList) {
            if (t.isConfirmed() && t instanceof model.InStoreTransaction) {
                model.InStoreTransaction ist = (model.InStoreTransaction) t;
                
                double subtotal = 0;
                for (TransactionItem item : ist.getItems()) {
                    subtotal += item.getLineTotal();
                }
                
                double netRevenue = ist.calculateTotal();
                double discountRate = ist.getDiscountRate();
                double discountAmount = subtotal * discountRate;
                
                totalSubtotal += subtotal;
                totalDiscountAmount += discountAmount;
                totalNetRevenue += netRevenue;
            }
        }
        
        return new double[] {totalSubtotal, totalDiscountAmount, totalNetRevenue};
    }
}

