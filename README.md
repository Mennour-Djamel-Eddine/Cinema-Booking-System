# Cinema Booking System - JavaFX Desktop Application

![Java](https://img.shields.io/badge/Java-17-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-19-orange)
![License](https://img.shields.io/badge/License-MIT-green)
![Architecture](https://img.shields.io/badge/Architecture-MVC-yellow)
![Database](https://img.shields.io/badge/Database-MySQL-blue)

A modern JavaFX desktop application for movie ticket booking, featuring user authentication, seat reservation, and a full-featured admin dashboard.

## Features

### User Features

* **Authentication**

  * Secure login & registration with role-based access (Admin/User)
  * Forgot password functionality
  * Social login options *(UI only)*

* **Movie Catalog**

  * Browse movies with title, genre, duration, and synopsis
  * Search and filter capabilities

* **Showtime Browsing**

  * View showtimes by date
  * Filter by movie

* **Seat Reservation**

  * Interactive seat map with visual availability indicators
  * Real-time seat selection feedback

* **Payment Processing**

  * Secure payment form
  * Booking confirmation and ticket generation

### Admin Features

* **Dashboard**

  * Tabbed interface for managing entities
  * Live data updates

* **Movie Management**

  * Add, edit, delete movies
  * Cascade delete of related showtimes and bookings

* **Showtime Management**

  * Schedule new showtimes
  * 30-minute conflict validation
  * Safe deletion with cascade behavior

* **User Management**

  * View, search, and delete registered users

## Technical Stack

| Layer        | Technology          |
| ------------ | ------------------- |
| Frontend     | JavaFX (Styled UI)  |
| Backend      | Java 17+            |
| Architecture | MVC                 |
| Build Tool   | Maven (Recommended) |
| Database     | MySQL + JDBC        |
| Testing      | JUnit               |

## Application Structure

### Model Layer (Domain Objects)

| Class           | Description        | Attributes                            |
| --------------- | ------------------ | ------------------------------------- |
| User            | System user        | ID, Name, Email, Password, Role       |
| Movie           | Movie data         | ID, Title, Genre, Duration, Synopsis  |
| Showtime        | Movie screening    | ID, MovieID, DateTime, Hall           |
| Seat            | Theater seat       | ID, ShowtimeID, SeatNumber, Booked    |
| Booking         | Reservation record | ID, UserID, MovieID, ShowtimeID, Paid |
| Payment         | Payment record     | ID, BookingID, Amount, Status, Method |
| PaymentStatus   | Enum               | PENDING, COMPLETED, FAILED            |
| UserRole        | Enum               | USER, ADMIN                           |

### Controller Layer (Business Logic)

| Controller           | Responsibilities   | Notable Features                              |
| -------------------- | ------------------ | --------------------------------------------- |
| AuthController       | Login/Registration | Validates credentials, handles authentication |
| AdminController      | Admin functions    | Validation, movie/showtime management         |
| MovieController      | Manage movies      | CRUD, search                                  |
| ShowtimeController   | Manage showtimes   | Date filtering, seat creation                 |
| SeatController       | Seat operations    | Show seat availability                        |
| PaymentController    | Payment logic      | Simulated processing                          |
| BookingController    | Booking logic      | *(Under development)*                         |

#### Key Logic Highlights

* validateShowtime() ensures 30-minute gap between showtimes
* Atomic operations and rollback handling (e.g., showtime + seats)
* Role-based access in AuthController
* Business rules like preventing past showtimes and enforcing movie duration
* Custom error handling and user-friendly messages

### View Layer

| View                    | Description            | UI Features                          |
| ----------------------- | ---------------------- | ------------------------------------ |
| LoginView               | Authentication screen  | Gradient background, form validation |
| MovieListView           | Browse catalog         | Search bar                           |
| ShowtimeSelectionView   | Showtime selection     | Filter by date                       |
| SeatView                | Seat booking interface | Interactive seating                  |
| PaymentView             | Payment form           | Generates ticket                     |
| AdminDashboardView      | Admin panel            | Tabbed layout                        |

### Data Access Layer (DAO)

| DAO Class             | Responsibilities | Special Features               |
| --------------------- | ---------------- | ------------------------------ |
|  UserDAO              | User CRUD        | Lookup by email, role          |
|  MovieDAO             | Movie CRUD       | Title search, cascade delete   |
|  ShowtimeDAO          | Showtime CRUD    | Filter by date, movie          |
|  SeatDAO              | Seat operations  | Batch creation, status updates |
|  BookingDAO           | Booking CRUD     | Tracks payment, links seats    |
|  PaymentDAO           | Payment records  | Records and updates            |
|  CascadeDeleteHelper  | Cascade deletion | Transactional safety           |

## Database Schema

CREATE TABLE users (id, name, email, password, role);
CREATE TABLE Movie (id, title, genre, duration, synopsis);
CREATE TABLE Showtime (id, movie_id, dateTime, hall);
CREATE TABLE Seats (id, showtime_id, seatNumber, isBooked);
CREATE TABLE Bookings (id, user_id, movie_id, showtime_id, isPaid);
CREATE TABLE Payment (
  id, booking_id, amount, PaymentStatus,
  payment_date, payment_method
);

-- M:N Relationship
CREATE TABLE Booking_Seats (booking_id, seat_id);

## Entity Relationships

Users     → Bookings    (1:N)
Movies    → Showtimes   (1:N)
Showtimes → Seats       (1:N)
Bookings  → Seats       (M:N via Booking_Seats)
Bookings  → Payments    (1:1)

## Technical Highlights

* **Validation & Security**

  * Email/password format checks
  * Role-based access control
  * Movie and showtime rules (e.g., no past scheduling)

* **Transactional Operations**

  * Seat batch creation
  * Safe rollbacks during failures
  * Cascade delete logic for movies/showtimes

* **Performance & Feedback**

  * Real-time seat updates
  * Live search filters
  * Fast batch operations with reduced DB hits

* **Error Handling**

  * Catch and log SQL exceptions
  * Display user-friendly messages

## Setup Instructions

### 1. Database Setup

mysql> CREATE DATABASE MovieBookingDB;
mysql> USE MovieBookingDB;
mysql> SOURCE movie_booking.sql;


### 2. Configuration

* Open DatabaseConnection.java
* Set your DB username/password

### 3. Build & Run

mvn clean install
mvn javafx:run

Let me know if you want:

* A dark-themed UI CSS for JavaFX
* UML diagrams for class relationships
* A contribution guide or license section
