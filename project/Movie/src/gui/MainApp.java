package gui;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Actor;
import model.Director;
import model.Movie;
import model.Series;
import model.Season;
import model.Episode;
import utils.DataLoader;
import java.util.Random;
import model.User;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

import exceptions.InvalidRatingException; // Εισαγωγή προσαρμοσμένης εξαίρεσης.

import java.io.IOException;
import java.time.LocalDate; // Εισαγωγή LocalDate για το τρέχον έτος.
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Απαιτείται για επεξεργάσιμα κελιά TableView.
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import javafx.beans.property.SimpleIntegerProperty; // Εισαγωγή SimpleIntegerProperty.

// Η κλάση MainApp είναι η κύρια κλάση της εφαρμογής διαχείρισης ταινιών και σειρών.
// Κληρονομεί από την javafx.application.Application.
public class MainApp extends Application {

    // Πίνακες (TableViews) για την εμφάνιση ταινιών και σειρών.
    private TableView<Movie> movieTable;
    private TableView<Series> seriesTable;
    // ObservableLists για την αποθήκευση όλων των ταινιών και σειρών.
    private ObservableList<Movie> allMovies;
    private ObservableList<Series> allSeries;
    // ObservableLists για την αποθήκευση των φιλτραρισμένων ταινιών και σειρών.
    private ObservableList<Movie> filteredMovies;
    private ObservableList<Series> filteredSeries;

    // Νέες ObservableLists για τις κορυφαίες ταινίες και σειρές.
    private ObservableList<Movie> topMovies;
    private ObservableList<Series> topSeries;

    // Πεδία αναζήτησης για ταινίες.
    private TextField movieTitleSearchField;
    private TextField movieActorSearchField;
    private TextField movieDirectorSearchField;
    private Spinner<Double> movieMinImdbSpinner;
    private Spinner<Double> movieMinUserRatingSpinner;

    // Πεδία αναζήτησης για σειρές.
    private TextField seriesTitleSearchField;
    private Spinner<Double> seriesMinUserRatingSpinner;

    // Πεδία φόρμας για προσθήκη ταινίας.
    private TextField addMovieTitleField;
    private Spinner<Integer> addMovieYearSpinner;
    private ComboBox<String> addMovieGenreComboBox;
    private Spinner<Integer> addMovieDurationSpinner;
    private ComboBox<Director> addMovieDirectorComboBox;
    private Spinner<Double> addMovieImdbSpinner;
    private ComboBox<Actor> addMovieActorComboBox;

    // Πεδία φόρμας για προσθήκη σειράς.
    private TextField addSeriesTitleField;
    private Spinner<Integer> addSeriesYearSpinner;
    private ComboBox<String> addSeriesGenreComboBox;
    private Spinner<Integer> addSeriesSeasonsSpinner;
    private ComboBox<Director> addSeriesDirectorComboBox;
    private ComboBox<Actor> addSeriesActorComboBox;

    // Πίνακας για αξιολογήσεις.
    private TableView<RatingEntry> ratingsTable;
    private ObservableList<RatingEntry> ratingsData;
    private TextField movieToRateField;
    private TextArea ratingDescriptionArea;
    private User loggedInUser; // Ο συνδεδεμένος χρήστης.

    // Αντικείμενο Random για τη δημιουργία τυχαίων αναγνωριστικών σύνδεσης.
    private static Random random = new Random();

    // Μέθοδος για τη δημιουργία ενός τυχαίου αριθμού (0-99).
    public static int pickRandomNumber() {
        return random.nextInt(100); // nextInt(100) επιστρέφει 0-99.
    }

    // Κατασκευαστής της κλάσης MainApp που δέχεται έναν χρήστη.
    public MainApp(User user) {
        this.loggedInUser = user;
    }

    @Override
    // Η μέθοδος start είναι το σημείο εισόδου για την κύρια εφαρμογή JavaFX.
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Movie & Series Manager"); // Ορίζει τον τίτλο του παραθύρου.

        // Αρχικοποιεί τις συλλογές δεδομένων.
        allMovies = FXCollections.observableArrayList(
                DataLoader.movies.stream()
                        .sorted(Comparator.comparingDouble(Movie::getAverageUserRating).reversed()) // Ταξινομεί τις ταινίες με βάση τη μέση βαθμολογία χρηστών (φθίνουσα).
                        .collect(Collectors.toList())
        );

        allSeries = FXCollections.observableArrayList(
                DataLoader.seriesList.stream()
                        .sorted(Comparator.comparingDouble(Series::getAverageUserRating).reversed()) // Ταξινομεί τις σειρές με βάση τη μέση βαθμολογία χρηστών (φθίνουσα).
                        .collect(Collectors.toList())
        );

        filteredMovies = FXCollections.observableArrayList(allMovies); // Αρχικοποιεί τις φιλτραρισμένες ταινίες με όλες τις ταινίες.
        filteredSeries = FXCollections.observableArrayList(allSeries); // Αρχικοποιεί τις φιλτραρισμένες σειρές με όλες τις σειρές.

        // Νέο: Αρχικοποιεί τις λίστες κορυφαίου περιεχομένου και τις γεμίζει.
        topMovies = FXCollections.observableArrayList();
        topSeries = FXCollections.observableArrayList();
        populateTopContent(); // Αρχική πλήρωση.

        TabPane tabPane = new TabPane(); // Δημιουργεί ένα TabPane για την οργάνωση των καρτελών.

        // Δημιουργία των καρτελών.
        Tab movieTab = new Tab("🎬 Ταινίες", createMovieTab());
        Tab seriesTab = new Tab("📺 Σειρές", createSeriesTab());
        Tab ratingsTab = new Tab("⭐ Αξιολογήσεις", createRatingsTab());
        Tab addMovieTab = new Tab("➕ Προσθήκη Ταινίας", createAddMovieTab());
        Tab addSeriesTab = new Tab("➕ Προσθήκη Σειράς", createAddSeriesTab());
        // Νέο: Καρτέλα για κορυφαίο περιεχόμενο.
        Tab topContentTab = new Tab("🏆 Top Ταινίες & Σειρές", createTopContentTab());


        tabPane.getTabs().addAll(movieTab, seriesTab, ratingsTab, addMovieTab, addSeriesTab, topContentTab); // Προσθέτει όλες τις καρτέλες.
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Απενεργοποιεί τη δυνατότητα κλεισίματος καρτελών.

