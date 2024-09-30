import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.io.*;
import java.util.stream.Collectors;


public class RentalManagement extends JFrame {
    private JTable rentalTable;
    private JSplitPane rentalManagementPanel;
    private JButton rentButton;
    private JButton backButton;
    private JList carIDList;
    private JList customerIDList;
    private JCheckBox yesNoCheckBox;
    private JButton returnButton;
    private JList rentalIDList;
    private JPanel rentDatePanel;
    private JPanel returnDatePanel;
    private JPanel actualReturnDatePanel;
    private JButton saveButton;
    private JButton loadButton;
    private JButton cancelButton2;
    private JButton cancelButton1;
    private RentalManager rentalManager;
    private CarManager carManager;  // Declare at the class level
    private CustomerManager customerManager;  // Declare at the class level

    //Reference to JDatechooser
    JDateChooser rentDatechooser = new JDateChooser();
    JDateChooser returnDatechooser = new JDateChooser();
    JDateChooser ActucalReturnDatechooser = new JDateChooser();

    private Map<Class<?>, Object> managersMap;
    private MainMenu mainMenu;



    public RentalManagement(RentalManager rentalManager, Map<Class<?>, Object> managersMap, MainMenu mainMenu) {
        this.rentalManager = rentalManager;
        this.managersMap = managersMap;  // Initialize the managersMap
        this.mainMenu = mainMenu;

        // Retrieve CarManager and CustomerManager from the updated map
        this.carManager = (CarManager) managersMap.get(CarManager.class);
        this.customerManager = (CustomerManager) managersMap.get(CustomerManager.class);
        this.rentalManager = (RentalManager) managersMap.get(RentalManager.class);

        // Load car and customer data
        carManager.loadCarsData();
        customerManager.loadCustomersData();
        rentalManager.loadRentalsData();

        // Update references in managersMap
        this.managersMap.put(CarManager.class, carManager);
        this.managersMap.put(CustomerManager.class, customerManager);

        // Call the method to initialize the UI
        rentalManagementUI();


        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RentCar();
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReturnCar();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRentalsData();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRentalsData();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { goBackToMenu();}
        });

        cancelButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { clearInputFields1();}
        });

        cancelButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { clearInputFields2();}
        });

    }

    // Method to initialize the UI
    private void rentalManagementUI() {
        // Use the existing rentalManagementPanel
        JFrame frame = new JFrame("Rental Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);


        // Add components to the rentalManagementPanel
        rentalManagementPanel.setBottomComponent(createTablePanel()); // Assume the left component of the split pane contains the table

        // Retrieve CarManager, CustomerManager, and RentalManager from the map
        CarManager carManager = (CarManager) managersMap.get(CarManager.class);
        CustomerManager customerManager = (CustomerManager) managersMap.get(CustomerManager.class);
        RentalManager rentalManager = (RentalManager) managersMap.get(RentalManager.class);

        // Populate carIDList with available cars from loaded data
        DefaultListModel<String> carListModel = new DefaultListModel<>();
        carManager.getAllCars().values().stream()
                .filter(car -> car.isAvailable())
                .forEach(car -> carListModel.addElement(car.getCarId()));
        carIDList.setModel(carListModel);

        // Populate customerIDList with customer IDs from loaded data
        DefaultListModel<String> customerListModel = new DefaultListModel<>();
        customerManager.getAllCustomers().values().forEach(customer -> customerListModel.addElement(customer.getCustomerId()));
        customerIDList.setModel(customerListModel);

        // Populate rentalIDList with rental IDs from loaded data
        DefaultListModel<String> rentalListModel = new DefaultListModel<>();
        rentalManager.getAllRentals().values().stream()
                .filter(rental -> rental.getActualReturnDate() == null)
                .forEach(rental -> rentalListModel.addElement(rental.getRentalId()));
        rentalIDList.setModel(rentalListModel);


        //JDatechooser
        rentDatePanel.add(rentDatechooser);
        returnDatePanel.add(returnDatechooser);
        actualReturnDatePanel.add(ActucalReturnDatechooser);

        // Set frame content pane
        frame.setContentPane(rentalManagementPanel);

        // Set frame visibility
        frame.setVisible(true);
    }

    // ... (other parts of the class)

    private void RentCar() {
        String selectedCarId = getSelectedValue(carIDList);
        String selectedCustomerId = getSelectedValue(customerIDList);

        if (selectedCarId == null || selectedCustomerId == null) {
            showError("Please select a car and a customer.");
            return;
        }

        LocalDate rentDate = getDateFromChooser(rentDatechooser);
        LocalDate returnDate = getDateFromChooser(returnDatechooser);

        LocalDate today = LocalDate.now();

        if (rentDate == null || returnDate == null || rentDate.isAfter(returnDate) || rentDate.isBefore(today)) {
            showError("Please select valid rent and return dates.");
            return;
        }

        Rental rentedCar = rentalManager.rentCar(selectedCarId, selectedCustomerId, rentDate, returnDate);

        if (rentedCar != null) {
            updateUIAfterRent(rentedCar);
            showSuccess("Car rented successfully.");
        } else {
            showError("Failed to rent the car. Please check the inputs.");
        }

        clearInputFields1();
    }

    private void ReturnCar() {
        String selectedRentalId = getSelectedValue(rentalIDList);
        LocalDate actualReturnDate = getDateFromChooser(ActucalReturnDatechooser);

        if (selectedRentalId == null || actualReturnDate == null) {
            showError("Please enter valid rental ID and actual return date.");
            return;
        }

        Rental rental = rentalManager.getRentalById(selectedRentalId);
        if (rental == null) {
            showError("Invalid rental ID. Please enter a valid rental ID.");
            return;
        }

        boolean isDamage = yesNoCheckBox.isSelected();
        boolean success = rentalManager.returnCar(rental, actualReturnDate, isDamage);

        if (success) {
            updateUIAfterReturn(rental);
            showSuccess("Car returned successfully.");
        } else {
            showError("Failed to return the car. Please check the inputs.");
        }
        clearInputFields2();
    }

    private String getSelectedValue(JList<String> list) {
        return list.getSelectedValue();
    }

    private LocalDate getDateFromChooser(JDateChooser dateChooser) {
        return dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void updateUIAfterRent(Rental rentedCar) {
        updateListModel(carIDList, carManager.getAllAvailableCarIds());
        updateListModel(rentalIDList, rentalManager.getAllUnreturnedRentalIds());

        DefaultTableModel model = (DefaultTableModel) rentalTable.getModel();
        model.addRow(getRowData(rentedCar));
    }

    private void updateUIAfterReturn(Rental rental) {
        updateListModel(carIDList, carManager.getAllAvailableCarIds());
        updateListModel(rentalIDList, rentalManager.getAllUnreturnedRentalIds());

        DefaultTableModel model = (DefaultTableModel) rentalTable.getModel();
        int rowIndex = findRowIndex(model, rental.getRentalId());

        if (rowIndex != -1) {
            model.setValueAt(rental.getActualReturnDate(), rowIndex, 5);
            model.setValueAt(rental.isDamage() ? "Damaged" : "Not Damaged", rowIndex, 6);
            model.setValueAt(rental.isLateReturn() ? "Yes" : "No", rowIndex, 7);
            model.setValueAt(rental.getExtraCost(), rowIndex, 9);
        }
    }

    private void updateListModel(JList<String> list, Collection<String> values) {
        DefaultListModel<String> model = new DefaultListModel<>();
        values.forEach(model::addElement);
        list.setModel(model);
    }

    private Object[] getRowData(Rental rentedCar) {
        // Assuming rentedCar has appropriate getter methods, modify this accordingly
        return new Object[]{
                rentedCar.getRentalId(),
                rentedCar.getCarId(),
                rentedCar.getCustomerId(),
                rentedCar.getRentDate(),
                rentedCar.getReturnDate(),
                rentedCar.getActualReturnDate(),
                rentedCar.isDamage() ? "Damaged" : "Not Damaged",
                rentedCar.isLateReturn() ? "Yes" : "No",
                rentedCar.getCost(),
                rentedCar.getExtraCost()
        };
    }

    private int findRowIndex(DefaultTableModel model, String rentalId) {
        // Assuming rentalId is stored in the first column of the table
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(rentalId)) {
                return i;
            }
        }
        return -1; // Return -1 if the rentalId is not found
    }


    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }



    // Method to create the panel containing the JTable
    private JScrollPane createTablePanel() {
        // Create column names for the JTable
        String[] columnNames = {"Rental ID", "Car ID", "Customer ID", "Rent Date", "Return Date", "Actual Return Date", "Damage", "Late Return", "Rental Cost", "Extra Cost"};

        // Create a DefaultTableModel
        DefaultTableModel model = new DefaultTableModel(null, columnNames);

        // Populate the model with data from the rentalManager
        updateTableData(model);

        // Create the JTable with the DefaultTableModel
        rentalTable = new JTable(model);

        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(rentalTable);

        return scrollPane;
    }

    private void updateTableData(DefaultTableModel model) {
        // Clear existing data
        model.setRowCount(0);
        // Populate the model with data from the rentalManager
        Map<String, Rental> allRentals = rentalManager.getAllRentals();
        for (Rental rental : allRentals.values()) {
            Object[] rowData = {
                    rental.getRentalId(),
                    rental.getCarId(),
                    rental.getCustomerId(),
                    rental.getRentDate(),
                    rental.getReturnDate(),
                    rental.getActualReturnDate(),
                    rental.isDamage() ? "Damaged" : "Not Damaged",
                    rental.isLateReturn() ? "Yes" : "No",
                    rental.getCost(),
                    rental.getExtraCost()
            };
            model.addRow(rowData);
        }
    }


    private void goBackToMenu() {
        // Hide the current CustomerManagement frame
        dispose();
        // Show the main menu UI using the instance of the MainMenu class
        mainMenu.showMenuUI();
    }

    private void clearInputFields1() {
        carIDList.clearSelection();
        customerIDList.clearSelection();
        rentDatechooser.setDate(null);
        returnDatechooser.setDate(null);
        rentButton.setEnabled(true);
    }

    private void clearInputFields2() {
        rentalIDList.clearSelection();
        ActucalReturnDatechooser.setDate(null);
        yesNoCheckBox.setSelected(false);

        returnButton.setEnabled(true);
    }
    // Method to save rentals data to a file
    public void saveRentalsData() {
        String filePath = "Rentals.ser";
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            if (rentalManager == null || rentalManager.getAllRentals().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No Rental data to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Save the RentalManager object to the file
            objectOut.writeObject(rentalManager);

            System.out.println("Data saved to " + filePath);

            JOptionPane.showMessageDialog(null, "Rentals saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: File not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            // Handle other IOExceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Unable to save data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        clearInputFields1();
        clearInputFields2();
    }

    // Method to load rentals data from a file
    public void loadRentalsData() {
        String filePath = "Rentals.ser";
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            // Load the RentalManager object from the file
            RentalManager loadedRentalManager = null;
            try {
                loadedRentalManager = (RentalManager) objectIn.readObject();
            } catch (EOFException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Incomplete or corrupted file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            // Check if the loaded data is valid
            if (loadedRentalManager != null) {
                rentalManager = loadedRentalManager;
                // Update the JTable data after loading
                updateTableData((DefaultTableModel) rentalTable.getModel());

                System.out.println("Data loaded from " + filePath);

                JOptionPane.showMessageDialog(null, "Rentals loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error: Invalid data format.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | ClassNotFoundException e) {
            // Handle IOException or ClassNotFoundException
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Unable to load data.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        clearInputFields1();
        clearInputFields2();
    }
}
