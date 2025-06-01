package dao;

import database.DatabaseConnection;
import model.Seat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {
    private Connection conn;

    public SeatDAO() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection failed! " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean initializeSeatsForShowtime(int showtimeId, int rows, int cols) {
        try {
            conn.setAutoCommit(false);

            // Clear any existing seats for this showtime
            String deleteQuery = "DELETE FROM seats WHERE showtime_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, showtimeId);
                deleteStmt.executeUpdate();
            }

            // Insert new seats
            String insertQuery = "INSERT INTO seats (showtime_id, seatNumber, isBooked) VALUES (?, ?, false)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                for (int row = 0; row < rows; row++) {
                    char rowChar = (char) ('A' + row);
                    for (int col = 1; col <= cols; col++) {
                        String seatNumber = String.format("%c%d", rowChar, col);
                        insertStmt.setInt(1, showtimeId);
                        insertStmt.setString(2, seatNumber);
                        insertStmt.addBatch();
                    }
                }
                insertStmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            System.err.println("Failed to initialize seats: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }

    public Seat getSeatByID(int id) {
        String query = "SELECT * FROM seats WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Seat(
                        rs.getInt("id"),
                        rs.getInt("showtime_id"),
                        rs.getString("seatNumber"),
                        rs.getBoolean("isBooked")
                );
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch seat: " + e.getMessage());
            logSQLException(e);
        }
        return null;
    }

    public List<Seat> getSeatsByShowtime(int showtimeId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE showtime_id = ? ORDER BY seatNumber ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt("id"),
                        rs.getInt("showtime_id"),
                        rs.getString("seatNumber"),
                        rs.getBoolean("isBooked")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch seats: " + e.getMessage());
            logSQLException(e);
        }
        return seats;
    }

    public boolean updateSeatStatus(int seatId, boolean isBooked) {
        String query = "UPDATE seats SET isBooked = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isBooked);
            stmt.setInt(2, seatId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update seat status: " + e.getMessage());
            logSQLException(e);
        }
        return false;
    }

    /**
     * Helper method to log SQL exceptions with detailed information
     * @param e The SQLException to log
     */
    private void logSQLException(SQLException e) {
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("Message: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}
