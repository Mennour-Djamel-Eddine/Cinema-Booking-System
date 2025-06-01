package view;

import controller.ShowtimeController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import model.Movie;
import model.Showtime;
import model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowtimeSelectionView extends Application {

    private final ShowtimeController showtimeController = new ShowtimeController();
    private Movie selectedMovie;
    private DatePicker datePicker;
    private ListView<Showtime> showtimeListView;
    private Label statusLabel;
    private User currentUser;

    public ShowtimeSelectionView(Movie movie, User user) {
        this.selectedMovie = movie;
        this.currentUser = user;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Showtime Selection");
        primaryStage.setMaximized(true);

        // Main layout with gradient background
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a237e, #4a148c);");

        // Content container (white card with shadow)
        VBox contentCard = new VBox(20);
        contentCard.setPadding(new Insets(30));
        contentCard.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        contentCard.setMaxWidth(900);
        contentCard.setMaxHeight(700);

        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(15);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        contentCard.setEffect(dropShadow);

        // Header with title and app icon
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label appIcon = new Label("🎟️");
        appIcon.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        appIcon.setTextFill(Color.valueOf("#6200ea"));

        Label titleLabel = new Label("Showtime Selection");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.valueOf("#303f9f"));

        header.getChildren().addAll(appIcon, titleLabel);

        // Create split pane for filter panel and showtime list
        SplitPane splitPane = new SplitPane();
        splitPane.setStyle("-fx-background-color: transparent; -fx-box-border: transparent;");

        // Left side: Filter Panel
        VBox leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        Label filterHeader = new Label("Filter Options");
        filterHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        filterHeader.setTextFill(Color.valueOf("#303f9f"));

        // Create filter panel
        VBox filterPanel = createFilterPanel();
        VBox.setVgrow(filterPanel, Priority.ALWAYS);

        leftPanel.getChildren().addAll(filterHeader, filterPanel);

        // Right side: Showtime List Panel
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        Label showtimesHeader = new Label("Available Showtimes");
        showtimesHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        showtimesHeader.setTextFill(Color.valueOf("#303f9f"));

        // Create showtimes list
        showtimeListView = createShowtimeListView();
        VBox.setVgrow(showtimeListView, Priority.ALWAYS);

        // Status label
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statusLabel.setOpacity(0);

        // Select button
        Button selectButton = createStyledButton("Select Showtime", "#6200ea", 40);
        selectButton.setOnAction(e -> handleShowtimeSelection(primaryStage));
        HBox buttonBox = new HBox(selectButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        rightPanel.getChildren().addAll(showtimesHeader, showtimeListView, statusLabel, buttonBox);

        // Add panels to split pane
        splitPane.getItems().addAll(leftPanel, rightPanel);
        splitPane.setDividerPositions(0.3);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        // Add all components to content card
        contentCard.getChildren().addAll(header, splitPane);

        // Center the content card
        mainLayout.setCenter(contentCard);
        BorderPane.setAlignment(contentCard, Pos.CENTER);
        BorderPane.setMargin(contentCard, new Insets(30));

        // Set the scene
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        // Load initial data
        if (selectedMovie != null) {
            filterShowtimes();
        }
    }

    public ShowtimeSelectionView(Movie movie) {
        this.selectedMovie = movie;
    }

    private VBox createFilterPanel() {
        VBox filterPane = new VBox(20);
        filterPane.setPadding(new Insets(10));

        // Movie display
        Label movieLabel = createFieldLabel("Selected Movie:");
        Label movieTitleLabel = new Label(selectedMovie != null ? selectedMovie.getTitle() : "No movie selected");
        movieTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        movieTitleLabel.setTextFill(Color.valueOf("#303f9f"));

        // Date selection
        Label dateLabel = createFieldLabel("Date:");
        datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setValue(LocalDate.now());
        styleDatePicker(datePicker);

        // Filter button
        Button filterButton = createStyledButton("Find Showtimes", "#6200ea", 40);
        filterButton.setMaxWidth(Double.MAX_VALUE);
        filterButton.setOnAction(e -> filterShowtimes());

        // Reset filter button
        Button resetButton = createStyledButton("Reset Filters", "#9e9e9e", 40);
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setOnAction(e -> resetFilters());

        // Add fields with spacing
        VBox movieBox = new VBox(5, movieLabel, movieTitleLabel);
        VBox dateBox = new VBox(5, dateLabel, datePicker);

        // Add spacer and buttons
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        filterPane.getChildren().addAll(movieBox, dateBox, spacer, filterButton, resetButton);
        return filterPane;
    }

    private void styleDatePicker(DatePicker datePicker) {
        datePicker.setPrefHeight(35);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-background-color: white; -fx-background-radius: 5; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
    }

    private ListView<Showtime> createShowtimeListView() {
        ListView<Showtime> listView = new ListView<>();
        listView.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e0e0e0;");

        listView.setCellFactory(param -> new ListCell<Showtime>() {
            @Override
            protected void updateItem(Showtime showtime, boolean empty) {
                super.updateItem(showtime, empty);

                if (empty || showtime == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    VBox cellContent = new VBox(5);
                    cellContent.setPadding(new Insets(8, 5, 8, 5));

                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

                    Label dateLabel = new Label(showtime.getDateTime().format(dateFormatter));
                    dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    dateLabel.setTextFill(Color.valueOf("#303f9f"));

                    Label timeLabel = new Label("Time: " + showtime.getDateTime().format(timeFormatter));
                    timeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

                    Label hallLabel = new Label("Hall: " + showtime.getHall());
                    hallLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

                    cellContent.getChildren().addAll(dateLabel, timeLabel, hallLabel);
                    setGraphic(cellContent);
                    setPrefHeight(USE_COMPUTED_SIZE);

                    // Alternate row colors
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #f9f9f9;");
                    } else {
                        setStyle("-fx-background-color: white;");
                    }
                }
            }
        });

        // Selection highlighting
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                highlightSelection();
            }
        });

        return listView;
    }

    private void handleShowtimeSelection(Stage currentStage) {
        Showtime selected = showtimeListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatusMessage("Please select a showtime first", false);
            return;
        }

        // Open seat selection view with user
        SeatView seatView = new SeatView(selected.getId(), currentUser);
        Stage seatStage = new Stage();
        seatView.start(seatStage);

        currentStage.close();
    }

    private void filterShowtimes() {
        if (selectedMovie == null) {
            showStatusMessage("No movie selected", false);
            return;
        }

        List<Showtime> showtimes = showtimeController.getShowtimesForMovieAndDate(
                selectedMovie.getID(),
                datePicker.getValue()
        );

        if (showtimes.isEmpty()) {
            showtimeListView.setItems(FXCollections.emptyObservableList());
            showStatusMessage("No showtimes available for this selection", false);
        } else {
            showtimeListView.setItems(FXCollections.observableArrayList(showtimes));
            showStatusMessage("Found " + showtimes.size() + " showtime(s)", true);
        }
    }

    private void resetFilters() {
        datePicker.setValue(LocalDate.now());
        if (selectedMovie != null) {
            filterShowtimes();
        } else {
            showtimeListView.setItems(FXCollections.emptyObservableList());
        }
        showStatusMessage("Filters reset", true);
    }

    private void highlightSelection() {
        FadeTransition fade = new FadeTransition(Duration.millis(150), showtimeListView);
        fade.setFromValue(0.9);
        fade.setToValue(1.0);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);
        fade.play();
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

    private Button createStyledButton(String text, String color, int height) {
        Button button = new Button(text);
        button.setPrefHeight(height);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;");
        button.setCursor(Cursor.HAND);

        // Hover effect
        button.setOnMouseEntered(e -> {
            if (color.equals("#6200ea")) {
                button.setStyle("-fx-background-color: #7c4dff; -fx-text-fill: white; -fx-background-radius: 20;");
            } else if (color.equals("#9e9e9e")) {
                button.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-background-radius: 20;");
            }
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;");
        });

        return button;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.valueOf("#424242"));
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