        Scene scene = new Scene(tabPane, 1200, 700); // Δημιουργεί τη σκηνή με το TabPane και το μέγεθος.
        primaryStage.setScene(scene); // Ορίζει τη σκηνή στο κύριο παράθυρο.
        primaryStage.show(); // Εμφανίζει το παράθυρο.
    }

    // Δημιουργεί και επιστρέφει το περιεχόμενο της καρτέλας Ταινίες.
    private VBox createMovieTab() {
        VBox mainBox = new VBox(10); // Κύριος container για την καρτέλα ταινιών.
        mainBox.setPadding(new Insets(10)); // Εσωτερικά περιθώρια.

        // Panel αναζήτησης.
        VBox searchPanel = createMovieSearchPanel();

        // Πίνακας ταινιών (TableView).
        movieTable = new TableView<>();
        movieTable.setItems(filteredMovies); // Συνδέει τον πίνακα με τις φιλτραρισμένες ταινίες.

        // Ορισμός στηλών για τον πίνακα ταινιών.
        TableColumn<Movie, String> titleCol = new TableColumn<>("Τίτλος");
        titleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(200);

        TableColumn<Movie, String> directorCol = new TableColumn<>("Σκηνοθέτης");
        directorCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDirector().getFullName()));
        directorCol.setPrefWidth(150);

        TableColumn<Movie, String> actorCol = new TableColumn<>("Πρωταγωνιστής");
        actorCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLeadActor().getFullName()));
        actorCol.setPrefWidth(150);

        TableColumn<Movie, Number> imdbCol = new TableColumn<>("IMDb");
        imdbCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getImdbRating()));
        imdbCol.setPrefWidth(80);

        TableColumn<Movie, Number> avgUserRatingCol = new TableColumn<>("Μ.Ο. Χρηστών");
        avgUserRatingCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getAverageUserRating()));
        avgUserRatingCol.setPrefWidth(100);

        TableColumn<Movie, String> genreCol = new TableColumn<>("Είδος");
        genreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGenre()));
        genreCol.setPrefWidth(100);

        TableColumn<Movie, Number> yearCol = new TableColumn<>("Έτος");
        yearCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getYear()));
        yearCol.setPrefWidth(80);

        movieTable.getColumns().addAll(titleCol, directorCol, actorCol, imdbCol, avgUserRatingCol, genreCol, yearCol); // Προσθέτει όλες τις στήλες.
        movieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Πολιτική αλλαγής μεγέθους στηλών.

        mainBox.getChildren().addAll(searchPanel, new Separator(), movieTable); // Προσθέτει το panel αναζήτησης, ένα διαχωριστικό και τον πίνακα.
        VBox.setVgrow(movieTable, Priority.ALWAYS); // Επιτρέπει στον πίνακα να επεκτείνεται κατακόρυφα.

        return mainBox;
    }

    // Δημιουργεί και επιστρέφει το περιεχόμενο της καρτέλας Σειρές.
    private VBox createSeriesTab() {
        VBox mainBox = new VBox(10); // Κύριος container για την καρτέλα σειρών.
        mainBox.setPadding(new Insets(10));

        // Panel αναζήτησης.
        VBox searchPanel = createSeriesSearchPanel();

        // Πίνακας σειρών (TableView).
        seriesTable = new TableView<>();
        seriesTable.setItems(filteredSeries); // Συνδέει τον πίνακα με τις φιλτραρισμένες σειρές.
        // Ενεργοποιεί την επεξεργασία για τον πίνακα.
        seriesTable.setEditable(true);

        // Ορισμός στηλών για τον πίνακα σειρών.
        TableColumn<Series, String> titleCol = new TableColumn<>("Τίτλος");
        titleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(200);

        TableColumn<Series, String> genreCol = new TableColumn<>("Είδος");
        genreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGenre()));
        genreCol.setPrefWidth(100);

        // Τροποποιημένο: Κάνει τη στήλη σεζόν επεξεργάσιμη.
        TableColumn<Series, Integer> seasonsCol = new TableColumn<>("Σεζόν");
        seasonsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getSeasons().size()).asObject());
        seasonsCol.setPrefWidth(80);
        seasonsCol.setEditable(true); // Ενεργοποιεί την επεξεργασία για αυτή τη στήλη.
        seasonsCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter())); // Χρησιμοποιεί TextFieldTableCell για εισαγωγή ακέραιων.

        // Χειρίζεται το γεγονός commit όταν επεξεργάζεται ο αριθμός σεζόν.
        seasonsCol.setOnEditCommit(event -> {
            Series series = event.getRowValue(); // Λαμβάνει την επεξεργαζόμενη σειρά.
            int newSeasonCount = event.getNewValue(); // Λαμβάνει τη νέα ακέραια τιμή.

            try {
                // Εξασφαλίζει ότι ο νέος αριθμός σεζόν είναι έγκυρος.
                if (newSeasonCount < 1) {
                    showAlert("Σφάλμα Επεξεργασίας", "Ο αριθμός των σεζόν πρέπει να είναι τουλάχιστον 1.", Alert.AlertType.ERROR);
                    // Επαναφέρει την παλιά τιμή αν είναι άκυρη.
                    seriesTable.refresh();
                    return;
                }

                updateSeriesSeasons(series, newSeasonCount); // Ενημερώνει τον αριθμό σεζόν.
                showAlert("Επιτυχία", "Ο αριθμός των σεζόν για τη σειρά '" + series.getTitle() + "' ενημερώθηκε σε " + newSeasonCount + ".", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Σφάλμα", "Παρουσιάστηκε σφάλμα κατά την ενημέρωση των σεζόν: " + e.getMessage(), Alert.AlertType.ERROR);
                // Επαναφέρει την παλιά τιμή σε περίπτωση σφάλματος.
                seriesTable.refresh();
            }
            populateTopContent(); // Νέο: Ενημερώνει το κορυφαίο περιεχόμενο μετά την ενημέρωση των σεζόν της σειράς.
        });

        // ΝΕΟ: Δημιουργία νέας στήλης για Συνολικά Επεισόδια.
        TableColumn<Series, Integer> totalEpisodesCol = new TableColumn<>("Συνολικά Επεισόδια");
        totalEpisodesCol.setCellValueFactory(cellData -> {
            // Υπολογίζει το άθροισμα των επεισοδίων σε όλες τις σεζόν.
            return new SimpleIntegerProperty(cellData.getValue().getTotalEpisodes()).asObject();
        });
        totalEpisodesCol.setPrefWidth(120); // Προσαρμόζει το πλάτος όπως χρειάζεται.

        // Κάνει τη στήλη Συνολικά Επεισόδια επεξεργάσιμη.
        totalEpisodesCol.setEditable(true); // Ενεργοποιεί την επεξεργασία για αυτή τη στήλη.
        totalEpisodesCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter())); // Χρησιμοποιεί TextFieldTableCell για εισαγωγή ακέραιων.

        // Χειρίζεται το γεγονός commit όταν επεξεργάζεται ο αριθμός των συνολικών επεισοδίων.
        totalEpisodesCol.setOnEditCommit(event -> {
            Series series = event.getRowValue(); // Λαμβάνει την επεξεργαζόμενη σειρά.
            int newTotalEpisodes = event.getNewValue(); // Λαμβάνει τη νέα ακέραια τιμή.
            // λογική για την ενημέρωση μεμονωμένων επεισοδίων σεζόν ή ενός ειδικού πεδίου.
            seriesTable.refresh(); // Ανανεώνει για να επανέλθει στην υπολογισμένη τιμή αν είναι παράγωγο.
            populateTopContent(); // Ενημερώνει το κορυφαίο περιεχόμενο.
        });


        TableColumn<Series, Number> avgUserRatingCol = new TableColumn<>("Μ.Ο. Χρηστών");
        avgUserRatingCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getAverageUserRating()));
        avgUserRatingCol.setPrefWidth(100);

        // Προσθέτει όλες τις στήλες, συμπεριλαμβανομένης της νέας στήλης totalEpisodesCol.
        seriesTable.getColumns().addAll(titleCol, genreCol, seasonsCol, totalEpisodesCol, avgUserRatingCol);
        seriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainBox.getChildren().addAll(searchPanel, new Separator(), seriesTable); // Προσθέτει το panel αναζήτησης, ένα διαχωριστικό και τον πίνακα.
        VBox.setVgrow(seriesTable, Priority.ALWAYS); // Επιτρέπει στον πίνακα να επεκτείνεται κατακόρυφα.

        return mainBox;
    }

    /**
     * Ενημερώνει τον αριθμό των σεζόν για ένα δεδομένο αντικείμενο Series.
     * Αυτή η μέθοδος προσθέτει ή αφαιρεί αντικείμενα Season από τη λίστα σεζόν της σειράς
     * για να ταιριάζει με τον νέο επιθυμητό αριθμό σεζόν.
     * @param series Το αντικείμενο Series προς ενημέρωση.
     * @param newSeasonCount Ο νέος συνολικός αριθμός σεζόν για τη σειρά.
     */
    private void updateSeriesSeasons(Series series, int newSeasonCount) {
        List<Season> currentSeasons = series.getSeasons(); // Τρέχουσες σεζόν.
        int currentSeasonCount = currentSeasons.size(); // Τρέχων αριθμός σεζόν.

        if (newSeasonCount > currentSeasonCount) {
            // Προσθήκη νέων σεζόν.
            for (int i = currentSeasonCount + 1; i <= newSeasonCount; i++) {
                // Διόρθωση: Περάστε τόσο τον αριθμό σεζόν (i) όσο και ένα έτος.
                // Για τις δυναμικά προστιθέμενες σεζόν, το τρέχον έτος είναι μια λογική προεπιλογή.
                Season newSeason = new Season(i, LocalDate.now().getYear());
                series.addSeason(newSeason);
            }
        } else if (newSeasonCount < currentSeasonCount) {
            // Αφαίρεση σεζόν.
            // Αφαιρείται από το τέλος για να αποφευχθούν προβλήματα μετατόπισης.
            for (int j = currentSeasonCount - 1; j >= newSeasonCount; j--) {
                currentSeasons.remove(j);
            }
        }

        // Ανανεώνει τον πίνακα για να αντικατοπτρίζει τις αλλαγές (ειδικά αν τα υποκείμενα δεδομένα δεν είναι απευθείας παρατηρήσιμα).
        seriesTable.refresh();
        performSeriesSearch(); // Εφαρμόζει ξανά φίλτρα και ταξινόμηση αν χρειάζεται.
    }


    // Δημιουργεί και επιστρέφει το περιεχόμενο της καρτέλας Προσθήκη Ταινίας.
    private VBox createAddMovieTab() {
        VBox mainBox = new VBox(15); // Κύριος container.
        mainBox.setPadding(new Insets(20));

        Label titleLabel = new Label("➕ Προσθήκη Νέας Ταινίας");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        // Δημιουργία panel φόρμας.
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20));
        formPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");

        // Πεδίο τίτλου.
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label titleFieldLabel = new Label("Τίτλος:");
        titleFieldLabel.setPrefWidth(120);
        addMovieTitleField = new TextField();
        addMovieTitleField.setPrefWidth(300);
        addMovieTitleField.setPromptText("Εισάγετε τον τίτλο της ταινίας...");
        titleRow.getChildren().addAll(titleFieldLabel, addMovieTitleField);

        // Γραμμή Έτους και Διάρκειας.
        HBox yearDurationRow = new HBox(20);
        yearDurationRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox yearBox = new HBox(10);
        yearBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label yearLabel = new Label("Έτος:");
        yearLabel.setPrefWidth(120);
        addMovieYearSpinner = new Spinner<>(1900, 2030, 2024, 1); // Spinner για το έτος.
        addMovieYearSpinner.setPrefWidth(100);
        addMovieYearSpinner.setEditable(true);
        yearBox.getChildren().addAll(yearLabel, addMovieYearSpinner);

        HBox durationBox = new HBox(10);
        durationBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label durationLabel = new Label("Διάρκεια (λεπτά):");
        durationLabel.setPrefWidth(120);
        addMovieDurationSpinner = new Spinner<>(1, 500, 120, 1); // Spinner για τη διάρκεια.
        addMovieDurationSpinner.setPrefWidth(100);
        addMovieDurationSpinner.setEditable(true);
        durationBox.getChildren().addAll(durationLabel, addMovieDurationSpinner);

        yearDurationRow.getChildren().addAll(yearBox, durationBox);

        // Πεδίο Είδους.
        HBox genreRow = new HBox(10);
        genreRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label genreLabel = new Label("Είδος:");
        genreLabel.setPrefWidth(120);
        addMovieGenreComboBox = new ComboBox<>(); // ComboBox για το είδος.
        addMovieGenreComboBox.getItems().addAll( // Προσθέτει διαθέσιμα είδη.
                "Action", "Adventure", "Comedy", "Crime", "Drama", "Fantasy",
                "Horror", "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller", "Western"
        );
        addMovieGenreComboBox.setPrefWidth(200);
        addMovieGenreComboBox.setEditable(true);
        genreRow.getChildren().addAll(genreLabel, addMovieGenreComboBox);

        // Πεδίο Σκηνοθέτη.
        HBox directorRow = new HBox(10);
        directorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label directorLabel = new Label("Σκηνοθέτης:");
        directorLabel.setPrefWidth(120);
        addMovieDirectorComboBox = new ComboBox<>(); // ComboBox για τον σκηνοθέτη.
        addMovieDirectorComboBox.setItems(FXCollections.observableArrayList(DataLoader.directors)); // Συνδέει με τους διαθέσιμους σκηνοθέτες.
        addMovieDirectorComboBox.setPrefWidth(250);
        addMovieDirectorComboBox.setConverter(new javafx.util.StringConverter<Director>() { // Converter για εμφάνιση ονόματος.
            @Override
            public String toString(Director director) {
                return director != null ? director.getFullName() : "";
            }

            @Override
            public Director fromString(String string) {
                return DataLoader.directors.stream()
                        .filter(d -> d.getFullName().equals(string))
                        .findFirst().orElse(null);
            }
        });
        directorRow.getChildren().addAll(directorLabel, addMovieDirectorComboBox);

        // Πεδίο Ηθοποιού.
        HBox actorRow = new HBox(10);
        actorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label actorLabel = new Label("Πρωταγωνιστής:");
        actorLabel.setPrefWidth(120);
        addMovieActorComboBox = new ComboBox<>(); // ComboBox για τον ηθοποιό.
        addMovieActorComboBox.setItems(FXCollections.observableArrayList(DataLoader.actors)); // Συνδέει με τους διαθέσιμους ηθοποιούς.
        addMovieActorComboBox.setPrefWidth(250);
        addMovieActorComboBox.setConverter(new javafx.util.StringConverter<Actor>() { // Converter για εμφάνιση ονόματος.
            @Override
            public String toString(Actor actor) {
                return actor != null ? actor.getFullName() : "";
            }

            @Override
            public Actor fromString(String string) {
                return DataLoader.actors.stream()
                        .filter(a -> a.getFullName().equals(string))
                        .findFirst().orElse(null);
            }
        });
        actorRow.getChildren().addAll(actorLabel, addMovieActorComboBox);

        // Πεδίο Βαθμολογίας IMDB.
        HBox imdbRow = new HBox(10);
        imdbRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label imdbLabel = new Label("Βαθμολογία IMDB:");
        imdbLabel.setPrefWidth(120);
        addMovieImdbSpinner = new Spinner<>(1.0, 10.0, 7.0, 0.1); // Spinner για βαθμολογία IMDB.
        addMovieImdbSpinner.setPrefWidth(100);
        addMovieImdbSpinner.setEditable(true);
        Label imdbHint = new Label("(Αν > 7.5, θα προστεί στις καλύτερες ταινίες του σκηνοθέτη)"); // Υπόδειξη.
        imdbHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        imdbRow.getChildren().addAll(imdbLabel, addMovieImdbSpinner, imdbHint);

        // Κουμπιά.
        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        Button addButton = new Button("Προσθήκη Ταινίας");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        addButton.setPrefWidth(150);
        addButton.setOnAction(e -> addNewMovie()); // Ορίζει ενέργεια για την προσθήκη ταινίας.

        Button clearButton = new Button("Καθαρισμός");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        clearButton.setPrefWidth(150);
        clearButton.setOnAction(e -> clearAddMovieForm()); // Ορίζει ενέργεια για καθαρισμό φόρμας.

        buttonRow.getChildren().addAll(addButton, clearButton);

        formPanel.getChildren().addAll( // Προσθέτει όλα τα πεδία και κουμπιά στο formPanel.
                titleRow, yearDurationRow, genreRow,
                directorRow, actorRow, imdbRow, buttonRow
        );

        mainBox.getChildren().addAll(titleLabel, formPanel); // Προσθέτει τον τίτλο και το formPanel στον κύριο container.
        return mainBox;
    }

    // Δημιουργεί και επιστρέφει το περιεχόμενο της καρτέλας Αξιολογήσεις.
    private VBox createRatingsTab() {
        VBox mainBox = new VBox(10);
        mainBox.setPadding(new Insets(10));

        // Δημιουργία panel πληροφοριών τρέχοντος χρήστη.
        HBox userInfoPanel = new HBox(10);
        userInfoPanel.setPadding(new Insets(10));
        userInfoPanel.setStyle("-fx-background-color: #e9f7ef; -fx-background-radius: 5; -fx-border-color: #c8e6c9; -fx-border-radius: 5;");

        // Αρχικοποιεί τον τρέχοντα χρήστη (μπορείτε να το τροποποιήσετε για να το πάρετε από τη σύνδεση).
        loggedInUser = new User(loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getUsername(), loggedInUser.getEmail(), loggedInUser.getId());


        Label userInfoLabel = new Label(String.format("👤 Χρήστης: %s %s (ID: %d)",
                loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getId()));
        userInfoLabel.setStyle("-fx-font-weight: bold;");
        userInfoPanel.getChildren().add(userInfoLabel);

        // Δημιουργία φόρμας αξιολόγησης.
        VBox ratingForm = new VBox(10);
        ratingForm.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");

        Label titleLabel = new Label("⭐ Αξιολόγηση Ταινίας");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Επιλογή ταινίας.
        HBox movieRow = new HBox(10);

        Label movieLabel = new Label("Ταινία:");
        movieLabel.setPrefWidth(100);
        movieToRateField = new TextField();
        movieToRateField.setPromptText("Εισάγετε τον τίτλο της ταινίας...");
        movieRow.getChildren().addAll(movieLabel, movieToRateField);

        // Περιγραφή αξιολόγησης.
        HBox descRow = new HBox(10);

        Label descLabel = new Label("Περιγραφή:");
        descLabel.setPrefWidth(100);
        ratingDescriptionArea = new TextArea();
        ratingDescriptionArea.setPromptText("Γράψτε την κριτική σας...");
        ratingDescriptionArea.setPrefRowCount(3);
        descRow.getChildren().addAll(descLabel, ratingDescriptionArea);

        // Κουμπί υποβολής.
        Button submitButton = new Button("Υποβολή Αξιολόγησης");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOnAction(e -> submitRating()); // Ορίζει ενέργεια για την υποβολή αξιολόγησης.

        ratingForm.getChildren().addAll(titleLabel, movieRow, descRow, submitButton);

        // Πίνακας αξιολογήσεων.
        ratingsTable = new TableView<>();
        ratingsData = FXCollections.observableArrayList(); // Αρχικοποιεί τα δεδομένα αξιολογήσεων.
        ratingsTable.setItems(ratingsData); // Συνδέει τον πίνακα με τα δεδομένα αξιολογήσεων.

        // Ορισμός στηλών για τον πίνακα αξιολογήσεων.
        TableColumn<RatingEntry, String> movieCol = new TableColumn<>("Ταινία");
        movieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMovieTitle()));
        movieCol.setPrefWidth(200);

        TableColumn<RatingEntry, String> userCol = new TableColumn<>("Χρήστης");
        userCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserInfo()));
        userCol.setPrefWidth(150);

        TableColumn<RatingEntry, String> descCol = new TableColumn<>("Περιγραφή");
        descCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        descCol.setPrefWidth(300);

        ratingsTable.getColumns().addAll(movieCol, userCol, descCol);
        ratingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainBox.getChildren().addAll(userInfoPanel, ratingForm, new Separator(), ratingsTable);
        VBox.setVgrow(ratingsTable, Priority.ALWAYS);

        return mainBox;
    }

    // Εσωτερική κλάση για την αναπαράσταση μιας καταχώρησης αξιολόγησης.
    public static class RatingEntry {
        private String movieTitle;
        private String userInfo;
        private String description;

        // Κατασκευαστής.
        public RatingEntry(String movieTitle, String userInfo, String description) {
            this.movieTitle = movieTitle;
            this.userInfo = userInfo;
            this.description = description;
        }

        // Getters.
        public String getMovieTitle() {
            return movieTitle;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public String getDescription() {
            return description;
        }
    }

    // Μέθοδος για τον χειρισμό της υποβολής αξιολόγησης.
    private void submitRating() {
        String movieTitle = movieToRateField.getText().trim(); // Λαμβάνει τον τίτλο της ταινίας.
        String description = ratingDescriptionArea.getText().trim(); // Λαμβάνει την περιγραφή.

        if (movieTitle.isEmpty()) {
            showAlert("Σφάλμα", "Παρακαλώ εισάγετε τίτλο ταινίας.", Alert.AlertType.ERROR);
            return;
        }

        if (description.isEmpty()) {
            showAlert("Σφάλμα", "Παρακαλώ εισάγετε περιγραφή.", Alert.AlertType.ERROR);
            return;
        }

        // Ελέγχει αν η ταινία υπάρχει.
        Movie targetMovie = allMovies.stream()
                .filter(movie -> movie.getTitle().equalsIgnoreCase(movieTitle))
                .findFirst()
                .orElse(null);

        if (targetMovie == null) {
            showAlert("Σφάλμα", "Η ταινία δεν βρέθηκε στη βάση δεδομένων.", Alert.AlertType.ERROR);
            return;
        }

        // Δημιουργεί και προσθέτει την καταχώρηση αξιολόγησης.
        String userInfo = String.format("%s %s (ID: %d)",
                loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getId());
        RatingEntry newEntry = new RatingEntry(movieTitle, userInfo, description);
        ratingsData.add(newEntry); // Προσθέτει την νέα αξιολόγηση στα δεδομένα.

        // Για απλότητα, ας υποθέσουμε ότι η υποβολή μιας αξιολόγησης ενημερώνει επίσης τη βαθμολογία χρήστη της ταινίας.
        // Σε μια πραγματική εφαρμογή, αυτό θα περιλάμβανε τον υπολογισμό μέσου όρου, αλλά προς το παρόν, απλώς θα ενεργοποιήσουμε μια ανανέωση
        // που επηρεάζει την εμφάνιση του 'top content'.
        // Για να ενημερώσετε πραγματικά τη μέση βαθμολογία χρήστη της ταινίας, θα πρέπει να προσθέσετε λογική εδώ.
        // Για παράδειγμα: targetMovie.addUserRating(someRatingValue);

        // Καθαρίζει τη φόρμα.
        movieToRateField.clear();
        ratingDescriptionArea.clear();

        showAlert("Επιτυχία", "Η αξιολόγηση υποβλήθηκε επιτυχώς!", Alert.AlertType.INFORMATION);
        populateTopContent(); // Νέο: Ενημερώνει το κορυφαίο περιεχόμενο μετά την υποβολή αξιολόγησης.
    }

    // Δημιουργεί και επιστρέφει το περιεχόμενο της καρτέλας Προσθήκη Σειράς.
    private VBox createAddSeriesTab() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));

        Label titleLabel = new Label("➕ Προσθήκη Νέας Σειράς");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        // Δημιουργία panel φόρμας.
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20));
        formPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");

        // Πεδίο τίτλου.
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label titleFieldLabel = new Label("Τίτλος:");
        titleFieldLabel.setPrefWidth(120);
        addSeriesTitleField = new TextField();
        addSeriesTitleField.setPrefWidth(300);
        addSeriesTitleField.setPromptText("Εισάγετε τον τίτλο της σειράς...");
        titleRow.getChildren().addAll(titleFieldLabel, addSeriesTitleField);

        // Γραμμή Έτους και Σεζόν.
        HBox yearSeasonsRow = new HBox(20);
        yearSeasonsRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox yearBox = new HBox(10);
        yearBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label yearLabel = new Label("Έτος:");
        yearLabel.setPrefWidth(120);
        addSeriesYearSpinner = new Spinner<>(1900, 2030, 2024, 1); // Spinner για το έτος.
        addSeriesYearSpinner.setPrefWidth(100);
        addSeriesYearSpinner.setEditable(true);
        yearBox.getChildren().addAll(yearLabel, addSeriesYearSpinner);

        HBox seasonsBox = new HBox(10);
        seasonsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label seasonsLabel = new Label("Αριθμός Σεζόν:");
        seasonsLabel.setPrefWidth(120);
        addSeriesSeasonsSpinner = new Spinner<>(1, 50, 1, 1); // Spinner για τον αριθμό σεζόν.
        addSeriesSeasonsSpinner.setPrefWidth(100);
        addSeriesSeasonsSpinner.setEditable(true);
        seasonsBox.getChildren().addAll(seasonsLabel, addSeriesSeasonsSpinner);

        yearSeasonsRow.getChildren().addAll(yearBox, seasonsBox);

        // Πεδίο Είδους.
        HBox genreRow = new HBox(10);
        genreRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label genreLabel = new Label("Είδος:");
        genreLabel.setPrefWidth(120);
        addSeriesGenreComboBox = new ComboBox<>(); // ComboBox για το είδος.
        addSeriesGenreComboBox.getItems().addAll(
                "Action", "Adventure", "Comedy", "Crime", "Drama", "Fantasy",
                "Horror", "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller", "Western"
        );
        addSeriesGenreComboBox.setPrefWidth(200);
        addSeriesGenreComboBox.setEditable(true);
        genreRow.getChildren().addAll(genreLabel, addSeriesGenreComboBox);

        // Πεδίο Σκηνοθέτη.
        HBox directorRow = new HBox(10);
        directorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label directorLabel = new Label("Σκηνοθέτης:");
        directorLabel.setPrefWidth(120);
        addSeriesDirectorComboBox = new ComboBox<>(); // ComboBox για τον σκηνοθέτη.
        addSeriesDirectorComboBox.setItems(FXCollections.observableArrayList(DataLoader.directors));
        addSeriesDirectorComboBox.setPrefWidth(250);
        addSeriesDirectorComboBox.setConverter(new javafx.util.StringConverter<Director>() {
            @Override
            public String toString(Director director) {
                return director != null ? director.getFullName() : "";
            }

            @Override
            public Director fromString(String string) {
                return DataLoader.directors.stream()
                        .filter(d -> d.getFullName().equals(string))
                        .findFirst().orElse(null);
            }
        });
        directorRow.getChildren().addAll(directorLabel, addSeriesDirectorComboBox);

        // Πεδίο Ηθοποιού.
        HBox actorRow = new HBox(10);
        actorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label actorLabel = new Label("Πρωταγωνιστής:");
        actorLabel.setPrefWidth(120);
        addSeriesActorComboBox = new ComboBox<>(); // ComboBox για τον ηθοποιό.
        addSeriesActorComboBox.setItems(FXCollections.observableArrayList(DataLoader.actors));
        addSeriesActorComboBox.setPrefWidth(250);
        addSeriesActorComboBox.setConverter(new javafx.util.StringConverter<Actor>() {
            @Override
            public String toString(Actor actor) {
                return actor != null ? actor.getFullName() : "";
            }

            @Override
            public Actor fromString(String string) {
                return DataLoader.actors.stream()
                        .filter(a -> a.getFullName().equals(string))
                        .findFirst().orElse(null);
            }
        });
        actorRow.getChildren().addAll(actorLabel, addSeriesActorComboBox);

        // Κουμπιά.
        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        Button addButton = new Button("Προσθήκη Σειράς");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        addButton.setPrefWidth(150);
        addButton.setOnAction(e -> addNewSeries()); // Ορίζει ενέργεια για την προσθήκη σειράς.

        Button clearButton = new Button("Καθαρισμός");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        clearButton.setPrefWidth(150);
        clearButton.setOnAction(e -> clearAddSeriesForm()); // Ορίζει ενέργεια για καθαρισμό φόρμας.

        buttonRow.getChildren().addAll(addButton, clearButton);

        formPanel.getChildren().addAll( // Προσθέτει όλα τα πεδία και κουμπιά στο formPanel.
                titleRow, yearSeasonsRow, genreRow,
                directorRow, actorRow, buttonRow
        );

        mainBox.getChildren().addAll(titleLabel, formPanel); // Προσθέτει τον τίτλο και το formPanel στον κύριο container.
        return mainBox;
    }

    // Προσθέτει μια νέα ταινία.
    private void addNewMovie() {
        try {
            // Επικυρώνει τα πεδία εισόδου.
            if (addMovieTitleField.getText().trim().isEmpty()) {
                showAlert("Σφάλμα", "Παρακαλώ εισάγετε τίτλο ταινίας.", Alert.AlertType.ERROR);
                return;
            }

            if (addMovieGenreComboBox.getValue() == null || addMovieGenreComboBox.getValue().trim().isEmpty()) {
                showAlert("Σφάλμα", "Παρακαλώ επιλέξτε είδος ταινίας.", Alert.AlertType.ERROR);
                return;
            }

            if (addMovieDirectorComboBox.getValue() == null) {
                showAlert("Σφάλμα", "Παρακαλώ επιλέξτε σκηνοθέτη.", Alert.AlertType.ERROR);
                return;
            }

            if (addMovieActorComboBox.getValue() == null) {
                showAlert("Σφάλμα", "Παρακαλώ επιλέξτε πρωταγωνιστή.", Alert.AlertType.ERROR);
                return;
            }

            // Δημιουργεί νέα ταινία.
            Movie newMovie = new Movie(
                    addMovieTitleField.getText().trim(),
                    addMovieYearSpinner.getValue(),
                    addMovieGenreComboBox.getValue().trim(),
                    addMovieDurationSpinner.getValue(),
                    addMovieDirectorComboBox.getValue(),
                    addMovieImdbSpinner.getValue(),
                    addMovieActorComboBox.getValue()
            );

            // Ελέγχει αν η βαθμολογία IMDB είναι πάνω από 7.5 για να προστεθεί στα καλύτερα έργα του σκηνοθέτη.
            if (addMovieImdbSpinner.getValue() > 7.5) {
                Director director = addMovieDirectorComboBox.getValue();
                director.addBestWork(newMovie.getTitle()); // Προσθέτει στα καλύτερα έργα του σκηνοθέτη.

                showAlert("Επιτυχία",
                        "Η ταινία προστέθηκε επιτυχώς!\n" +
                                "Επίσης προστέθηκε στις καλύτερες ταινίες του σκηνοθέτη " +
                                director.getFullName() + " λόγω υψηλής βαθμολογίας IMDB (" +
                                addMovieImdbSpinner.getValue() + " > 7.5).",
                        Alert.AlertType.INFORMATION);
            } else {
                showAlert("Επιτυχία", "Η ταινία προστέθηκε επιτυχώς!", Alert.AlertType.INFORMATION);
            }

            // Προσθέτει στις συλλογές.
            DataLoader.movies.add(newMovie); // Προσθέτει στα δεδομένα του DataLoader.
            allMovies.add(newMovie); // Προσθέτει στην ObservableList.

            // Ταξινομεί τις ταινίες με βάση τη μέση βαθμολογία χρήστη (η νέα ταινία θα έχει αρχικά 0.0).
            allMovies.sort(Comparator.comparingDouble(Movie::getAverageUserRating).reversed());

            // Ανανεώνει τις φιλτραρισμένες ταινίες και εκτελεί αναζήτηση για ενημέρωση της εμφάνισης.
            performMovieSearch();
            populateTopContent(); // Νέο: Ενημερώνει το κορυφαίο περιεχόμενο μετά την προσθήκη νέας ταινίας.

            // Καθαρίζει τη φόρμα.
            clearAddMovieForm();

        } catch (IllegalArgumentException ex) {
            showAlert("Σφάλμα", "Μη έγκυρη βαθμολογία IMDB: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Σφάλμα", "Παρουσιάστηκε σφάλμα κατά την προσθήκη της ταινίας: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Προσθέτει μια νέα σειρά.
    private void addNewSeries() {
        try {
            // Επικυρώνει τα πεδία εισόδου.
            if (addSeriesTitleField.getText().trim().isEmpty()) {
                showAlert("Σφάλμα", "Παρακαλώ εισάγετε τίτλο σειράς.", Alert.AlertType.ERROR);
                return;
            }

            if (addSeriesGenreComboBox.getValue() == null || addSeriesGenreComboBox.getValue().trim().isEmpty()) {
                showAlert("Σφάλμα", "Παρακαλώ επιλέξτε είδος σειράς.", Alert.AlertType.ERROR);
                return;
            }

            if (addSeriesDirectorComboBox.getValue() == null) {
                showAlert("Σφάλμα", "Παρακαλώ επιλέξτε σκηνοθέτη.", Alert.AlertType.ERROR);
                return;
            }

            if (addSeriesActorComboBox.getValue() == null) {
                showAlert("Σφάλμα", "Παρακαλώ επιλέξτε πρωταγωνιστή.", Alert.AlertType.ERROR);
                return;
            }

            // Δημιουργεί νέα σειρά.
            Series newSeries = new Series(
                    addSeriesTitleField.getText().trim(),
                    addSeriesGenreComboBox.getValue().trim()
            );

            // Προσθέτει σεζόν στη σειρά.
            int numberOfSeasons = addSeriesSeasonsSpinner.getValue();
            int seriesStartYear = addSeriesYearSpinner.getValue(); // Λαμβάνει το έτος από τον spinner.

            for (int i = 1; i <= numberOfSeasons; i++) {
                // Διόρθωση: Περάστε τόσο τον αριθμό σεζόν όσο και το έτος έναρξης της σειράς.
                Season season = new Season(i, seriesStartYear);
                newSeries.addSeason(season);
            }

            // Όταν προσθέτετε μια νέα σειρά, ορίστε τα συνολικά επεισόδια της με βάση τις σεζόν που προστέθηκαν.
            // Αυτή είναι μια κοινή προσέγγιση αν τα συνολικά επεισόδια είναι μια παράγωγη τιμή κατά τη δημιουργία.
            // Αυτή η γραμμή υποθέτει ότι έχετε setTotalEpisodes και calculateTotalEpisodesFromSeasons στην κλάση Series.
            // newSeries.setTotalEpisodes(newSeries.calculateTotalEpisodesFromSeasons());


            showAlert("Επιτυχία", "Η σειρά προστέθηκε επιτυχώς με " + numberOfSeasons + " σεζόν!", Alert.AlertType.INFORMATION);

            // Προσθέτει στις συλλογές.
            DataLoader.seriesList.add(newSeries);
            allSeries.add(newSeries);

            // Ταξινομεί τις σειρές με βάση τη μέση βαθμολογία χρήστη (η νέα σειρά θα έχει αρχικά 0.0).
            allSeries.sort(Comparator.comparingDouble(Series::getAverageUserRating).reversed());

            // Ανανεώνει τις φιλτραρισμένες σειρές και εκτελεί αναζήτηση για ενημέρωση της εμφάνισης.
            performSeriesSearch();
            populateTopContent(); // Νέο: Ενημερώνει το κορυφαίο περιεχόμενο μετά την προσθήκη νέας σειράς.

            // Καθαρίζει τη φόρμα.
            clearAddSeriesForm();

        } catch (Exception ex) {
            showAlert("Σφάλμα", "Παρουσιάστηκε σφάλμα κατά την προσθήκη της σειράς: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Καθαρίζει τη φόρμα προσθήκης ταινίας.
    private void clearAddMovieForm() {
        addMovieTitleField.clear();
        addMovieYearSpinner.getValueFactory().setValue(2024);
        addMovieGenreComboBox.setValue(null);
        addMovieDurationSpinner.getValueFactory().setValue(120);
        addMovieDirectorComboBox.setValue(null);
        addMovieActorComboBox.setValue(null);
        addMovieImdbSpinner.getValueFactory().setValue(7.0);
    }

    // Καθαρίζει τη φόρμα προσθήκης σειράς.
    private void clearAddSeriesForm() {
        addSeriesTitleField.clear();
        addSeriesYearSpinner.getValueFactory().setValue(2024);
        addSeriesGenreComboBox.setValue(null);
        addSeriesSeasonsSpinner.getValueFactory().setValue(1);
        addSeriesDirectorComboBox.setValue(null);
        addSeriesActorComboBox.setValue(null);
    }

    // Δημιουργεί το panel αναζήτησης ταινιών.
    private VBox createMovieSearchPanel() {
        VBox searchPanel = new VBox(10);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        Label searchLabel = new Label("🔍 Αναζήτηση Ταινιών");
        searchLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Πρώτη γραμμή: Αναζήτηση τίτλου και ηθοποιού.
        HBox firstRow = new HBox(15);
        firstRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Τίτλος:");
        movieTitleSearchField = new TextField();
        movieTitleSearchField.setPromptText("Αναζήτηση τίτλου...");
        movieTitleSearchField.setPrefWidth(200);
        movieTitleSearchField.textProperty().addListener((obs, oldVal, newVal) -> performMovieSearch()); // Ενεργοποιεί αναζήτηση при αλλαγή κειμένου.
        titleBox.getChildren().addAll(titleLabel, movieTitleSearchField);

        VBox actorBox = new VBox(5);
        Label actorLabel = new Label("Ηθοποιός:");
        movieActorSearchField = new TextField();
        movieActorSearchField.setPromptText("Αναζήτηση ηθοποιού...");
        movieActorSearchField.setPrefWidth(200);
        movieActorSearchField.textProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        actorBox.getChildren().addAll(actorLabel, movieActorSearchField);

        firstRow.getChildren().addAll(titleBox, actorBox);

        // Δεύτερη γραμμή: Αναζήτηση σκηνοθέτη και βαθμολογία IMDB.
        HBox secondRow = new HBox(15);
        secondRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox directorBox = new VBox(5);
        Label directorLabel = new Label("Σκηνοθέτης:");
        movieDirectorSearchField = new TextField();
        movieDirectorSearchField.setPromptText("Αναζήτηση σκηνοθέτη...");
        movieDirectorSearchField.setPrefWidth(200);
        movieDirectorSearchField.textProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        directorBox.getChildren().addAll(directorLabel, movieDirectorSearchField);

        VBox imdbBox = new VBox(5);
        Label imdbLabel = new Label("Ελάχιστο IMDB:");
        movieMinImdbSpinner = new Spinner<>(0.0, 10.0, 0.0, 0.1); // Spinner για ελάχιστο IMDB.
        movieMinImdbSpinner.setPrefWidth(120);
        movieMinImdbSpinner.setEditable(true);
        movieMinImdbSpinner.valueProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        imdbBox.getChildren().addAll(imdbLabel, movieMinImdbSpinner);

        secondRow.getChildren().addAll(directorBox, imdbBox);

        // Τρίτη γραμμή: Βαθμολογία χρήστη.
        HBox thirdRow = new HBox(15);
        thirdRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox userRatingBox = new VBox(5);
        Label userRatingLabel = new Label("Ελάχιστη Βαθμολογία Χρηστών:");
        movieMinUserRatingSpinner = new Spinner<>(0.0, 10.0, 0.0, 0.1); // Spinner για ελάχιστη βαθμολογία χρήστη.
        movieMinUserRatingSpinner.setPrefWidth(120);
        movieMinUserRatingSpinner.setEditable(true);
        movieMinUserRatingSpinner.valueProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        userRatingBox.getChildren().addAll(userRatingLabel, movieMinUserRatingSpinner);

        Button clearButton = new Button("Καθαρισμός Φίλτρων");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearMovieFilters()); // Ορίζει ενέργεια για καθαρισμό φίλτρων.

        thirdRow.getChildren().addAll(userRatingBox, clearButton);

        searchPanel.getChildren().addAll(searchLabel, firstRow, secondRow, thirdRow);
        return searchPanel;
    }

    // Δημιουργεί το panel αναζήτησης σειρών.
    private VBox createSeriesSearchPanel() {
        VBox searchPanel = new VBox(10);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        Label searchLabel = new Label("🔍 Αναζήτηση Σειρών");
        searchLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox searchRow = new HBox(15);
        searchRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Τίτλος:");
        seriesTitleSearchField = new TextField();
        seriesTitleSearchField.setPromptText("Αναζήτηση τίτλου σειράς...");
        seriesTitleSearchField.setPrefWidth(200);
        seriesTitleSearchField.textProperty().addListener((obs, oldVal, newVal) -> performSeriesSearch());
        titleBox.getChildren().addAll(titleLabel, seriesTitleSearchField);

        VBox userRatingBox = new VBox(5);
        Label userRatingLabel = new Label("Ελάχιστη Βαθμολογία Χρηστών:");
        seriesMinUserRatingSpinner = new Spinner<>(0.0, 10.0, 0.0, 0.1); // Spinner για ελάχιστη βαθμολογία χρήστη.
        seriesMinUserRatingSpinner.setPrefWidth(120);
        seriesMinUserRatingSpinner.setEditable(true);
        seriesMinUserRatingSpinner.valueProperty().addListener((obs, oldVal, newVal) -> performSeriesSearch());
        userRatingBox.getChildren().addAll(userRatingLabel, seriesMinUserRatingSpinner);

        Button clearButton = new Button("Καθαρισμός Φίλτρων");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearSeriesFilters());

        searchRow.getChildren().addAll(titleBox, userRatingBox, clearButton);

        searchPanel.getChildren().addAll(searchLabel, searchRow);
        return searchPanel;
    }

    // Εκτελεί την αναζήτηση και το φιλτράρισμα ταινιών.
    private void performMovieSearch() {
        String titleSearch = movieTitleSearchField.getText().toLowerCase().trim();
        String actorSearch = movieActorSearchField.getText().toLowerCase().trim();
        String directorSearch = movieDirectorSearchField.getText().toLowerCase().trim();
        double minImdb = movieMinImdbSpinner.getValue();
        double minUserRating = movieMinUserRatingSpinner.getValue();

        List<Movie> filtered = allMovies.stream()
                .filter(movie -> titleSearch.isEmpty() ||
                        movie.getTitle().toLowerCase().contains(titleSearch)) // Φιλτράρισμα κατά τίτλο.
                .filter(movie -> actorSearch.isEmpty() ||
                        movie.getLeadActor().getFullName().toLowerCase().contains(actorSearch)) // Φιλτράρισμα κατά ηθοποιό.
                .filter(movie -> directorSearch.isEmpty() ||
                        movie.getDirector().getFullName().toLowerCase().contains(directorSearch)) // Φιλτράρισμα κατά σκηνοθέτη.
                .filter(movie -> movie.getImdbRating() >= minImdb) // Φιλτράρισμα κατά ελάχιστο IMDB.
                .filter(movie -> movie.getAverageUserRating() >= minUserRating) // Φιλτράρισμα κατά ελάχιστη βαθμολογία χρήστη.
                .collect(Collectors.toList());

        filteredMovies.clear(); // Καθαρίζει την προηγούμενη λίστα.
        filteredMovies.addAll(filtered); // Προσθέτει τα φιλτραρισμένα αποτελέσματα.
    }

    // Εκτελεί την αναζήτηση και το φιλτράρισμα σειρών.
    private void performSeriesSearch() {
        String titleSearch = seriesTitleSearchField.getText().toLowerCase().trim();
        double minUserRating = seriesMinUserRatingSpinner.getValue();

        List<Series> filtered = allSeries.stream()
                .filter(series -> titleSearch.isEmpty() ||
                        series.getTitle().toLowerCase().contains(titleSearch)) // Φιλτράρισμα κατά τίτλο.
                .filter(series -> series.getAverageUserRating() >= minUserRating) // Φιλτράρισμα κατά ελάχιστη βαθμολογία χρήστη.
                .collect(Collectors.toList());

        filteredSeries.clear();
        filteredSeries.addAll(filtered);
    }

    // Καθαρίζει τα φίλτρα αναζήτησης ταινιών.
    private void clearMovieFilters() {
        movieTitleSearchField.clear();
        movieActorSearchField.clear();
        movieDirectorSearchField.clear();
        movieMinImdbSpinner.getValueFactory().setValue(0.0);
        movieMinUserRatingSpinner.getValueFactory().setValue(0.0);
        performMovieSearch(); // Εφαρμόζει ξανά την αναζήτηση μετά τον καθαρισμό.
    }

    // Καθαρίζει τα φίλτρα αναζήτησης σειρών.
    private void clearSeriesFilters() {
        seriesTitleSearchField.clear();
        seriesMinUserRatingSpinner.getValueFactory().setValue(0.0);
        performSeriesSearch(); // Εφαρμόζει ξανά την αναζήτηση μετά τον καθαρισμό.
    }

    // Εμφανίζει ένα αναδυόμενο παράθυρο (Alert Dialog).
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Δημιουργεί την καρτέλα για την εμφάνιση των κορυφαίων ταινιών και σειρών.
     * @return Ένα VBox που περιέχει το UI για την καρτέλα κορυφαίου περιεχομένου.
     */
    private VBox createTopContentTab() {
        VBox mainBox = new VBox(10);
        mainBox.setPadding(new Insets(10));

        Label titleLabel = new Label("🏆 Top Ταινίες & Σειρές (Βαθμολογία ≥ 7.5)");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");
        mainBox.getChildren().add(titleLabel);

        // --- Πίνακας Κορυφαίων Ταινιών ---
        Label moviesLabel = new Label("Κορυφαίες Ταινίες:");
        moviesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 0 0 0;");
        mainBox.getChildren().add(moviesLabel);

        TableView<Movie> topMovieTable = new TableView<>();
        topMovieTable.setItems(topMovies); // Συνδέει με τη νέα observable list.

        TableColumn<Movie, String> movieTitleCol = new TableColumn<>("Τίτλος");
        movieTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        movieTitleCol.setPrefWidth(200);

        TableColumn<Movie, Number> movieImdbCol = new TableColumn<>("IMDb");
        movieImdbCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getImdbRating()));
        movieImdbCol.setPrefWidth(80);

        topMovieTable.getColumns().addAll(movieTitleCol, movieImdbCol); // Μόνο τίτλος και IMDb.
        topMovieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        topMovieTable.setPrefHeight(250); // Περιορίζει το ύψος για ορατότητα.

        mainBox.getChildren().addAll(topMovieTable, new Separator());
        VBox.setVgrow(topMovieTable, Priority.NEVER); // Να μην μεγαλώνει υπερβολικά ο πίνακας ταινιών.

        // --- Πίνακας Κορυφαίων Σειρών ---
        Label seriesLabel = new Label("Κορυφαίες Σειρές:");
        seriesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 0 0 0;");
        mainBox.getChildren().add(seriesLabel);

        TableView<Series> topSeriesTable = new TableView<>();
        topSeriesTable.setItems(topSeries); // Συνδέει με τη νέα observable list.

        TableColumn<Series, String> seriesTitleCol = new TableColumn<>("Τίτλος");
        seriesTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        seriesTitleCol.setPrefWidth(200);

        TableColumn<Series, Number> seriesAvgUserRatingCol = new TableColumn<>("Μ.Ο. Χρηστών");
        seriesAvgUserRatingCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAverageUserRating()));
        seriesAvgUserRatingCol.setPrefWidth(100);


        topSeriesTable.getColumns().addAll(seriesTitleCol, seriesAvgUserRatingCol); // Μόνο τίτλος και Μέσος Όρος Βαθμολογίας Χρηστών.
        topSeriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        topSeriesTable.setPrefHeight(250); // Περιορίζει το ύψος για ορατότητα.

        mainBox.getChildren().add(topSeriesTable);
        VBox.setVgrow(topSeriesTable, Priority.NEVER); // Να μην μεγαλώνει υπερβολικά ο πίνακας σειρών.

        return mainBox;
    }

    /**
     * Γεμίζει τις λίστες topMovies και topSeries με βάση κριτήρια βαθμολογίας (>= 7.5).
     */
    private void populateTopContent() {
        // Φιλτράρει ταινίες: ΜΟΝΟ με βαθμολογία IMDb >= 7.5.
        List<Movie> filteredTopMovies = allMovies.stream()
                .filter(movie -> movie.getImdbRating() >= 7.5)
                .sorted(Comparator.comparingDouble(Movie::getImdbRating).reversed()) // Ταξινομεί με βάση τη βαθμολογία IMDb.
                .collect(Collectors.toList());
        topMovies.setAll(filteredTopMovies); // Θέτει όλες τις κορυφαίες ταινίες.

        // Φιλτράρει σειρές: Παραμένει με Μέση Βαθμολογία Χρηστών >= 7.5.
        List<Series> filteredTopSeries = allSeries.stream()
                .filter(series -> series.getAverageUserRating() >= 7.5)
                .sorted(Comparator.comparingDouble(Series::getAverageUserRating).reversed())
                .collect(Collectors.toList());
        topSeries.setAll(filteredTopSeries); // Θέτει όλες τις κορυφαίες σειρές.
    }

    // Η μέθοδος main είναι το σημείο εκκίνησης της εφαρμογής.
    public static void main(String[] args) {
        launch(args); // Ξεκινά την εφαρμογή JavaFX.
    }
}