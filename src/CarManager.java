import javax.swing.*;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CarManager implements Serializable {
    // Map to store cars with their carId as the key
    private Map<String, Car> carMap;

    // Constructor to initialize the CarManager
    public CarManager() {
        this.carMap = new HashMap<>();
    }

    // Method to add a car to the carMap
    public void addCar(Car car) {
        carMap.put(car.getCarId(), car);
    }

    // Method to remove a car from the carMap based on carId
    public void removeCar(String carId) {
        carMap.remove(carId);
    }

    // Method to get a car from the carMap based on carId
    public Car getCarById(String carId) {
        return carMap.get(carId);
    }


    // Method to update a car in the carMap
    public void updateCar(String carId, String brand, String model, CarClass carClass, double costPerDay, boolean availability) {
        // Retrieve the selected car from the map
        Car selectedCar = carMap.get(carId);

        // Update the selected car with the new values
        if (selectedCar != null) {
            selectedCar.setBrand(brand);
            selectedCar.setModel(model);
            selectedCar.setCarClass(carClass);
            selectedCar.setCostPerDay(costPerDay);
            selectedCar.setAvailability(availability);
        }
    }

    // Method to get a copy of all cars in the carMap
    public Map<String, Car> getAllCars() {
        return new HashMap<>(carMap);
    }

    //Method to get All Available Car Ids
    public Collection<String> getAllAvailableCarIds() {
        return carMap.values()
                .stream()
                .filter(Car::isAvailable)
                .map(Car::getCarId)
                .collect(Collectors.toList());
    }

    public void saveCarsData() {
        String filePath = "Cars.ser";
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            if (carMap == null || carMap.isEmpty()) {
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

    public void loadCarsData() {
        String filePath = "Cars.ser";
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            // Load the CarManager object from the file
            CarManager loadedCarManager = (CarManager) objectIn.readObject();

            // Update the carMap with the loaded data
            this.carMap = loadedCarManager.carMap;

            System.out.println("Cars data loaded from " + filePath);
            System.out.println("Number of loaded cars: " + carMap.size());

            // Print information about each loaded car
            carMap.values().forEach(System.out::println);

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
