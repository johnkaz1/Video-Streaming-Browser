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

// Η κλάση LoginWindow είναι η κύρια κλάση για το παράθυρο σύνδεσης της εφαρμογής.
// Κληρονομεί από την javafx.application.Application, καθιστώντας την μια εφαρμογή JavaFX.
public class LoginWindow extends Application {

    // Πεδία κειμένου για το όνομα χρήστη και το email.
    private TextField usernameField;
    private TextField emailField;
    // Ετικέτα για την εμφάνιση μηνυμάτων κατάστασης (π.χ. σφάλμα σύνδεσης).
    private Label statusLabel;
    // Το κύριο Stage (παράθυρο) της εφαρμογής.
    private Stage primaryStage;

    @Override
    // Η μέθοδος start είναι το σημείο εισόδου για όλες τις εφαρμογές JavaFX.
    // Το primaryStage είναι το κύριο παράθυρο της εφαρμογής.
    public void start(Stage stage) {
        this.primaryStage = stage;
        // Ορίζει τον τίτλο του παραθύρου.
        primaryStage.setTitle("Movie & Series Manager - Σύνδεση");

        // Προσπαθεί να φορτώσει τα δεδομένα των χρηστών από ένα αρχείο.
        try {
            // Φορτώνει μόνο τους χρήστες για τη διαδικασία σύνδεσης. Τα υπόλοιπα δεδομένα θα φορτωθούν μετά την επιτυχή σύνδεση.
            DataLoader.loadUsers("Movie/src/utils/Users.txt");
        } catch (IOException e) {
            // Αν αποτύχει η φόρτωση από την πρώτη διαδρομή, δοκιμάζει μια εναλλακτική.
            try {
                DataLoader.loadUsers("Users.txt");
            } catch (IOException ex) {
                // Εμφανίζει ένα μήνυμα σφάλματος αν η φόρτωση των χρηστών αποτύχει.
                showErrorDialog("Σφάλμα", "Δεν ήταν δυνατή η φόρτωση των στοιχείων χρηστών.\n" + ex.getMessage());
                return; // Τερματίζει την εκκίνηση της εφαρμογής.
            }
        }

        // Δημιουργεί τη σκηνή σύνδεσης.
        Scene loginScene = createLoginScene();
        // Ορίζει τη σκηνή για το κύριο παράθυρο.
        primaryStage.setScene(loginScene);
        // Απενεργοποιεί τη δυνατότητα αλλαγής μεγέθους του παραθύρου.
        primaryStage.setResizable(false);
        // Εμφανίζει το παράθυρο.
        primaryStage.show();
    }

    // Δημιουργεί και επιστρέφει τη σκηνή σύνδεσης.
    private Scene createLoginScene() {
        // Κύριος container (VBox) για τη διάταξη των στοιχείων.
        VBox mainContainer = new VBox(20); // 20 είναι το κενό μεταξύ των στοιχείων.
        mainContainer.setPadding(new Insets(40)); // Εσωτερικά περιθώρια.
        mainContainer.setAlignment(Pos.CENTER); // Στοίχιση στο κέντρο.
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);"); // Στυλ φόντου.

        // Ετικέτα τίτλου της εφαρμογής.
        Label titleLabel = new Label("🎬 Movie & Series Manager");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Ετικέτα υποτίτλου για οδηγίες σύνδεσης.
        Label subtitleLabel = new Label("Παρακαλώ εισάγετε τα στοιχεία σας για σύνδεση");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");

