package controller;

import model.Movie;
import model.Showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminController {
    private final MovieController movieController;
    private final ShowtimeController showtimeController;

    public AdminController() {
        this.movieController = new MovieController();
        this.showtimeController = new ShowtimeController();
    }

    // ==================== MOVIE MANAGEMENT ====================

    public boolean addMovie(String title, String genre, int duration, String synopsis) {
        if (title == null || title.trim().isEmpty() || duration <= 0) {
            return false;
        }
        Movie movie = new Movie(0, title.trim(), genre, duration, synopsis);
        return movieController.addMovie(movie);
    }

    public boolean deleteMovie(int movieId) {
        return dao.CascadeDeleteHelper.safeDeleteMovie(movieId);
    }

    public List<Movie> getAllMovies() {
        return movieController.getAllMovies();
    }

    public Movie getMovieById(int movieId) {
        return movieController.getMovieByID(movieId);
    }

    // ==================== SHOWTIME MANAGEMENT ====================

    public boolean addShowtime(int movieId, String date, String time, String hall) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(
                    date + "T" + time,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );
            return showtimeController.addShowtime(movieId, dateTime, hall);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean deleteShowtime(int showtimeId) {
        return dao.CascadeDeleteHelper.safeDeleteShowtime(showtimeId);
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeController.getAllShowtimes();
    }

    // ==================== BOOKING MANAGEMENT ====================

    public String validateShowtime(int movieId, String dateStr, String timeStr, String hall) {
        // Validate time format (HH:MM)
        if (!timeStr.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            return "Invalid time format. Please use HH:MM (e.g., 14:30)";
        }

        // Parse the date and time
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            return "Invalid date format";
        }

        // Check if date is in the past (additional check even though UI prevents it)
        if (date.isBefore(LocalDate.now())) {
            return "Cannot create showtime for a past date";
        }

        // Get the movie duration
        Movie movie = getMovieById(movieId);
        if (movie == null) {
            return "Selected movie not found";
        }
        int durationMinutes = movie.getDuration();

        // Calculate start and end times
        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.parse(timeStr));
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        // Check for overlapping showtimes in the same hall
        for (Showtime existing : getAllShowtimes()) {
            if (existing.getHall().equals(hall)) {
                LocalDateTime existingStart = existing.getDateTime();
                LocalDateTime existingEnd = existingStart.plusMinutes(getMovieById(existing.getMovieId()).getDuration());

                // Check for overlap (with buffer time of 30 minutes between showtimes)
                if (!(endDateTime.isBefore(existingStart.minusMinutes(30)) ||
                        startDateTime.isAfter(existingEnd.plusMinutes(30)))) {
                    Movie existingMovie = getMovieById(existing.getMovieId());
                    return String.format("Time conflict with %s (%s - %s) in the same hall. " +
                                    "Need at least 30 minutes between showtimes.",
                            existingMovie.getTitle(),
                            existingStart.format(DateTimeFormatter.ofPattern("h:mm a")),
                            existingEnd.format(DateTimeFormatter.ofPattern("h:mm a")));
                }
            }
        }

        return null;
    }
}
