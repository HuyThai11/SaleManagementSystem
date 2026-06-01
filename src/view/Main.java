
package view;
import model.Product;
import model.Customer;
import model.Transaction;
import model.TransactionItem;


public class Main {
    public static void main(String[] args) {
        Product p1 = new Product(
        "P01",
        "Laptop",
        "Electronics",
        1500,
        10);

    Product p2 = new Product(
        "P02",
        "Mouse",
        "Accessories",
        50,
        20);

    Customer c1 = new Customer(
        "C01",
        "Thai",
        "0123456789",
        "HCM");
    TransactionItem item1 =
        new TransactionItem(p1, 2);

    TransactionItem item2 =
        new TransactionItem(p2, 1);
    Transaction transaction =
        new Transaction(
                "T01",
                c1,
                "25/05/2026");
    
    transaction.getItemList().add(item1);
    transaction.getItemList().add(item2);

    transaction.calculateTotal();
    System.out.println(transaction);

    System.out.println(item1);

    System.out.println(item2);

    System.out.println(
        "Total: "
        + transaction.getTotalAmount());
    }
}
