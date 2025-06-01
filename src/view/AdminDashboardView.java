package view;

import controller.AdminController;
import dao.UserDAO;
import javafx.application.Application;
import javafx.collections.FXCollections;
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
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.Movie;
import model.Showtime;
import model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardView extends Application {
    private AdminController adminController;
    private ListView<Movie> movieListView;
    private ListView<Showtime> showtimeListView;
    private Label statusLabel;
    private ComboBox<Movie> movieCombo;
    private ListView<User> userListView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMaximized(true);
        adminController = new AdminController();

        // Main layout with gradient background
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a237e, #4a148c);");

        // Content container (white card with shadow)
        VBox contentCard = new VBox(20);
        contentCard.setPadding(new Insets(30));
        contentCard.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        contentCard.setMaxWidth(1000);
        contentCard.setMaxHeight(800);

        // Add drop shadow effect to the content card
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(15);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        contentCard.setEffect(dropShadow);

        // Header with title and app icon
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label appIcon = new Label("⚙️");
        appIcon.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        appIcon.setTextFill(Color.valueOf("#6200ea"));

        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.valueOf("#303f9f"));

        // Add logout button to header
        Button logoutButton = createStyledButton("Back to Login", "#757575", 30);
        logoutButton.setOnAction(e -> {
            try {
                new LoginView().start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox headerRight = new HBox(logoutButton);
        headerRight.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(headerRight, Priority.ALWAYS);

        header.getChildren().addAll(appIcon, titleLabel, headerRight);

        // Status label for operation feedback
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statusLabel.setOpacity(0);

        // Create styled tab pane
        TabPane tabPane = createStyledTabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Add components to content card
        contentCard.getChildren().addAll(header, tabPane, statusLabel);

        // Center the content card in the main layout
        mainLayout.setCenter(contentCard);
        BorderPane.setAlignment(contentCard, Pos.CENTER);
        BorderPane.setMargin(contentCard, new Insets(30));

        // Set the scene
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    private TabPane createStyledTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent; -fx-tab-min-width: 150px;");

        // Create tabs with custom styling
        Tab movieTab = createStyledTab("Manage Movies", "🎬", createMovieTab());
        Tab showtimeTab = createStyledTab("Manage Showtimes", "🕒", createShowtimeTab());
        Tab userTab = createStyledTab("Manage Users", "👥", createUserTab()); // New user tab

        tabPane.getTabs().addAll(movieTab, showtimeTab, userTab);

        return tabPane;
    }

    private Tab createStyledTab(String title, String icon, Pane content) {
        Tab tab = new Tab();

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 16));

        Label titleLabel = new Label(" " + title);
        titleLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

        HBox tabBox = new HBox(5, iconLabel, titleLabel);
        tabBox.setAlignment(Pos.CENTER_LEFT);

        tab.setGraphic(tabBox);
        tab.setContent(content);

        return tab;
    }

    private VBox createUserTab() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        // Title for the user list section
        Label listTitle = createSectionTitle("Registered Users");

        // User count label
        Label userCountLabel = new Label();
        userCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        userCountLabel.setTextFill(Color.valueOf("#303f9f"));

        // Add refresh button next to the title
        Button refreshButton = createStyledButton("Refresh", "#2196F3", 30);
        refreshButton.setOnAction(e -> refreshUserList(userCountLabel));

        HBox titleBox = new HBox(10, listTitle, refreshButton, userCountLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // User List with custom styling
        userListView = new ListView<>();
        userListView.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e0e0e0;");
        styleUserListView();
        refreshUserList(userCountLabel); // Initial load
        VBox.setVgrow(userListView, Priority.ALWAYS);

        // Search functionality
        TextField searchField = createStyledTextField("Search by name or email");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUserList(newValue);
        });

        // Buttons with styling
        Button deleteButton = createStyledButton("Delete Selected User", "#d32f2f", 40);
        deleteButton.setOnAction(e -> {
            User selected = userListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirmDialog = createConfirmDialog(
                        "Confirm Delete",
                        "Are you sure you want to delete user \"" + selected.getName() + "\" (" + selected.getEmail() + ")?"
                );

                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            UserDAO userDAO = new UserDAO();
                            if (userDAO.deleteUser(selected.getID())) {
                                refreshUserList(userCountLabel);
                                showStatusMessage("User deleted successfully", true);
                            } else {
                                showStatusMessage("Failed to delete user", false);
                            }
                            userDAO.close();
                        } catch (SQLException ex) {
                            showStatusMessage("Database error: " + ex.getMessage(), false);
                        }
                    }
                });
            } else {
                showStatusMessage("Please select a user to delete", false);
            }
        });

        HBox buttonBox = new HBox(15, deleteButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Layout
        layout.getChildren().addAll(titleBox, searchField, userListView, buttonBox);

        return layout;
    }

    private VBox createMovieTab() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        // Title for the movie list section
        Label listTitle = createSectionTitle("Current Movies");

        // Add refresh button next to the title
        Button refreshButton = createStyledButton("Refresh", "#2196F3", 30);
        refreshButton.setOnAction(e -> refreshMovieList());

        HBox titleBox = new HBox(10, listTitle, refreshButton);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Movie List with custom styling
        movieListView = new ListView<>();
        movieListView.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e0e0e0;");
        styleMovieListView();
        refreshMovieList();
        VBox.setVgrow(movieListView, Priority.ALWAYS);

        // Create a card for the add movie form
        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(20));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");

        Label formTitle = createSectionTitle("Add New Movie");

        // Form Fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);

        TextField titleField = createStyledTextField("Enter movie title");
        TextField genreField = createStyledTextField("Enter genre (e.g., Action, Comedy)");
        TextField durationField = createStyledTextField("Enter duration in minutes");

        TextArea synopsisArea = new TextArea();
        synopsisArea.setPromptText("Enter movie synopsis or description");
        synopsisArea.setPrefRowCount(3);
        synopsisArea.setWrapText(true);
        synopsisArea.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 5;");

        // Add fields to grid
        formGrid.add(createFieldLabel("Title:"), 0, 0);
        formGrid.add(titleField, 1, 0);
        formGrid.add(createFieldLabel("Genre:"), 0, 1);
        formGrid.add(genreField, 1, 1);
        formGrid.add(createFieldLabel("Duration:"), 0, 2);
        formGrid.add(durationField, 1, 2);
        formGrid.add(createFieldLabel("Synopsis:"), 0, 3);
        formGrid.add(synopsisArea, 1, 3);

        // Set column constraints
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setHgrow(Priority.NEVER);
        labelColumn.setMinWidth(80);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        fieldColumn.setFillWidth(true);

        formGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        // Buttons with styling
        Button addButton = createStyledButton("Add Movie", "#6200ea", 40);
        Button clearButton = createStyledButton("Clear Form", "#9e9e9e", 40);
        Button deleteButton = createStyledButton("Delete Selected", "#d32f2f", 40);

        HBox buttonBox = new HBox(15, addButton, clearButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Button Actions
        addButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String genre = genreField.getText().trim();
                String durationText = durationField.getText().trim();
                String synopsis = synopsisArea.getText().trim();

                if (title.isEmpty() || genre.isEmpty() || durationText.isEmpty()) {
                    showStatusMessage("Please fill all required fields", false);
                    return;
                }

                int duration = Integer.parseInt(durationText);

                if (adminController.addMovie(title, genre, duration, synopsis)) {
                    refreshMovieList();
                    refreshMovieCombo(); // Refresh the combo box in showtime tab
                    clearFields(titleField, genreField, durationField, synopsisArea);
                    showStatusMessage("Movie added successfully", true);
                } else {
                    showStatusMessage("Failed to add movie", false);
                }
            } catch (NumberFormatException ex) {
                showStatusMessage("Duration must be a valid number", false);
            }
        });

        clearButton.setOnAction(e -> {
            clearFields(titleField, genreField, durationField, synopsisArea);
            showStatusMessage("Form cleared", true);
        });

        deleteButton.setOnAction(e -> {
            Movie selected = movieListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirmDialog = createConfirmDialog(
                        "Confirm Delete",
                        "Are you sure you want to delete the movie \"" + selected.getTitle() + "\"?"
                );

                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (adminController.deleteMovie(selected.getID())) {
                            refreshMovieList();
                            refreshMovieCombo(); // Refresh the combo box in showtime tab
                            showStatusMessage("Movie deleted successfully", true);
                        } else {
                            showStatusMessage("Failed to delete movie", false);
                        }
                    }
                });
            } else {
                showStatusMessage("Please select a movie to delete", false);
            }
        });

        // Add all elements to the form card
        formCard.getChildren().addAll(formTitle, formGrid, buttonBox);

        // Layout
        layout.getChildren().addAll(titleBox, movieListView, formCard);

        return layout;
    }

    private void styleUserListView() {
        userListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Create a custom cell with better formatting
                    VBox cellContent = new VBox(5);
                    cellContent.setPadding(new Insets(8, 5, 8, 5));

                    Label nameLabel = new Label(user.getName());
                    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    nameLabel.setTextFill(Color.valueOf("#303f9f"));

                    Label emailLabel = new Label(user.getEmail());
                    emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

                    Label roleLabel = new Label("Role: " + user.getUserRole().name());
                    roleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                    roleLabel.setTextFill(Color.valueOf("#757575"));

                    cellContent.getChildren().addAll(nameLabel, emailLabel, roleLabel);

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
    }

    private void refreshUserList(Label countLabel) {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.getAllUsers();
            userDAO.close();

            userListView.getItems().setAll(users);
            countLabel.setText("Total: " + users.size() + " users");
        } catch (SQLException e) {
            showStatusMessage("Error loading users: " + e.getMessage(), false);
        }
    }

    private void filterUserList(String searchText) {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> allUsers = userDAO.getAllUsers();
            userDAO.close();

            if (searchText == null || searchText.isEmpty()) {
                userListView.getItems().setAll(allUsers);
            } else {
                String searchLower = searchText.toLowerCase();
                List<User> filtered = allUsers.stream()
                        .filter(user -> user.getName().toLowerCase().contains(searchLower) ||
                                user.getEmail().toLowerCase().contains(searchLower))
                        .toList();
                userListView.getItems().setAll(filtered);
            }
        } catch (SQLException e) {
            showStatusMessage("Error filtering users: " + e.getMessage(), false);
        }
    }

    private VBox createShowtimeTab() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        // Title for the showtime list section
        Label listTitle = createSectionTitle("Current Showtimes");

        // Add refresh button next to the title
        Button refreshButton = createStyledButton("Refresh", "#2196F3", 30);
        refreshButton.setOnAction(e -> refreshShowtimeList());

        HBox titleBox = new HBox(10, listTitle, refreshButton);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Showtime List with custom styling
        showtimeListView = new ListView<>();
        showtimeListView.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e0e0e0;");
        styleShowtimeListView();
        refreshShowtimeList();
        VBox.setVgrow(showtimeListView, Priority.ALWAYS);

        // Create a card for the add showtime form
        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(20));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");

        Label formTitle = createSectionTitle("Add New Showtime");

        // Form Fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);

        // Movie Selection
        movieCombo = new ComboBox<>();
        refreshMovieCombo(); // Initialize with current movies
        movieCombo.setConverter(new StringConverter<Movie>() {
            @Override
            public String toString(Movie movie) {
                return movie != null ? movie.getTitle() : "";
            }

            @Override
            public Movie fromString(String string) {
                return null; // Not needed
            }
        });
        styleComboBox(movieCombo);

        // Date and Time Fields
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        // Prevent selecting past dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        styleDatePicker(datePicker);

        TextField timeField = createStyledTextField("HH:MM (e.g., 18:30)");
        TextField hallField = createStyledTextField("Hall number");

        // Add fields to grid
        formGrid.add(createFieldLabel("Movie:"), 0, 0);
        formGrid.add(movieCombo, 1, 0);
        formGrid.add(createFieldLabel("Date:"), 0, 1);
        formGrid.add(datePicker, 1, 1);
        formGrid.add(createFieldLabel("Time:"), 0, 2);
        formGrid.add(timeField, 1, 2);
        formGrid.add(createFieldLabel("Hall:"), 0, 3);
        formGrid.add(hallField, 1, 3);

        // Set column constraints
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setHgrow(Priority.NEVER);
        labelColumn.setMinWidth(80);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        fieldColumn.setFillWidth(true);

        formGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        // Buttons with styling
        Button addButton = createStyledButton("Add Showtime", "#6200ea", 40);
        Button clearButton = createStyledButton("Clear Form", "#9e9e9e", 40);
        Button deleteButton = createStyledButton("Delete Selected", "#d32f2f", 40);

        HBox buttonBox = new HBox(15, addButton, clearButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Button Actions
        addButton.setOnAction(e -> {
            Movie selectedMovie = movieCombo.getValue();
            LocalDate date = datePicker.getValue();
            String time = timeField.getText().trim();
            String hall = hallField.getText().trim();

            if (selectedMovie == null || date == null || time.isEmpty() || hall.isEmpty()) {
                showStatusMessage("Please fill all required fields", false);
                return;
            }

            // Check if date is in the past (should be prevented by date picker, but double check)
            if (date.isBefore(LocalDate.now())) {
                showStatusMessage("Cannot create showtime for a past date", false);
                return;
            }

            // Validate time format
            if (!time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                showStatusMessage("Invalid time format. Use HH:MM (e.g., 14:30)", false);
                return;
            }

            // Check for overlapping showtimes
            String validationResult = adminController.validateShowtime(selectedMovie.getID(), date.toString(), time, hall);
            if (validationResult != null) {
                showStatusMessage(validationResult, false);
                return;
            }

            if (adminController.addShowtime(
                    selectedMovie.getID(),
                    date.toString(),
                    time,
                    hall
            )) {
                refreshShowtimeList();
                clearFields(timeField, hallField);
                showStatusMessage("Showtime added successfully", true);
            } else {
                showStatusMessage("Failed to add showtime", false);
            }
        });

        clearButton.setOnAction(e -> {
            movieCombo.setValue(null);
            datePicker.setValue(LocalDate.now());
            timeField.clear();
            hallField.clear();
            showStatusMessage("Form cleared", true);
        });

        deleteButton.setOnAction(e -> {
            Showtime selected = showtimeListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirmDialog = createConfirmDialog(
                        "Confirm Delete",
                        "Are you sure you want to delete this showtime?"
                );

                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (adminController.deleteShowtime(selected.getId())) {
                            refreshShowtimeList();
                            showStatusMessage("Showtime deleted successfully", true);
                        } else {
                            showStatusMessage("Failed to delete showtime", false);
                        }
                    }
                });
            } else {
                showStatusMessage("Please select a showtime to delete", false);
            }
        });

        // Add all elements to the form card
        formCard.getChildren().addAll(formTitle, formGrid, buttonBox);

        // Layout
        layout.getChildren().addAll(titleBox, showtimeListView, formCard);

        return layout;
    }

    // ================ Helper Methods ================

    private void refreshMovieCombo() {
        movieCombo.setItems(FXCollections.observableArrayList(adminController.getAllMovies()));
    }

    private void styleMovieListView() {
        movieListView.setCellFactory(param -> new ListCell<Movie>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);

                if (empty || movie == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Create a custom cell with better formatting
                    VBox cellContent = new VBox(5);
                    cellContent.setPadding(new Insets(8, 5, 8, 5));

                    Label titleLabel = new Label(movie.getTitle());
                    titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    titleLabel.setTextFill(Color.valueOf("#303f9f"));

                    Label detailsLabel = new Label(movie.getGenre() + " | " + movie.getDuration() + " min");
                    detailsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                    detailsLabel.setTextFill(Color.valueOf("#757575"));

                    cellContent.getChildren().addAll(titleLabel, detailsLabel);

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
    }

    private void styleShowtimeListView() {
        showtimeListView.setCellFactory(param -> new ListCell<Showtime>() {
            @Override
            protected void updateItem(Showtime showtime, boolean empty) {
                super.updateItem(showtime, empty);

                if (empty || showtime == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Create a custom cell with better formatting
                    VBox cellContent = new VBox(5);
                    cellContent.setPadding(new Insets(8, 5, 8, 5));

                    // Get movie title for this showtime
                    String movieTitle = "Unknown Movie";
                    for (Movie movie : adminController.getAllMovies()) {
                        if (movie.getID() == showtime.getMovieId()) {
                            movieTitle = movie.getTitle();
                            break;
                        }
                    }

                    Label movieLabel = new Label(movieTitle);
                    movieLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    movieLabel.setTextFill(Color.valueOf("#303f9f"));

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a");
                    Label datetimeLabel = new Label(showtime.getDateTime().format(formatter));
                    datetimeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

                    Label hallLabel = new Label("Hall: " + showtime.getHall());
                    hallLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                    hallLabel.setTextFill(Color.valueOf("#757575"));

                    cellContent.getChildren().addAll(movieLabel, datetimeLabel, hallLabel);

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
    }

    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(35);
        field.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 5 10;");
        return field;
    }

    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setPrefHeight(35);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
    }

    private void styleDatePicker(DatePicker datePicker) {
        datePicker.setPrefHeight(35);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-background-color: white; -fx-background-radius: 5; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.valueOf("#424242"));
        return label;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.valueOf("#303f9f"));
        return label;
    }

    private Button createStyledButton(String text, String color, int height) {
        Button button = new Button(text);
        button.setPrefHeight(height);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;");
        button.setCursor(Cursor.HAND);

        // Hover effect - lighten the button color
        button.setOnMouseEntered(e -> {
            if (color.equals("#6200ea")) {
                button.setStyle("-fx-background-color: #7c4dff; -fx-text-fill: white; -fx-background-radius: 20;");
            } else if (color.equals("#9e9e9e")) {
                button.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-background-radius: 20;");
            } else if (color.equals("#d32f2f")) {
                button.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-background-radius: 20;");
            } else if (color.equals("#2196F3")) {
                button.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-background-radius: 20;");
            } else if (color.equals("#757575")) {
                button.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-background-radius: 20;");
            }
        });

        // Return to original color on mouse exit
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;");
        });

        return button;
    }

    private Alert createConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the dialog pane
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        dialogPane.getStyleClass().add("modern-dialog");

        return alert;
    }

    private void showStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isSuccess ? Color.valueOf("#00c853") : Color.valueOf("#d50000"));

        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), statusLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Fade out after delay
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), statusLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(3));
        fadeOut.play();
    }

    private void refreshMovieList() {
        movieListView.getItems().setAll(adminController.getAllMovies());
    }

    private void refreshShowtimeList() {
        showtimeListView.getItems().setAll(adminController.getAllShowtimes());
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void clearFields(TextField field1, TextField field2, TextField field3, TextArea area) {
        field1.clear();
        field2.clear();
        field3.clear();
        area.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
