package dao;

import database.DatabaseConnection;
import model.Showtime;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {
    private Connection conn;

    public ShowtimeDAO() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean addShowtime(Showtime showtime) {
        String query = "INSERT INTO Showtime (movie_id, dateTime, hall) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, showtime.getMovieId());
            stmt.setTimestamp(2, Timestamp.valueOf(showtime.getDateTime()));
            stmt.setString(3, showtime.getHall());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        showtime.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to add showtime: " + e.getMessage());
        }
        return false;
    }

    public Showtime getShowtimeByID(int id) {
        String query = "SELECT * FROM Showtime WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Showtime(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getTimestamp("dateTime").toLocalDateTime(),
                        rs.getString("hall")
                );
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch showtime: " + e.getMessage());
        }
        return null;
    }

    public List<Showtime> getShowtimesByMovieID(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();
        String query = "SELECT * FROM Showtime WHERE movie_id = ? ORDER BY dateTime ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                showtimes.add(new Showtime(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getTimestamp("dateTime").toLocalDateTime(),
                        rs.getString("hall")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch showtimes by movie: " + e.getMessage());
        }
        return showtimes;
    }

    public List<Showtime> getShowtimesByMovieAndDate(int movieId, LocalDate date) {
        List<Showtime> showtimes = new ArrayList<>();
        String query = "SELECT * FROM Showtime WHERE movie_id = ? AND DATE(dateTime) = ? ORDER BY dateTime ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                showtimes.add(new Showtime(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getTimestamp("dateTime").toLocalDateTime(),
                        rs.getString("hall")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch showtimes by movie and date: " + e.getMessage());
        }
        return showtimes;
    }

    public List<Showtime> getAllShowtimes() {
        List<Showtime> showtimes = new ArrayList<>();
        String query = "SELECT * FROM Showtime ORDER BY dateTime ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                showtimes.add(new Showtime(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getTimestamp("dateTime").toLocalDateTime(),
                        rs.getString("hall")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch all showtimes: " + e.getMessage());
        }
        return showtimes;
    }

    public boolean deleteShowtime(int id) {
        String query = "DELETE FROM Showtime WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete showtime: " + e.getMessage());
        }
        return false;
    }

    /**
     * Closes the database connection
     */
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }
}
