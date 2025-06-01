package view;

import controller.SeatController;
import controller.ShowtimeController;
import dao.BookingDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Booking;
import model.Seat;
import model.Showtime;
import model.User;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View class for seat selection functionality
 */
public class SeatView extends Application {
    // Constants for styling and layout
    private static final int SEAT_SIZE = 40;
    private static final int GRID_SPACING = 5;
    private static final int PADDING = 15;
    private static final String BACKGROUND_COLOR = "#f5f5f5";
    private static final String AVAILABLE_SEAT_COLOR = "#4CAF50";
    private static final String BOOKED_SEAT_COLOR = "#ff4444";
    private static final String SELECTED_SEAT_COLOR = "#2196F3";
    private static final String UNAVAILABLE_SEAT_COLOR = "#cccccc";
    private static final String SUCCESS_COLOR = "#00c853";
    private static final String ERROR_COLOR = "#d50000";

    // Theater configuration
    private static final int ROWS = 10;
    private static final int COLS = 15;

    // Controllers
    private final SeatController seatController;
    private final ShowtimeController showtimeController;
    private final BookingDAO bookingDAO;

    // Model data
    private final int selectedShowtimeId;
    private Seat selectedSeat;
    private Showtime showtime;
    private User currentUser;

    // UI components
    private Label statusLabel;
    private Button bookButton;
    private GridPane seatGrid;
    private Map<String, Button> seatButtonMap;

