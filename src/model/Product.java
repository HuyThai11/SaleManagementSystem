
package model;


public class Product {
    
    private String productId;
    private String productName;
    private String category;
    private double price;
    private int stockQuantity;
    
public Product(){

}

public Product(String productId, String productName, String category, double price, int stockQuantity){
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
}
public String getProductId(){
    return productId;
}
public String getProductName() {
    return productName;
}

public String getCategory(){
    return category;
}

public double getPrice(){
    return price;
}

public int getStockQuantity(){
    return stockQuantity;
}

public void setProductName(String productName){

    if(productName != null
            && !productName.trim().isEmpty()){

        this.productName = productName;
    }
}

public void setCategory(String category){

    if(category != null
            && !category.trim().isEmpty()){

        this.category = category;
    }
}
public void setPrice(double price){

    if(price > 0){// BR2: price must not be invalid 
        this.price = price;
    }
}
public void setStockQuantity(int stockQuantity){
// // recalculate subtotal when quantity changes
    if(stockQuantity >= 0){// BR4: stock must not be negative
        this.stockQuantity = stockQuantity;
    }
}
// phan them vao ai auditlog
@Override
public String toString() {

    return productId + " - " + productName;
    }
}
