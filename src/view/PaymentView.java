package view;

import controller.PaymentController;
import controller.AuthController;
import dao.BookingDAO;
import dao.SeatDAO;
import dao.ShowtimeDAO;
import dao.MovieDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import model.Booking;
import model.Payment;
import model.Seat;
import model.Showtime;
import model.Movie;
import model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentView extends Application {
    private User currentUser;
    private PaymentController paymentController = new PaymentController();
    private AuthController authController = new AuthController();
    private BookingDAO bookingDAO = new BookingDAO();
    private SeatDAO seatDAO = new SeatDAO();
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private MovieDAO movieDAO = new MovieDAO();
    private Label statusLabel;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Integer seatId;
    private Integer bookingId;

    public PaymentView(int seatId, int bookingId, User user) {
        this.seatId = seatId;
        this.bookingId = bookingId;
        this.currentUser = user;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Payment System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a237e, #4a148c);");

        VBox contentCard = new VBox(20);
        contentCard.setPadding(new Insets(30));
        contentCard.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        contentCard.setMaxWidth(800);
        contentCard.setMaxHeight(600);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(15);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        contentCard.setEffect(dropShadow);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label appIcon = new Label("💳");
        appIcon.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        appIcon.setTextFill(Color.valueOf("#6200ea"));

        Label titleLabel = new Label("Movie Ticket Payment");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.valueOf("#303f9f"));

        header.getChildren().addAll(appIcon, titleLabel);

        VBox paymentForm = createPaymentForm();
        VBox.setVgrow(paymentForm, Priority.ALWAYS);

        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statusLabel.setOpacity(0);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);

        contentCard.getChildren().addAll(header, paymentForm, statusLabel);
        mainLayout.setCenter(contentCard);
        BorderPane.setMargin(contentCard, new Insets(30));

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createPaymentForm() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f8f9fa;");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        TextField bookingIdField = createStyledTextField("Booking ID");
        TextField amountField = createStyledTextField("Amount");
        amountField.setText("10.00");
        amountField.setDisable(true);

        if (bookingId != null) {
            bookingIdField.setText(String.valueOf(bookingId));
            bookingIdField.setDisable(true);
        } else if (seatId != null) {
            Booking booking = bookingDAO.getBookingBySeatId(seatId);
            if (booking != null) {
                bookingIdField.setText(String.valueOf(booking.getID()));
                bookingIdField.setDisable(true);
            }
        }

        Label bookingIdLabel = createFieldLabel("Booking ID:");
        Label amountLabel = createFieldLabel("Amount:");
        Label methodLabel = createFieldLabel("Payment Method:");

        ComboBox<String> paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("Credit Card", "Bank Transfer");
        paymentMethodCombo.setValue("Credit Card");
        paymentMethodCombo.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        form.addRow(0, bookingIdLabel, bookingIdField);
        form.addRow(1, amountLabel, amountField);
        form.addRow(2, methodLabel, paymentMethodCombo);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setHgrow(Priority.NEVER);
        labelColumn.setMinWidth(120);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        fieldColumn.setFillWidth(true);

        form.getColumnConstraints().addAll(labelColumn, fieldColumn);

        Button processBtn = createStyledButton("Confirm Payment", "#4CAF50", 40);
        processBtn.setPrefWidth(200);

        Button returnBtn = createStyledButton("Return to Login Page", "#757575", 40);
        returnBtn.setPrefWidth(200);
        returnBtn.setOnAction(e -> {
            try {
                // Clean up unpaid booking and seat reservation
                if (bookingId != null) {
                    Booking booking = bookingDAO.getBookingByID(bookingId);
                    if (booking != null && !booking.isPaid()) {
                        // Delete the unpaid booking
                        bookingDAO.deleteBooking(bookingId);
                        // Remove seat reservation if exists
                        if (seatId != null) {
                            seatDAO.updateSeatStatus(seatId, false);
                        }
                    }
                }

                new LoginView().start(new Stage());
                ((Stage) returnBtn.getScene().getWindow()).close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        processBtn.setOnAction(e -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText());
                double amount = Double.parseDouble(amountField.getText());
                String paymentMethod = paymentMethodCombo.getValue();

                // First check if booking exists
                Booking booking = bookingDAO.getBookingByID(bookingId);
                if (booking == null) {
                    showStatusMessage("Error: Booking ID " + bookingId + " not found!", false);
                    return;
                }

                // Check if booking is already paid
                if (booking.isPaid()) {
                    showStatusMessage("Error: This booking is already paid!", false);
                    return;
                }

                // Process payment
                boolean success = paymentController.processPayment(bookingId, amount, paymentMethod);

                if (success) {
                    showStatusMessage("Payment processed successfully!", true);
                    // Update booking status to paid
                    booking.setPaid(true);
                    bookingDAO.updateBooking(booking);

                    // Now mark seat as booked
                    if (seatId != null) {
                        seatDAO.updateSeatStatus(seatId, true);
                    }

                    displayTicket(bookingId);
                } else {
                    showStatusMessage("Payment failed. Please try again.", false);
                    // Clean up failed payment
                    bookingDAO.deleteBooking(bookingId);
                    if (seatId != null) {
                        seatDAO.updateSeatStatus(seatId, false);
                    }
                }
            } catch (NumberFormatException ex) {
                showStatusMessage("Invalid input. Please enter a valid booking ID.", false);
            }
        });

        HBox buttonBox = new HBox(20, processBtn, returnBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        layout.getChildren().addAll(form, buttonBox);
        return layout;
    }

    private void displayTicket(int bookingId) {
        // Fetch booking details from database
        Booking booking = bookingDAO.getBookingByID(bookingId);
        if (booking == null) {
            showStatusMessage("Error: Booking information not found!", false);
            return;
        }

        // Fetch user details
        User user = currentUser;
        if (user == null) {
            showStatusMessage("Error: User information not found!", false);
            return;
        }

        // Fetch seat details
        Seat seat = seatDAO.getSeatByID(seatId != null ? seatId : findSeatIdForBooking(bookingId));
        if (seat == null) {
            showStatusMessage("Error: Seat information not found!", false);
            return;
        }

        // Fetch showtime details
        Showtime showtime = showtimeDAO.getShowtimeByID(booking.getShowtimeID());
        if (showtime == null) {
            showStatusMessage("Error: Showtime information not found!", false);
            return;
        }

        // Fetch movie details
        Movie movie = movieDAO.getMovieByID(booking.getMovieID());
        if (movie == null) {
            showStatusMessage("Error: Movie information not found!", false);
            return;
        }

        // Create ticket dialog
        Dialog<Void> ticketDialog = new Dialog<>();
        ticketDialog.setTitle("Your Movie Ticket");
        ticketDialog.setHeaderText("Booking Confirmation #" + bookingId);
        ticketDialog.setResizable(true);

        // Create ticket content
        VBox ticketContent = new VBox(15);
        ticketContent.setPadding(new Insets(20));
        ticketContent.setStyle("-fx-background-color: white;");

        // Format showtime date
        String formattedShowtime = showtime.getDateTime().format(dateFormatter);

        // Create ticket sections
        VBox userSection = createTicketSection("CUSTOMER INFORMATION");
        userSection.getChildren().addAll(
                createTicketField("Name:", user.getName()),
                createTicketField("Email:", user.getEmail())
        );

        VBox movieSection = createTicketSection("MOVIE INFORMATION");
        movieSection.getChildren().addAll(
                createTicketField("Title:", movie.getTitle()),
                createTicketField("Genre:", movie.getGenre()),
                createTicketField("Duration:", movie.getDuration() + " minutes")
        );

        VBox showtimeSection = createTicketSection("SHOWTIME INFORMATION");
        showtimeSection.getChildren().addAll(
                createTicketField("Date/Time:", formattedShowtime),
                createTicketField("Hall:", showtime.getHall()),
                createTicketField("Seat:", seat.getSeatNumber())
        );

        VBox bookingSection = createTicketSection("BOOKING DETAILS");
        bookingSection.getChildren().addAll(
                createTicketField("Booking ID:", String.valueOf(bookingId)),
                createTicketField("Amount Paid:", "$10.00")
        );

        Label thanksLabel = new Label("Thank you for your purchase!");
        thanksLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        thanksLabel.setTextFill(Color.valueOf("#4CAF50"));
        thanksLabel.setAlignment(Pos.CENTER);
        thanksLabel.setMaxWidth(Double.MAX_VALUE);

        ticketContent.getChildren().addAll(
                userSection,
                movieSection,
                showtimeSection,
                bookingSection,
                thanksLabel
        );

        // Set dialog content
        ticketDialog.getDialogPane().setContent(ticketContent);
        ticketDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        ticketDialog.getDialogPane().setStyle("-fx-background-color: white;");

        // Show the dialog
        ticketDialog.showAndWait();
    }

    private VBox createTicketSection(String title) {
        VBox section = new VBox(8);
        section.setPadding(new Insets(0, 0, 10, 0));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.valueOf("#303F9F"));
        titleLabel.setUnderline(true);

        section.getChildren().add(titleLabel);
        return section;
    }

    private HBox createTicketField(String label, String value) {
        HBox field = new HBox(5);

        Label fieldLabel = new Label(label);
        fieldLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        fieldLabel.setTextFill(Color.valueOf("#424242"));
        fieldLabel.setMinWidth(100);

        Label fieldValue = new Label(value);
        fieldValue.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        field.getChildren().addAll(fieldLabel, fieldValue);
        return field;
    }

    private int findSeatIdForBooking(int bookingId) {
        Booking booking = bookingDAO.getBookingByID(bookingId);
        if (booking != null) {
            List<Seat> seats = seatDAO.getSeatsByShowtime(booking.getShowtimeID());
            if (!seats.isEmpty()) {
                return seats.get(0).getId();
            }
        }
        return -1;
    }

    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(35);
        field.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 5 10;");
        return field;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.valueOf("#424242"));
        return label;
    }

    private Button createStyledButton(String text, String color, int height) {
        Button button = new Button(text);
        button.setPrefHeight(height);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;");
        button.setCursor(javafx.scene.Cursor.HAND);

        if ("#4CAF50".equals(color)) {
            button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-background-radius: 20;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;"));
        } else {
            button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-background-radius: 20;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;"));
        }

        return button;
    }

    private void showStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isSuccess ? Color.valueOf("#00c853") : Color.valueOf("#d50000"));

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), statusLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), statusLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(3));
        fadeOut.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
