package Mangers;

public interface PaymentStrategy {
    boolean processPayment(double amount, String... paymentDetails);
}
