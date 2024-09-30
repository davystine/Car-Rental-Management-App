import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CustomerManager implements Serializable {
    // Map to store customers with their customerId as the key
    private Map<String, Customer> customerMap;

    // Constructor to initialize the CustomerManager
    public CustomerManager() {
        this.customerMap = new HashMap<>();
    }

    // Method to add a customer to the customerMap
    public void addCustomer(Customer customer) {
        customerMap.put(customer.getCustomerId(), customer);
    }

    public void addCorporateCustomer(CorporateCustomer customer) {
        customerMap.put(customer.getCustomerId(), customer);
    }

    // Method to remove a customer from the customerMap based on customerId
    public void removeCustomer(String customerId) {
        customerMap.remove(customerId);
    }

    // Method to get a customer from the customerMap based on customerId
    public Customer getCustomerById(String customerId) {
        return customerMap.get(customerId);
    }

    // Method to get a copy of all customers in the customerMap
    public Map<String, Customer> getAllCustomers() {
        return new HashMap<>(customerMap);
    }

    public void loadCustomersData() {
        String filePath = "Customers.ser";
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            // Load the CustomerManager object from the file
            CustomerManager loadedCustomerManager = (CustomerManager) objectIn.readObject();

            // Update the customerMap with the loaded data
            this.customerMap = loadedCustomerManager.customerMap;

            System.out.println("Customers data loaded from " + filePath);
            System.out.println("Number of loaded customers: " + customerMap.size());

            // Print information about each loaded customer
            customerMap.values().forEach(System.out::println);

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
            // Handle the absence of the file if needed
        } catch (IOException | ClassNotFoundException e) {
            // Handle other IOExceptions or class not found exception
            e.printStackTrace();
            // Handle the inability to load data if needed
        }
    }
    public class ExampleUsage {
        public static void main(String[] args) {
            // Create an instance of CustomerManager
            CustomerManager customerManager = new CustomerManager();

            // Call loadCustomersData to update the customerMap
            customerManager.loadCustomersData();

            // Now you can use customerManager and access the updated customerMap
            Map<String, Customer> updatedCustomerMap = customerManager.getAllCustomers();

            // Print information about each customer in the updated map
            updatedCustomerMap.values().forEach(System.out::println);
        }
    }

}
