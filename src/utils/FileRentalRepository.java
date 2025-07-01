package utils;
import Mangers.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public  class FileRentalRepository implements RentalRepository {
    private static final String RENTAL_FILE = "rentals.txt";

    @Override
    public void saveRental(String customerId, String carId, LocalDate startDate, LocalDate endDate) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RENTAL_FILE, true))) {
            writer.printf("%s,%s,%s,%s%n", customerId, carId, startDate, endDate);
        } catch (IOException e) {
            System.err.println("Error saving rental: " + e.getMessage());
        }
    }

    @Override
    public boolean hasActiveRental(String customerId) {
        File file = new File(RENTAL_FILE);
        if (!file.exists()) return false;

        LocalDate today = LocalDate.now();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String id = parts[0];
                LocalDate endDate = LocalDate.parse(parts[3]);

                if (id.equals(customerId) && !endDate.isBefore(today)) {
                    return true;
                }
            }
        } catch (IOException | DateTimeParseException e) {
            System.err.println("Error reading rentals file: " + e.getMessage());
        }
        return false;
    }
    // In FileRentalRepository.java
    public List<RentalRecord> getRentalsByCustomer(String customerId) {
        List<RentalRecord> rentals = new ArrayList<>();
        File file = new File(RENTAL_FILE);

        if (!file.exists()) {
            return rentals;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(customerId)) {
                    LocalDate startDate = LocalDate.parse(parts[2]);
                    LocalDate endDate = LocalDate.parse(parts[3]);
                    rentals.add(new RentalRecord(customerId, parts[1], startDate, endDate));
                }
            }
        } catch (IOException | DateTimeParseException e) {
            System.err.println("Error reading rental records: " + e.getMessage());
        }

        return rentals;
    }

    @Override
    public void markRentalAsReturned(String customerId) {
        File file = new File("rentals.txt");
        List<String> updatedLines = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && !parts[0].equals(customerId)) {
                        updatedLines.add(line);  // Keep lines that don't belong to this customer
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading rentals file: " + e.getMessage());
            return;
        }

        // Write updated lines back to file
        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : updatedLines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("❌ Error writing to rentals file: " + e.getMessage());
        }
    }

}
