import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class Rental implements Serializable {
    private static int rentalCounter = 1000;
    private String rentalId;
    private String carId;
    private String customerId;
    private LocalDate rentDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private boolean damage;
    private boolean lateReturn;

    private Double cost;
    private Double extraCost; // Replace finalCost with extraCost
    private Car car;
    private CarManager carManager; // Add a CarManager field

    // Constructor for the Rental class
    public Rental(String carId, String customerId, LocalDate rentDate, LocalDate returnDate, CarManager carManager) {
        this.carManager = carManager; // Set the carManager field
        this.rentalId = generateRentalId();
        this.carId = carId;
        this.customerId = customerId;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.damage = false;
        this.lateReturn = false;

        // Set the car field using the carId
        this.car = carManager.getCarById(carId);

        this.cost = calculateCost();
        this.extraCost = 0.0; // Initialize extraCost to 0.0
    }

    // Method to generate rental ID
    private synchronized String generateRentalId(){
        int random5DigitNumber = generateRandom5DigitNumber();
        return "RNT" + random5DigitNumber + rentalCounter++;
    }
    private int generateRandom5DigitNumber() {
        Random random = new Random();
        return 10000 + random.nextInt(90000); // Generates a random integer between 10000 and 99999
    }
    // Getters and setters

    public static int getRentalCounter() {
        return rentalCounter;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getCarId() {
        return carId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
        updateExtraCost(); // Update extraCost when actualReturnDate is set
    }

    public boolean isDamage() {
        return damage;
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
        updateExtraCost(); // Update extraCost when damage is set
    }

    public boolean isLateReturn() {
        return lateReturn;
    }

    public void setLateReturn(boolean lateReturn) {
        this.lateReturn = lateReturn;
    }

    public Double getCost() {
        return cost;
    }

    public Double getExtraCost() {
        return extraCost;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setExtraCost(Double extraCost) {
        this.extraCost = extraCost;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setCarManager(CarManager carManager) {
        this.carManager = carManager;
    }

    // Replace calculateFinalCost with calculateExtraCost
    public Double calculateExtraCost() {
        if (actualReturnDate == null) {
            return 0.0; // or set to another appropriate value for an empty state
        }

        long lateDays = calculateLateDays();
        double lateFee = lateDays * 50.0;

        return lateFee + (damage ? 50.0 : 0.0);
    }

    // Method to calculate the late days between actual return and expected return
    private long calculateLateDays() {
        return ChronoUnit.DAYS.between(returnDate, actualReturnDate);
    }

    // Change private to package-private (no access modifier)
    double calculateCost() {
        // Replace this with your actual cost calculation logic
        long days = returnDate.toEpochDay() - rentDate.toEpochDay();
        return days * car.getCostPerDay();
    }

    // Update extraCost based on adjustments
    private void updateExtraCost() {
        extraCost = calculateExtraCost();
    }

    @Override
    public String toString() {
        return "Rental{" +
                "rentalId='" + rentalId + '\'' +
                ", carId='" + carId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", rentDate=" + rentDate +
                ", returnDate=" + returnDate +
                ", actualReturnDate=" + actualReturnDate +
                ", damage=" + damage +
                ", lateReturn=" + lateReturn +
                ", cost=" + cost +
                ", extraCost=" + extraCost +
                '}';
    }
}
