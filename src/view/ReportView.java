package view;

import report.ReportService;

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
                String date =
                    InputHelper.readLine("Enter date (dd/MM/yyyy): ");

                double dailyRevenue =
                    reportService.generateDailyReport(date);

                    System.out.printf( "Revenue: %,.0f VND%n", dailyRevenue);

                break;

            case 2:
                int month =
                    InputHelper.readInt("Month: ");

                int year =
                    InputHelper.readInt("Year: ");

                double monthlyRevenue =
                            reportService.generateMonthlyReport(month, year);

                    System.out.printf("Revenue: %,.0f VND%n", monthlyRevenue);

                break;

            case 3:
                reportService.getBestSellingProducts();
                break;

            case 4:
                reportService.getTopCustomers();
                break;

            case 5:
                reportService.printVIPCustomerList();
                break;

            case 6:
                reportService.printRevenueAfterDiscount();
                break;

            case 7:
                reportService.printVIPTierDistribution();
                break;

            case 8:
                reportService.printRevenueByCustomerType();
                break;

            case 9:
                reportService.printTotalDiscountGiven();
                break;

            case 10:
                reportService.printLowStockProducts();
                break;

            case 11:
                reportService.printActiveProducts();
                break;

            case 12:
                reportService.printInactiveProducts();
                break;

            case 13:
                reportService.printProductPriceHistory();
                break;
        }
    }
}