    /**
     * Constructor initializes controllers and showtime ID
     * @param showtimeId ID of the selected showtime
     */
    public SeatView(int showtimeId, User user) {
        this.selectedShowtimeId = showtimeId;
        this.currentUser = user;
        this.seatController = new SeatController();
        this.showtimeController = new ShowtimeController();
        this.bookingDAO = new BookingDAO();
        this.seatButtonMap = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) {
        // Load showtime information
        showtime = showtimeController.getShowtimeById(selectedShowtimeId);
        if (showtime == null) {
            showAlert("Error", "Showtime not found");
            primaryStage.close();
            return;
        }

        setupStage(primaryStage);

        // Main layout
        BorderPane root = createMainLayout();

        // Load UI components
        HBox headerBox = createHeader();
        seatGrid = createSeatGrid();
        ScrollPane scrollPane = createScrollPane(seatGrid);
        HBox bottomPanel = createBottomPanel();

        // Assemble UI
        root.setTop(headerBox);
        root.setCenter(scrollPane);
        root.setBottom(bottomPanel);

        // Create and set scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sets up the stage properties
     */
    private void setupStage(Stage primaryStage) {
        primaryStage.setTitle("Seat Selection");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
    }

    /**
     * Creates the main layout container
     */
    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(PADDING));
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        return root;
    }

    /**
     * Creates the header with showtime information
     */
    private HBox createHeader() {
        String showtimeInfo = formatShowtimeInfo(showtime);
        Label headerLabel = new Label("Select a seat for: " + showtimeInfo);
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox headerBox = new HBox(headerLabel);
        headerBox.setPadding(new Insets(10));
        headerBox.setAlignment(Pos.CENTER);
        return headerBox;
    }

    /**
     * Creates the seat grid with all seats for the selected showtime
     */
    private GridPane createSeatGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(GRID_SPACING);
        grid.setVgap(GRID_SPACING);
        grid.setPadding(new Insets(PADDING));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        grid.setAlignment(Pos.CENTER);

        // Add row labels (A, B, C, etc.)
        char rowChar = 'A';
        for (int row = 0; row < ROWS; row++) {
            Label rowLabel = new Label(String.valueOf(rowChar));
            rowLabel.setStyle("-fx-font-weight: bold;");
            grid.add(rowLabel, 0, row + 1);
            rowChar++;
        }

        // Add column labels (1, 2, 3, etc.)
        for (int col = 0; col < COLS; col++) {
            Label colLabel = new Label(String.valueOf(col + 1));
            colLabel.setStyle("-fx-font-weight: bold;");
            grid.add(colLabel, col + 1, 0);
        }

        // Add screen representation at the top
        Label screenLabel = new Label("SCREEN");
        screenLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #e0e0e0; -fx-padding: 5;");
        screenLabel.setPrefWidth((COLS * SEAT_SIZE) + ((COLS - 1) * GRID_SPACING));
        screenLabel.setAlignment(Pos.CENTER);
        grid.add(screenLabel, 1, ROWS + 2, COLS, 1);

        // Populate seats
        populateSeats(grid);

        return grid;
    }

    /**
     * Populates the seat grid with buttons representing seats
     */
    private void populateSeats(GridPane grid) {
        List<Seat> seats = seatController.getSeatsByShowtime(selectedShowtimeId);
        char rowChar = 'A';

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String seatNumber = String.format("%c%d", rowChar, col + 1);
                Seat seat = findSeatByNumber(seats, seatNumber);

                Button seatBtn = createSeatButton(seat, seatNumber);
                grid.add(seatBtn, col + 1, row + 1); // +1 to account for row/col labels

                // Store button reference for later use
                seatButtonMap.put(seatNumber, seatBtn);
            }
            rowChar++;
        }
    }

    /**
     * Creates a scroll pane for the seat grid
     */
    private ScrollPane createScrollPane(GridPane grid) {
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: " + BACKGROUND_COLOR + "; -fx-border-color: " + BACKGROUND_COLOR + ";");
        return scrollPane;
    }

    /**
     * Creates the bottom panel with action buttons and status label
     */
    private HBox createBottomPanel() {
        // Status label
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");

        // Action buttons
        Button backButton = createStyledButton("Back", "#607D8B", SEAT_SIZE);
        backButton.setOnAction(e -> ((Stage) backButton.getScene().getWindow()).close());

        bookButton = createStyledButton("Book Seat", AVAILABLE_SEAT_COLOR, SEAT_SIZE);
        bookButton.setId("bookButton");
        bookButton.setDisable(true);
        bookButton.setOnAction(e -> handleSeatBooking());

        // Legend for seat colors
        HBox legend = createSeatLegend();

        // Button container
        HBox buttonBox = new HBox(10, backButton, bookButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Bottom panel with three sections
        BorderPane bottomBorder = new BorderPane();
        bottomBorder.setLeft(legend);
        bottomBorder.setCenter(statusLabel);
        bottomBorder.setRight(buttonBox);
        bottomBorder.setPadding(new Insets(10));

        return new HBox(bottomBorder);
    }

    /**
     * Creates a legend explaining seat colors
     */
    private HBox createSeatLegend() {
        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER_LEFT);

        String[] labels = {"Available", "Selected", "Booked"};
        String[] colors = {AVAILABLE_SEAT_COLOR, SELECTED_SEAT_COLOR, BOOKED_SEAT_COLOR};

        for (int i = 0; i < labels.length; i++) {
            Button sample = new Button();
            sample.setPrefSize(20, 20);
            sample.setStyle("-fx-background-color: " + colors[i] + "; -fx-background-radius: 3;");
            sample.setDisable(true);

            Label text = new Label(labels[i]);
            text.setStyle("-fx-font-size: 12px;");

            HBox item = new HBox(5, sample, text);
            item.setAlignment(Pos.CENTER_LEFT);

            legend.getChildren().add(item);
        }

        return legend;
    }

    /**
     * Creates a button representing a seat
     */
    private Button createSeatButton(Seat seat, String seatNumber) {
        Button seatBtn = new Button(seatNumber);
        seatBtn.setPrefSize(SEAT_SIZE, SEAT_SIZE);
        seatBtn.setMinSize(SEAT_SIZE, SEAT_SIZE);
        seatBtn.setMaxSize(SEAT_SIZE, SEAT_SIZE);

        if (seat != null) {
            if (seat.isBooked()) {
                styleBookedSeat(seatBtn);
            } else {
                styleAvailableSeat(seatBtn, seat);
            }
        } else {
            styleUnavailableSeat(seatBtn);
        }

        return seatBtn;
    }

    /**
     * Styles a seat button as booked
     */
    private void styleBookedSeat(Button seatBtn) {
        seatBtn.setStyle("-fx-background-color: " + BOOKED_SEAT_COLOR + "; -fx-text-fill: white;");
        seatBtn.setTooltip(new Tooltip("Booked"));
        seatBtn.setDisable(true);
    }

    /**
     * Styles a seat button as available
     */
    private void styleAvailableSeat(Button seatBtn, Seat seat) {
        seatBtn.setStyle("-fx-background-color: " + AVAILABLE_SEAT_COLOR + "; -fx-text-fill: white;");
        seatBtn.setTooltip(new Tooltip("Available"));
        seatBtn.setOnAction(e -> handleSeatSelection(seat, seatBtn));
    }

    /**
     * Styles a seat button as unavailable
     */
    private void styleUnavailableSeat(Button seatBtn) {
        seatBtn.setStyle("-fx-background-color: " + UNAVAILABLE_SEAT_COLOR + ";");
        seatBtn.setDisable(true);
        seatBtn.setTooltip(new Tooltip("Not available"));
    }

    /**
     * Handles the selection of a seat
     */
    private void handleSeatSelection(Seat seat, Button seatBtn) {
        // Deselect previous selection
        if (selectedSeat != null) {
            Button prevBtn = seatButtonMap.get(selectedSeat.getSeatNumber());
            if (prevBtn != null) {
                prevBtn.setStyle("-fx-background-color: " + AVAILABLE_SEAT_COLOR + "; -fx-text-fill: white;");
            }
        }

        // Select new seat
        selectedSeat = seat;
        seatBtn.setStyle("-fx-background-color: " + SELECTED_SEAT_COLOR + "; -fx-text-fill: white;");
        bookButton.setDisable(false);

        showStatusMessage("Selected seat: " + seat.getSeatNumber() + " (Price: $10.00)", true);
    }

    /**
     * Handles the booking of a selected seat
     */
    private void handleSeatBooking() {
        if (selectedSeat == null) {
            showAlert("Error", "Please select a seat first");
            return;
        }

        try {
            // Use the current user's ID instead of hardcoded 1
            boolean bookingCreated = bookingDAO.createNewBookingForSeat(
                    selectedSeat.getId(),
                    currentUser.getID(),  // Use current user's ID
                    showtime.getMovieId(),
                    selectedShowtimeId
            );

            if (!bookingCreated) {
                showAlert("Error", "Failed to create booking");
                return;
            }

            Booking booking = bookingDAO.getBookingBySeatId(selectedSeat.getId());
            if (booking == null) {
                showAlert("Error", "Booking not found after creation");
                return;
            }

            // Pass the user to PaymentView
            PaymentView paymentView = new PaymentView(selectedSeat.getId(), booking.getID(), currentUser);
            Stage paymentStage = new Stage();
            paymentView.start(paymentStage);

            ((Stage) bookButton.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert("Error", "An error occurred while processing your booking: " + e.getMessage());
        }
    }

    /**
     * Formats showtime information for display
     */
    private String formatShowtimeInfo(Showtime showtime) {
        if (showtime == null) return "";

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        return showtime.getDateTime().format(dateFormatter) + " at " +
                showtime.getDateTime().format(timeFormatter) + " in Hall " + showtime.getHall();
    }

    /**
     * Finds a seat by its seat number
     */
    private Seat findSeatByNumber(List<Seat> seats, String seatNumber) {
        return seats.stream()
                .filter(s -> s.getSeatNumber().equalsIgnoreCase(seatNumber))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a styled button with hover effects
     */
    private Button createStyledButton(String text, String color, int height) {
        Button button = new Button(text);
        button.setPrefHeight(height);
        button.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");
        button.setCursor(javafx.scene.Cursor.HAND);

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;"));

        return button;
    }

    /**
     * Shows a status message
     */
    private void showStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isSuccess ? Color.valueOf(SUCCESS_COLOR) : Color.valueOf(ERROR_COLOR));
    }

    /**
     * Shows an alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        launch(args);
    }
}
