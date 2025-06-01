package controller;

import dao.MovieDAO;
import model.Movie;

import java.util.List;

public class MovieController {
    private MovieDAO movieDAO;

    public MovieController() {
        this.movieDAO = new MovieDAO();
    }

    // ================ Existing Methods ================
    public boolean addMovie(Movie movie) {
        return movieDAO.addMovie(movie);
    }

    public Movie getMovieByTitle(String title) {
        return movieDAO.getMovieByTitle(title);
    }

    public List<Movie> getAllMovies() {
        return movieDAO.getAllMovies();
    }

    // ================ New Methods for Admin ================
    public Movie getMovieByID(int id) {
        return movieDAO.getMovieByID(id);
    }
}
