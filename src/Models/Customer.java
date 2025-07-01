package Models;

import java.util.Objects;

public class Customer {
    private final String id;
    private String name;
    private final String licenseNumber;
    private String password;
    private String rentedCarId; // ID of the car customer is renting
    private String type; // "admin" or "customer"
    private double totalPenalties;
    // Updated constructor with type
    public Customer(String id, String name, String licenseNumber, String password, String type) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.password = password;
        this.rentedCarId = null;
        this.type = type;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getPassword() { return password; }
    public String getRentedCarId() { return rentedCarId; }
    public String getType() { return type; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setRentedCarId(String rentedCarId) { this.rentedCarId = rentedCarId; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', license='%s', type='%s'}", id, name, licenseNumber, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    public double getTotalPenalties() {
        return totalPenalties;
    }

    public void addPenalty(double amount) {
        this.totalPenalties += amount;
    }
}
