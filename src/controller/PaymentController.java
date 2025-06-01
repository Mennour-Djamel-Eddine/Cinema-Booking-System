package controller;

import dao.PaymentDAO;
import model.Payment;
import model.PaymentStatus;

import java.util.List;

public class PaymentController {
    private final PaymentDAO paymentDao;

    public PaymentController() {
        this.paymentDao = new PaymentDAO();
    }

    public boolean processPayment(int bookingId, double amount, String paymentMethod) {
        Payment payment = new Payment(bookingId, (float)amount, PaymentStatus.PENDING);
        payment.setPaymentMethod(paymentMethod);

        if (paymentDao.addPayment(payment)) {
            boolean paymentSuccess = processPaymentThroughGateway(paymentMethod);
            PaymentStatus status = paymentSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

            if (paymentDao.updatePaymentStatus(payment.getID(), status)) {
                if (status == PaymentStatus.COMPLETED) {
                    updateBookingPaymentStatus(bookingId, true);
                }
                return paymentSuccess;
            }
        }
        return false;
    }

    // Private helper methods

    private boolean processPaymentThroughGateway(String paymentMethod) {
        // Simulate payment processing with different success rates per method
        double successRate = 0.8; // Default 80% success rate

        switch(paymentMethod.toLowerCase()) {
            case "credit card":
                successRate = 0.85;
                break;
            case "debit card":
                successRate = 0.9;
                break;
            case "paypal":
                successRate = 0.75;
                break;
            case "bank transfer":
                successRate = 0.7;
                break;
        }

        return Math.random() < successRate;
    }

    private void updateBookingPaymentStatus(int bookingId, boolean isPaid) {
        // This would call your BookingController to update the booking status
        // Implementation depends on your Booking system
        // For now, we'll just simulate it
        System.out.println("Updating booking " + bookingId + " payment status to: " + isPaid);

        // In a real implementation, you would do something like:
        // BookingController bookingController = new BookingController();
        // bookingController.updatePaymentStatus(bookingId, isPaid);
    }
}
