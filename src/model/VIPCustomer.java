package model;

public class VIPCustomer extends Customer {
    private VIPTier tier;

    public VIPCustomer(String id, String name, String phone, String address, VIPTier tier) {
        super(id, name, phone, address);
        setTierManually(tier);
    }

    public VIPTier getTier() {
        return tier;
    }

    public void setTierManually(VIPTier tier) {
        if (tier == null) {
            throw new IllegalArgumentException("VIP tier cannot be null.");
        }
        this.tier = tier;
    }

    public void updateTier(int transactionCount) {
        VIPTier newTier = VIPTier.evaluate(transactionCount);
        if (newTier != null && newTier.getRequiredTransactionCount() > this.tier.getRequiredTransactionCount()) {
            this.tier = newTier;
        }
    }

    public String getNextTierInfo() {
        if (tier == VIPTier.PLATINUM) {
            return "You are at the highest VIP tier!";
        } else if (tier == VIPTier.GOLD) {
            return "Next tier: PLATINUM (" + VIPTier.PLATINUM.getRequiredTransactionCount() + " total transactions required)";
        } else {
            return "Next tier: GOLD (" + VIPTier.GOLD.getRequiredTransactionCount() + " total transactions required)";
        }
    }

    @Override
    public double calculateDiscount(double total) {
        return total * getDiscountRate();
    }

    @Override
    public String getCustomerType() {
        return "VIP";
    }

    @Override
    public double getDiscountRate() {
        return tier.getDiscountRate();
    }

    @Override
    public void displayInfo() {
        System.out.printf("%-8s %-20s %-15s %-25s [VIP Tier: %s]%n", getId(), getName(), getPhone(), getAddress(), tier);
        System.out.println("   -> " + getNextTierInfo());
    }
}
