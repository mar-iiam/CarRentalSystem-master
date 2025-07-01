package services;
import Mangers.AbstractService;
import Mangers.FilePersistence;
import Models.*;
import utils.*;

import java.io.*;
import java.util.Collection;
import java.util.Collections;

public class CustomerService extends AbstractService<Customer> implements FilePersistence<Customer> {
    private static final String CUSTOMER_FILE = "customers.txt";

    public CustomerService() {
        loadFromFile(CUSTOMER_FILE);
    }

    public boolean registerCustomer(String id, String name, String licenseNumber, String password) {
        if (exists(id)) {
            System.out.println("❌ Customer ID exists.");
            return false;
        }

        String type = id.matches("200\\d{2}") ? "admin" : "customer";
        String hashedPassword = passwordUtils.hashPassword(password);
        Customer customer = new Customer(id, name, licenseNumber, hashedPassword, type);
        items.put(id, customer);
        saveToFile(CUSTOMER_FILE, items.values());
        System.out.printf("✅ %s registered.\n", type.substring(0, 1).toUpperCase() + type.substring(1));
        return true;
    }

    public boolean validatePassword(Customer customer, String password) {
        return customer.getPassword().equals(passwordUtils.hashPassword(password));
    }

    @Override
    public void saveToFile(String filename, Collection<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Customer customer : customers) {
                writer.printf("%s,%s,%s,%s,%s,%s%n",
                        customer.getId(),
                        customer.getName(),
                        customer.getLicenseNumber(),
                        customer.getPassword(),
                        customer.getType(),
                        customer.getRentedCarId() != null ? customer.getRentedCarId() : "null");
            }
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    @Override
    public Collection<Customer> loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return Collections.emptyList();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6) {
                    String id = parts[0];
                    String name = parts[1];
                    String licenseNumber = parts[2];
                    String password = parts[3];
                    String type = parts[4];
                    String rentedCarId = parts[5].equalsIgnoreCase("null") ? null : parts[5];

                    Customer customer = new Customer(id, name, licenseNumber, password, type);
                    customer.setRentedCarId(rentedCarId);
                    items.put(id, customer);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return items.values();
    }
    // In CustomerService.java
    public void removeCarFromCustomer(String customerId) {
        Customer customer = getById(customerId);
        if (customer != null) {
            customer.setRentedCarId(null);
            saveToFile(CUSTOMER_FILE, items.values()); // Persist the change
        }
    }

    // Corresponding method to assign a car
    public void assignCarToCustomer(String customerId, String carId) {
        Customer customer = getById(customerId);
        if (customer != null) {
            customer.setRentedCarId(carId);
            saveToFile(CUSTOMER_FILE, items.values()); // Persist the change
        }
    }
    // In CustomerService.java
    public void addPenalty(String customerId, double penaltyAmount) {
        Customer customer = getById(customerId);
        if (customer != null && penaltyAmount > 0) {
            customer.addPenalty(penaltyAmount);
            saveToFile(CUSTOMER_FILE, items.values()); // Persist the change

            // Optional: Log the penalty
            System.out.printf("📝 Added $%.2f penalty to customer %s%n",
                    penaltyAmount, customerId);
        }
    }

    // In CustomerService.java
    public void displayCustomerPenalties(String customerId) {
        Customer customer = getById(customerId);
        if (customer != null) {
            System.out.printf("\n--- Penalty Summary for %s ---\n", customerId);
            System.out.printf("Customer Name: %s\n", customer.getName());
            System.out.printf("Total Penalties: $%.2f\n", customer.getTotalPenalties());

            if (customer.getTotalPenalties() > 0) {
                System.out.println("\nℹ️ Please pay your penalties at the counter.");
            }
        } else {
            System.out.println("❌ Customer not found.");
        }
    }
    public void displayAllCustomers() {
        if (items.isEmpty()) {
            System.out.println("\n📭 No customers found in the system.");
            return;
        }

        System.out.println("\n👥 All Registered Customers:");
        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-15s | %-8s | %-10s\n",
                "ID", "Name", "License", "Type", "Penalties");
        System.out.println("------------------------------------------------------------------");

        for (Customer customer : items.values()) {
            System.out.printf("%-10s | %-20s | %-15s | %-8s | $%-9.2f",
                    customer.getId(),
                    customer.getName(),
                    customer.getLicenseNumber(),
                    customer.getType(),
                    customer.getTotalPenalties());

            // Show rented car info only for regular customers
            if ("customer".equalsIgnoreCase(customer.getType())) {
                System.out.printf(" | Rented Car: %s",
                        customer.getRentedCarId() != null ? customer.getRentedCarId() : "None");
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------------------------");
    }
    // Inherited from AbstractService<Customer>
    public Customer getById(String id) {
        return items.get(id);
    }


}
