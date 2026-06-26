package manager;

import model.Customer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Product;
import model.Transaction;
import model.TransactionItem;
import model.InStoreTransaction;
import model.OnlineTransaction;
import model.VIPCustomer;
import java.util.function.Consumer;

public class TransactionManager {
    private final LinkedHashMap<String, Transaction> transactions;
    private Consumer<Transaction> onDataChanged;

    public TransactionManager() {
        transactions = new LinkedHashMap<>();
    }

    public void setOnDataChanged(Consumer<Transaction> callback) {
        this.onDataChanged = callback;
    }

    private void notifyDataChanged(Transaction transaction) {
        if (onDataChanged != null && transaction != null) {
            onDataChanged.accept(transaction);
        }
    }

    public void setTransactions(List<Transaction> newTransactions) {
        this.transactions.clear();
        for (Transaction t : newTransactions) {
            this.transactions.put(t.getTransactionId().toUpperCase(), t);
        }
    }

    public InStoreTransaction createInStoreTransaction(String id, Customer customer, String date) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        InStoreTransaction transaction = new InStoreTransaction(id, customer, date);
        transactions.put(id.toUpperCase(), transaction);
        notifyDataChanged(transaction);
        return transaction;
    }

    public InStoreTransaction createInStoreTransactionWithDiscount(String id, Customer customer, String date, double discountRate, String approvedBy) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        InStoreTransaction transaction = new InStoreTransaction(id, customer, date, discountRate, approvedBy);
        transactions.put(id.toUpperCase(), transaction);
        notifyDataChanged(transaction);
        return transaction;
    }

    public OnlineTransaction createOnlineTransaction(String id, Customer customer, String date, String shippingAddress) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        OnlineTransaction transaction = new OnlineTransaction(id, customer, date, shippingAddress);
        transactions.put(id.toUpperCase(), transaction);
        notifyDataChanged(transaction);
        return transaction;
    }

    public OnlineTransaction createOnlineTransaction(String id, Customer customer, String date, String shippingAddress, double shippingFee) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        OnlineTransaction transaction = new OnlineTransaction(id, customer, date, shippingAddress, shippingFee);
        transactions.put(id.toUpperCase(), transaction);
        notifyDataChanged(transaction);
        return transaction;
    }

    public Transaction findById(String id) {
        if (id == null) return null;
        return transactions.get(id.toUpperCase());
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
        notifyDataChanged(transaction);
    }

    public void cancelTransaction(String id) {
        Transaction transaction = findById(id);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }
        transaction.cancel();
        notifyDataChanged(transaction);
    }
    
    public void updateTransaction(String transactionId, String productId, int quantity) {
        Transaction transaction = findById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }
        transaction.updateQuantity(productId, quantity);
        notifyDataChanged(transaction);
    }
    
    public ArrayList<Transaction> getTransactionList() {
        return new ArrayList<>(transactions.values());
    }

    public double calculateRevenue() {
        double revenue = 0;
        for (Transaction t : transactions.values()) {
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
        for (Transaction transaction : transactions.values()) {
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
        for (Transaction transaction : transactions.values()) {
            transaction.displayInfo();
            System.out.println("----------------------------------------");
        }
    }
   
    public Map<Product, Integer> getBestSellingProducts() {
        Map<Product, Integer> result = new java.util.HashMap<>();
        for (Transaction transaction : transactions.values()) {
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
        Map<Customer, Double> result = new java.util.HashMap<>();
        for (Transaction transaction : transactions.values()) {
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
