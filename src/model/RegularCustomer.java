package model;

public class RegularCustomer extends Customer {
    public RegularCustomer(String id, String name, String phone, String address) {
        super(id, name, phone, address);
    }

    @Override
    public double calculateDiscount(double total) {
        return 0;
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }

    @Override
    public double getDiscountRate() {
        return 0.0;
    }
}
