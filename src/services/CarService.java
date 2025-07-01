package services;
import Mangers.AbstractService;
import Mangers.FilePersistence;
import Models.*;

import java.io.*;
import java.util.Collection;
import java.util.Collections;

// CarService.java
public class CarService extends AbstractService<Car> implements FilePersistence<Car> {
    private static final String CAR_FILE = "Cars.txt";

    public CarService() {
        loadFromFile(CAR_FILE);
    }

    public void showAvailableCars() {
        System.out.println("\n🚗 Available Cars:");
        items.values().stream()
                .filter(car -> car.getStatus() == CarStatus.AVAILABLE)
                .forEach(System.out::println);

        if (items.values().stream().noneMatch(car -> car.getStatus() == CarStatus.AVAILABLE)) {
            System.out.println("No cars available.");
        }
    }

    public boolean addCar(String id, String brand, String model, double pricePerDay) {
        if (exists(id) || pricePerDay <= 0) {
            System.out.println("❌ Invalid car details.");
            return false;
        }

        Car car = new Car(id, brand, model, pricePerDay);
        car.setStatus(CarStatus.AVAILABLE);
        items.put(id, car);
        saveToFile(CAR_FILE, items.values());
        System.out.println("✅ Car added.");
        return true;
    }

    public void updateCarStatus(String carId, CarStatus status) {
        Car car = getById(carId);
        if (car != null) {
            car.setStatus(status);
            saveToFile(CAR_FILE, items.values());
        }
    }

    @Override
    public void saveToFile(String filename, Collection<Car> cars) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Car car : cars) {
                writer.printf("%s,%s,%s,%.2f,%s%n",
                        car.getId(),
                        car.getBrand(),
                        car.getModel(),
                        car.getPricePerDay(),
                        car.getStatus());
            }
        } catch (IOException e) {
            System.err.println("Error saving cars: " + e.getMessage());
        }
    }

    @Override
    public Collection<Car> loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return Collections.emptyList();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length == 5) {
                    Car car = new Car(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                    car.setStatus(CarStatus.valueOf(parts[4]));
                    items.put(car.getId(), car);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading cars: " + e.getMessage());
        }
        return items.values();
    }

}