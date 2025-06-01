-- create the database
CREATE DATABASE IF NOT EXISTS MovieBookingDB;
USE MovieBookingDB;

-- User Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL
);

-- Movie Table
CREATE TABLE IF NOT EXISTS Movie(
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    duration INT,
    synopsis TEXT
);

-- Showtime TABLE
CREATE TABLE IF NOT EXISTS Showtime(
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT,
    dateTime DATETIME NOT NULL,
    hall VARCHAR(50),
    FOREIGN KEY (movie_id) REFERENCES Movie(id) ON DELETE CASCADE
);

-- Seats Table
CREATE TABLE IF NOT EXISTS Seats(
    id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT,
    seatNumber VARCHAR(10),
    isBooked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (showtime_id) REFERENCES Showtime(id) ON DELETE CASCADE
);

-- Bookings Table
CREATE TABLE IF NOT EXISTS Bookings(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    movie_id INT,
    showtime_id INT,
    isPaid BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (showtime_id) REFERENCES Showtime(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES Movie(id) ON DELETE CASCADE
);

-- Payment Table
CREATE TABLE Payment(
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    amount FLOAT NOT NULL,
    PaymentStatus ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50),
    FOREIGN KEY (booking_id) REFERENCES Bookings(id)
);

-- Many-to-Many RELATIONSHIP
CREATE TABLE IF NOT EXISTS Booking_Seats(
    booking_id INT,
    seat_id INT,
    PRIMARY KEY (booking_id, seat_id),
    FOREIGN KEY (booking_id) REFERENCES Bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES Seats(id) ON DELETE CASCADE
);
