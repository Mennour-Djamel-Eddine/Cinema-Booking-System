package view;

import controller.AuthController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.Cursor;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import model.User;
import model.UserRole;

public class LoginView extends Application {
    private final AuthController authController = new AuthController();
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setMaximized(true);
            StackPane rootContainer = new StackPane();

            // Create a background pane with gradient
            Pane backgroundPane = new Pane();
            backgroundPane.setStyle("-fx-background-color: linear-gradient(to bottom right," +
                    " #1a237e, #4527a0, #7b1fa2); -fx-background-radius: 0;");

            // Create a decorative pattern overlay
            GridPane patternOverlay = new GridPane();
            patternOverlay.setHgap(30);
            patternOverlay.setVgap(30);
            patternOverlay.setOpacity(0.05);

            // Add the components to the root container
            rootContainer.getChildren().addAll(backgroundPane, patternOverlay);

            // Main content container
            BorderPane mainContainer = new BorderPane();
            mainContainer.setPickOnBounds(false);

            // Left side content area (decorative)
            VBox leftContent = new VBox(20);
            leftContent.setAlignment(Pos.CENTER_LEFT);
            leftContent.setPadding(new Insets(0, 0, 0, 80));
            leftContent.setMaxWidth(500);

            // App name with drop shadow
            Label appNameLabel = new Label("MovieBooking");
            appNameLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 50));
            appNameLabel.setTextFill(Color.WHITE);

            DropShadow appNameShadow = new DropShadow();
            appNameShadow.setColor(Color.rgb(0, 0, 0, 0.5));
            appNameShadow.setOffsetX(2);
            appNameShadow.setOffsetY(2);
            appNameShadow.setRadius(5);
            appNameLabel.setEffect(appNameShadow);

            // Tagline
            Label taglineLabel = new Label("Book your favorite movies with ease");
            taglineLabel.setFont(Font.font("Montserrat", FontWeight.LIGHT, 20));
            taglineLabel.setTextFill(Color.WHITE);

            // Features list
            VBox featuresList = new VBox(15);
            featuresList.setPadding(new Insets(30, 0, 0, 10));

            String[] features = {
                    "✓ Browse latest movies",
                    "✓ Book tickets in seconds",
                    "✓ Choose your perfect seats",
                    "✓ Get exclusive offers"
            };

            for (String feature : features) {
                Label featureLabel = new Label(feature);
                featureLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 16));
                featureLabel.setTextFill(Color.WHITE);
                featuresList.getChildren().add(featureLabel);

                // Add animation for each feature item
                TranslateTransition tt = new TranslateTransition(Duration.millis(1000), featureLabel);
                tt.setFromX(-50);
                tt.setToX(0);
                tt.setDelay(Duration.millis(200 * featuresList.getChildren().size()));
                tt.play();
            }

            leftContent.getChildren().addAll(appNameLabel, taglineLabel, featuresList);
            mainContainer.setLeft(leftContent);

            // Card-like center panel with enhanced drop shadow
            VBox centerCard = new VBox(25);
            centerCard.setAlignment(Pos.CENTER);
            centerCard.setPadding(new Insets(50, 60, 50, 60));
            centerCard.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
            centerCard.setMaxWidth(450);
            centerCard.setMinHeight(600);

            // Add enhanced drop shadow effect to the card
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(20);
            dropShadow.setSpread(0.05);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(10);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
            centerCard.setEffect(dropShadow);

            // Logo as stylized text with emoji
            HBox logoBox = new HBox(10);
            logoBox.setAlignment(Pos.CENTER);

            Label logoLabel = new Label("🎬");
            logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 55));
            logoLabel.setTextFill(Color.valueOf("#6200ea"));

            logoBox.getChildren().add(logoLabel);
            centerCard.getChildren().add(logoBox);

            // Title with enhanced styling
            Label titleLabel = new Label("Welcome Back");
            titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));
            titleLabel.setTextFill(Color.valueOf("#303f9f"));
            titleLabel.setTextAlignment(TextAlignment.CENTER);
            centerCard.getChildren().add(titleLabel);

            // Subtitle with improved font
            Label subtitleLabel = new Label("Sign in to your account");
            subtitleLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 15));
            subtitleLabel.setTextFill(Color.valueOf("#757575"));
            centerCard.getChildren().add(subtitleLabel);

            // Spacer
            Region spacer = new Region();
            spacer.setPrefHeight(10);
            centerCard.getChildren().add(spacer);

            // Form grid with improved spacing
            GridPane formGrid = new GridPane();
            formGrid.setHgap(10);
            formGrid.setVgap(10);
            formGrid.setAlignment(Pos.CENTER);

            // Email field with icon and improved styling
            Label emailLabel = new Label("Email");
            emailLabel.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
            emailLabel.setTextFill(Color.valueOf("#424242"));

            // Email input container
            HBox emailContainer = new HBox(10);
            emailContainer.setAlignment(Pos.CENTER_LEFT);
            emailContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-padding: 2 12 2 12;");

            // Email icon (using unicode for simplicity)
            Label emailIcon = new Label("✉");
            emailIcon.setFont(Font.font("Arial", 16));
            emailIcon.setTextFill(Color.valueOf("#757575"));

            TextField emailField = new TextField();
            emailField.setPromptText("your@email.com");
            emailField.setPrefHeight(45);
            emailField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 14px;");
            HBox.setHgrow(emailField, Priority.ALWAYS);

            emailContainer.getChildren().addAll(emailIcon, emailField);

            formGrid.add(emailLabel, 0, 0);
            formGrid.add(emailContainer, 0, 1);

            // Password field with icon and improved styling
            Label pwLabel = new Label("Password");
            pwLabel.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
            pwLabel.setTextFill(Color.valueOf("#424242"));

            // Password input container
            HBox pwContainer = new HBox(10);
            pwContainer.setAlignment(Pos.CENTER_LEFT);
            pwContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-padding: 2 12 2 12;");

            // Password icon (using uniCode for simplicity)
            Label pwIcon = new Label("🔒");
            pwIcon.setFont(Font.font("Arial", 16));
            pwIcon.setTextFill(Color.valueOf("#757575"));

            PasswordField pwField = new PasswordField();
            pwField.setPromptText("Enter password");
            pwField.setPrefHeight(45);
            pwField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 14px;");
            HBox.setHgrow(pwField, Priority.ALWAYS);

            pwContainer.getChildren().addAll(pwIcon, pwField);

            formGrid.add(pwLabel, 0, 2);
            formGrid.add(pwContainer, 0, 3);

            // Remember me checkbox and Forgot password link
            HBox optionsBox = new HBox();
            optionsBox.setAlignment(Pos.CENTER);

            CheckBox rememberMe = new CheckBox("Remember me");
            rememberMe.setFont(Font.font("Montserrat", FontWeight.NORMAL, 13));
            rememberMe.setTextFill(Color.valueOf("#757575"));

            Region optionsSpacer = new Region();
            HBox.setHgrow(optionsSpacer, Priority.ALWAYS);

            Hyperlink forgotPassword = new Hyperlink("Forgot password?");
            forgotPassword.setFont(Font.font("Montserrat", FontWeight.NORMAL, 13));
            forgotPassword.setTextFill(Color.valueOf("#6200ea"));
            forgotPassword.setUnderline(false);

            optionsBox.getChildren().addAll(rememberMe, optionsSpacer, forgotPassword);

            // Constraint to make fields expand to full width
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            columnConstraints.setFillWidth(true);
            formGrid.getColumnConstraints().add(columnConstraints);

            centerCard.getChildren().add(formGrid);
            centerCard.getChildren().add(optionsBox);

            // Login button with enhanced styling and hover effect
            Button loginBtn = new Button("SIGN IN");
            loginBtn.setPrefHeight(50);
            loginBtn.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
            loginBtn.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white; -fx-background-radius: 25; -fx-cursor: hand;");
            loginBtn.setMaxWidth(Double.MAX_VALUE);

            // Enhanced hover effects with smooth transitions
            loginBtn.setOnMouseEntered(e -> {
                loginBtn.setStyle("-fx-background-color: #7c4dff; -fx-text-fill: white; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
                loginBtn.setCursor(Cursor.HAND);
            });

            loginBtn.setOnMouseExited(e ->
                    loginBtn.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white; -fx-background-radius: 25;"));

            loginBtn.setOnMousePressed(e ->
                    loginBtn.setStyle("-fx-background-color: #5600e8; -fx-text-fill: white; -fx-background-radius: 25;"));

            loginBtn.setOnMouseReleased(e ->
                    loginBtn.setStyle("-fx-background-color: #7c4dff; -fx-text-fill: white; -fx-background-radius: 25;"));

            // Social login options (as decorative elements)
            HBox socialLoginBox = new HBox(15);
            socialLoginBox.setAlignment(Pos.CENTER);

            Label orLabel = new Label("OR");
            orLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 12));
            orLabel.setTextFill(Color.valueOf("#9e9e9e"));

            // Social login divider
            HBox dividerBox = new HBox();
            dividerBox.setAlignment(Pos.CENTER);

            Region leftDivider = new Region();
            leftDivider.setStyle("-fx-background-color: #e0e0e0;");
            leftDivider.setPrefHeight(1);
            leftDivider.setPrefWidth(100);

            Region rightDivider = new Region();
            rightDivider.setStyle("-fx-background-color: #e0e0e0;");
            rightDivider.setPrefHeight(1);
            rightDivider.setPrefWidth(100);

            dividerBox.getChildren().addAll(leftDivider, new Label("   OR   "), rightDivider);
            dividerBox.setAlignment(Pos.CENTER);

            // Social login buttons
            Button googleBtn = createSocialButton("G", "#ea4335");
            Button facebookBtn = createSocialButton("f", "#1877f2");
            Button twitterBtn = createSocialButton("t", "#1da1f2");

            socialLoginBox.getChildren().addAll(googleBtn, facebookBtn, twitterBtn);

            // Register link with improved styling
            HBox registerBox = new HBox();
            registerBox.setAlignment(Pos.CENTER);

            Label noAccountLabel = new Label("Don't have an account? ");
            noAccountLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));
            noAccountLabel.setTextFill(Color.valueOf("#616161"));

            Hyperlink registerLink = new Hyperlink("Register");
            registerLink.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
            registerLink.setTextFill(Color.valueOf("#6200ea"));
            registerLink.setUnderline(false);

            registerBox.getChildren().addAll(noAccountLabel, registerLink);

            // Status message with enhanced styling
            statusLabel = new Label();
            statusLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));
            statusLabel.setTextAlignment(TextAlignment.CENTER);
            statusLabel.setOpacity(0);
            statusLabel.setMaxWidth(Double.MAX_VALUE);
            statusLabel.setPadding(new Insets(10, 0, 0, 0));

            // Add components to center card
            centerCard.getChildren().addAll(loginBtn, dividerBox, socialLoginBox, registerBox, statusLabel);

            // Add center card to main container with right alignment
            mainContainer.setRight(centerCard);
            BorderPane.setMargin(centerCard, new Insets(0, 80, 0, 0));

            // Add all components to root container
            rootContainer.getChildren().add(mainContainer);

            // Button actions
            loginBtn.setOnAction(e -> handleLogin(emailField.getText(), pwField.getText(), primaryStage));
            registerLink.setOnAction(e -> showRegisterDialog());
            forgotPassword.setOnAction(e -> showForgotPasswordDialog());

            socialLoginBox.setOnMouseClicked(e -> showStatusMessage("Social login is not available in demo mode", false));

            Scene scene = new Scene(rootContainer, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("MovieBooking - Sign In");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            primaryStage.show();

            // Add fade-in animation for the entire view
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootContainer);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing login view: " + e.getMessage());
        }
    }

    private Button createSocialButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(40, 40);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: " + color + "; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-width: 1;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + color + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 20; " +
                            "-fx-border-color: " + color + "; " +
                            "-fx-border-radius: 20; " +
                            "-fx-border-width: 1;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-text-fill: " + color + "; " +
                            "-fx-background-radius: 20; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-radius: 20; " +
                            "-fx-border-width: 1;"
            );
        });

        return button;
    }

    private void handleLogin(String email, String password, Stage primaryStage) {
        if (email.isEmpty() || password.isEmpty()) {
            showStatusMessage("Please fill all fields", false);
            return;
        }

        // Special case for admin login
        if ("admin".equals(email) && "admin".equals(password)) {
            showStatusMessage("Admin login successful!", true);
            openAdminDashboard(primaryStage);
            return;
        }

        User authenticatedUser = authController.login(email, password);
        if (authenticatedUser != null) {
            showStatusMessage("Login successful!", true);

            // Delay before opening new window to show success message
            new Thread(() -> {
                try {
                    Thread.sleep(800);
                    javafx.application.Platform.runLater(() -> {
                        // Open appropriate view based on role
                        if (authenticatedUser.getUserRole() == UserRole.Admin) {
                            openAdminDashboard(primaryStage);
                        } else {
                            openUserDashboard(primaryStage, authenticatedUser);
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else {
            showStatusMessage("Invalid credentials", false);
        }
    }

    private void openAdminDashboard(Stage primaryStage) {
        try {
            new AdminDashboardView().start(new Stage());
            primaryStage.close();
        } catch (Exception e) {
            System.err.println("Failed to open admin dashboard: " + e.getMessage());
        }
    }

    private void openUserDashboard(Stage primaryStage, User user) {
        MovieListView movieListView = new MovieListView(user);
        movieListView.start(new Stage());
        primaryStage.close();
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
    }

    private void showForgotPasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Reset Your Password");

        // Style the dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(400);
        dialogPane.setStyle("-fx-background-color: white;");

        // Setup content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label instructionLabel = new Label("Enter your email address to receive a password reset link");
        instructionLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 13));
        instructionLabel.setTextFill(Color.valueOf("#616161"));
        instructionLabel.setWrapText(true);

        TextField emailField = new TextField();
        emailField.setPromptText("your@email.com");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-padding: 8;");

        content.getChildren().addAll(instructionLabel, emailField);
        dialog.getDialogPane().setContent(content);

        // Add buttons
        ButtonType resetButtonType = new ButtonType("Send Reset Link", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, cancelButtonType);

        // Style buttons
        Button resetButton = (Button) dialog.getDialogPane().lookupButton(resetButtonType);
        resetButton.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white; -fx-font-weight: bold;");

        // Only enable send button when email is provided
        resetButton.setDisable(true);
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            resetButton.setDisable(newValue.trim().isEmpty() || !newValue.contains("@"));
        });

        // Show dialog and handle result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButtonType) {
                return emailField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(email -> {
            showStatusMessage("Password reset link sent to " + email, true);
        });
    }

    private void showRegisterDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Create Account");
        dialog.setHeaderText("Join MovieBooking");

        // Style the dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(450);
        dialogPane.setStyle("-fx-background-color: white;");

        // Create content container
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label instructionLabel = new Label("Fill in your details to create a new account");
        instructionLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 13));
        instructionLabel.setTextFill(Color.valueOf("#616161"));

        // Create fields with enhanced styling
        TextField nameField = new TextField();
        nameField.setPromptText("Your full name");
        nameField.setPrefHeight(45);
        nameField.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 10;"
        );

        TextField newEmailField = new TextField();
        newEmailField.setPromptText("your@email.com");
        newEmailField.setPrefHeight(45);
        newEmailField.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 10;"
        );

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a strong password");
        passwordField.setPrefHeight(45);
        passwordField.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 10;"
        );

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setPrefHeight(45);
        confirmPasswordField.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 10;"
        );

        // Labels with better spacing
        Label nameLabel = new Label("Full Name");
        nameLabel.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
        nameLabel.setTextFill(Color.valueOf("#424242"));

        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
        emailLabel.setTextFill(Color.valueOf("#424242"));

        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
        passwordLabel.setTextFill(Color.valueOf("#424242"));

        Label confirmPasswordLabel = new Label("Confirm Password");
        confirmPasswordLabel.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
        confirmPasswordLabel.setTextFill(Color.valueOf("#424242"));

        // Field containers for better organization
        VBox nameContainer = new VBox(5);
        nameContainer.getChildren().addAll(nameLabel, nameField);

        VBox emailContainer = new VBox(5);
        emailContainer.getChildren().addAll(emailLabel, newEmailField);

        VBox passwordContainer = new VBox(5);
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);

        VBox confirmPasswordContainer = new VBox(5);
        confirmPasswordContainer.getChildren().addAll(confirmPasswordLabel, confirmPasswordField);

        // Terms and conditions checkbox
        CheckBox termsCheckBox = new CheckBox("I agree to the Terms of Service and Privacy Policy");
        termsCheckBox.setFont(Font.font("Montserrat", FontWeight.NORMAL, 13));
        termsCheckBox.setTextFill(Color.valueOf("#616161"));

        // Password match indicator
        Label passwordMatchLabel = new Label();
        passwordMatchLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 12));

        // Add to main content
        content.getChildren().addAll(
                instructionLabel,
                nameContainer,
                emailContainer,
                passwordContainer,
                confirmPasswordContainer,
                passwordMatchLabel,
                termsCheckBox
        );

        dialog.getDialogPane().setContent(content);

        // Create custom buttons with styling
        ButtonType registerButtonType = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, cancelButtonType);

        // Style the buttons
        Button registerButton = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
        registerButton.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white; -fx-font-weight: bold;");

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242;");

        // Password matching validation
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswords(passwordField, confirmPasswordField, passwordMatchLabel, registerButton, termsCheckBox);
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswords(passwordField, confirmPasswordField, passwordMatchLabel, registerButton, termsCheckBox);
        });

        // Form validation for register button
        nameField.textProperty().addListener((observable, oldValue, newValue) ->
                validateRegisterForm(registerButton, nameField, newEmailField, passwordField, confirmPasswordField, termsCheckBox, passwordMatchLabel));

        newEmailField.textProperty().addListener((observable, oldValue, newValue) ->
                validateRegisterForm(registerButton, nameField, newEmailField, passwordField, confirmPasswordField, termsCheckBox, passwordMatchLabel));

        termsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                validateRegisterForm(registerButton, nameField, newEmailField, passwordField, confirmPasswordField, termsCheckBox, passwordMatchLabel));

        // Initial validation
        validateRegisterForm(registerButton, nameField, newEmailField, passwordField, confirmPasswordField, termsCheckBox, passwordMatchLabel);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                return new User(0, nameField.getText(), newEmailField.getText(),
                        passwordField.getText(), UserRole.User);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newUser -> {
            if (authController.register(newUser.getName(), newUser.getEmail(), newUser.getPassword())) {
                showStatusMessage("Registration successful! Please login.", true);
            } else {
                showStatusMessage("Registration failed. Email may already exist.", false);
            }
        });
    }

    // Helper method to validate password matching
    private void validatePasswords(PasswordField passwordField, PasswordField confirmPasswordField,
                                   Label passwordMatchLabel, Button registerButton, CheckBox termsCheckBox) {
        if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            passwordMatchLabel.setText("");
            passwordMatchLabel.setTextFill(Color.valueOf("#757575"));
            return;
        }

        if (passwordField.getText().equals(confirmPasswordField.getText())) {
            passwordMatchLabel.setText("✓ Passwords match");
            passwordMatchLabel.setTextFill(Color.valueOf("#00c853"));
        } else {
            passwordMatchLabel.setText("⨯ Passwords do not match");
            passwordMatchLabel.setTextFill(Color.valueOf("#d50000"));
        }
    }

    // Enhanced validation method for registration form
    private void validateRegisterForm(Button registerButton, TextField nameField, TextField emailField,
                                      PasswordField passwordField, PasswordField confirmPasswordField,
                                      CheckBox termsCheckBox, Label passwordMatchLabel) {
        boolean allFieldsFilled = !nameField.getText().trim().isEmpty() &&
                !emailField.getText().trim().isEmpty() &&
                !passwordField.getText().trim().isEmpty() &&
                !confirmPasswordField.getText().trim().isEmpty();

        boolean passwordsMatch = passwordField.getText().equals(confirmPasswordField.getText());
        boolean validEmail = emailField.getText().contains("@") && emailField.getText().contains(".");
        boolean termsAccepted = termsCheckBox.isSelected();

        registerButton.setDisable(!(allFieldsFilled && passwordsMatch && validEmail && termsAccepted));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
