package services;

import Mangers.PaymentStrategy;
import Mangers.RentalRecord;
import Mangers.RentalRepository;
import Models.*;
import utils.CashPaymentProcessor;
import utils.FileRentalRepository;
import utils.VisaPaymentProcessor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RentalService {
    private final CustomerService customerService;
    private final CarService carService;
    private final RentalRepository rentalRepository;
    private final Map<String, Integer> loginAttempts;
    private final Map<String, PaymentStrategy> paymentStrategies;

    public RentalService() {
        this.customerService = new CustomerService();
        this.carService = new CarService();
        this.rentalRepository = new FileRentalRepository();
        this.loginAttempts = new HashMap<>();
        this.paymentStrategies = initializePaymentStrategies();
    }

    private Map<String, PaymentStrategy> initializePaymentStrategies() {
        Map<String, PaymentStrategy> strategies = new HashMap<>();
        strategies.put("visa", new VisaPaymentProcessor());
        strategies.put("cash", new CashPaymentProcessor());
        return strategies;
    }

    // Delegated methods
    public void showAvailableCars() {
        carService.showAvailableCars();
    }

    public boolean registerCustomer(String id, String name, String licenseNumber, String password) {
        return customerService.registerCustomer(id, name, licenseNumber, password);
    }

    public boolean login(String id, String password) {
        final int MAX_LOGIN_ATTEMPTS = 3;
        loginAttempts.putIfAbsent(id, 0);

        if (loginAttempts.get(id) >= MAX_LOGIN_ATTEMPTS) {
            System.out.println("❌ Login failed: Maximum attempts exceeded.");
            System.out.println("Please call customer services at 1999.");
            System.exit(0);
            return false;
        }

        Customer customer = customerService.getById(id);
        if (customer == null || !customerService.validatePassword(customer, password)) {
            loginAttempts.put(id, loginAttempts.get(id) + 1);
            System.out.println("❌ Login failed: Invalid credentials.");
            return false;
        }

        loginAttempts.put(id, 0);
        System.out.println("✅ Login successful. Welcome, " + customer.getName() + "!");
        return true;
    }

    public boolean rentCar(Customer customer, String carId, LocalDate startDate, LocalDate endDate) {
        if (rentalRepository.hasActiveRental(customer.getId())) {
            System.out.println("⚠️ You already have an active rental.");
            return false;
        }

        Car car = carService.getById(carId);
        if (car == null || car.getStatus() != CarStatus.AVAILABLE) {
            System.out.println("❌ Car not available.");
            return false;
        }

        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            System.out.println("❌ Invalid dates.");
            return false;
        }

        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);
        double totalPrice = rentalDays * car.getPricePerDay();

        System.out.printf("\n--- Rental Summary ---\nCar: %s %s\nFrom: %s\nTo: %s\nDays: %d\nTotal Price: $%.2f\n",
                car.getBrand(), car.getModel(), startDate, endDate, rentalDays, totalPrice);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Confirm rental? (yes/no): ");
        if (!scanner.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("❌ Rental cancelled.");
            return false;
        }

        System.out.print("Payment method (cash/visa): ");
        String method = scanner.nextLine().toLowerCase();
        PaymentStrategy processor = paymentStrategies.get(method);

        if (processor == null) {
            System.out.println("❌ Invalid payment method.");
            return false;
        }

        String[] paymentDetails = null;
        if ("visa".equals(method)) {
            System.out.print("Enter card number (16 digits): ");
            String cardNumber = scanner.nextLine();
            System.out.print("Enter CVV (3 digits): ");
            String cvv = scanner.nextLine();
            paymentDetails = new String[]{cardNumber, cvv};
        }

        if (!processor.processPayment(totalPrice, paymentDetails)) {
            System.out.println("❌ Payment failed.");
            return false;
        }

        carService.updateCarStatus(carId, CarStatus.RENTED);
        customerService.assignCarToCustomer(customer.getId(), carId);
        rentalRepository.saveRental(customer.getId(), carId, startDate, endDate);

        System.out.println("✅ Car rented successfully!");
        return true;
    }

    public boolean returnCar(Customer customer) {
        String carId = customer.getRentedCarId();
        if (carId == null) {
            System.out.println("⚠️ No car rented.");
            return false;
        }

        carService.updateCarStatus(carId, CarStatus.AVAILABLE);
        customerService.removeCarFromCustomer(customer.getId());

        LocalDate returnDate = LocalDate.now();
        checkForLateReturn(customer.getId(), returnDate);

        // NEW: Mark rental as returned in the repository (file)
        rentalRepository.markRentalAsReturned(customer.getId());

        System.out.println("✅ Car returned successfully.");
        return true;
    }


    private void checkForLateReturn(String customerId, LocalDate returnDate) {
        // Get all rentals for this customer from the repository
        List<RentalRecord> customerRentals = rentalRepository.getRentalsByCustomer(customerId);

        if (customerRentals.isEmpty()) {
            return;
        }

        // Find the most recent rental that should have been returned by now
        RentalRecord activeRental = customerRentals.stream()
                .filter(rental -> !returnDate.isBefore(rental.getStartDate()))
                .max(Comparator.comparing(RentalRecord::getStartDate))
                .orElse(null);

        if (activeRental != null) {
            long daysLate = ChronoUnit.DAYS.between(activeRental.getStartDate(), returnDate);

            if (daysLate > 2) { // 2-day grace period
                double penalty = (daysLate - 2) * 50;
                System.out.printf("⚠️ Car is returned %d days late.\n", daysLate);
                System.out.printf("💸 Late return penalty: $%.2f\n", penalty);

                // Optional: Store penalty information
                customerService.addPenalty(customerId, penalty);
            }
        }
    }
    public void showCustomerPenalties(String customerId) {
        customerService.displayCustomerPenalties(customerId);
    }

    public void clearCustomerPenalties(String customerId) {
        Customer customer = customerService.getById(customerId);
        if (customer != null) {
            customerService.addPenalty(customerId, -customer.getTotalPenalties());
            System.out.println("✅ Penalties cleared for customer " + customerId);
        } else {
            System.out.println("❌ Customer not found.");
        }
    }
    public boolean addCar(String carId, String brand, String model, double pricePerDay) {
        return carService.addCar(carId, brand, model, pricePerDay);
    }
    public void showAllCustomers() {
        customerService.displayAllCustomers();
    }
    public Customer getCustomer(String id) {
        return customerService.getById(id);
    }
}