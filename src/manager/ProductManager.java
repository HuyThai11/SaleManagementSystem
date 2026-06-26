package manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import model.Product;

public class ProductManager {
    private final LinkedHashMap<String, Product> products;
    private Consumer<Product> onDataChanged;

    public ProductManager() {
        products = new LinkedHashMap<>();
    }

    public void setProducts(List<Product> newProducts) {
        this.products.clear();
        for (Product p : newProducts) {
            this.products.put(p.getProductId().toUpperCase(), p);
        }
    }

    public void setOnDataChanged(Consumer<Product> callback) {
        this.onDataChanged = callback;
    }

    private void notifyDataChanged(Product product) {
        if (onDataChanged != null && product != null) {
            onDataChanged.accept(product);
        }
    }

    public void addProduct(Product product) {
        Product existingProduct = findById(product.getProductId());
        if (existingProduct != null && existingProduct.isActive()) {
            throw new IllegalArgumentException("Product ID already exists and is active.");
        }
        products.put(product.getProductId().toUpperCase(), product);
        notifyDataChanged(product);
    }

    public Product findById(String productId) {
        if (productId == null) return null;
        return products.get(productId.toUpperCase());
    }

    public List<Product> searchProduct(String keyword) {
        List<Product> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }
        String lowerKeyword = keyword.toLowerCase();
        for (Product product : products.values()) {
            if (!product.isActive()) continue;
            
            if (product.getProductId().equalsIgnoreCase(keyword)
                    || product.getProductName().toLowerCase().contains(lowerKeyword)
                    || product.getCategory().toLowerCase().contains(lowerKeyword)) {
                result.add(product);
            }
        }
        return result;
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
        notifyDataChanged(product);
    }

    public boolean removeProduct(String id) {
        Product product = findById(id);
        if (product == null || !product.isActive()) {
            return false;
        }
        product.deactivate();
        notifyDataChanged(product);
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
        notifyDataChanged(product);
    }

    public ArrayList<Product> getActiveProducts() {
        ArrayList<Product> activeProducts = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.isActive()) {
                activeProducts.add(product);
            }
        }
        return activeProducts;
    }

    public ArrayList<Product> getInactiveProducts() {
        ArrayList<Product> inactiveProducts = new ArrayList<>();
        for (Product product : products.values()) {
            if (!product.isActive()) {
                inactiveProducts.add(product);
            }
        }
        return inactiveProducts;
    }

    public ArrayList<Product> getLowStockProducts() {
        ArrayList<Product> lowStockProducts = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.isActive() && product.isLowStock()) {
                lowStockProducts.add(product);
            }
        }
        return lowStockProducts;
    }

    public ArrayList<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
}
