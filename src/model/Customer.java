
package model;


public class Customer {
    //Attributes
    private String customerId;
    private String customerName;
    private String phone;
    private String address;
    public Customer() {

}
//Constructors
public Customer(String customerId,
                String customerName,
                String phone,
                String address) {

    this.customerId = customerId;
    this.customerName = customerName;
    this.phone = phone;
    this.address = address;
} 
//Getters
public String getCustomerId() {
    return customerId;
}

public String getCustomerName() {
    return customerName;
}

public String getPhone() {
    return phone;
}

public String getAddress() {
    return address;
}
//Setters

public void setCustomerName(String customerName){

    if(customerName != null
            && !customerName.trim().isEmpty()){

        this.customerName = customerName;
    }
}

public void setPhone(String phone){

    if(phone != null
            && phone.matches("\\d{10}")){

        this.phone = phone;
    }
}

public void setAddress(String address) {
    this.address = address;
}
//toString()
@Override
public String toString() {

    return customerId + " - " + customerName;
}
}

