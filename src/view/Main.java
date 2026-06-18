
package view;
import java.util.Map;
import java.util.Scanner;
import manager.ProductManager;
import manager.TransactionManager;
import model.Customer;
import model.RegularCustomer;
import model.Product;
import model.Transaction;
import manager.CustomerManager;
import report.ReportService;
import model.InStoreTransaction;
import model.OnlineTransaction;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductManager productManager = new ProductManager();
    private static final CustomerManager customerManager = new CustomerManager();
    private static final TransactionManager transactionManager = new TransactionManager();
    private static ReportService reportService;
    
    public static void main(String[] args) {
        reportService = new ReportService(customerManager, productManager, transactionManager);
        addSampleData();
        int choice;
        do {
            showMainMenu();
            choice = readInt("Choose: ");
            try {
                switch (choice) {
                    case 1:
                        productMenu();
                        break;
                    case 2:
                        customerMenu();
                        break;
                    case 3:
                        transactionMenu();
                        break;
                    case 4:
                        reportMenu();
                        break;
                    case 5:
                        System.out.println("Goodbye.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 5);
    }

    private static void showMainMenu() {
        System.out.println("========== SaleManagement ==========");
        System.out.println("1. Manager Products");
        System.out.println("2. Manager Customers");
        System.out.println("3. Manager Transactions");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
    }

    private static void productMenu() {
        int choice;
        do {
            System.out.println("----- Product Menu -----");
            System.out.println("1. Add product");
            System.out.println("2. Update product");
            System.out.println("3. Remove product");
            System.out.println("4. View all products");
            System.out.println("5. Search products");
            System.out.println("0. Back");
            choice = readInt("Choose: ");

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    updateProduct();
                    break;
                case 3:
                    removeProduct();
                    break;
                case 4:
                    productManager.displayAll();
                    break;
                case 5:
                    System.out.print("Enter Keyword: ");
                    String keyword = scanner.nextLine();
                    productManager.searchProduct(keyword);
                    break;
                default:
                    break;
            }
        } while (choice != 0);
    }

    private static void customerMenu() {
        int choice;
        do {
            System.out.println("----- Customer Menu -----");
            System.out.println("1. Add customer");
            System.out.println("2. Update customer");
            System.out.println("3. Remove customer");
            System.out.println("4. View all customers");
            System.out.println("0. Back");
            choice = readInt("Choose: ");

            switch (choice) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    updateCustomer();
                    break;
                case 3:
                    removeCustomer();
                    break;
                case 4:
                    customerManager.displayAll();
                    break;
                default:
                    break;
            }
        } while (choice != 0);
    }

    private static void transactionMenu() {
        int choice;
        do {
            System.out.println("----- Transaction Menu -----");
            System.out.println("1. Create In-Store Transaction");
            System.out.println("2. Create Online Transaction");
            System.out.println("3. Add product to transaction");
            System.out.println("4. Update product quantity");
            System.out.println("5. Remove product from transaction");
            System.out.println("6. Confirm transaction");
            System.out.println("7. Cancel transaction");
            System.out.println("8. View transaction history");
            System.out.println("0. Back");
            choice = readInt("Choose: ");

            switch (choice) {
                case 1:
                    createInStoreTransaction();
                    break;
                case 2:
                    createOnlineTransaction();
                    break;
                case 3:
                    addProductToTransaction();
                    break;
                case 4:
                    updateTransactionQuantity();
                    break;
                case 5:
                    removeProductFromTransaction();
                    break;
                case 6:
                    transactionManager.confirmTransaction(readLine("Transaction ID: "));
                    System.out.println("Transaction confirmed.");
                    break;
                case 7:
                    transactionManager.cancelTransaction(readLine("Transaction ID: "));
                    System.out.println("Transaction cancelled.");
                    break;
                case 8:
                    transactionManager.displayHistory();
                    break;
                default:
                    break;
            }
        } while (choice != 0);
    }

    private static void addProduct() {
        String id = readLine("Product ID: ");
        String name = readLine("Product name: ");
        String category = readLine("Category: ");
        double price = readDouble("Price: ");
        int stock = readInt("Stock: ");
        productManager.addProduct(new Product(id, name, category, price, stock));
        System.out.println("Product added.");
    }

    private static void updateProduct() {
        String id = readLine("Product ID: ");
        String name = readLine("New name: ");
        String category = readLine("New category: ");
        double price = readDouble("New price: ");
        int stock = readInt("New stock: ");
        productManager.updateProduct(id, name, category, price, stock);
        System.out.println("Product updated.");
    }

    private static void removeProduct() {
        String id = readLine("Product ID: ");
        if (productManager.removeProduct(id)) {
            System.out.println("Product removed.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void addCustomer() {
        String id = readLine("Customer ID: ");
        String name = readLine("Customer name: ");
        String phone = readLine("Phone: ");
        String address = readLine("Address: ");
        customerManager.addCustomer(new RegularCustomer(id, name, phone, address));
        System.out.println("Customer added.");
    }

    private static void updateCustomer() {
        String id = readLine("Customer ID: ");
        String name = readLine("New name: ");
        String phone = readLine("New phone: ");
        String address = readLine("New address: ");
        customerManager.updateCustomer(id, name, phone, address);
        System.out.println("Customer updated.");
    }

    private static void removeCustomer() {
        String id = readLine("Customer ID: ");
        if (customerManager.removeCustomer(id)) {
            System.out.println("Customer removed.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    private static void createInStoreTransaction() {
        String id = readLine("Transaction ID: ");
        String customerId = readLine("Customer ID: ");
        Customer customer = customerManager.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        String date = readLine("Date: ");
        
        String hasDiscount = readLine("Apply discount? (y/n): ");
        if (hasDiscount.equalsIgnoreCase("y")) {
            double discountRate = readDouble("Discount rate (e.g. 0.1 for 10%): ");
            String approvedBy = readLine("Approved by: ");
            transactionManager.createInStoreTransactionWithDiscount(id, customer, date, discountRate, approvedBy);
        } else {
            transactionManager.createInStoreTransaction(id, customer, date);
        }
        System.out.println("In-Store Transaction created. Add product before confirm.");
    }

    private static void createOnlineTransaction() {
        String id = readLine("Transaction ID: ");
        String customerId = readLine("Customer ID: ");
        Customer customer = customerManager.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        String date = readLine("Date: ");
        String shippingAddress = readLine("Shipping Address: ");
        
        String useCustomFee = readLine("Use custom shipping fee? (y/n): ");
        if (useCustomFee.equalsIgnoreCase("y")) {
            double shippingFee = readDouble("Shipping Fee: ");
            transactionManager.createOnlineTransaction(id, customer, date, shippingAddress, shippingFee);
        } else {
            transactionManager.createOnlineTransaction(id, customer, date, shippingAddress);
        }
        System.out.println("Online Transaction created. Add product before confirm.");
    }

    private static void addProductToTransaction() {
        Transaction transaction = getTransactionFromInput();
        String productId = readLine("Product ID: ");
        Product product = (Product) productManager.findById(productId);
        if (product == null) {
           System.out.println("Error: Product ID."+ productId + " not found.");
           return;                                                                                                                          
        }
        int quantity = readInt("Quantity: ");
        transaction.addProduct(product, quantity);
        System.out.println("Product added to transaction.");
    }

    private static void updateTransactionQuantity() {
        Transaction transaction = getTransactionFromInput();
        String productId = readLine("Product ID: ");
        int quantity = readInt("New quantity: ");
        String transactionId = transaction.getTransactionId();
        transactionManager.updateTransaction( transactionId, productId, quantity);
        System.out.println("Quantity updated.");
    }

    private static void removeProductFromTransaction() {
        Transaction transaction = getTransactionFromInput();
        String productId = readLine("Product ID: ");
        transaction.removeProduct(productId);
        System.out.println("Product removed from transaction.");
    }

    private static Transaction getTransactionFromInput() {
        String transactionId = readLine("Transaction ID: ");
        Transaction transaction = transactionManager.findById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }
        return transaction;
    }

    private static String readLine(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int readInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            try {
                return Double.parseDouble(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
    private static void reportMenu() {

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

        int choice = readInt("Choose: ");

        switch(choice){

            case 1:
                String date =
                    readLine("Enter date (dd/MM/yyyy): ");

                double dailyRevenue =
                    reportService.generateDailyReport(date);

                    System.out.printf( "Revenue: %,.0f VND%n", dailyRevenue);

                break;

            case 2:
                int month =
                    readInt("Month: ");

                int year =
                    readInt("Year: ");

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

    private static void addSampleData() {
        productManager.addProduct(new Product("P01", "Laptop", "Electronics", 15000000, 10));
        productManager.addProduct(new Product("P02", "Mouse", "Accessories", 200000, 30));
        customerManager.addCustomer(new RegularCustomer("C01", "Nguyen Van A", "0901234567", "HCM"));
    }
}