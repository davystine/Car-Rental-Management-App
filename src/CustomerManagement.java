import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Map;


public class CustomerManagement extends JFrame {
    private JSplitPane customerManagementPanel;
    private JButton addButton;
    private JButton deleteButton;
    private JTextField lastnameTxt;
    private JTextField phoneTxt;
    private JTextField passportTxt;
    private JButton updateButton;
    private JButton cancelButton;
    private JTextField firstnameTxt;
    private JTextField companynameTxt;
    private JTextField addressTxt;
    private JTable customerTable;
    private JButton saveButton;
    private JButton loadButton;
    private JButton backButton;
    private CustomerManager customerManager;
    private MainMenu mainMenu;


    public CustomerManagement(CustomerManager customerManager, MainMenu mainMenu) {
        this.customerManager = customerManager;
        this.mainMenu = mainMenu;

        // Call the method to customerManagement the UI
        customerManagementUI();

        // Call the method to create the table panel
        initializeCustomerTable();

        // Load data from file in the constructor
        loadCustomersData();

        // Save the list of Customers to a file
        saveButton.addActionListener(e -> saveCustomersData());

        // Load the list of Customers from a file
        loadButton.addActionListener(e -> loadCustomersData());

        // Add a new customer
        addButton.addActionListener(e -> addCustomer());

        // Update customer data and refresh the table
        updateButton.addActionListener(e -> {
            updateCustomerData();
            clearInputFields();
            updateTableData((DefaultTableModel) customerTable.getModel());
        });

        // Delete a customer
        deleteButton.addActionListener(e -> deleteCustomer());

        // Clear input fields
        cancelButton.addActionListener(e -> clearInputFields());

        // Handle back button action (you can replace this with your actual back functionality)
        backButton.addActionListener(e -> {
            goBackToMenu();
        });

    }

