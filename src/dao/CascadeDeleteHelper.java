package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CascadeDeleteHelper {

    public static boolean safeDeleteMovie(int movieId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // First delete related booking_seats records
                deleteBookingSeatsForMovie(conn, movieId);

                // Delete payment records
                deletePaymentsForMovie(conn, movieId);

                // Delete bookings for this movie
                deleteBookingsForMovie(conn, movieId);

                // Get showtimes for this movie
                List<Integer> showtimeIds = getShowtimeIdsByMovie(conn, movieId);

                // Delete seats for these showtimes
                for (Integer showtimeId : showtimeIds) {
                    deleteSeatsForShowtime(conn, showtimeId);
                }

                // Delete showtimes
                deleteShowtimesForMovie(conn, movieId);

                // Finally delete the movie
                deleteMovie(conn, movieId);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error in cascade delete for movie: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean safeDeleteShowtime(int showtimeId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Get booking IDs for this showtime
                List<Integer> bookingIds = getBookingIdsByShowtime(conn, showtimeId);

                // Delete booking_seats for these bookings
                for (Integer bookingId : bookingIds) {
                    deleteBookingSeatsForBooking(conn, bookingId);
                }

                // Delete payments for these bookings
                for (Integer bookingId : bookingIds) {
                    deletePaymentsForBooking(conn, bookingId);
                }

                // Delete bookings for this showtime
                deleteBookingsForShowtime(conn, showtimeId);

                // Delete seats for this showtime
                deleteSeatsForShowtime(conn, showtimeId);

                // Finally delete the showtime
                deleteShowtime(conn, showtimeId);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error in cascade delete for showtime: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    // Helper methods

    private static void deleteBookingSeatsForMovie(Connection conn, int movieId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM booking_seats WHERE booking_id IN " +
                        "(SELECT id FROM bookings WHERE movie_id = ?)")) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete booking_seats for movie: " + e.getMessage());
            throw e;
        }
    }

    private static void deletePaymentsForMovie(Connection conn, int movieId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM payment WHERE booking_id IN " +
                        "(SELECT id FROM bookings WHERE movie_id = ?)")) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete payments for movie: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteBookingsForMovie(Connection conn, int movieId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookings WHERE movie_id = ?")) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete bookings for movie: " + e.getMessage());
            throw e;
        }
    }

    private static List<Integer> getShowtimeIdsByMovie(Connection conn, int movieId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM showtime WHERE movie_id = ?")) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }
            }
        }
        return ids;
    }

    private static void deleteSeatsForShowtime(Connection conn, int showtimeId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM seats WHERE showtime_id = ?")) {
            stmt.setInt(1, showtimeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete seats for showtime: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteShowtimesForMovie(Connection conn, int movieId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM showtime WHERE movie_id = ?")) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete showtimes for movie: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteMovie(Connection conn, int movieId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM movie WHERE id = ?")) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete movie: " + e.getMessage());
            throw e;
        }
    }

    private static List<Integer> getBookingIdsByShowtime(Connection conn, int showtimeId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM bookings WHERE showtime_id = ?")) {
            stmt.setInt(1, showtimeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }
            }
        }
        return ids;
    }

    private static void deleteBookingSeatsForBooking(Connection conn, int bookingId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM booking_seats WHERE booking_id = ?")) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete booking_seats for booking: " + e.getMessage());
            throw e;
        }
    }

    private static void deletePaymentsForBooking(Connection conn, int bookingId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM payment WHERE booking_id = ?")) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete payments for booking: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteBookingsForShowtime(Connection conn, int showtimeId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookings WHERE showtime_id = ?")) {
            stmt.setInt(1, showtimeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete bookings for showtime: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteShowtime(Connection conn, int showtimeId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM showtime WHERE id = ?")) {
            stmt.setInt(1, showtimeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete showtime: " + e.getMessage());
            throw e;
        }
    }
}
