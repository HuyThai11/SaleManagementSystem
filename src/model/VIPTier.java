package model;

public enum VIPTier {
    SILVER(0.05, 5),
    GOLD(0.10, 10),
    PLATINUM(0.15, 20);

    private final double discountRate;
    private final int requiredTransactionCount;

    VIPTier(double discountRate, int requiredTransactionCount) {
        this.discountRate = discountRate;
        this.requiredTransactionCount = requiredTransactionCount;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public int getRequiredTransactionCount() {
        return requiredTransactionCount;
    }

    public static VIPTier evaluate(int transactionCount) {
        if (transactionCount >= PLATINUM.getRequiredTransactionCount()) {
            return PLATINUM;
        } else if (transactionCount >= GOLD.getRequiredTransactionCount()) {
            return GOLD;
        } else if (transactionCount >= SILVER.getRequiredTransactionCount()) {
            return SILVER;
        }
        return null;
    }
}
