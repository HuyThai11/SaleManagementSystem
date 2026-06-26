package report;

import model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportServiceTest {

    @Test
    public void testGenerateMonthlyReport() {
        // Setup data
        Customer customer = new RegularCustomer("C01", "Alice", "0123456789", "Address");
        Product product = new Product("P01", "Laptop", "Electronics", 1000.0, 50);

        Transaction t1 = new InStoreTransaction("T01", customer, "15/06/2026", 0.0, "Admin");
        t1.addProduct(product, 2); // 2000.0
        t1.confirm();

        Transaction t2 = new InStoreTransaction("T02", customer, "20/06/2026", 0.1, "Admin");
        t2.addProduct(product, 1); // 1000.0 * 0.9 = 900.0
        t2.confirm();

        Transaction t3 = new InStoreTransaction("T03", customer, "10/05/2026", 0.0, "Admin");
        t3.addProduct(product, 1); // 1000.0
        t3.confirm();

        Transaction t4 = new InStoreTransaction("T04", customer, "30/06/2026", 0.0, "Admin");
        t4.addProduct(product, 1); // Unconfirmed, should be ignored

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);
        transactions.add(t3);
        transactions.add(t4);

        ReportService reportService = new ReportService(transactions);

        // Test monthly report for June 2026
        double expectedRevenueJune = 2000.0 + 900.0; // t1 + t2
        double actualRevenueJune = reportService.generateMonthlyReport(6, 2026);
        assertEquals(expectedRevenueJune, actualRevenueJune, "Monthly report should sum confirmed transactions for the specific month");

        // Test monthly report for May 2026
        double expectedRevenueMay = 1000.0; // t3
        double actualRevenueMay = reportService.generateMonthlyReport(5, 2026);
        assertEquals(expectedRevenueMay, actualRevenueMay, "Monthly report should sum confirmed transactions for the specific month");
    }
}
