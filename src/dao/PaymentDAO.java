package dao;

import database.DatabaseConnection;
import model.Payment;
import model.PaymentStatus;

import java.sql.*;

public class PaymentDAO {

    public boolean addPayment(Payment payment) {
        String query = "INSERT INTO Payment (booking_id, amount, PaymentStatus, payment_date, payment_method) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, payment.getBookingID());
            stmt.setFloat(2, payment.getAmount());
            stmt.setString(3, payment.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(5, payment.getPaymentMethod());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setID(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePaymentStatus(int paymentId, PaymentStatus newStatus) {
        String query = "UPDATE Payment SET PaymentStatus = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, paymentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
