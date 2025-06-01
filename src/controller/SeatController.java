package controller;

import dao.SeatDAO;
import model.Seat;
import java.util.List;

public class SeatController {
    private final SeatDAO seatDao;

    public SeatController() {
        this.seatDao = new SeatDAO();
    }

    /**
     * Gets all seats for a specific showtime
     * @param showtimeId The ID of the showtime
     * @return List of Seat objects
     */
    public List<Seat> getSeatsByShowtime(int showtimeId) {
        return seatDao.getSeatsByShowtime(showtimeId);
    }

    /**
     * Closes the database connection
     */
    public void close() {
        seatDao.closeConnection();
    }
}
