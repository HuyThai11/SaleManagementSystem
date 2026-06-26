package model;


import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public abstract class Transaction {
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
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(date.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected dd/MM/yyyy.");
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
        if (!product.isActive()) {
            throw new IllegalArgumentException("Cannot add inactive product to transaction.");
        }
        product.reserveStock(quantity);

        TransactionItem oldItem = findItem(product.getProductId());
        if (oldItem == null) {
            items.add(new TransactionItem(product, quantity));
        } else {
            int newQuantity = oldItem.getQuantity() + quantity;
            oldItem.setQuantity(newQuantity);
        }
    }

    public void updateQuantity(String productId, int newQuantity) {
        checkCanEdit();
        TransactionItem item = findItem(productId);
        if (item == null) {
            throw new IllegalArgumentException("Product is not in this transaction.");
        }
        
        int diff = newQuantity - item.getQuantity();
        if (diff > 0) {
            item.getProduct().reserveStock(diff);
        } else if (diff < 0) {
            item.getProduct().releaseStock(-diff);
        }
        
        item.setQuantity(newQuantity);
    }

    public void removeProduct(String productId) {
        checkCanEdit();
        TransactionItem item = findItem(productId);
        if (item == null) {
            throw new IllegalArgumentException("Product is not in this transaction.");
        }
        item.getProduct().releaseStock(item.getQuantity());
        items.remove(item);
    }

    protected double calculateSubTotal() {
        double total = 0;
        for (TransactionItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }

    public abstract double calculateTotal();

    public void confirm() {
        checkCanEdit();
        if (items.isEmpty()) {
            throw new IllegalStateException("Transaction must have at least one product.");
        }
        for (TransactionItem item : items) {
            Product product = item.getProduct();
            if (product == null) {
                throw new IllegalStateException("Transaction contains an invalid null product.");
            }
            if (!product.isActive()) {
                throw new IllegalStateException("Cannot confirm. Product " + product.getProductName() + " is inactive.");
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
        for (TransactionItem item : items) {
            item.getProduct().releaseStock(item.getQuantity());
        }
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

    public abstract void displayInfo();

    private void checkCanEdit() {
        if (confirmed || cancelled) {
            throw new IllegalStateException("Cannot edit confirmed or cancelled transaction.");
        }
    }
}