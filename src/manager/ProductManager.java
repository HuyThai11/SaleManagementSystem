
package manager;

import java.util.ArrayList;
import model.Product;

public class ProductManager {
    private final ArrayList<Product> products;

    public ProductManager() {
        products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (findById(product.getProductId()) != null) {
            throw new IllegalArgumentException("Product ID already exists.");
        }
        products.add(product);
    }

    public Product findById(String productId) {
        for (Product product : products) {
            if (product.getProductId().equalsIgnoreCase(productId)) {
                return product;
            }
        }
        return null;
    }

    public void updateProduct(String id, String name, String category, double price, int stock) {
        Product product = findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        product.setProductName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setStockQuantity(stock);
    }

    public boolean removeProduct(String id) {
        Product product = findById(id);
        if (product == null) {
            return false;
        }
        products.remove(product);
        return true;
    }

    public int checkStock(String productId) {
        Product product = findById(productId);
        if (product == null) {
            return -1;
        }
        return product.getStockQuantity();
    }

    public void updateStock(String productId, int newStock) {
        Product product = findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        product.setStockQuantity(newStock);
    }

    /**
     *
     */
    public void displayAll() {
        System.out.printf("%-8s %-20s %-15s %10s %8s%n", "ID", "Name", "Category", "Price", "Stock");
        for (Product product : products) {
            product.displayInfo();
        }
    }

   
}
