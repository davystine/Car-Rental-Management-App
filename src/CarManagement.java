import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Map;

public class CarManagement extends JFrame {
    private JSplitPane carManagementPanel;  // Adjust the type to JSplitPane
    private JTable carTable;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton cancelButton;
    private JTextField brandTxt;
    private JTextField modelTxt;
    private JList carClassList;
    private JTextField costPerDayTxt;
    private JCheckBox yesNoCheckBox;
    private JButton saveButton;
    private JButton loadButton;
    private JButton backButton;
    // Reference to the CarManager
    private CarManager carManager;

    private MainMenu mainMenu;

    // Constructor
    public CarManagement(CarManager carManager, MainMenu mainMenu) {
        this.carManager = carManager;
        this.mainMenu = mainMenu;

        // Call the method to carManagement the UI
        carManagementUI();

        //Call the method to carTable UI
        initializeCarTable();

        //Load All saved Cars
        loadCarsData();


        /* action listener for add Button */
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCar();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCar();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCar();
            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInputFields();
            }
        });

        //save the list cars to a file
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCarsData();
            }
        });

        //load the list Cars  from File
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               loadCarsData();
            }
        });

        backButton.addActionListener(e -> {
            goBackToMenu();
        });
    }

    // Add a public method to call the private carManagementUI() method
    public void showCarManagementUI() {
        carManagementUI();
    }

    // Method to initialize the carManagement UI
    private void carManagementUI() {
        // Use the existing carManagementPanel
        JFrame frame = new JFrame("Car Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Populate the carClassList with enum values
        carClassList.setListData(CarClass.values());

        // Set frame content pane
        frame.setContentPane(carManagementPanel);

        // Set frame visibility
        frame.setVisible(true);
    }

    //Method to Initialize Car Table
    private void initializeCarTable() {
        // Create column names for the JTable
        String[] columnNames = {"Car ID", "Brand", "Model", "Car Class", "Cost Per Day", "Availability"};

        // Create a DefaultTableModel
        DefaultTableModel model = new DefaultTableModel(null, columnNames);

        // Populate the model with data from the CustomerManager
        updateTableData(model);

        // Create the JTable with the DefaultTableModel
        carTable = new JTable(model);

        // Add the MouseListener to the customerTable
        carTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    System.out.println("Mouse clicked on the table. Calling populateFields...");
                    populateCarFields();
                }
            }
        });

        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(carTable);

        // Set the bottom component of the split pane
        carManagementPanel.setBottomComponent(scrollPane);
    }

    //Method to add Car through Table
    public void addCar(){
        // Get values from JTextField components
        String brand = brandTxt.getText();
        String model = modelTxt.getText();
        CarClass carClass = (CarClass) carClassList.getSelectedValue();

        // Handle costPerDay input
        double costPerDay;
        try {
            costPerDay = Double.parseDouble(costPerDayTxt.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Please enter a valid numeric value for Cost Per Day.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;  // Exit the actionPerformed method to prevent further processing
        }

        boolean availability = yesNoCheckBox.isSelected();

        // Validate if all fields are filled
        if (brand.isEmpty() || model.isEmpty() || carClass == null) {
            JOptionPane.showMessageDialog(null,
                    "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new Car object
        Car newCar = new Car(brand, model, carClass, costPerDay, availability);

        // Add the new car to the CarManager
        carManager.addCar(newCar);

        // Update the JTable data
        updateTableData((DefaultTableModel) carTable.getModel());

        // Optionally, clear the input fields after adding a car
        clearInputFields();
    }



    // Method to update the selected car in the CarManager and refresh the carTable
    private void updateCar() {
        // Get the selected row index
        int selectedRowIndex = carTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRowIndex != -1) {
            // Retrieve the car ID from the selected row
            String carId = carTable.getValueAt(selectedRowIndex, 0).toString();

            // Get values from JTextField components
            String brand = brandTxt.getText();
            String model = modelTxt.getText();
            CarClass carClass = (CarClass) carClassList.getSelectedValue();

            // Handle costPerDay input
            double costPerDay;
            try {
                costPerDay = Double.parseDouble(costPerDayTxt.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Please enter a valid numeric value for Cost Per Day.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;  // Exit the actionPerformed method to prevent further processing
            }

            boolean availability = yesNoCheckBox.isSelected();

            // Validate if all fields are filled
            if (brand.isEmpty() || model.isEmpty() || carClass == null) {
                JOptionPane.showMessageDialog(null,
                        "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the car in the CarManager
            carManager.updateCar(carId, brand, model, carClass, costPerDay, availability);

            // Update the JTable data
            updateTableData((DefaultTableModel) carTable.getModel());

            // Optionally, clear the input fields after updating
            clearInputFields();
        } else {
            // No row selected, handle accordingly
            System.out.println("No row selected in carTable.");
        }
    }


    //Method to delete selected Car
    private void deleteCar(){
        // Get the selected row from the JTable
        int selectedRow = carTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null,
                    "Please select a car to delete.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve the car ID from the selected row
        String carId = carTable.getValueAt(selectedRow, 0).toString();

        // Confirm deletion with the user
        int choice = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this car?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // Delete the selected car
            carManager.removeCar(carId);

            // Update the JTable data
            updateTableData((DefaultTableModel) carTable.getModel());

            // Optionally, clear the input fields after deletion
            clearInputFields();
        }
    }

    // Method to populate fields based on the selected row in carTable
    private void populateCarFields() {
        // Get the selected row index
        int selectedRowIndex = carTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRowIndex != -1) {
            // Assuming your Car object has corresponding getters
            String brand = carTable.getValueAt(selectedRowIndex, 1).toString();
            String model = carTable.getValueAt(selectedRowIndex, 2).toString();
            String carClass = carTable.getValueAt(selectedRowIndex, 3).toString();
            String costPerDay = carTable.getValueAt(selectedRowIndex, 4).toString();
            String availability = carTable.getValueAt(selectedRowIndex, 5).toString();

            // Now you can use these values to populate your fields or perform other actions
            brandTxt.setText(brand);
            modelTxt.setText(model);

            // Assuming carClassList is a JList
            // Find the index of the carClass in the enum values and set the selected index
            CarClass[] carClassValues = CarClass.values();
            for (int i = 0; i < carClassValues.length; i++) {
                if (carClassValues[i].toString().equals(carClass)) {
                    carClassList.setSelectedIndex(i);
                    break;
                }
            }

            costPerDayTxt.setText(costPerDay);

            // Assuming yesNoCheckBox is a JCheckBox
            yesNoCheckBox.setSelected(availability.equals("Available"));

            // Additional logic as needed
        } else {
            // No row selected, handle accordingly
            System.out.println("No row selected in carTable.");
        }
        addButton.setEnabled(false);
    }

    // Method to clear input fields
    private void clearInputFields() {
        brandTxt.setText("");
        modelTxt.setText("");
        carClassList.clearSelection();
        costPerDayTxt.setText("");
        yesNoCheckBox.setSelected(false);
        addButton.setEnabled(true);
    }

    // Method to update the JTable data from the CarManager
    private void updateTableData(DefaultTableModel model) {
        // Clear existing data
        model.setRowCount(0);

        // Populate the model with data from the CarManager
        Map<String, Car> allCars = carManager.getAllCars();
        for (Car car : allCars.values()) {
            Object[] rowData = {
                    car.getCarId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getCarClass(),
                    car.getCostPerDay(),
                    car.isAvailable() ? "Available" : "Not Available"
            };
            model.addRow(rowData);
        }
    }

    //Goback to Main menu
    private void goBackToMenu() {
        // Hide the current CustomerManagement frame
        dispose();
        // Show the main menu UI using the instance of the MainMenu class
        mainMenu.showMenuUI();
    }

    public void saveCarsData() {
        String filePath = "Cars.ser";
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            if (carManager == null || carManager.getAllCars().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No data to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Save the CustomerManager object to the file
            objectOut.writeObject(carManager);

            System.out.println("Data saved to " + filePath);

            JOptionPane.showMessageDialog(null, "Cars saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

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


    public void loadCarsData() {
        String filePath = "Cars.ser";
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            // Load the CarManager object from the file
            CarManager loadedCarManager = null;
            try {
                loadedCarManager = (CarManager) objectIn.readObject();
            } catch (EOFException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Incomplete or corrupted file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Check if the loaded data is valid
            if (loadedCarManager != null) {
                carManager = loadedCarManager;

                // Update the JTable data after loading
                updateTableData((DefaultTableModel) carTable.getModel());

                System.out.println("Data loaded from " + filePath);

                JOptionPane.showMessageDialog(null, "Cars loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
