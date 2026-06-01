package model;

import java.util.ArrayList;

public class Transaction {

    private String transactionId;
    private Customer customer;
    private String date;
    private ArrayList<TransactionItem> itemList;
    private double totalAmount;

    
    public Transaction() {
        itemList = new ArrayList<>();
        totalAmount = 0;
    }

    public Transaction(String transactionId,
                       Customer customer,
                       String date) {

        this.transactionId = transactionId;
        this.customer = customer;
        this.date = date;
        this.itemList = new ArrayList<>();
        this.totalAmount = 0;
    }

    // Calculate total bill (BR6)
    public void calculateTotal() {

        totalAmount = 0;

        for (TransactionItem item : itemList) {
            totalAmount += item.getSubtotal();
        }
    }

    // Add item into transaction
    public void addItem(TransactionItem item) {

        if (item != null) {
            itemList.add(item);
            calculateTotal();
        }
    }

    // Remove item from transaction
    public void removeItem(TransactionItem item) {

        if (item != null) {
            itemList.remove(item);
            calculateTotal();
        }
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<TransactionItem> getItemList() {
        return itemList;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    // Setters
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {

        return "Transaction ID: " + transactionId
                + ", Customer: " + customer.getCustomerName()
                + ", Total Amount: " + totalAmount;
    }
}