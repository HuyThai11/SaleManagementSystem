
package model;


public class TransactionItem {
    private Product product;
    private int quantity;
    private double subtotal;
public TransactionItem() {
    subtotal = 0;
}

public TransactionItem(Product product,
                       int quantity) {

    this.product = product;
    this.quantity = quantity;

    this.calculateSubtotal();
}
public void calculateSubtotal() {

    subtotal = product.getPrice() * quantity; // BR6
}
public Product getProduct() {
    return product;
}

public int getQuantity() {
    return quantity;
}

public double getSubtotal() {
    return subtotal;
}
public void setProduct(Product product) {
    this.product = product;
    
    calculateSubtotal();
}

public void setQuantity(int quantity) {

    if(quantity > 0){
        this.quantity = quantity;

        calculateSubtotal();
    }
}

@Override
public String toString() {

    return product.getProductName()
            + " - Qty: "
            + quantity;
}

}
    