    // Method to initialize the UI
    private void customerManagementUI() {
        // Use the existing carManagementPanel
        JFrame frame = new JFrame("Customer Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Set frame content pane
        frame.setContentPane(customerManagementPanel);

        // Set frame visibility
        frame.setVisible(true);
    }

    //Initialize Table Pale
    private void initializeCustomerTable() {
        // Create column names for the JTable
        String[] columnNames = {"Customer ID", "First Name", "Last Name", "Phone", "Address", "ID/Passport No", "Company Name"};

        // Create a DefaultTableModel
        DefaultTableModel model = new DefaultTableModel(null, columnNames);

        // Populate the model with data from the CustomerManager
        updateTableData(model);

        // Create the JTable with the DefaultTableModel
        customerTable = new JTable(model);

        // Add the MouseListener to the customerTable
        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    System.out.println("Mouse clicked on the table. Calling populateFields...");
                    populateFields();
                }
            }
        });

        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(customerTable);

        // Set the bottom component of the split pane
        customerManagementPanel.setBottomComponent(scrollPane);
    }

    //Method to showCustomerManagementUI
    public void showCustomerManagementUI() {
        customerManagementUI();
    }

    //method to add customer
    public void addCustomer() {
        // Get values from JTextField components
        String firstname = firstnameTxt.getText();
        String lastname = lastnameTxt.getText();
        String phone = phoneTxt.getText();
        String address = addressTxt.getText();
        String passportno = passportTxt.getText();
        String companyname = companynameTxt.getText();

        // Validate if all fields are filled
        if (firstname.isEmpty() || lastname.isEmpty() || phone.isEmpty() || address.isEmpty() || passportno.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer newCustomer;
        if (companyname.isEmpty()) {
            // Create a new Customer object
            newCustomer = new Customer(firstname, lastname, phone, address, passportno);
        } else {
            // Create a new CorporateCustomer object
            newCustomer = new CorporateCustomer(firstname, lastname, phone, address, passportno, companyname);
        }

        // Add the new customer to the CustomerManager
        customerManager.addCustomer(newCustomer);

        // Update the JTable data
        updateTableData((DefaultTableModel) customerTable.getModel());

        // Optionally, clear the input fields after adding a customer
        clearInputFields();

        initializeCustomerTable();
    }

    // Method to update the Customer Table data from the CustomerManager and update customer data
    private void updateCustomerData() {
        // Get the selected row from the JTable
        int selectedRow = customerTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            // Display an error message if no row is selected
            JOptionPane.showMessageDialog(null, "Please select a customer to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve the customer ID from the selected row
        String customerId = customerTable.getValueAt(selectedRow, 0).toString();
        System.out.println("Selected Customer ID: " + customerId);

        // Retrieve existing customer information
        Customer existingCustomer = customerManager.getCustomerById(customerId);

        // Check if existingCustomer is null
        if (existingCustomer == null) {
            System.out.println("Existing customer is null.");
            return;
        }

        // Update the existing customer's data based on changes in the input fields
        existingCustomer.setFirstName(firstnameTxt.getText());
        existingCustomer.setLastName(lastnameTxt.getText());
        existingCustomer.setPhone(phoneTxt.getText());
        existingCustomer.setAddress(addressTxt.getText());
        existingCustomer.setIdOrPassportNumber(passportTxt.getText());

        // If it's a CorporateCustomer, update the company name field
        if (existingCustomer instanceof CorporateCustomer) {
            ((CorporateCustomer) existingCustomer).setCompanyName(companynameTxt.getText());
        }

        System.out.println("Customer data updated successfully.");
    }

    // Method to delete a customer
    private void deleteCustomer() {
        // Get the selected row from the JTable
        int selectedRow = customerTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            // Display an error message if no row is selected
            JOptionPane.showMessageDialog(null, "Please select a customer to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve the customer ID from the selected row
        String customerId = customerTable.getValueAt(selectedRow, 0).toString();

        // Ask for confirmation before deleting
        int confirmResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this customer?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            // Delete the customer from the CustomerManager
            customerManager.removeCustomer(customerId);

            // Update the JTable data
            updateTableData((DefaultTableModel) customerTable.getModel());

            // Optionally, clear the input fields after deleting a customer
            clearInputFields();
        }
    }

    //Method to Populate Fields
    private void populateFields() {
        System.out.println("Populating fields from selected row...");
        // Get the selected row from the JTable
        int selectedRow = customerTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            // Display an error message if no row is selected
            JOptionPane.showMessageDialog(null, "Please select a customer to populate fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Selected Row: " + selectedRow);

        // Retrieve the customer ID from the selected row
        String customerId = customerTable.getValueAt(selectedRow, 0).toString();
        System.out.println("Selected Customer ID: " + customerId);

        // Retrieve existing customer information
        Customer existingCustomer = customerManager.getCustomerById(customerId);

        // Check if existingCustomer is null
        if (existingCustomer == null) {
            System.out.println("Existing customer is null.");
            return;
        }

        // Populate text fields with existing customer information
        firstnameTxt.setText(existingCustomer.getFirstName());
        lastnameTxt.setText(existingCustomer.getLastName());
        phoneTxt.setText(existingCustomer.getPhone());
        addressTxt.setText(existingCustomer.getAddress());
        passportTxt.setText(existingCustomer.getIdOrPassportNumber());

        // If it's a CorporateCustomer, populate the company name field
        if (existingCustomer instanceof CorporateCustomer) {
            companynameTxt.setText(((CorporateCustomer) existingCustomer).getCompanyName());
        }

        System.out.println("Fields populated successfully.");

        // Disable the "Add" button since a row is selected
        addButton.setEnabled(false);
    }

    // Method to clear input fields
    private void clearInputFields() {
        firstnameTxt.setText("");
        lastnameTxt.setText("");
        phoneTxt.setText("");
        addressTxt.setText("");
        passportTxt.setText("");
        companynameTxt.setText("");

        // enable the "Add" button since a row is selected
        addButton.setEnabled(true);
    }

    //Goback to Main menu
    private void goBackToMenu() {
        // Hide the current CustomerManagement frame
        dispose();
        // Show the main menu UI using the instance of the MainMenu class
        mainMenu.showMenuUI();
    }
    // Method to update the JTable data from the CustomerManager
    private void updateTableData(DefaultTableModel model) {
        // Clear existing data
        model.setRowCount(0);

        // Populate the model with data from the CustomerManager
        Map<String, Customer> allCustomers = customerManager.getAllCustomers();
        for (Customer customer : allCustomers.values()) {
            Object[] rowData;

            if (customer instanceof CorporateCustomer) {
                CorporateCustomer corporateCustomer = (CorporateCustomer) customer;
                rowData = new Object[]{
                        corporateCustomer.getCustomerId(),
                        corporateCustomer.getFirstName(),
                        corporateCustomer.getLastName(),
                        corporateCustomer.getPhone(),
                        corporateCustomer.getAddress(),
                        corporateCustomer.getIdOrPassportNumber(),
                        corporateCustomer.getCompanyName(),
                };
            } else {
                // For individual customers
                rowData = new Object[]{
                        customer.getCustomerId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getPhone(),
                        customer.getAddress(),
                        customer.getIdOrPassportNumber(),
                        "" // Empty string for the company name of individual customers
                };
            }

            model.addRow(rowData);
        }
    }

    public void saveCustomersData() {
        String filePath = "Customers.ser";
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            if (customerManager == null || customerManager.getAllCustomers().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No data to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Save the CustomerManager object to the file
            objectOut.writeObject(customerManager);

            System.out.println("Data saved to " + filePath);

            JOptionPane.showMessageDialog(null, "Customers saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: File not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            // Handle other IOExceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Unable to save data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        clearInputFields();
    }

    public void loadCustomersData() {
        String filePath = "Customers.ser";
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            // Load the CustomerManager object from the file
            CustomerManager loadedCustomerManager = null;
            try {
                loadedCustomerManager = (CustomerManager) objectIn.readObject();
            } catch (EOFException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Incomplete or corrupted file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Check if the loaded data is valid
            if (loadedCustomerManager != null) {
                customerManager = loadedCustomerManager;

                // Update the JTable data after loading
                updateTableData((DefaultTableModel) customerTable.getModel());

                System.out.println("Data loaded from " + filePath);

                JOptionPane.showMessageDialog(null, "Customers loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error: Invalid data format.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | ClassNotFoundException e) {
            // Handle IOException or ClassNotFoundException
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Unable to load data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        clearInputFields();
    }
}
