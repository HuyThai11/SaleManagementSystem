package manager;

import java.util.ArrayList;
import model.Customer;
import model.VIPCustomer;
import model.VIPTier;

public class CustomerManager {
    private final ArrayList<Customer> customers;

    public CustomerManager() {
        customers = new ArrayList<>();
    }

    public void setCustomers(java.util.List<Customer> newCustomers) {
        this.customers.clear();
        this.customers.addAll(newCustomers);
    }

    public void addCustomer(Customer customer) {
        if (findById(customer.getId()) != null) {
            throw new IllegalArgumentException("Customer ID already exists.");
        }
        customers.add(customer);
    }

    public Customer findById(String customerId) {
        for (Customer customer : customers) {
            if (customer.getId().equalsIgnoreCase(customerId)) {
                return customer;
            }
        }
        return null;
    }

    public void updateCustomer(String id, String name, String phone, String address) {
        Customer customer = findById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        customer.setName(name);
        customer.setPhone(phone);
        customer.setAddress(address);
    }

    public boolean removeCustomer(String id) {
        Customer customer = findById(id);
        if (customer == null) {
            return false;
        }
        customers.remove(customer);
        return true;
    }

    public void upgradeToVIP(String id, VIPTier tier) {
        Customer customer = findById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        if (customer instanceof VIPCustomer) {
            throw new IllegalArgumentException("Customer is already a VIP.");
        }
        
        VIPCustomer vipCustomer = new VIPCustomer(customer.getId(), customer.getName(), customer.getPhone(), customer.getAddress(), tier);
        int index = customers.indexOf(customer);
        customers.set(index, vipCustomer);
    }

    public void setVIPTier(String id, VIPTier tier) {
        Customer customer = findById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        if (customer instanceof VIPCustomer) {
            ((VIPCustomer) customer).setTierManually(tier);
        } else {
            throw new IllegalArgumentException("Customer is not a VIP.");
        }
    }

    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    public void displayAll() {
        System.out.printf("%-8s %-20s %-15s %-25s%n", "ID", "Name", "Phone", "Address");
        for (Customer customer : customers) {
            customer.displayInfo(); // Polymorphic call
        }
    }
}