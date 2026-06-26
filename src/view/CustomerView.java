package view;

import manager.CustomerManager;
import model.RegularCustomer;

public class CustomerView {
    private final CustomerManager customerManager;

    public CustomerView(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void showMenu() {
        int choice;
        do {
            System.out.println("----- Customer Menu -----");
            System.out.println("1. Add customer");
            System.out.println("2. Update customer");
            System.out.println("3. Remove customer");
            System.out.println("4. View all customers");
            System.out.println("0. Back");
            choice = InputHelper.readInt("Choose: ");

            try {
                switch (choice) {
                    case 1:
                        addCustomer();
                        break;
                    case 2:
                        updateCustomer();
                        break;
                    case 3:
                        removeCustomer();
                        break;
                    case 4:
                        customerManager.displayAll();
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 0);
    }

    private void addCustomer() {
        String id = InputHelper.readLine("Customer ID: ");
        String name = InputHelper.readLine("Customer name: ");
        String phone = InputHelper.readLine("Phone: ");
        String address = InputHelper.readLine("Address: ");
        customerManager.addCustomer(new RegularCustomer(id, name, phone, address));
        System.out.println("Customer added.");
    }

    private void updateCustomer() {
        String id = InputHelper.readLine("Customer ID: ");
        String name = InputHelper.readLine("New name: ");
        String phone = InputHelper.readLine("New phone: ");
        String address = InputHelper.readLine("New address: ");
        customerManager.updateCustomer(id, name, phone, address);
        System.out.println("Customer updated.");
    }

    private void removeCustomer() {
        String id = InputHelper.readLine("Customer ID: ");
        if (customerManager.removeCustomer(id)) {
            System.out.println("Customer removed.");
        } else {
            System.out.println("Customer not found.");
        }
    }
}
