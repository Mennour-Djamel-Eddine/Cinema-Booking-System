package controller;

import dao.MovieDAO;
import dao.ShowtimeDAO;
import dao.SeatDAO;
import model.Showtime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ShowtimeController {
    private final ShowtimeDAO showtimeDao;
    private final MovieDAO movieDao;
    private final SeatDAO seatDao;

    public ShowtimeController() {
        this.showtimeDao = new ShowtimeDAO();
        this.movieDao = new MovieDAO();
        this.seatDao = new SeatDAO();
    }

    public int addShowtimeWithSeats(int movieId, LocalDateTime dateTime, String hall, int rows, int cols) {
        // Validate inputs
        if (movieId <= 0 || dateTime == null || hall == null || hall.trim().isEmpty() || rows <= 0 || cols <= 0) {
            return -1;
        }

        // Check if movie exists
        if (movieDao.getMovieByID(movieId) == null) {
            return -1;
        }

        Showtime showtime = new Showtime(0, movieId, dateTime, hall);
        if (showtimeDao.addShowtime(showtime)) {
            if (seatDao.initializeSeatsForShowtime(showtime.getId(), rows, cols)) {
                return showtime.getId();
            } else {
                // Rollback showtime creation if seat initialization fails
                showtimeDao.deleteShowtime(showtime.getId());
                return -1;
            }
        }
        return -1;
    }

    public List<Showtime> getShowtimesForMovieAndDate(int movieId, LocalDate date) {
        return showtimeDao.getShowtimesByMovieAndDate(movieId, date);
    }


    public Showtime getShowtimeById(int showtimeId) {
        return showtimeDao.getShowtimeByID(showtimeId);
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeDao.getAllShowtimes();
    }

    public List<Showtime> getShowtimesByMovie(int movieId) {
        if (movieId <= 0) {
            return List.of();
        }
        return showtimeDao.getShowtimesByMovieID(movieId);
    }

    public void close() {
        try {
            seatDao.closeConnection();
        } catch (Exception e) {
            System.err.println("Error closing connections: " + e.getMessage());
        }
    }

    public boolean addShowtime(int movieId, LocalDateTime dateTime, String hall) {
        return addShowtimeWithSeats(movieId, dateTime, hall, 10, 15) > 0;
    }
}
