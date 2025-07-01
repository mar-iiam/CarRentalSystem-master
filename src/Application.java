import services.RentalService;
import Models.Customer;
import utils.DateUtils;

import java.time.LocalDate;
import java.util.Scanner;

public class Application {
    private final Scanner scanner = new Scanner(System.in);
    private final RentalService rentalService = new RentalService();

    public void run() {
        Customer loggedInCustomer = null;
        boolean carsShown = false;

        while (true) {
            System.out.println("\n--- Car Rental System ---");

            if (loggedInCustomer == null) {
                loggedInCustomer = showLoginMenu();
            } else if (isAdmin(loggedInCustomer)) {
                handleAdminMenu();
                loggedInCustomer = null;
            } else {
                carsShown = handleCustomerMenu(loggedInCustomer, carsShown);
                if (!carsShown) loggedInCustomer = null;
            }
        }
    }

    private Customer showLoginMenu() {
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
        int choice = getIntInput();

        return switch (choice) {
            case 1 -> {
                handleRegistration();
                yield null;
            }
            case 2 -> handleLogin();
            case 0 -> {
                System.out.println("\uD83D\uDC4B Goodbye!");
                System.exit(0);
                yield null;
            }
            default -> {
                System.out.println("\u274C Invalid choice.");
                yield null;
            }
        };
    }

    private void handleRegistration() {
        String id = prompt("ID");
        boolean isAdmin = id.matches("200\\d{2}");
        String name = prompt("Name");
        String license = isAdmin ? "N/A" : prompt("License");
        String password = prompt("Password");
        rentalService.registerCustomer(id, name, license, password);
    }

    private Customer handleLogin() {
        String id = prompt("ID");
        String password = prompt("Password");
        if (rentalService.login(id, password)) {
            return rentalService.getCustomer(id);
        }
        return null;
    }

    private void handleAdminMenu() {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. View All Customers");
            System.out.println("2. View Available Cars");
            System.out.println("3. Add New Car");
            System.out.println("4. View Customer Penalties");
            System.out.println("5. Clear Customer Penalties");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            switch (getIntInput()) {
                case 1 -> rentalService.showAllCustomers();
                case 2 -> rentalService.showAvailableCars();
                case 3 -> handleAddCar();
                case 4 -> rentalService.showCustomerPenalties(prompt("Customer ID"));
                case 5 -> rentalService.clearCustomerPenalties(prompt("Customer ID"));
                case 6 -> {
                    System.out.println("\uD83D\uDD12 Logged out.");
                    return;
                }
                default -> System.out.println("\u274C Invalid choice.");
            }
        }
    }

    private void handleAddCar() {
        String carId = prompt("Car ID");
        String brand = prompt("Brand");
        String model = prompt("Model");
        System.out.print("Price per day: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        rentalService.addCar(carId, brand, model, price);
    }

    private boolean handleCustomerMenu(Customer customer, boolean carsShown) {
        System.out.println("\nCustomer Menu:");
        if (!carsShown) {
            System.out.println("1. View Available Cars");
            System.out.println("2. Rent a Car");
            System.out.println("3. Return Car");
            System.out.println("4. View Penalties");
            System.out.println("5. Logout");
        } else {
            System.out.println("1. Rent a Car");
            System.out.println("2. Return Car");
            System.out.println("3. View Penalties");
            System.out.println("4. Logout");
        }

        int choice = getIntInput();

        if (!carsShown) {
            return switch (choice) {
                case 1 -> {
                    rentalService.showAvailableCars();
                    yield true;
                }
                case 2 -> {
                    handleRental(customer);
                    yield false;
                }
                case 3 -> {
                    rentalService.returnCar(customer);
                    yield false;
                }
                case 4 -> {
                    rentalService.showCustomerPenalties(customer.getId());
                    yield false;
                }
                case 5 -> {
                    System.out.println("\uD83D\uDD12 Logged out.");
                    yield false;
                }
                default -> {
                    System.out.println("\u274C Invalid choice.");
                    yield false;
                }
            };
        } else {
            return switch (choice) {
                case 1 -> {
                    handleRental(customer);
                    yield true;
                }
                case 2 -> {
                    rentalService.returnCar(customer);
                    yield true;
                }
                case 3 -> {
                    rentalService.showCustomerPenalties(customer.getId());
                    yield true;
                }
                case 4 -> {
                    System.out.println("\uD83D\uDD12 Logged out.");
                    yield false;
                }
                default -> {
                    System.out.println("\u274C Invalid choice.");
                    yield true;
                }
            };
        }
    }

    private void handleRental(Customer customer) {
        String carId = prompt("Car ID");
        LocalDate startDate = DateUtils.parseDate(prompt("Start Date (yyyy-MM-dd)"));
        LocalDate endDate = DateUtils.parseDate(prompt("End Date (yyyy-MM-dd)"));

        if (startDate == null || endDate == null || !DateUtils.isValidDateRange(startDate, endDate)) {
            System.out.println("\u274C Invalid dates.");
            return;
        }

        rentalService.rentCar(customer, carId, startDate, endDate);
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        int number = scanner.nextInt();
        scanner.nextLine();
        return number;
    }

    private String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine();
    }

    private boolean isAdmin(Customer customer) {
        return "admin".equalsIgnoreCase(customer.getType());
    }
}