        // Panel (VBox) για τη φόρμα σύνδεσης.
        VBox loginPanel = new VBox(15); // 15 είναι το κενό μεταξύ των στοιχείων.
        loginPanel.setPadding(new Insets(30)); // Εσωτερικά περιθώρια.
        loginPanel.setAlignment(Pos.CENTER); // Στοίχιση στο κέντρο.
        loginPanel.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);"); // Στυλ panel.
        loginPanel.setMaxWidth(400); // Μέγιστο πλάτος.

        // Ετικέτα για το πεδίο ονόματος χρήστη.
        Label usernameLabel = new Label("Όνομα Χρήστη:");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Πεδίο εισαγωγής για το όνομα χρήστη.
        usernameField = new TextField();
        usernameField.setPromptText("Εισάγετε το όνομα χρήστη..."); // Κείμενο placeholder.
        usernameField.setPrefHeight(40); // Προτιμώμενο ύψος.
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Ετικέτα για το πεδίο email.
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Πεδίο εισαγωγής για το email.
        emailField = new TextField();
        emailField.setPromptText("Εισάγετε το email σας...");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Ετικέτα για την εμφάνιση μηνυμάτων κατάστασης (π.χ. σφάλμα).
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;"); // Κόκκινο κείμενο για σφάλματα.
        statusLabel.setVisible(false); // Αρχικά αόρατο.

        // Κουμπί σύνδεσης.
        Button loginButton = new Button("Σύνδεση");
        loginButton.setPrefWidth(200); // Προτιμώμενο πλάτος.
        loginButton.setPrefHeight(45); // Προτιμώμενο ύψος.
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;"); // Στυλ κουμπιού.
        loginButton.setOnAction(e -> handleLogin()); // Ορίζει ενέργεια κατά το πάτημα.

        // Ενεργοποιεί τη σύνδεση με το πλήκτρο Enter στα πεδία κειμένου.
        usernameField.setOnAction(e -> handleLogin());
        emailField.setOnAction(e -> handleLogin());

        // Κουμπί εξόδου.
        Button exitButton = new Button("Έξοδος");
        exitButton.setPrefWidth(200);
        exitButton.setPrefHeight(35);
        exitButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        exitButton.setOnAction(e -> System.exit(0)); // Τερματίζει την εφαρμογή.

        // Προσθήκη εφέ hover για τα κουμπιά.
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;"));

        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;"));

        // Ετικέτα πληροφοριών για διαθέσιμους χρήστες.
        Label infoLabel = new Label("💡 Διαθέσιμοι χρήστες:");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");

        // VBox για την εμφάνιση της λίστας των διαθέσιμων χρηστών.
        VBox usersInfo = new VBox(3);
        usersInfo.setAlignment(Pos.CENTER);

        // Εμφανίζει τους διαθέσιμους χρήστες από τα φορτωμένα δεδομένα.
        if (!DataLoader.users.isEmpty()) {
            for (User user : DataLoader.users) {
                Label userLabel = new Label("👤 " + user.getUsername() + " (" + user.getEmail() + ")");
                userLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                usersInfo.getChildren().add(userLabel);
            }
        } else {
            // Μήνυμα αν δεν βρεθούν χρήστες.
            Label noUsersLabel = new Label("Δεν βρέθηκαν χρήστες");
            noUsersLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");
            usersInfo.getChildren().add(noUsersLabel);
        }

        // Προσθέτει όλα τα στοιχεία στο login panel.
        loginPanel.getChildren().addAll(
                usernameLabel, usernameField,
                emailLabel, emailField,
                statusLabel,
                loginButton, exitButton
        );

        // Προσθέτει όλα τα στοιχεία στον κύριο container.
        mainContainer.getChildren().addAll(
                titleLabel, subtitleLabel, loginPanel,
                infoLabel, usersInfo
        );

        // Επιστρέφει τη σκηνή με τον κύριο container και το μέγεθός της.
        return new Scene(mainContainer, 500, 650);
    }

    // Χειρίζεται την ενέργεια σύνδεσης.
    private void handleLogin() {
        String username = usernameField.getText().trim(); // Λαμβάνει το όνομα χρήστη και αφαιρεί κενά.
        String email = emailField.getText().trim(); // Λαμβάνει το email και αφαιρεί κενά.

        // Καθαρίζει προηγούμενα μηνύματα κατάστασης.
        statusLabel.setVisible(false);

        // Επικυρώνει την είσοδο: ελέγχει αν τα πεδία είναι κενά.
        if (username.isEmpty() || email.isEmpty()) {
            showStatus("Παρακαλώ συμπληρώστε και τα δύο πεδία!", true); // Εμφανίζει μήνυμα σφάλματος.
            return;
        }

        // Ελέγχει τα διαπιστευτήρια του χρήστη.
        User authenticatedUser = authenticateUser(username, email);

        if (authenticatedUser != null) {
            // Αν η σύνδεση είναι επιτυχής, εμφανίζει μήνυμα επιτυχίας.
            showStatus("Επιτυχής σύνδεση! Φόρτωση εφαρμογής...", false);

            // Μικρή καθυστέρηση για να φανεί το μήνυμα επιτυχίας.
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000); // Καθυστέρηση 1 δευτερόλεπτο.
                    return null;
                }
            };

            // Όταν ολοκληρωθεί η εργασία καθυστέρησης.
            task.setOnSucceeded(e -> {
                try {
                    // Φορτώνει όλα τα υπόλοιπα δεδομένα της εφαρμογής.
                    DataLoader.loadAllData();

                    // Ξεκινά την κύρια εφαρμογή.
                    launchMainApplication(authenticatedUser);
                } catch (IOException ex) {
                    // Εμφανίζει σφάλμα αν αποτύχει η φόρτωση δεδομένων.
                    showErrorDialog("Σφάλμα", "Σφάλμα κατά τη φόρτωση των δεδομένων:\n" + ex.getMessage());
                }
            });

            // Ξεκινά την εργασία σε νέο νήμα.
            new Thread(task).start();

        } else {
            // Αν η σύνδεση αποτύχει, εμφανίζει μήνυμα λάθους.
            showStatus("Λανθασμένα στοιχεία σύνδεσης!", true);
        }
    }

    // Επικυρώνει τα διαπιστευτήρια του χρήστη.
    private User authenticateUser(String username, String email) {
        // Ψάχνει στη λίστα των χρηστών για έναν χρήστη με τα δοθέντα διαπιστευτήρια.
        return DataLoader.users.stream()
                .filter(user -> user.verifyCredentials(username, email))
                .findFirst() // Επιστρέφει τον πρώτο που βρέθηκε ή null αν κανένας δεν ταιριάζει.
                .orElse(null);
    }

    // Ξεκινά την κύρια εφαρμογή (MainApp).
    private void launchMainApplication(User user) {
        try {
            // Κλείνει το παράθυρο σύνδεσης.
            primaryStage.close();
            // Επιλέγει έναν τυχαίο αριθμό για το Login ID του χρήστη.
            int loginId = MainApp.pickRandomNumber();
            user.setId(loginId); // Ορίζει το Login ID στο αντικείμενο User.

            // Δημιουργεί και εμφανίζει την κύρια εφαρμογή.
            MainApp mainApp = new MainApp(user); // Δημιουργεί νέα MainApp με τον συνδεδεμένο χρήστη.
            Stage mainStage = new Stage(); // Δημιουργεί νέο Stage για την κύρια εφαρμογή.
            mainApp.start(mainStage); // Ξεκινά την κύρια εφαρμογή.

            // Ορίζει τον τίτλο του παραθύρου της κύριας εφαρμογής με πληροφορίες χρήστη.
            mainStage.setTitle("Movie & Series Manager - Καλώς ήρθες " + user.getFirstName() + " " + user.getLastName() + " " + "LoginId =" + " " + loginId);
            System.out.println(user); // Εκτυπώνει πληροφορίες χρήστη στην κονσόλα.
        } catch (Exception e) {
            // Εμφανίζει σφάλμα αν κάτι πάει στραβά κατά την εκκίνηση της κύριας εφαρμογής.
            showErrorDialog("Σφάλμα", "Σφάλμα κατά την εκκίνηση της εφαρμογής:\n" + e.getMessage());
        }
    }

    // Εμφανίζει ένα μήνυμα κατάστασης στην ετικέτα statusLabel.
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message); // Ορίζει το κείμενο του μηνύματος.
        // Αλλάζει το χρώμα του κειμένου ανάλογα με το αν είναι σφάλμα ή επιτυχία.
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "#e74c3c" : "#27ae60") + "; -fx-font-size: 12px;");
        statusLabel.setVisible(true); // Κάνει την ετικέτα ορατή.
    }

    // Εμφανίζει ένα αναδυόμενο παράθυρο σφάλματος (Alert Dialog).
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Δημιουργεί ένα Alert τύπου ERROR.
        alert.setTitle(title); // Ορίζει τον τίτλο του Alert.
        alert.setHeaderText(null); // Δεν έχει κεφαλίδα κειμένου.
        alert.setContentText(message); // Ορίζει το περιεχόμενο του μηνύματος.
        alert.showAndWait(); // Εμφανίζει το Alert και περιμένει να κλείσει.
    }

    // Η μέθοδος main είναι το σημείο εκκίνησης της εφαρμογής.
    public static void main(String[] args) {
        launch(args); // Ξεκινά την εφαρμογή JavaFX.
    }
}