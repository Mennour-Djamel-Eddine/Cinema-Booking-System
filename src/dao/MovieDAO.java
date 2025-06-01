package dao;

import database.DatabaseConnection;
import model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    // Add a new movie
    public boolean addMovie(Movie movie) {
        String query = "INSERT INTO Movie (title, genre, duration, synopsis) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getSynopsis());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        movie.setID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
        }
        return false;
    }

    // Get movie by ID
    public Movie getMovieByID(int id) {
        String query = "SELECT * FROM Movie WHERE id = ?";
        Movie movie = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getString("synopsis")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting movie by ID: " + e.getMessage());
        }
        return movie;
    }

    // Get movie by title
    public Movie getMovieByTitle(String title) {
        String query = "SELECT * FROM Movie WHERE title = ?";
        Movie movie = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getString("synopsis")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting movie by title: " + e.getMessage());
        }
        return movie;
    }

    // Get all movies
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM Movie ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getString("synopsis")
                );
                movies.add(movie);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all movies: " + e.getMessage());
        }
        return movies;
    }
}
