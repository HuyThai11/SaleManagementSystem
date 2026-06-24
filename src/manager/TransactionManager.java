package manager;


import model.Customer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.Product;
import model.Transaction;
import model.TransactionItem;
import model.InStoreTransaction;
import model.OnlineTransaction;
import model.VIPCustomer;

public class TransactionManager {
    private final ArrayList<Transaction> transactions;

    public TransactionManager() {
        transactions = new ArrayList<>();
    }

    public void setTransactions(java.util.List<Transaction> newTransactions) {
        this.transactions.clear();
        this.transactions.addAll(newTransactions);
    }

    public InStoreTransaction createInStoreTransaction(String id, Customer customer, String date) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        InStoreTransaction transaction = new InStoreTransaction(id, customer, date);
        transactions.add(transaction);
        return transaction;
    }

    public InStoreTransaction createInStoreTransactionWithDiscount(String id, Customer customer, String date, double discountRate, String approvedBy) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        InStoreTransaction transaction = new InStoreTransaction(id, customer, date, discountRate, approvedBy);
        transactions.add(transaction);
        return transaction;
    }

    public OnlineTransaction createOnlineTransaction(String id, Customer customer, String date, String shippingAddress) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        OnlineTransaction transaction = new OnlineTransaction(id, customer, date, shippingAddress);
        transactions.add(transaction);
        return transaction;
    }

    public OnlineTransaction createOnlineTransaction(String id, Customer customer, String date, String shippingAddress, double shippingFee) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        OnlineTransaction transaction = new OnlineTransaction(id, customer, date, shippingAddress, shippingFee);
        transactions.add(transaction);
        return transaction;
    }

    public Transaction findById(String id) {
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionId().equalsIgnoreCase(id)) {
                return transaction;
            }
        }
        return null;
    }

    public void confirmTransaction(String id) {
        Transaction transaction = findById(id);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }
        transaction.confirm();

        // Auto VIP tier update
        Customer customer = transaction.getCustomer();
        if (customer instanceof VIPCustomer) {
            VIPCustomer vipCustomer = (VIPCustomer) customer;
            int confirmedCount = countConfirmedTransactions(vipCustomer);
            vipCustomer.updateTier(confirmedCount);
        }
    }

    public void cancelTransaction(String id) {
        Transaction transaction = findById(id);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }
        transaction.cancel();
    }
    
    public void updateTransaction(
        String transactionId,
        String productId,
        int quantity) {

    Transaction transaction = findById(transactionId);

    if(transaction == null){
        throw new IllegalArgumentException(
                "Transaction not found.");
    }

    transaction.updateQuantity(
            productId,
            quantity);
    }
    
    public ArrayList<Transaction> getTransactionList() {
        return transactions;
    }

    public double calculateRevenue() {
        double revenue = 0;
        for (Transaction t : transactions) {
            if (t.isConfirmed()) {
                revenue += t.calculateTotal();
            }
        }
        return revenue;
    }

    public int countConfirmedTransactions(Customer customer) {
        if (customer == null) {
            return 0;
        }
        int count = 0;
        for (Transaction transaction : transactions) {
            if (transaction.isConfirmed() && transaction.getCustomer().getId().equalsIgnoreCase(customer.getId())) {
                count++;
            }
        }
        return count;
    }

    public void displayHistory() {
        if (transactions.isEmpty()) {
            System.out.println("No transaction yet.");
            return;
        }
        for (Transaction transaction : transactions) {
            transaction.displayInfo();
            System.out.println("----------------------------------------");
        }
    }
   
    public Map<Product, Integer> getBestSellingProducts() {
        Map<Product, Integer> result = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (!transaction.isConfirmed()) {
                continue;
            }
            for (TransactionItem item : transaction.getItems()) {
                Product product = item.getProduct();
                int current = result.getOrDefault(product, 0);
                result.put(product, current + item.getQuantity());
            }
        }
        return result;
    }
    
    public Map<Customer, Double> getTopCustomers() {
        Map<Customer, Double> result = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (!transaction.isConfirmed()) {
                continue;
            }
            Customer customer = transaction.getCustomer();
            double current = result.getOrDefault(customer, 0.0);
            result.put(customer, current + transaction.calculateTotal());
        }
        return result;
    }
}
