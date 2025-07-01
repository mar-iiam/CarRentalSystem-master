package utils;
import Mangers.*;
public class CashPaymentProcessor implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount, String... paymentDetails) {
        System.out.printf("💵 Please pay $%.2f at the counter when picking up the car.\n", amount);
        return true;
    }
}