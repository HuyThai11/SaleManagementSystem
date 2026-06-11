
package view;
import java.util.Map;
import java.util.Scanner;
import manager.ProductManager;
import manager.TransactionManager;
import model.Customer;
import model.Product;
import model.Transaction;
import manager.CustomerManager;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductManager productManager = new ProductManager();
    private static final CustomerManager customerManager = new CustomerManager();
    private static final TransactionManager transactionManager = new TransactionManager();

    
    public static void main(String[] args) {
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
                        System.out.println("========== REPORT ==========");
                        System.out.println("Total Revenue: " + transactionManager.calculateRevenue());
                        System.out.println();

                        System.out.println("----------- BEST-SELLING PRODUCTS -----------");
                        Map<Product, Integer> bestSelling = transactionManager.getBestSellingProducts();
                        bestSelling.entrySet().stream()
                                .sorted((a, b) -> b.getValue() - a.getValue())
                                .forEach(entry -> System.out.printf("%-8s %-20s %d units sold%n",
                                        entry.getKey().getProductId(),
                                        entry.getKey().getProductName(),
                                        entry.getValue()));
                        System.out.println("----------------------------------------------");
                        System.out.println();

                        System.out.println("----------- TOP CUSTOMERS -----------");
                        Map<Customer, Double> topCustomers = transactionManager.getTopCustomers();
                        topCustomers.entrySet().stream()
                                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                                .forEach(entry -> System.out.printf("%-8s %-20s %.0f VND%n",
                                        entry.getKey().getId(),
                                        entry.getKey().getName(),
                                        entry.getValue()));
                        System.out.println("--------------------------------------");
                        System.out.println();

                        transactionManager.displayHistory();
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
            System.out.println("1. Create transaction");
            System.out.println("2. Add product to transaction");
            System.out.println("3. Update product quantity");
            System.out.println("4. Remove product from transaction");
            System.out.println("5. Confirm transaction");
            System.out.println("6. Cancel transaction");
            System.out.println("7. View transaction history");
            System.out.println("0. Back");
            choice = readInt("Choose: ");

            switch (choice) {
                case 1:
                    createTransaction();
                    break;
                case 2:
                    addProductToTransaction();
                    break;
                case 3:
                    updateTransactionQuantity();
                    break;
                case 4:
                    removeProductFromTransaction();
                    break;
                case 5:
                    transactionManager.confirmTransaction(readLine("Transaction ID: "));
                    System.out.println("Transaction confirmed.");
                    break;
                case 6:
                    transactionManager.cancelTransaction(readLine("Transaction ID: "));
                    System.out.println("Transaction cancelled.");
                    break;
                case 7:
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
        customerManager.addCustomer(new Customer(id, name, phone, address));
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

    private static void createTransaction() {
        String id = readLine("Transaction ID: ");
        String customerId = readLine("Customer ID: ");
        Customer customer = customerManager.findById(customerId);
        String date = readLine("Date: ");
        transactionManager.createTransaction(id, customer, date);
        System.out.println("Transaction created. Add product before confirm.");
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

    private static void addSampleData() {
        productManager.addProduct(new Product("P01", "Laptop", "Electronics", 15000000, 10));
        productManager.addProduct(new Product("P02", "Mouse", "Accessories", 200000, 30));
        customerManager.addCustomer(new Customer("C01", "Nguyen Van A", "0901234567", "HCM"));
    }
}