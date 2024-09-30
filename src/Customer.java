import java.io.Serializable;
import java.util.Random;

public class Customer implements Serializable {
    private static int customerCounter = 1000;
    private String customerId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String idOrPassportNumber;

    // Constructor for the Customer class
    public Customer(String firstName, String lastName, String phone, String address, String idOrPassportNumber) {
        this.customerId = generateCustomerId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.idOrPassportNumber = idOrPassportNumber;
    }

    // Method to generate customer ID
    private synchronized String generateCustomerId(){
        int random5DigitNumber = generateRandom5DigitNumber();
        return "CSTR" + random5DigitNumber + customerCounter++;
    }

    private int generateRandom5DigitNumber() {
        Random random = new Random();
        return 10000 + random.nextInt(90000); // Generates a random integer between 10000 and 99999
    }

    // Protected setter for customerId
    protected void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // Getters and setters go here

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getIdOrPassportNumber() {
        return idOrPassportNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIdOrPassportNumber(String idOrPassportNumber) {
        this.idOrPassportNumber = idOrPassportNumber;
    }

    // Override toString method for better object representation
    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", idOrPassportNumber='" + idOrPassportNumber + '\'' +
                '}';
    }
}
