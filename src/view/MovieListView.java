package view;

import controller.MovieController;
import controller.ShowtimeController;
import dao.CascadeDeleteHelper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import model.Movie;
import model.Showtime;
import model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MovieListView extends Application {

    private MovieController movieController = new MovieController();
    private ShowtimeController showtimeController = new ShowtimeController();
    private ObservableList<String> movieTitles = FXCollections.observableArrayList();
    private TextField titleField, genreField, durationField;
    private TextArea synopsisArea;
    private Label statusLabel;
    private User currentUser;

    public MovieListView(User user) {
        this.currentUser = user;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Clean up past showtimes before showing the UI
            cleanupPastShowtimes();

            // Main container with improved gradient background
            primaryStage.setMaximized(true);
            StackPane rootContainer = new StackPane();

            // Create a background pane with gradient
            Pane backgroundPane = new Pane();
            backgroundPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a237e, #4527a0, #7b1fa2); -fx-background-radius: 0;");

            // Create a decorative pattern overlay (optional)
            GridPane patternOverlay = new GridPane();
            patternOverlay.setHgap(30);
            patternOverlay.setVgap(30);
            patternOverlay.setOpacity(0.05);

            // Add the components to the root container
            rootContainer.getChildren().addAll(backgroundPane, patternOverlay);

            // Main content container - now centered
            BorderPane mainContainer = new BorderPane();
            mainContainer.setPickOnBounds(false);

            // Card-like center panel with enhanced transparency
            VBox centerCard = new VBox(25);
            centerCard.setAlignment(Pos.TOP_CENTER);
            centerCard.setPadding(new Insets(40, 50, 40, 50));
            centerCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 20;");
            centerCard.setMaxWidth(1000);
            centerCard.setMinHeight(700);

            // Add enhanced drop shadow effect to the card
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(20);
            dropShadow.setSpread(0.05);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(10);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
            centerCard.setEffect(dropShadow);

            // Header with title and app icon
            HBox header = new HBox(15);
            header.setAlignment(Pos.CENTER);

            Label appIcon = new Label("🎬");
            appIcon.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            appIcon.setTextFill(Color.valueOf("#6200ea"));

            Label titleLabel = new Label("Movie List");
            titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));
            titleLabel.setTextFill(Color.valueOf("#303f9f"));

            header.getChildren().addAll(appIcon, titleLabel);

            // Create a split pane for movie list and details
            SplitPane splitPane = new SplitPane();
            splitPane.setStyle("-fx-background-color: transparent; -fx-box-border: transparent;");

            // --- Left side: Movie List with search ---
            VBox leftPanel = new VBox(15);
            leftPanel.setPadding(new Insets(20));
            leftPanel.setStyle("-fx-background-color: rgba(248, 249, 250, 0.9); -fx-background-radius: 15;");

            Label moviesHeader = new Label("Available Movies");
            moviesHeader.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
            moviesHeader.setTextFill(Color.valueOf("#303f9f"));

            // Search functionality with styled components
            HBox searchBox = new HBox(10);
            searchBox.setAlignment(Pos.CENTER_LEFT);

            // Search input container
            HBox searchContainer = new HBox(10);
            searchContainer.setAlignment(Pos.CENTER_LEFT);
            searchContainer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 20; -fx-padding: 2 15 2 15;");

            // Search icon
            Label searchIcon = new Label("🔍");
            searchIcon.setFont(Font.font("Arial", 16));
            searchIcon.setTextFill(Color.valueOf("#757575"));

            TextField searchField = new TextField();
            searchField.setPromptText("Search movies...");
            searchField.setPrefHeight(35);
            searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 14px;");
            HBox.setHgrow(searchField, Priority.ALWAYS);

            searchContainer.getChildren().addAll(searchIcon, searchField);

            Button searchButton = createStyledButton("Search", "#6200ea", 35);
            Button clearSearchButton = createStyledButton("Clear", "#9e9e9e", 35);

            searchBox.getChildren().addAll(searchContainer, searchButton, clearSearchButton);

            // Movie list with custom styling
            ListView<String> movieListView = new ListView<>();
            movieListView.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #e0e0e0;");
            movieListView.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(item);
                        setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));
                        setStyle("-fx-background-color: white; -fx-background-radius: 0; -fx-padding: 10 15;");
                        setOnMouseEntered(e -> setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10 15;"));
                        setOnMouseExited(e -> setStyle("-fx-background-color: white; -fx-padding: 10 15;"));
                    }
                }
            });
            refreshMovieList(movieListView);
            VBox.setVgrow(movieListView, Priority.ALWAYS);

            leftPanel.getChildren().addAll(moviesHeader, searchBox, movieListView);

            // --- Right side: Movie Details Panel ---
            VBox rightPanel = new VBox(15);
            rightPanel.setPadding(new Insets(20));
            rightPanel.setStyle("-fx-background-color: rgba(248, 249, 250, 0.9); -fx-background-radius: 15;");

            Label detailsHeader = new Label("Movie Details");
            detailsHeader.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
            detailsHeader.setTextFill(Color.valueOf("#303f9f"));

            // Create and add details panel
            GridPane detailsPanel = createDetailsPanel(movieListView);
            VBox.setVgrow(detailsPanel, Priority.ALWAYS);

            // Status label for operation feedback
            statusLabel = new Label();
            statusLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));
            statusLabel.setOpacity(0);
            statusLabel.setMaxWidth(Double.MAX_VALUE);
            statusLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            // Show Showtimes button
            Button showShowtimesButton = createStyledButton("Show Showtimes", "#6200ea", 45);
            showShowtimesButton.setMaxWidth(Double.MAX_VALUE);
            showShowtimesButton.setOnAction(e -> {
                String selectedTitle = movieListView.getSelectionModel().getSelectedItem();
                if (selectedTitle != null) {
                    Movie selectedMovie = movieController.getMovieByTitle(selectedTitle);
                    if (selectedMovie != null) {
                        openShowtimeSelectionView(primaryStage, selectedMovie);
                    }
                } else {
                    showStatusMessage("Please select a movie first", false);
                }
            });

            rightPanel.getChildren().addAll(detailsHeader, detailsPanel, statusLabel, showShowtimesButton);

            // Add panels to split pane
            splitPane.getItems().addAll(leftPanel, rightPanel);
            splitPane.setDividerPositions(0.4);
            VBox.setVgrow(splitPane, Priority.ALWAYS);

            // Add all components to center card
            centerCard.getChildren().addAll(header, splitPane);

            // Center the card in the main container
            mainContainer.setCenter(centerCard);

            // Add all components to root container
            rootContainer.getChildren().add(mainContainer);

            // Set up button actions
            setupButtonActions(movieListView, searchField, searchButton, clearSearchButton);

            Scene scene = new Scene(rootContainer, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("MovieBooking - Movie List");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Add fade-in animation for the entire view
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootContainer);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing movie list view: " + e.getMessage());
        }
    }

    private void cleanupPastShowtimes() {
        LocalDate today = LocalDate.now();
        List<Showtime> allShowtimes = showtimeController.getAllShowtimes();

        for (Showtime showtime : allShowtimes) {
            if (showtime.getDateTime().toLocalDate().isBefore(today)) {
                CascadeDeleteHelper.safeDeleteShowtime(showtime.getId());
            }
        }
    }

    public void refreshMovieList(ListView<String> movieListView) {
        // Get only movies that have showtimes
        List<Movie> moviesWithShowtimes = movieController.getAllMovies().stream()
                .filter(movie -> !showtimeController.getShowtimesByMovie(movie.getID()).isEmpty())
                .collect(Collectors.toList());

        movieTitles.setAll(moviesWithShowtimes.stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList()));
        movieListView.setItems(movieTitles);
    }

    private void openShowtimeSelectionView(Stage primaryStage, Movie movie) {
        try {
            ShowtimeSelectionView showtimeView = new ShowtimeSelectionView(movie, currentUser);
            Stage stage = new Stage();
            showtimeView.start(stage);
            primaryStage.close();
        } catch (Exception e) {
            System.err.println("Failed to open showtime selection view: " + e.getMessage());
        }
    }

    private Button createStyledButton(String text, String color, int height) {
        Button button = new Button(text);
        button.setPrefHeight(height);
        button.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 25; -fx-cursor: hand;");

        // Enhanced hover effects with smooth transitions
        button.setOnMouseEntered(e -> {
            if (color.equals("#6200ea")) {
                button.setStyle("-fx-background-color: #7c4dff; -fx-text-fill: white; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
            } else if (color.equals("#9e9e9e")) {
                button.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
            }
            button.setCursor(Cursor.HAND);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 25;");
        });

        button.setOnMousePressed(e -> {
            if (color.equals("#6200ea")) {
                button.setStyle("-fx-background-color: #5600e8; -fx-text-fill: white; -fx-background-radius: 25;");
            } else if (color.equals("#9e9e9e")) {
                button.setStyle("-fx-background-color: #8e8e8e; -fx-text-fill: white; -fx-background-radius: 25;");
            }
        });

        button.setOnMouseReleased(e -> {
            if (color.equals("#6200ea")) {
                button.setStyle("-fx-background-color: #7c4dff; -fx-text-fill: white; -fx-background-radius: 25;");
            } else if (color.equals("#9e9e9e")) {
                button.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: white; -fx-background-radius: 25;");
            }
        });

        return button;
    }

    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(40);
        field.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-padding: 10; -fx-font-family: 'Montserrat';");
        return field;
    }

    private GridPane createDetailsPanel(ListView<String> movieListView) {
        GridPane detailsPanel = new GridPane();
        detailsPanel.setHgap(15);
        detailsPanel.setVgap(15);
        detailsPanel.setPadding(new Insets(15));
        detailsPanel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 15;");

        // Create styled detail fields
        titleField = createStyledTextField("Movie title");
        titleField.setEditable(false);
        genreField = createStyledTextField("Genre (e.g., Action, Comedy)");
        genreField.setEditable(false);
        durationField = createStyledTextField("Duration in minutes");
        durationField.setEditable(false);

        synopsisArea = new TextArea();
        synopsisArea.setPromptText("Movie synopsis or description");
        synopsisArea.setPrefRowCount(5);
        synopsisArea.setWrapText(true);
        synopsisArea.setEditable(false);
        synopsisArea.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-padding: 10; -fx-font-family: 'Montserrat';");

        // Style the labels
        Label titleLabel = createFieldLabel("Title:");
        Label genreLabel = createFieldLabel("Genre:");
        Label durationLabel = createFieldLabel("Duration:");
        Label synopsisLabel = createFieldLabel("Synopsis:");

        // Add fields to grid
        detailsPanel.add(titleLabel, 0, 0);
        detailsPanel.add(titleField, 1, 0);
        detailsPanel.add(genreLabel, 0, 1);
        detailsPanel.add(genreField, 1, 1);
        detailsPanel.add(durationLabel, 0, 2);
        detailsPanel.add(durationField, 1, 2);
        detailsPanel.add(synopsisLabel, 0, 3);
        detailsPanel.add(synopsisArea, 1, 3);

        // Set column constraints
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setHgrow(Priority.NEVER);
        labelColumn.setMinWidth(100);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        fieldColumn.setFillWidth(true);

        detailsPanel.getColumnConstraints().addAll(labelColumn, fieldColumn);

        // Initialize row constraints for all rows first
        for (int i = 0; i < 4; i++) {
            detailsPanel.getRowConstraints().add(new RowConstraints());
        }

        // Then modify the synopsis row to take more space
        RowConstraints synopsisRow = new RowConstraints();
        synopsisRow.setVgrow(Priority.ALWAYS);
        detailsPanel.getRowConstraints().set(3, synopsisRow);

        // Update details when movie is selected
        movieListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Movie selectedMovie = movieController.getMovieByTitle(newVal);
                if (selectedMovie != null) {
                    titleField.setText(selectedMovie.getTitle());
                    genreField.setText(selectedMovie.getGenre());
                    durationField.setText(String.valueOf(selectedMovie.getDuration()));
                    synopsisArea.setText(selectedMovie.getSynopsis());
                    highlightPanel(detailsPanel);
                }
            }
        });

        return detailsPanel;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.valueOf("#424242"));
        return label;
    }

    private void highlightPanel(GridPane panel) {
        String originalStyle = panel.getStyle();
        panel.setStyle(originalStyle + "-fx-background-color: #f0f8ff; -fx-effect: dropshadow(gaussian, rgba(98,0,234,0.2), 10, 0, 0, 0);");

        // Reset after brief highlight
        new Thread(() -> {
            try {
                Thread.sleep(300);
                javafx.application.Platform.runLater(() -> {
                    panel.setStyle(originalStyle);
                });
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void setupButtonActions(ListView<String> movieListView, TextField searchField,
                                    Button searchButton, Button clearSearchButton) {
        // Search button action
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<Movie> foundMovies = movieController.getAllMovies().stream()
                        .filter(movie -> movie.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                        .filter(movie -> !showtimeController.getShowtimesByMovie(movie.getID()).isEmpty())
                        .collect(Collectors.toList());

                if (foundMovies.isEmpty()) {
                    movieTitles.clear();
                    movieListView.setItems(movieTitles);
                    showStatusMessage("No movies found matching \"" + searchTerm + "\"", false);
                } else {
                    movieTitles.setAll(foundMovies.stream()
                            .map(Movie::getTitle)
                            .collect(Collectors.toList()));
                    movieListView.setItems(movieTitles);
                    showStatusMessage("Found " + foundMovies.size() + " movie(s)", true);
                }
            } else {
                refreshMovieList(movieListView);
            }
        });

        // Clear search button action
        clearSearchButton.setOnAction(e -> {
            searchField.clear();
            refreshMovieList(movieListView);
            showStatusMessage("Search cleared", true);
        });

        // Enable search when pressing Enter in search field
        searchField.setOnAction(e -> searchButton.fire());
    }

    private void showStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isSuccess ? Color.valueOf("#00c853") : Color.valueOf("#d50000"));

        // Enhanced animation with bounce effect
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), statusLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition translateUp = new TranslateTransition(Duration.millis(200), statusLabel);
        translateUp.setFromY(10);
        translateUp.setToY(0);

        fadeIn.play();
        translateUp.play();

        // Fade out after delay if success
        if (isSuccess) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), statusLabel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.seconds(3));
            fadeOut.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
