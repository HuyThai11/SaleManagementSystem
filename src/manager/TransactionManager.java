package manager;

import java.util.ArrayList;
import model.Customer;
import model.Transaction;

public class TransactionManager {
    private final ArrayList<Transaction> transactions;

    public TransactionManager() {
        transactions = new ArrayList<>();
    }

    public Transaction createTransaction(String id, Customer customer, String date) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        Transaction transaction = new Transaction(id, customer, date);
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
    
    public double calculateRevenue() {

        double revenue = 0;

        for(Transaction transaction : transactions){

            if(transaction.isConfirmed()){

                revenue += transaction.calculateTotal();
        }
    }

    return revenue;
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
}
