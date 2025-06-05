package gui;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import utils.DataLoader;

import java.io.IOException;

public class LoginWindow extends Application {

    private TextField usernameField;
    private TextField emailField;
    private Label statusLabel;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Movie & Series Manager - Σύνδεση");

        // Try to load user data
        try {
            // Load only users for login - we'll load the rest after successful login
            DataLoader.loadUsers("Movie/src/utils/Users.txt");
        } catch (IOException e) {
            // Try alternative paths
            try {
                DataLoader.loadUsers("Users.txt");
            } catch (IOException ex) {
                showErrorDialog("Σφάλμα", "Δεν ήταν δυνατή η φόρτωση των στοιχείων χρηστών.\n" + ex.getMessage());
                return;
            }
        }

        Scene loginScene = createLoginScene();
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Scene createLoginScene() {
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        // Title
        Label titleLabel = new Label("🎬 Movie & Series Manager");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label("Παρακαλώ εισάγετε τα στοιχεία σας για σύνδεση");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");

        // Login form panel
        VBox loginPanel = new VBox(15);
        loginPanel.setPadding(new Insets(30));
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        loginPanel.setMaxWidth(400);

        // Username field
        Label usernameLabel = new Label("Όνομα Χρήστη:");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        usernameField = new TextField();
        usernameField.setPromptText("Εισάγετε το όνομα χρήστη...");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        emailField = new TextField();
        emailField.setPromptText("Εισάγετε το email σας...");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Status label for messages
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
        statusLabel.setVisible(false);

        // Login button
        Button loginButton = new Button("Σύνδεση");
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;");
        loginButton.setOnAction(e -> handleLogin());

        // Make Enter key trigger login
        usernameField.setOnAction(e -> handleLogin());
        emailField.setOnAction(e -> handleLogin());

        // Exit button
        Button exitButton = new Button("Έξοδος");
        exitButton.setPrefWidth(200);
        exitButton.setPrefHeight(35);
        exitButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        exitButton.setOnAction(e -> System.exit(0));

        // Add hover effects
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;"));

        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;"));

        // Available users info
        Label infoLabel = new Label("💡 Διαθέσιμοι χρήστες:");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");

        VBox usersInfo = new VBox(3);
        usersInfo.setAlignment(Pos.CENTER);

        // Show available users from the loaded data
        if (!DataLoader.users.isEmpty()) {
            for (User user : DataLoader.users) {
                Label userLabel = new Label("👤 " + user.getUsername() + " (" + user.getEmail() + ")");
                userLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                usersInfo.getChildren().add(userLabel);
            }
        } else {
            Label noUsersLabel = new Label("Δεν βρέθηκαν χρήστες");
            noUsersLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");
            usersInfo.getChildren().add(noUsersLabel);
        }

        // Add all elements to login panel
        loginPanel.getChildren().addAll(
                usernameLabel, usernameField,
                emailLabel, emailField,
                statusLabel,
                loginButton, exitButton
        );

        // Add all elements to main container
        mainContainer.getChildren().addAll(
                titleLabel, subtitleLabel, loginPanel,
                infoLabel, usersInfo
        );

        return new Scene(mainContainer, 500, 650);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        // Clear previous status
        statusLabel.setVisible(false);

        // Validate input
        if (username.isEmpty() || email.isEmpty()) {
            showStatus("Παρακαλώ συμπληρώστε και τα δύο πεδία!", true);
            return;
        }

        // Check credentials
        User authenticatedUser = authenticateUser(username, email);

        if (authenticatedUser != null) {
            showStatus("Επιτυχής σύνδεση! Φόρτωση εφαρμογής...", false);

            // Small delay to show success message
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000); // 1 second delay
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                try {
                    // Load all remaining data
                    DataLoader.loadAllData();

                    // Launch main application
                    launchMainApplication(authenticatedUser);
                } catch (IOException ex) {
                    showErrorDialog("Σφάλμα", "Σφάλμα κατά τη φόρτωση των δεδομένων:\n" + ex.getMessage());
                }
            });

            new Thread(task).start();

        } else {
            showStatus("Λανθασμένα στοιχεία σύνδεσης!", true);
        }
    }

    private User authenticateUser(String username, String email) {
        return DataLoader.users.stream()
                .filter(user -> user.verifyCredentials(username, email))
                .findFirst()
                .orElse(null);
    }

    private void launchMainApplication(User user) {
        try {
            // Close login window
            primaryStage.close();
            int loginId = MainApp.pickRandomNumber();
            user.setId(loginId);

            // Create and show main application
            MainApp mainApp = new MainApp(user);
            Stage mainStage = new Stage();
            mainApp.start(mainStage);


            // Set the window title to include user info
            mainStage.setTitle("Movie & Series Manager - Καλώς ήρθες " + user.getFirstName() + " " + user.getLastName() + " " + "LoginId =" + " " + loginId);
            System.out.println(user);
        } catch (Exception e) {
            showErrorDialog("Σφάλμα", "Σφάλμα κατά την εκκίνηση της εφαρμογής:\n" + e.getMessage());
        }
    }


    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "#e74c3c" : "#27ae60") + "; -fx-font-size: 12px;");
        statusLabel.setVisible(true);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}