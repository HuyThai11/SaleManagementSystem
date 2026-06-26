package manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import model.Customer;
import model.VIPCustomer;
import model.VIPTier;

public class CustomerManager {
    private final LinkedHashMap<String, Customer> customers;
    private Consumer<Customer> onDataChanged;

    public CustomerManager() {
        customers = new LinkedHashMap<>();
    }

    public void setOnDataChanged(Consumer<Customer> callback) {
        this.onDataChanged = callback;
    }

    private void notifyDataChanged(Customer customer) {
        if (onDataChanged != null && customer != null) {
            onDataChanged.accept(customer);
        }
    }

    public void setCustomers(List<Customer> newCustomers) {
        this.customers.clear();
        for (Customer c : newCustomers) {
            this.customers.put(c.getId().toUpperCase(), c);
        }
    }

    public void addCustomer(Customer customer) {
        if (findById(customer.getId()) != null) {
            throw new IllegalArgumentException("Customer ID already exists.");
        }
        customers.put(customer.getId().toUpperCase(), customer);
        notifyDataChanged(customer);
    }

    public Customer findById(String customerId) {
        if (customerId == null) return null;
        return customers.get(customerId.toUpperCase());
    }

    public void updateCustomer(String id, String name, String phone, String address) {
        Customer customer = findById(id);
        if (customer == null || !customer.isActive()) {
            throw new IllegalArgumentException("Customer not found or inactive.");
        }
        customer.setName(name);
        customer.setPhone(phone);
        customer.setAddress(address);
        notifyDataChanged(customer);
    }

    public boolean removeCustomer(String id) {
        Customer customer = findById(id);
        if (customer == null || !customer.isActive()) {
            return false;
        }
        customer.deactivate();
        notifyDataChanged(customer);
        return true;
    }

    public void upgradeToVIP(String id, VIPTier tier) {
        Customer customer = findById(id);
        if (customer == null || !customer.isActive()) {
            throw new IllegalArgumentException("Customer not found or inactive.");
        }
        if (customer instanceof VIPCustomer) {
            throw new IllegalArgumentException("Customer is already a VIP.");
        }
        
        VIPCustomer vipCustomer = new VIPCustomer(customer.getId(), customer.getName(), customer.getPhone(), customer.getAddress(), tier);
        customers.put(customer.getId().toUpperCase(), vipCustomer);
        notifyDataChanged(vipCustomer);
    }

    public void setVIPTier(String id, VIPTier tier) {
        Customer customer = findById(id);
        if (customer == null || !customer.isActive()) {
            throw new IllegalArgumentException("Customer not found or inactive.");
        }
        if (customer instanceof VIPCustomer) {
            ((VIPCustomer) customer).setTierManually(tier);
        } else {
            throw new IllegalArgumentException("Customer is not a VIP.");
        }
        notifyDataChanged(customer);
    }

    public ArrayList<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public ArrayList<Customer> getActiveCustomers() {
        ArrayList<Customer> active = new ArrayList<>();
        for (Customer c : customers.values()) {
            if (c.isActive()) active.add(c);
        }
        return active;
    }

    public void displayAll() {
        System.out.printf("%-8s %-20s %-15s %-25s%n", "ID", "Name", "Phone", "Address");
        for (Customer customer : getActiveCustomers()) {
            customer.displayInfo(); // Polymorphic call
        }
    }
}