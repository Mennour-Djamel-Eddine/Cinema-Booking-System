package model;

import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment {
    private int ID;
    private int bookingID;
    private float amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Constructors
    public Payment(int bookingID, float amount, PaymentStatus status) {
        this(0, bookingID, amount, status, LocalDateTime.now(), "Unknown");
    }

    public Payment(int bookingID, float amount, PaymentStatus status, String paymentMethod) {
        this(0, bookingID, amount, status, LocalDateTime.now(), paymentMethod);
    }

    public Payment(int ID, int bookingID, float amount, PaymentStatus status,
                   LocalDateTime paymentDate, String paymentMethod) {
        this.ID = ID;
        this.bookingID = bookingID;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getBookingID() {
        return bookingID;
    }

    public float getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getFormattedDate() {
        return paymentDate.format(DATE_FORMATTER);
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    @Override
    public String toString() {
        return String.format(
                "Payment[ID=%d, BookingID=%d, Amount=%s, Method=%s, Status=%s, Date=%s]",
                ID, bookingID, getFormattedAmount(), paymentMethod, status, getFormattedDate()
        );
    }
}
