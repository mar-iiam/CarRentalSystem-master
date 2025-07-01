package Mangers;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

// RentalRecord.java
public class RentalRecord {
    private final String customerId;
    private final String carId;
    private final LocalDate startDate;
    public final LocalDate endDate;

    public RentalRecord(String customerId, String carId, LocalDate startDate, LocalDate endDate) {
        this.customerId = customerId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getCarId() { return carId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }


}