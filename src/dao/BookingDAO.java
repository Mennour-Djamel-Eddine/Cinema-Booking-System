package dao;

import database.DatabaseConnection;
import model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private Connection conn;

    public BookingDAO() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection failed! " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a new booking (updated to match Booking constructor)
    public boolean addBooking(Booking booking) {
        String query = "INSERT INTO bookings (user_id, movie_id, showtime_id, isPaid) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserID());
            stmt.setInt(2, booking.getMovieID());
            stmt.setInt(3, booking.getShowtimeID());
            stmt.setBoolean(4, booking.isPaid());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setID(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to add booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Get a booking by ID (updated to match Booking constructor)
    public Booking getBookingByID(int id) {
        String query = "SELECT * FROM bookings WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("movie_id"),
                        rs.getInt("showtime_id"),
                        rs.getBoolean("isPaid")
                );
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch booking: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get booking by seat ID (new method)
    public Booking getBookingBySeatId(int seatId) {
        String query = "SELECT b.* FROM bookings b " +
                "JOIN booking_seats bs ON b.id = bs.booking_id " +
                "WHERE bs.seat_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, seatId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("movie_id"),
                        rs.getInt("showtime_id"),
                        rs.getBoolean("isPaid")
                );
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch booking by seat: " + e.getMessage());
        }
        return null;
    }

    // Link seat to booking (new method)
    public boolean linkSeatToBooking(int bookingId, int seatId) {
        String query = "INSERT INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            stmt.setInt(2, seatId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to link seat to booking: " + e.getMessage());
        }
        return false;
    }

    // Create new booking for seat (new method)
    public boolean createNewBookingForSeat(int seatId, int userId, int movieId, int showtimeId) {
        try {
            // Create new booking (isPaid = false by default)
            Booking newBooking = new Booking(0, userId, movieId, showtimeId, false);
            if (!addBooking(newBooking)) {
                return false;
            }

            // Link seat to booking (but don't mark as booked yet)
            return linkSeatToBooking(newBooking.getID(), seatId);
        } catch (Exception e) {
            System.err.println("Failed to create booking for seat: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBooking(Booking booking) {
        String query = "UPDATE bookings SET user_id = ?, movie_id = ?, showtime_id = ?, isPaid = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, booking.getUserID());
            stmt.setInt(2, booking.getMovieID());
            stmt.setInt(3, booking.getShowtimeID());
            stmt.setBoolean(4, booking.isPaid());
            stmt.setInt(5, booking.getID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBooking(int id) {
        String query = "DELETE FROM bookings WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
