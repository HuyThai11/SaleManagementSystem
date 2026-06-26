package model;

public class OnlineTransaction extends Transaction {
    public static final double DEFAULT_SHIPPING_FEE = 30000;
    
    private String shippingAddress;
    private double shippingFee;

    // Constructor with default shipping fee
    public OnlineTransaction(String transactionId, Customer customer, String date, String shippingAddress) {
        super(transactionId, customer, date);
        setShippingAddress(shippingAddress);
        this.shippingFee = DEFAULT_SHIPPING_FEE;
    }

    // Constructor with custom shipping fee
    public OnlineTransaction(String transactionId, Customer customer, String date, String shippingAddress, double shippingFee) {
        super(transactionId, customer, date);
        setShippingAddress(shippingAddress);
        setShippingFee(shippingFee);
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address cannot be empty.");
        }
        this.shippingAddress = shippingAddress.trim();
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        if (shippingFee < 0) {
            throw new IllegalArgumentException("Shipping fee cannot be negative.");
        }
        this.shippingFee = shippingFee;
    }

    @Override
    public double calculateTotal() {
        double subtotal = calculateSubTotal();
        double vipDiscountAmount = getCustomer().calculateDiscount(subtotal);
        double afterVip = subtotal - vipDiscountAmount;
        return afterVip + shippingFee;
    }

    @Override
    public void displayInfo() {
        System.out.println("Transaction ID: " + getTransactionId());
        System.out.println("Customer: " + getCustomer().getName());
        System.out.println("Date: " + getDate());
        System.out.println("Status: " + getStatus());
        System.out.println("Type: Online");
        System.out.printf("%-20s %5s %10s %12s%n", "Product", "Qty", "Price", "Line Total");
        for (TransactionItem item : getItems()) {
            item.displayInfo();
        }
        double subtotal = calculateSubTotal();
        System.out.printf("Subtotal: %.0f%n", subtotal);

        double vipDiscount = getCustomer().calculateDiscount(subtotal);
        if (vipDiscount > 0) {
            System.out.printf("VIP Discount (%s): -%.0f%n", getCustomer().getCustomerType(), vipDiscount);
        }

        System.out.println("Shipping Address: " + shippingAddress);
        System.out.printf("Shipping Fee: %.0f%n", shippingFee);
        System.out.printf("Total: %.0f%n", calculateTotal());
    }
}
