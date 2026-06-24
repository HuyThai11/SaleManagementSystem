package view;

import manager.ProductManager;
import model.Product;

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
                    productManager.displayAll();
                    break;
                case 5:
                    String keyword = InputHelper.readLine("Enter Keyword: ");
                    productManager.searchProduct(keyword);
                    break;
                default:
                    break;
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
