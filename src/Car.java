import java.io.Serializable;
import java.util.Random;

/**
 * Car Class holds car objects
 */
public class Car implements Serializable {
    private static int carCounter = 1000;
    private String carId;
    private String brand;
    private String model;
    private CarClass carClass;
    private double costPerDay;
    private boolean availability;

    // Constructor
    public Car(String brand, String model, CarClass carClass, double costPerDay, boolean availability) {
        // Automatically generate carId using the method
        this.carId = generateCarId();
        this.brand = brand;
        this.model = model;
        this.carClass = carClass;
        this.costPerDay = costPerDay;
        this.availability = availability;
    }

    // Method to generate carId
    private synchronized String generateCarId() {
        int random5DigitNumber = generateRandom4DigitNumber();
        return "CR" + random5DigitNumber + carCounter++;
    }

    /**
     *
     * @return
     */
    private int generateRandom4DigitNumber() {
        Random random = new Random();
        return 10000 + random.nextInt(90000); // Generates a random integer between 10000 and 99999
    }

    // Getters and setters go here

    public String getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public double getCostPerDay() { return costPerDay;}

    public boolean isAvailable() {
        return this.availability;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    public void setCostPerDay(double costPerDay) {
        this.costPerDay = costPerDay;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    // Override toString method for better object representation
    @Override
    public String toString() {
        return "Car{" +
                "carId='" + carId + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", carClass=" + carClass +
                ", costPerDay=" + costPerDay +
                ", availability=" + availability +
                '}';
    }
}