package view;

import report.ReportService;
import model.Customer;
import model.Product;
import model.Transaction;
import model.VIPCustomer;
import model.VIPTier;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ReportView {
    private final ReportService reportService;

    public ReportView(ReportService reportService) {
        this.reportService = reportService;
    }

    public void showMenu() {
        System.out.println("===== REPORT MENU =====");
        System.out.println("1. Daily Revenue");
        System.out.println("2. Monthly Revenue");
        System.out.println("3. Best Selling Products");
        System.out.println("4. Top Customers");
        System.out.println("5. VIP Customer List");
        System.out.println("6. Revenue After Discount");
        System.out.println("7. VIP Tier Distribution");
        System.out.println("8. Revenue by Customer Type");
        System.out.println("9. Total Discount Given");
        System.out.println("10. Low Stock Products");
        System.out.println("11. Active Products");
        System.out.println("12. Inactive Products");
        System.out.println("13. Product Price History");

        int choice = InputHelper.readInt("Choose: ");

        switch(choice){

            case 1:
                String date = InputHelper.readLine("Enter date (dd/MM/yyyy): ");
                double dailyRevenue = reportService.generateDailyReport(date);
                System.out.printf( "Revenue: %,.0f VND%n", dailyRevenue);
                break;

            case 2:
                int month = InputHelper.readInt("Month: ");
                int year = InputHelper.readInt("Year: ");
                double monthlyRevenue = reportService.generateMonthlyReport(month, year);
                System.out.printf("Revenue: %,.0f VND%n", monthlyRevenue);
                break;

            case 3:
                System.out.println("\n=== BEST SELLING PRODUCTS ===");
                List<Map.Entry<String, Integer>> bestSelling = reportService.getBestSellingProducts();
                for (Map.Entry<String, Integer> entry : bestSelling) {
                    System.out.println(entry.getKey() + " - Qty Sold: " + entry.getValue());
                }
                break;

            case 4:
                System.out.println("\n=== TOP CUSTOMERS ===");
                List<Map.Entry<Customer, Double>> topCustomers = reportService.getTopCustomers();
                for (Map.Entry<Customer, Double> entry : topCustomers) {
                    System.out.printf("%s - Total Purchase: %.0f%n", entry.getKey().getName(), entry.getValue());
                }
                break;

            case 5:
                System.out.println("\n=== VIP CUSTOMER LIST ===");
                System.out.printf("%-8s %-20s %-15s %-15s %-10s%n", "ID", "Name", "Phone", "Tier", "Discount%");
                for (VIPCustomer vip : reportService.getVIPCustomerList()) {
                    System.out.printf("%-8s %-20s %-15s %-15s %.0f%%%n",
                            vip.getId(), vip.getName(), vip.getPhone(),
                            vip.getTier().name(), vip.getDiscountRate() * 100);
                }
                break;

            case 6:
                System.out.println("\n=== REVENUE AFTER DISCOUNT ===");
                System.out.printf("%-15s %-20s %-15s%n", "Transaction ID", "Customer", "Final Total");
                Map<Transaction, Double> revAfterDiscount = reportService.getRevenueAfterDiscount();
                double totalRev = 0;
                for (Map.Entry<Transaction, Double> entry : revAfterDiscount.entrySet()) {
                    System.out.printf("%-15s %-20s %.0f%n", entry.getKey().getTransactionId(), entry.getKey().getCustomer().getName(), entry.getValue());
                    totalRev += entry.getValue();
                }
                System.out.println("--------------------------------------------------");
                System.out.printf("Total Revenue: %.0f%n", totalRev);
                break;

            case 7:
                System.out.println("\n=== VIP TIER DISTRIBUTION ===");
                System.out.printf("%-10s %5s%n", "Tier", "Count");
                Map<VIPTier, Integer> distribution = reportService.getVIPTierDistribution();
                System.out.printf("%-10s %5d%n", "PLATINUM", distribution.get(VIPTier.PLATINUM));
                System.out.printf("%-10s %5d%n", "GOLD", distribution.get(VIPTier.GOLD));
                System.out.printf("%-10s %5d%n", "SILVER", distribution.get(VIPTier.SILVER));
                break;

            case 8:
                double[] revByType = reportService.getRevenueByCustomerType();
                double regCount = revByType[0];
                double regRev = revByType[1];
                double vipCount = revByType[2];
                double vipRev = revByType[3];
                double total = regRev + vipRev;
                
                System.out.println("\n=== REVENUE BY CUSTOMER TYPE ===");
                if (total == 0) {
                    System.out.println("No confirmed revenue recorded.");
                    break;
                }
                System.out.println("--- Regular Customers ---");
                System.out.printf("Count (unique buyers): %.0f%n", regCount);
                System.out.printf("Revenue: %.0f%n", regRev);
                System.out.printf("Percentage: %.2f%%%n", (regRev/total)*100);

                System.out.println("--- VIP Customers ---");
                System.out.printf("Count (unique buyers): %.0f%n", vipCount);
                System.out.printf("Revenue: %.0f%n", vipRev);
                System.out.printf("Percentage: %.2f%%%n", (vipRev/total)*100);
                System.out.println("-------------------------");
                System.out.printf("Total Revenue: %.0f%n", total);
                break;

            case 9:
                double[] discountData = reportService.getTotalDiscountGiven();
                System.out.println("\n=== TOTAL DISCOUNT GIVEN (IN-STORE) ===");
                System.out.printf("Total Subtotal: %.0f%n", discountData[0]);
                System.out.printf("Total Discount Amount: %.0f%n", discountData[1]);
                System.out.printf("Total Net Revenue: %.0f%n", discountData[2]);
                break;

            case 10:
                System.out.println("\n=== LOW STOCK PRODUCTS ===");
                List<Product> lowStock = reportService.getLowStockProducts();
                if (lowStock.isEmpty()) {
                    System.out.println("No low stock products found.");
                } else {
                    System.out.printf("%-8s %-20s %-8s %-10s%n", "ID", "Name", "Stock", "Status");
                    for (Product p : lowStock) {
                        String status = p.isActive() ? "Active" : "Inactive";
                        System.out.printf("%-8s %-20s %-8d %-10s%n", p.getProductId(), p.getProductName(), p.getStockQuantity(), status);
                    }
                }
                break;

            case 11:
                System.out.println("\n=== ACTIVE PRODUCTS ===");
                List<Product> activeProducts = reportService.getActiveProducts();
                if (activeProducts.isEmpty()) {
                    System.out.println("No active products found.");
                } else {
                    System.out.printf("%-8s %-20s %-15s %10s %8s%n", "ID", "Name", "Category", "Price", "Stock");
                    for (Product p : activeProducts) {
                        p.displayInfo();
                    }
                }
                break;

            case 12:
                System.out.println("\n=== INACTIVE PRODUCTS ===");
                List<Product> inactiveProducts = reportService.getInactiveProducts();
                if (inactiveProducts.isEmpty()) {
                    System.out.println("No inactive products found.");
                } else {
                    System.out.printf("%-8s %-20s %-15s %10s%n", "ID", "Name", "Category", "Last Price");
                    for (Product p : inactiveProducts) {
                        System.out.printf("%-8s %-20s %-15s %10.0f%n", p.getProductId(), p.getProductName(), p.getCategory(), p.getPrice());
                    }
                }
                break;

            case 13:
                System.out.println("\n=== PRODUCT PRICE HISTORY ===");
                List<Product> allProducts = reportService.getProductPriceHistory();
                if (allProducts.isEmpty()) {
                    System.out.println("No products found.");
                } else {
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
                break;
        }
    }
}
