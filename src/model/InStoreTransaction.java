package model;

public class InStoreTransaction extends Transaction {
    private double discountRate;
    private String approvedBy;

    // Normal constructor (no discount)
    public InStoreTransaction(String transactionId, Customer customer, String date) {
        super(transactionId, customer, date);
        this.discountRate = 0.0;
        this.approvedBy = "N/A";
    }

    // Constructor with discount
    public InStoreTransaction(String transactionId, Customer customer, String date, double discountRate, String approvedBy) {
        super(transactionId, customer, date);
        setDiscountRate(discountRate);
        setApprovedBy(approvedBy);
        if (discountRate > 0 && (this.approvedBy == null || this.approvedBy.trim().isEmpty() || this.approvedBy.equals("N/A"))) {
            throw new IllegalArgumentException("Approver name is required for transactions with a discount.");
        }
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        if (discountRate < 0.0 || discountRate > 1.0) {
            throw new IllegalArgumentException("Discount rate must be between 0.0 and 1.0.");
        }
        this.discountRate = discountRate;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy != null ? approvedBy.trim() : "N/A";
    }

    @Override
    public double calculateTotal() {
        double subtotal = calculateSubTotal();
        double vipDiscountAmount = getCustomer().calculateDiscount(subtotal);
        double afterVip = subtotal - vipDiscountAmount;
        return afterVip - (afterVip * discountRate);
    }

    @Override
    public void displayInfo() {
        System.out.println("Transaction ID: " + getTransactionId());
        System.out.println("Customer: " + getCustomer().getName());
        System.out.println("Date: " + getDate());
        System.out.println("Status: " + getStatus());
        System.out.println("Type: In-Store");
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

        if (discountRate > 0) {
            System.out.printf("Manual Discount: %.0f%% (Approved by: %s)%n", discountRate * 100, approvedBy);
        } else {
            System.out.println("Manual Discount: None");
        }
        System.out.printf("Total: %.0f%n", calculateTotal());
    }
}
