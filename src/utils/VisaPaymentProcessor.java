package utils;
import Mangers.*;
public class VisaPaymentProcessor extends BasePaymentProcessor {
    @Override
    public boolean processPayment(double amount, String... paymentDetails) {
        if (paymentDetails.length < 2) return false;

        String cardNumber = paymentDetails[0];
        String cvv = paymentDetails[1];

        if (!cardNumber.startsWith("4") || cardNumber.length() != 16 || !validateCardNumber(cardNumber)) {
            return false;
        }

        if (!cvv.matches("\\d{3}")) {
            return false;
        }

        System.out.printf("✅ Visa payment of $%.2f approved.\n", amount);
        return true;
    }
}
