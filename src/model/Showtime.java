package model;

import java.time.LocalDateTime;

public class Showtime {
    private int id;
    private int movieId;
    private LocalDateTime dateTime;
    private String hall;

    // Constructor
    public Showtime(int id, int movieId, LocalDateTime dateTime, String hall) {
        this.id = id;
        this.movieId = movieId;
        this.dateTime = dateTime;
        this.hall = hall;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getHall() {
        return hall;
    }
}
