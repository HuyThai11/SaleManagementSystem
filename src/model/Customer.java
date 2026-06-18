
package model;




public abstract class Customer extends Person {
    private String phone;
    private String address;

    public Customer(String id, String name, String phone, String address) {
        super(id, name);
        setPhone(phone);
        setAddress(address);
    }

    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty.");
        }
        
        if(!phone.matches("\\d{10}")){
            throw new IllegalArgumentException("Phone must contain exactly 10 digits.");
        
        }
        this.phone = phone.trim();
    }
    

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        this.address = address.trim();
    }
    
    public void setName(String name) {
    if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "Customer name cannot be empty.");
    }
    super.setName(name);
    }

    public abstract double calculateDiscount(double total);

    public abstract String getCustomerType();

    public abstract double getDiscountRate();

    @Override
    public void displayInfo() {
        System.out.printf("%-8s %-20s %-15s %-25s%n", getId(), getName(), phone, address);
    }
}
