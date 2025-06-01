package model;

public enum PaymentStatus {
    PENDING, COMPLETED, FAILED;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}