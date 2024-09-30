import java.util.Random;

public class CorporateCustomer extends Customer {
    private static int corporateCustomerCounter = 5000;

    private String companyName;

    // Constructor for the CorporateCustomer class
    public CorporateCustomer(String firstName, String lastName, String phone, String address, String idOrPassportNumber, String companyName) {
        super(firstName, lastName, phone, address, idOrPassportNumber);
        this.companyName = companyName;
        setCustomerId(generateCorporateCustomerId());
    }

    // Method to generate corporate customer ID
    private synchronized String generateCorporateCustomerId(){
        int random5DigitNumber = generateRandom5DigitNumber();
        return "CORP" + random5DigitNumber + corporateCustomerCounter++;
    }

    private int generateRandom5DigitNumber() {
        Random random = new Random();
        return 10000 + random.nextInt(90000); // Generates a random integer between 10000 and 99999
    }


    // Getter and setter for companyName
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    // Override toString method for better object representation
    @Override
    public String toString() {
        return "CorporateCustomer{" +
                "customerId='" + getCustomerId() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", idOrPassportNumber='" + getIdOrPassportNumber() + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
