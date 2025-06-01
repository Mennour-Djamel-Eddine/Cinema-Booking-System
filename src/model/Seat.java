package model;

public class Seat {
    private int id;
    private int showtimeID;
    private String seatNumber;
    private boolean isBooked;

    // Full constructor with all fields (used when retrieving from DB)
    public Seat(int id, int showtimeID, String seatNumber, boolean isBooked) {
        this.id = id;
        this.showtimeID = showtimeID;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }
}
