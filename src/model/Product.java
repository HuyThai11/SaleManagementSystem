
package model;

import java.util.ArrayList;

public class Product {
    private String productId;
    private String productName;
    private String category;
    private double price;
    private int stockQuantity;
    private boolean active;
    private ArrayList<Double> priceHistory;

    public Product(String productId, String productName, String category, double price, int stockQuantity) {
        this.active = true;
        this.priceHistory = new ArrayList<>();
        setProductId(productId);
        setProductName(productName);
        setCategory(category);
        setPrice(price);
        setStockQuantity(stockQuantity);
    }

    public String getProductId() {
        return productId;
    }

    private void setProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }
        this.productId = productId.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        this.productName = productName.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        this.category = category.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (this.price > 0 && this.price != price) {
            this.priceHistory.add(this.price);
        }
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        this.stockQuantity = stockQuantity;
    }

    public boolean hasEnoughStock(int quantity) {
        return quantity > 0 && stockQuantity >= quantity;
    }

    public void reduceStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalArgumentException("Not enough stock for " + productName + ".");
        }
        stockQuantity -= quantity;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isLowStock() {
        return stockQuantity < 5;
    }

    public ArrayList<Double> getPriceHistory() {
        return priceHistory;
    }

    public void displayInfo() {
        System.out.printf("%-8s %-20s %-15s %10.0f %8d%n",
                productId, productName, category, price, stockQuantity);
    }
}