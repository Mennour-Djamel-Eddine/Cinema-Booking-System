package model;

public class Booking {
    private int ID;
    private int userID;
    private int movieID;
    private int showtimeID;
    private boolean isPaid;

    public Booking(int ID, int userID, int movieID, int showtimeID, boolean isPaid) {
        this.ID = ID;
        this.userID = userID;
        this.movieID = movieID;
        this.isPaid = isPaid;
        this.showtimeID = showtimeID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return userID;
    }

    public int getShowtimeID() {
        return showtimeID;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public int getMovieID() {
        return movieID;
    }
}
