package view;

import manager.ProductManager;
import model.Product;
import java.util.List;

public class ProductView {
    private final ProductManager productManager;

    public ProductView(ProductManager productManager) {
        this.productManager = productManager;
    }

    public void showMenu() {
        int choice;
        do {
            System.out.println("----- Product Menu -----");
            System.out.println("1. Add product");
            System.out.println("2. Update product");
            System.out.println("3. Remove product");
            System.out.println("4. View all products");
            System.out.println("5. Search products");
            System.out.println("0. Back");
            choice = InputHelper.readInt("Choose: ");

            try {
                switch (choice) {
                    case 1:
                        addProduct();
                        break;
                    case 2:
                        updateProduct();
                        break;
                    case 3:
                        removeProduct();
                        break;
                    case 4:
                        System.out.printf("%-8s %-20s %-15s %10s %8s%n", "ID", "Name", "Category", "Price", "Stock");
                        for (Product product : productManager.getAllProducts()) {
                            product.displayInfo();
                        }
                        break;
                    case 5:
                        String keyword = InputHelper.readLine("Enter Keyword: ");
                        List<Product> searchResults = productManager.searchProduct(keyword);
                        if (searchResults.isEmpty()) {
                            System.out.println("Product not found");
                        } else {
                            for (Product p : searchResults) {
                                System.out.println("ID: " + p.getProductId());
                                System.out.println("Name: " + p.getProductName());
                                System.out.println("Category: " + p.getCategory());
                                System.out.println("Price: " + p.getPrice());
                                System.out.println("Stock Quantity: " + p.getStockQuantity());
                                System.out.println("-----------------");
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 0);
    }

    private void addProduct() {
        String id = InputHelper.readLine("Product ID: ");
        String name = InputHelper.readLine("Product name: ");
        String category = InputHelper.readLine("Category: ");
        double price = InputHelper.readDouble("Price: ");
        int stock = InputHelper.readInt("Stock: ");
        productManager.addProduct(new Product(id, name, category, price, stock));
        System.out.println("Product added.");
    }

    private void updateProduct() {
        String id = InputHelper.readLine("Product ID: ");
        String name = InputHelper.readLine("New name: ");
        String category = InputHelper.readLine("New category: ");
        double price = InputHelper.readDouble("New price: ");
        int stock = InputHelper.readInt("New stock: ");
        productManager.updateProduct(id, name, category, price, stock);
        System.out.println("Product updated.");
    }

    private void removeProduct() {
        String id = InputHelper.readLine("Product ID: ");
        if (productManager.removeProduct(id)) {
            System.out.println("Product removed.");
        } else {
            System.out.println("Product not found.");
        }
    }
}
