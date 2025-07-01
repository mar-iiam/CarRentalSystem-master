package Models;

import java.util.Objects;

public class Car {

        private String id;
        private String brand;
        private String model;
        private double pricePerDay;
        private CarStatus status;

        public Car(String id, String brand, String model, double pricePerDay) {
            this.id = id;
            this.brand = brand;
            this.model = model;
            this.pricePerDay = pricePerDay;
            this.status = CarStatus.AVAILABLE; // default
        }

        public String getId() { return id; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public double getPricePerDay() { return pricePerDay; }
        public CarStatus getStatus() { return status; }

        public void setStatus(CarStatus status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return String.format("Car ID: %s | Brand: %s | Model: %s | Price/Day: %.2f | Status: %s",
                    id, brand, model, pricePerDay, status);
        }
    }


