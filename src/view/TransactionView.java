package view;

import manager.CustomerManager;
import manager.ProductManager;
import manager.TransactionManager;
import model.Customer;
import model.Product;
import model.Transaction;

public class TransactionView {
    private final TransactionManager transactionManager;
    private final CustomerManager customerManager;
    private final ProductManager productManager;

    public TransactionView(TransactionManager transactionManager, CustomerManager customerManager, ProductManager productManager) {
        this.transactionManager = transactionManager;
        this.customerManager = customerManager;
        this.productManager = productManager;
    }

    public void showMenu() {
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
            choice = InputHelper.readInt("Choose: ");

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
                    transactionManager.confirmTransaction(InputHelper.readLine("Transaction ID: "));
                    System.out.println("Transaction confirmed.");
                    break;
                case 7:
                    transactionManager.cancelTransaction(InputHelper.readLine("Transaction ID: "));
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

    private void createInStoreTransaction() {
        String id = InputHelper.readLine("Transaction ID: ");
        String customerId = InputHelper.readLine("Customer ID: ");
        Customer customer = customerManager.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        String date = InputHelper.readLine("Date: ");
        
        String hasDiscount = InputHelper.readLine("Apply discount? (y/n): ");
        if (hasDiscount.equalsIgnoreCase("y")) {
            double discountRate = InputHelper.readDouble("Discount rate (e.g. 0.1 for 10%): ");
            String approvedBy = InputHelper.readLine("Approved by: ");
            transactionManager.createInStoreTransactionWithDiscount(id, customer, date, discountRate, approvedBy);
        } else {
            transactionManager.createInStoreTransaction(id, customer, date);
        }
        System.out.println("In-Store Transaction created. Add product before confirm.");
    }

    private void createOnlineTransaction() {
        String id = InputHelper.readLine("Transaction ID: ");
        String customerId = InputHelper.readLine("Customer ID: ");
        Customer customer = customerManager.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        String date = InputHelper.readLine("Date: ");
        String shippingAddress = InputHelper.readLine("Shipping Address: ");
        
        String useCustomFee = InputHelper.readLine("Use custom shipping fee? (y/n): ");
        if (useCustomFee.equalsIgnoreCase("y")) {
            double shippingFee = InputHelper.readDouble("Shipping Fee: ");
            transactionManager.createOnlineTransaction(id, customer, date, shippingAddress, shippingFee);
        } else {
            transactionManager.createOnlineTransaction(id, customer, date, shippingAddress);
        }
        System.out.println("Online Transaction created. Add product before confirm.");
    }

    private void addProductToTransaction() {
        Transaction transaction = getTransactionFromInput();
        String productId = InputHelper.readLine("Product ID: ");
        Product product = productManager.findById(productId);
        if (product == null) {
           System.out.println("Error: Product ID."+ productId + " not found.");
           return;                                                                                                                          
        }
        int quantity = InputHelper.readInt("Quantity: ");
        transaction.addProduct(product, quantity);
        System.out.println("Product added to transaction.");
    }

    private void updateTransactionQuantity() {
        Transaction transaction = getTransactionFromInput();
        String productId = InputHelper.readLine("Product ID: ");
        int quantity = InputHelper.readInt("New quantity: ");
        String transactionId = transaction.getTransactionId();
        transactionManager.updateTransaction( transactionId, productId, quantity);
        System.out.println("Quantity updated.");
    }

    private void removeProductFromTransaction() {
        Transaction transaction = getTransactionFromInput();
        String productId = InputHelper.readLine("Product ID: ");
        transaction.removeProduct(productId);
        System.out.println("Product removed from transaction.");
    }

    private Transaction getTransactionFromInput() {
        String transactionId = InputHelper.readLine("Transaction ID: ");
        Transaction transaction = transactionManager.findById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }
        return transaction;
    }
}
