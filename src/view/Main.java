
package view;
import manager.ProductManager;
import manager.TransactionManager;
import manager.CustomerManager;
import report.ReportService;
import io.ProductFileHandler;
import io.CustomerFileHandler;
import io.TransactionFileHandler;

public class Main {
    private static final ProductManager productManager = new ProductManager();
    private static final CustomerManager customerManager = new CustomerManager();
    private static final TransactionManager transactionManager = new TransactionManager();
    
    private static final ProductFileHandler productFileHandler = new ProductFileHandler();
    private static final CustomerFileHandler customerFileHandler = new CustomerFileHandler();
    private static TransactionFileHandler transactionFileHandler;
    
    public static void main(String[] args) {
        // Integrate File I/O for Milestone 4
        productManager.setProducts(productFileHandler.loadProducts());
        customerManager.setCustomers(customerFileHandler.loadCustomers());
        
        transactionFileHandler = new TransactionFileHandler(productManager, customerManager);
        transactionManager.setTransactions(transactionFileHandler.loadTransactions());

        ReportService reportService = new ReportService(customerManager, productManager, transactionManager);
        // addSampleData(); // Disabled for actual data persistence

        ProductView productView = new ProductView(productManager);
        CustomerView customerView = new CustomerView(customerManager);
        TransactionView transactionView = new TransactionView(transactionManager, customerManager, productManager);
        ReportView reportView = new ReportView(reportService);

        int choice;
        do {
            showMainMenu();
            choice = InputHelper.readInt("Choose: ");
            try {
                switch (choice) {
                    case 1:
                        productView.showMenu();
                        break;
                    case 2:
                        customerView.showMenu();
                        break;
                    case 3:
                        transactionView.showMenu();
                        break;
                    case 4:
                        reportView.showMenu();
                        break;
                    case 5:
                        System.out.println("Saving data to files before exit...");
                        productFileHandler.saveProducts(productManager.getAllProducts());
                        customerFileHandler.saveCustomers(customerManager.getAllCustomers());
                        transactionFileHandler.saveTransactions(transactionManager.getTransactionList());
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
}