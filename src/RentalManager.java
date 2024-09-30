import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RentalManager implements Serializable {
    private Map<String, Rental> rentalMap;
    private CarManager carManager;
    private CustomerManager customerManager;



    public RentalManager(CarManager carManager, CustomerManager customerManager) {
        this.rentalMap = new HashMap<>();
        this.carManager = carManager;
        this.customerManager = customerManager;
    }

    // Method to rent a car
    public Rental rentCar(String carId, String customerId, LocalDate rentDate, LocalDate returnDate) {
        // Load data from serialized files
        carManager.loadCarsData();
        customerManager.loadCustomersData();

        // Check if carId and customerId exist in the respective managers
        Car car = carManager.getCarById(carId);
        Customer customer = customerManager.getCustomerById(customerId);

        if (car != null && customer != null) {
            // Check if the car is available for rent
            if (car.isAvailable()) {
                // Create a new Rental instance
                Rental rental = new Rental(carId, customerId, rentDate, returnDate, carManager);

                // Update the car's availability status to false
                car.setAvailability(false);

                // Add the rental to the rentalMap
                rentalMap.put(rental.getRentalId(), rental);

                //Save Car data to Update the availablity status
                //carManager.saveCarsData();

                return rental;
            } else {
                System.out.println("Car with ID " + carId + " is not available for rent.");
            }
        } else {
            System.out.println("Invalid carId or customerId. Cannot rent a car.");
        }

        return null;
    }

    // Method to return a car
    public boolean returnCar(Rental rental, LocalDate actualReturnDate, boolean damage) {
        try {

            if (rental != null) {
                // Add validation for actualReturnDate
                if (actualReturnDate.isBefore(rental.getRentDate())) {
                    System.out.println("Error: Actual return date cannot be later than the expected rent date.");
                    return false;
                }

                // Set the actual return date and damage status
                rental.setActualReturnDate(actualReturnDate);
                rental.setDamage(damage);

                // Update the lateReturn flag
                if (actualReturnDate.isAfter(rental.getReturnDate())) {
                    rental.setLateReturn(true);
                } else {
                    rental.setLateReturn(false);
                }

                // Update the car's availability status to true
                Car car = carManager.getCarById(rental.getCarId());
                if (car != null) {
                    car.setAvailability(true);

                    //carManager.saveCarsData();

                    return true; // Indicate success
                } else {
                    System.out.println("Error: Car not found with ID " + rental.getCarId());
                }
            } else {
                System.out.println("Error: Rental is null.");
            }
        } catch (Exception e) {
            // Handle exceptions appropriately, log details
            e.printStackTrace();
        }

        return false; // Indicate failure
    }


    // Method to get a rental by ID
    public Rental getRentalById(String rentalId) {
        return rentalMap.get(rentalId);
    }

    // Method to get all rentals
    public Map<String, Rental> getAllRentals() {
        return new HashMap<>(rentalMap);
    }

    public Collection<String> getAllUnreturnedRentalIds() {
        return rentalMap.values()
                .stream()
                .filter(rental -> rental.getActualReturnDate() == null)
                .map(Rental::getRentalId)
                .collect(Collectors.toList());
    }


    public void saveRentalsData() {
        String filePath = "Rentals.ser";
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            if (rentalMap == null || rentalMap.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No data to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Save the CarManager object to the file
            objectOut.writeObject(this);

            System.out.println("Data saved to " + filePath);

            JOptionPane.showMessageDialog(null, "Car Availabity Status Updated Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: File not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            // Handle other IOExceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Unable to save data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void loadRentalsData() {
        String filePath = "Rentals.ser";
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            // Load the RentalManager object from the file
            RentalManager loadedRentalManager = (RentalManager) objectIn.readObject();

            // Update the carMap with the loaded data
            this.rentalMap = loadedRentalManager.rentalMap;

            System.out.println("Cars data loaded from " + filePath);
            System.out.println("Number of loaded cars: " + rentalMap.size());

            // Print information about each loaded car
            rentalMap.values().forEach(System.out::println);

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
            System.err.println("Error: File not found - " + filePath);
            // Handle the absence of the file if needed
        } catch (IOException | ClassNotFoundException e) {
            // Handle other IOExceptions or class not found exception
            e.printStackTrace();
            System.err.println("Error: Unable to load cars data");
            // Handle the inability to load data if needed
        }
    }
}
