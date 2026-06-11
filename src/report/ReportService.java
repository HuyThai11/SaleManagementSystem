
package report;

import model.Customer;
import model.Transaction;
import model.TransactionItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    private final ArrayList<Transaction> transactionList;

    public ReportService(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
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
}

