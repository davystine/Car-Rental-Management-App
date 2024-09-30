import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class MainMenu {
    private JButton carManagementButton;
    private JButton customerManagementButton;
    private JButton rentalManagementButton;
    private JButton logoutButton;
    private JPanel mainMenuPanel;
    private Map<Class<?>, Object> managersMap;

    public MainMenu() {
        carManagementButton.addActionListener(e -> showCarManagementUI());
        customerManagementButton.addActionListener(e -> showCustomerManagementUI());
        rentalManagementButton.addActionListener(e -> showManagementUI(new RentalManagement(getRentalManager(), managersMap, this)));
        logoutButton.addActionListener(e -> logout());

        // Initialize instances of CarManager and CustomerManager
        CarManager carManagerInstance = new CarManager();
        CustomerManager customerManagerInstance = new CustomerManager();

        // Create a map to hold your managers
        managersMap = new HashMap<>();
        managersMap.put(CarManager.class, carManagerInstance);
        managersMap.put(CustomerManager.class, customerManagerInstance);
    }

    public JPanel getMainMenuPanel() {
        return mainMenuPanel;
    }

    private RentalManager getRentalManager() {
        if (!managersMap.containsKey(RentalManager.class)) {
            CarManager carManagerInstance = (CarManager) managersMap.get(CarManager.class);
            CustomerManager customerManagerInstance = (CustomerManager) managersMap.get(CustomerManager.class);
            RentalManager rentalManagerInstance = new RentalManager(carManagerInstance, customerManagerInstance);
            managersMap.put(RentalManager.class, rentalManagerInstance);
        }
        return (RentalManager) managersMap.get(RentalManager.class);
    }

    private void showManagementUI(JFrame managementUI) {
        managementUI.setVisible(true);
        closeMenuFrame();
    }

    public void showCustomerManagementUI() {
        CustomerManager customerManager = new CustomerManager();
        CustomerManagement customerManagement = new CustomerManagement(customerManager, this);
        customerManagement.showCustomerManagementUI();
    }

    public void showCarManagementUI() {
        CarManager carManager = new CarManager();
        CarManagement carManagement = new CarManagement(carManager, this);
        carManagement.showCarManagementUI();
    }

    private void logout() {
        // Show a confirmation dialog
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);

        // Check the user's choice
        if (result == JOptionPane.YES_OPTION) {
            // User clicked Yes, perform logout

            // Optionally, perform any necessary cleanup or actions on logout

            // Optionally, close the current menu frame if needed
            closeMenuFrame();

            // Show the login panel
            showLoginPanel();
        } else {
            // User clicked No, do nothing
        }
    }

    private void closeMenuFrame() {
        JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(mainMenuPanel);
        menuFrame.dispose();
    }

    private void showLoginPanel() {
        // Create an instance of the Login class
        Login login = new Login();

        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(800, 600);
        loginFrame.setContentPane(login.getLoginPanel());

        loginFrame.setVisible(true);
    }


    public void showMenuUI() {
        JFrame frame = new JFrame("Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        frame.setContentPane(getMainMenuPanel());

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create an instance of the Menu class
            MainMenu menu = new MainMenu();

            // Show the Menu UI
            menu.showMenuUI();
        });
    }
}
