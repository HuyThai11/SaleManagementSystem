package model;


import java.util.ArrayList;

public class Transaction {
    private String transactionId;// id giao dịch (không trống)
    private Customer customer;// khách hàng thực hiện giao dịch (không null)
    private String date;// ngày giao dịch (không trống, định dạng "dd/MM/yyyy")
    private ArrayList<TransactionItem> items;// danh sách sản phẩm trong giao dịch
    private boolean confirmed;// xác nhận giao dịch??
    private boolean cancelled;// hủy giao dịch??

    public Transaction(String transactionId, Customer customer, String date) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty.");
        }
        this.transactionId = transactionId.trim();
        this.customer = customer;
        this.date = date.trim();
        this.items = new ArrayList<>();
        this.confirmed = false;
        this.cancelled = false;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<TransactionItem> getItems() {
        return items;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void addProduct(Product product, int quantity) {
        checkCanEdit();
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        if (!product.hasEnoughStock(quantity)) {
            throw new IllegalArgumentException("Not enough stock.");
        }

        TransactionItem oldItem = findItem(product.getProductId());
        if (oldItem == null) {
            items.add(new TransactionItem(product, quantity));
        } else {
            int newQuantity = oldItem.getQuantity() + quantity;
            if (!product.hasEnoughStock(newQuantity)) {
                throw new IllegalArgumentException("Not enough stock.");
            }
            oldItem.setQuantity(newQuantity);
        }
    }

    public void updateQuantity(String productId, int newQuantity) {
        checkCanEdit();
        TransactionItem item = findItem(productId);
        if (item == null) {
            throw new IllegalArgumentException("Product is not in this transaction.");
        }
        if (!item.getProduct().hasEnoughStock(newQuantity)) {
            throw new IllegalArgumentException("Not enough stock.");
        }
        item.setQuantity(newQuantity);
    }

    public void removeProduct(String productId) {
        checkCanEdit();
        TransactionItem item = findItem(productId);
        if (item == null) {
            throw new IllegalArgumentException("Product is not in this transaction.");
        }
        items.remove(item);
    }

    public double calculateTotal() {
        double total = 0;
        for (TransactionItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }

    public void confirm() {
        checkCanEdit();
        if (items.isEmpty()) {
            throw new IllegalStateException("Transaction must have at least one product.");
        }
        for (TransactionItem item : items) {
            Product product = item.getProduct();
            if (!product.hasEnoughStock(item.getQuantity())) {
                throw new IllegalArgumentException("Not enough stock for " + product.getProductName() + ".");
            }
        }
        for (TransactionItem item : items) {
            Product product = item.getProduct();
            product.reduceStock(item.getQuantity());
        }
        confirmed = true;
    }

    public void cancel() {
        checkCanEdit();
        cancelled = true;
    }

    public TransactionItem findItem(String productId) {
        for (TransactionItem item : items) {
            if (item.getProduct().getProductId().equalsIgnoreCase(productId)) {
                return item;
            }
        }
        return null;
    }

    public String getStatus() {
        if (cancelled) {
            return "Cancelled";
        }
        if (confirmed) {
            return "Confirmed";
        }
        return "Pending";
    }

    public void displayInfo() {
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Date: " + date);
        System.out.println("Status: " + getStatus());
        System.out.printf("%-20s %5s %10s %12s%n", "Product", "Qty", "Price", "Line Total");
        for (TransactionItem item : items) {
            item.displayInfo();
        }
        System.out.printf("Total: %.0f%n", calculateTotal());
    }

    private void checkCanEdit() {
        if (confirmed || cancelled) {
            throw new IllegalStateException("Cannot edit confirmed or cancelled transaction.");
        }
    }
}