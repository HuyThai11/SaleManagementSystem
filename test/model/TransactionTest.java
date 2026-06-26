package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    public void testValidDate() {
        Customer c = new RegularCustomer("C01", "John", "123456", "Address");
        
        Transaction t = new Transaction("T01", c, "15/06/2026") {
            @Override
            public double calculateTotal() {
                return 0;
            }
            @Override
            public void displayInfo() {}
        };
        
        assertEquals("15/06/2026", t.getDate(), "Date should match the input valid date");
    }

    @Test
    public void testInvalidDateFormat() {
        Customer c = new RegularCustomer("C01", "John", "123456", "Address");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("T01", c, "32/13/2025") {
                @Override
                public double calculateTotal() {
                    return 0;
                }
                @Override
                public void displayInfo() {}
            };
        });
        
        assertTrue(exception.getMessage().contains("Invalid date format"), "Should throw exception for malformed date");
    }
}
