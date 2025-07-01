package Mangers;

import java.time.LocalDate;
import java.util.List;

public interface RentalRepository {
    void saveRental(String customerId, String carId, LocalDate startDate, LocalDate endDate);
    boolean hasActiveRental(String customerId);

    List<RentalRecord> getRentalsByCustomer(String customerId);
    void markRentalAsReturned(String customerId);

}
