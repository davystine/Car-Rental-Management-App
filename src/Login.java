import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Login {
    private JTextField usernameTxt;
    private JPasswordField passwordTxt;
    private JButton loginButton;
    private JButton cancelButton;
    private JPanel loginPanel;

    private static final String LOGIN_DATA_FILE = "LoginData.ser";

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform login logic here
                String username = usernameTxt.getText();
                char[] passwordChars = passwordTxt.getPassword();
                String password = new String(passwordChars);

                if (validateLogin(username, password)) {
                    openMainMenu();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add any cancellation logic here
                System.exit(0);
            }
        });
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    private boolean validateLogin(String username, String password) {
        // For simplicity, using hardcoded admin credentials
        return "admin".equalsIgnoreCase(username) && "1234".equals(password);
    }


    private void openMainMenu() {
        JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
        loginFrame.setVisible(false);
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create an instance of the Menu class
        MainMenu MainMenu = new MainMenu();
        frame.setContentPane(MainMenu.getMainMenuPanel());

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Load login data from the file or create a new file if it doesn't exist
        try {
            FileInputStream fileIn = new FileInputStream(LOGIN_DATA_FILE);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            // In a real-world application, you might have a class representing login data
            // here we are using a simple array for demonstration purposes
            Object[] loginData = (Object[]) in.readObject();
            in.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            // Create a new login data file with default admin credentials
            saveDefaultLoginData();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Create an instance of the Login class
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Login");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                Login login = new Login();
                frame.setContentPane(login.loginPanel);

                frame.setVisible(true);
            }
        });
    }

    private static void saveDefaultLoginData() {
        try {
            // In a real-world application, you might have a class representing login data
            // here we are using a simple array for demonstration purposes
            Object[] loginData = {"Admin", "1234"};
            FileOutputStream fileOut = new FileOutputStream(LOGIN_DATA_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(loginData);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
