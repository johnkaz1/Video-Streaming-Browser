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
import model.Season; // Import Season class
import utils.DataLoader;
import java.util.Random;
import model.User;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

import exceptions.InvalidRatingException;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Required for editable TableView cells
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import javafx.beans.property.SimpleIntegerProperty; // Import SimpleIntegerProperty

public class MainApp extends Application {

    private TableView<Movie> movieTable;
    private TableView<Series> seriesTable;
    private ObservableList<Movie> allMovies;
    private ObservableList<Series> allSeries;
    private ObservableList<Movie> filteredMovies;
    private ObservableList<Series> filteredSeries;

    // Search fields for movies
    private TextField movieTitleSearchField;
    private TextField movieActorSearchField;
    private TextField movieDirectorSearchField;
    private Spinner<Double> movieMinImdbSpinner;
    private Spinner<Double> movieMinUserRatingSpinner;

    // Search fields for series
    private TextField seriesTitleSearchField;
    private Spinner<Double> seriesMinUserRatingSpinner;

    // Add Movie form fields
    private TextField addMovieTitleField;
    private Spinner<Integer> addMovieYearSpinner;
    private ComboBox<String> addMovieGenreComboBox;
    private Spinner<Integer> addMovieDurationSpinner;
    private ComboBox<Director> addMovieDirectorComboBox;
    private Spinner<Double> addMovieImdbSpinner;
    private ComboBox<Actor> addMovieActorComboBox;

    // Add Series form fields
    private TextField addSeriesTitleField;
    private Spinner<Integer> addSeriesYearSpinner;
    private ComboBox<String> addSeriesGenreComboBox;
    private Spinner<Integer> addSeriesSeasonsSpinner;
    private ComboBox<Director> addSeriesDirectorComboBox;
    private ComboBox<Actor> addSeriesActorComboBox;

    private TableView<RatingEntry> ratingsTable;
    private ObservableList<RatingEntry> ratingsData;
    private TextField movieToRateField;
    private TextArea ratingDescriptionArea;
    private User loggedInUser;

    //Get random Id for user login
    private static Random random = new Random();

    public static int pickRandomNumber() {
        return random.nextInt(100); // nextInt(100) returns 0-99
    }
    public MainApp(User user) {
        this.loggedInUser = user;
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Movie & Series Manager");

        // Initialize data collections
        allMovies = FXCollections.observableArrayList(
                DataLoader.movies.stream()
                        .sorted(Comparator.comparingDouble(Movie::getAverageUserRating).reversed())
                        .collect(Collectors.toList())
        );

        allSeries = FXCollections.observableArrayList(
                DataLoader.seriesList.stream()
                        .sorted(Comparator.comparingDouble(Series::getAverageUserRating).reversed())
                        .collect(Collectors.toList())
        );

        filteredMovies = FXCollections.observableArrayList(allMovies);
        filteredSeries = FXCollections.observableArrayList(allSeries);

        TabPane tabPane = new TabPane();

        // Tabs
        Tab movieTab = new Tab("🎬 Ταινίες", createMovieTab());
        Tab seriesTab = new Tab("📺 Σειρές", createSeriesTab());
        Tab ratingsTab = new Tab("⭐ Αξιολογήσεις", createRatingsTab());
        Tab addMovieTab = new Tab("➕ Προσθήκη Ταινίας", createAddMovieTab());
        Tab addSeriesTab = new Tab("➕ Προσθήκη Σειράς", createAddSeriesTab());

        tabPane.getTabs().addAll(movieTab, seriesTab, ratingsTab, addMovieTab, addSeriesTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabPane, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMovieTab() {
        VBox mainBox = new VBox(10);
        mainBox.setPadding(new Insets(10));

        // Search panel
        VBox searchPanel = createMovieSearchPanel();

        // Movie table
        movieTable = new TableView<>();
        movieTable.setItems(filteredMovies);

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

        movieTable.getColumns().addAll(titleCol, directorCol, actorCol, imdbCol, avgUserRatingCol, genreCol, yearCol);
        movieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainBox.getChildren().addAll(searchPanel, new Separator(), movieTable);
        VBox.setVgrow(movieTable, Priority.ALWAYS);

        return mainBox;
    }

    private VBox createSeriesTab() {
        VBox mainBox = new VBox(10);
        mainBox.setPadding(new Insets(10));

        // Search panel
        VBox searchPanel = createSeriesSearchPanel();

        // Series table
        seriesTable = new TableView<>();
        seriesTable.setItems(filteredSeries);
        // Enable editing for the table
        seriesTable.setEditable(true);

        TableColumn<Series, String> titleCol = new TableColumn<>("Τίτλος");
        titleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(200);

        TableColumn<Series, String> genreCol = new TableColumn<>("Είδος");
        genreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGenre()));
        genreCol.setPrefWidth(100);

        // Modified: Make seasons column editable
        TableColumn<Series, Integer> seasonsCol = new TableColumn<>("Σεζόν");
        seasonsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getSeasons().size()).asObject());
        seasonsCol.setPrefWidth(80);
        seasonsCol.setEditable(true); // Enable editing for this column
        seasonsCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter())); // Use TextFieldTableCell for integer input

        // Handle commit event when a season count is edited
        seasonsCol.setOnEditCommit(event -> {
            Series series = event.getRowValue();
            int newSeasonCount = event.getNewValue(); // Get the new integer value

            try {
                // Ensure new season count is valid
                if (newSeasonCount < 1) {
                    showAlert("Σφάλμα Επεξεργασίας", "Ο αριθμός των σεζόν πρέπει να είναι τουλάχιστον 1.", Alert.AlertType.ERROR);
                    // Revert to the old value if invalid
                    seriesTable.refresh();
                    return;
                }

                updateSeriesSeasons(series, newSeasonCount);
                showAlert("Επιτυχία", "Ο αριθμός των σεζόν για τη σειρά '" + series.getTitle() + "' ενημερώθηκε σε " + newSeasonCount + ".", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Σφάλμα", "Παρουσιάστηκε σφάλμα κατά την ενημέρωση των σεζόν: " + e.getMessage(), Alert.AlertType.ERROR);
                // Revert to the old value in case of an error
                seriesTable.refresh();
            }
        });


        TableColumn<Series, Number> avgUserRatingCol = new TableColumn<>("Μ.Ο. Χρηστών");
        avgUserRatingCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getAverageUserRating()));
        avgUserRatingCol.setPrefWidth(100);

        seriesTable.getColumns().addAll(titleCol, genreCol, seasonsCol, avgUserRatingCol);
        seriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainBox.getChildren().addAll(searchPanel, new Separator(), seriesTable);
        VBox.setVgrow(seriesTable, Priority.ALWAYS);

        return mainBox;
    }

    /**
     * Updates the number of seasons for a given Series object.
     * This method adds or removes Season objects from the series's season list
     * to match the new desired season count.
     * @param series The Series object to update.
     * @param newSeasonCount The new total number of seasons for the series.
     */
    private void updateSeriesSeasons(Series series, int newSeasonCount) {
        List<Season> currentSeasons = series.getSeasons();
        int currentSeasonCount = currentSeasons.size();

        if (newSeasonCount > currentSeasonCount) {
            // Add new seasons
            for (int i = currentSeasonCount + 1; i <= newSeasonCount; i++) {
                // Assuming Season constructor takes season number and initializes with default episodes (e.g., 10)
                Season newSeason = new Season(i);
                series.addSeason(newSeason);
            }
        } else if (newSeasonCount < currentSeasonCount) {
            // Remove seasons
            // Remove from the end to avoid shifting issues
            for (int i = currentSeasonCount - 1; i >= newSeasonCount; i--) {
                currentSeasons.remove(i);
            }
        }
        // No action needed if newSeasonCount == currentSeasonCount

        // Refresh the table to reflect changes (especially if the underlying data isn't directly observable)
        // A more robust way is to re-filter and re-sort if necessary
        // filteredSeries.setAll(allSeries.stream().sorted(Comparator.comparingDouble(Series::getAverageUserRating).reversed()).collect(Collectors.toList()));
        // Or simply refresh the table view
        seriesTable.refresh();
        performSeriesSearch(); // Re-apply filters and sort if needed, though refresh is usually enough for cell edits
    }


    private VBox createAddMovieTab() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));

        Label titleLabel = new Label("➕ Προσθήκη Νέας Ταινίας");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        // Create form panel
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20));
        formPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");

        // Title field
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label titleFieldLabel = new Label("Τίτλος:");
        titleFieldLabel.setPrefWidth(120);
        addMovieTitleField = new TextField();
        addMovieTitleField.setPrefWidth(300);
        addMovieTitleField.setPromptText("Εισάγετε τον τίτλο της ταινίας...");
        titleRow.getChildren().addAll(titleFieldLabel, addMovieTitleField);

        // Year and Duration row
        HBox yearDurationRow = new HBox(20);
        yearDurationRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox yearBox = new HBox(10);
        yearBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label yearLabel = new Label("Έτος:");
        yearLabel.setPrefWidth(120);
        addMovieYearSpinner = new Spinner<>(1900, 2030, 2024, 1);
        addMovieYearSpinner.setPrefWidth(100);
        addMovieYearSpinner.setEditable(true);
        yearBox.getChildren().addAll(yearLabel, addMovieYearSpinner);

        HBox durationBox = new HBox(10);
        durationBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label durationLabel = new Label("Διάρκεια (λεπτά):");
        durationLabel.setPrefWidth(120);
        addMovieDurationSpinner = new Spinner<>(1, 500, 120, 1);
        addMovieDurationSpinner.setPrefWidth(100);
        addMovieDurationSpinner.setEditable(true);
        durationBox.getChildren().addAll(durationLabel, addMovieDurationSpinner);

        yearDurationRow.getChildren().addAll(yearBox, durationBox);

        // Genre field
        HBox genreRow = new HBox(10);
        genreRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label genreLabel = new Label("Είδος:");
        genreLabel.setPrefWidth(120);
        addMovieGenreComboBox = new ComboBox<>();
        addMovieGenreComboBox.getItems().addAll(
                "Action", "Adventure", "Comedy", "Crime", "Drama", "Fantasy",
                "Horror", "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller", "Western"
        );
        addMovieGenreComboBox.setPrefWidth(200);
        addMovieGenreComboBox.setEditable(true);
        genreRow.getChildren().addAll(genreLabel, addMovieGenreComboBox);

        // Director field
        HBox directorRow = new HBox(10);
        directorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label directorLabel = new Label("Σκηνοθέτης:");
        directorLabel.setPrefWidth(120);
        addMovieDirectorComboBox = new ComboBox<>();
        addMovieDirectorComboBox.setItems(FXCollections.observableArrayList(DataLoader.directors));
        addMovieDirectorComboBox.setPrefWidth(250);
        addMovieDirectorComboBox.setConverter(new javafx.util.StringConverter<Director>() {
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

        // Actor field
        HBox actorRow = new HBox(10);
        actorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label actorLabel = new Label("Πρωταγωνιστής:");
        actorLabel.setPrefWidth(120);
        addMovieActorComboBox = new ComboBox<>();
        addMovieActorComboBox.setItems(FXCollections.observableArrayList(DataLoader.actors));
        addMovieActorComboBox.setPrefWidth(250);
        addMovieActorComboBox.setConverter(new javafx.util.StringConverter<Actor>() {
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

        // IMDB Rating field
        HBox imdbRow = new HBox(10);
        imdbRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label imdbLabel = new Label("Βαθμολογία IMDB:");
        imdbLabel.setPrefWidth(120);
        addMovieImdbSpinner = new Spinner<>(1.0, 10.0, 7.0, 0.1);
        addMovieImdbSpinner.setPrefWidth(100);
        addMovieImdbSpinner.setEditable(true);
        Label imdbHint = new Label("(Αν > 7.5, θα προστεθεί στις καλύτερες ταινίες του σκηνοθέτη)");
        imdbHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        imdbRow.getChildren().addAll(imdbLabel, addMovieImdbSpinner, imdbHint);

        // Buttons
        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        Button addButton = new Button("Προσθήκη Ταινίας");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        addButton.setPrefWidth(150);
        addButton.setOnAction(e -> addNewMovie());

        Button clearButton = new Button("Καθαρισμός");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        clearButton.setPrefWidth(150);
        clearButton.setOnAction(e -> clearAddMovieForm());

        buttonRow.getChildren().addAll(addButton, clearButton);

        formPanel.getChildren().addAll(
                titleRow, yearDurationRow, genreRow,
                directorRow, actorRow, imdbRow, buttonRow
        );

        mainBox.getChildren().addAll(titleLabel, formPanel);
        return mainBox;
    }

    private VBox createRatingsTab() {
        VBox mainBox = new VBox(10);
        mainBox.setPadding(new Insets(10));

        // Create current user info panel
        HBox userInfoPanel = new HBox(10);
        userInfoPanel.setPadding(new Insets(10));
        userInfoPanel.setStyle("-fx-background-color: #e9f7ef; -fx-background-radius: 5; -fx-border-color: #c8e6c9; -fx-border-radius: 5;");

        // Initialize current user (you can modify this to get from login)
        loggedInUser = new User(loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getUsername(), loggedInUser.getEmail(), loggedInUser.getId());


        Label userInfoLabel = new Label(String.format("👤 Χρήστης: %s %s (ID: %d)",
                loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getId()));
        userInfoLabel.setStyle("-fx-font-weight: bold;");
        userInfoPanel.getChildren().add(userInfoLabel);

        // Create rating form
        VBox ratingForm = new VBox(10);
        ratingForm.setPadding(new Insets(15));
        ratingForm.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");

        Label titleLabel = new Label("⭐ Αξιολόγηση Ταινίας");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Movie selection
        HBox movieRow = new HBox(10);

        Label movieLabel = new Label("Ταινία:");
        movieLabel.setPrefWidth(100);
        movieToRateField = new TextField();
        movieToRateField.setPromptText("Εισάγετε τον τίτλο της ταινίας...");
        movieRow.getChildren().addAll(movieLabel, movieToRateField);

        // Rating description
        HBox descRow = new HBox(10);

        Label descLabel = new Label("Περιγραφή:");
        descLabel.setPrefWidth(100);
        ratingDescriptionArea = new TextArea();
        ratingDescriptionArea.setPromptText("Γράψτε την κριτική σας...");
        ratingDescriptionArea.setPrefRowCount(3);
        descRow.getChildren().addAll(descLabel, ratingDescriptionArea);

        // Submit button
        Button submitButton = new Button("Υποβολή Αξιολόγησης");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOnAction(e -> submitRating());

        ratingForm.getChildren().addAll(titleLabel, movieRow, descRow, submitButton);

        // Ratings table
        ratingsTable = new TableView<>();
        ratingsData = FXCollections.observableArrayList();
        ratingsTable.setItems(ratingsData);

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
    public static class RatingEntry {
        private String movieTitle;
        private String userInfo;
        private String description;

        public RatingEntry(String movieTitle, String userInfo, String description) {
            this.movieTitle = movieTitle;
            this.userInfo = userInfo;
            this.description = description;
        }

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

    // Add this method to handle rating submission
    private void submitRating() {
        String movieTitle = movieToRateField.getText().trim();
        String description = ratingDescriptionArea.getText().trim();

        if (movieTitle.isEmpty()) {
            showAlert("Σφάλμα", "Παρακαλώ εισάγετε τίτλο ταινίας.", Alert.AlertType.ERROR);
            return;
        }

        if (description.isEmpty()) {
            showAlert("Σφάλμα", "Παρακαλώ εισάγετε περιγραφή.", Alert.AlertType.ERROR);
            return;
        }

        // Check if movie exists
        boolean movieExists = allMovies.stream()
                .anyMatch(movie -> movie.getTitle().equalsIgnoreCase(movieTitle));

        if (!movieExists) {
            showAlert("Σφάλμα", "Η ταινία δεν βρέθηκε στη βάση δεδομένων.", Alert.AlertType.ERROR);
            return;
        }

        // Create and add the rating entry
        String userInfo = String.format("%s %s (ID: %d)",
                loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getId());
        RatingEntry newEntry = new RatingEntry(movieTitle, userInfo, description);
        ratingsData.add(newEntry);

        // Clear form
        movieToRateField.clear();
        ratingDescriptionArea.clear();

        showAlert("Επιτυχία", "Η αξιολόγηση υποβλήθηκε επιτυχώς!", Alert.AlertType.INFORMATION);
    }

    private VBox createAddSeriesTab() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));

        Label titleLabel = new Label("➕ Προσθήκη Νέας Σειράς");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        // Create form panel
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20));
        formPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");

        // Title field
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label titleFieldLabel = new Label("Τίτλος:");
        titleFieldLabel.setPrefWidth(120);
        addSeriesTitleField = new TextField();
        addSeriesTitleField.setPrefWidth(300);
        addSeriesTitleField.setPromptText("Εισάγετε τον τίτλο της σειράς...");
        titleRow.getChildren().addAll(titleFieldLabel, addSeriesTitleField);

        // Year and Seasons row
        HBox yearSeasonsRow = new HBox(20);
        yearSeasonsRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox yearBox = new HBox(10);
        yearBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label yearLabel = new Label("Έτος:");
        yearLabel.setPrefWidth(120);
        addSeriesYearSpinner = new Spinner<>(1900, 2030, 2024, 1);
        addSeriesYearSpinner.setPrefWidth(100);
        addSeriesYearSpinner.setEditable(true);
        yearBox.getChildren().addAll(yearLabel, addSeriesYearSpinner);

        HBox seasonsBox = new HBox(10);
        seasonsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label seasonsLabel = new Label("Αριθμός Σεζόν:");
        seasonsLabel.setPrefWidth(120);
        addSeriesSeasonsSpinner = new Spinner<>(1, 50, 1, 1);
        addSeriesSeasonsSpinner.setPrefWidth(100);
        addSeriesSeasonsSpinner.setEditable(true);
        seasonsBox.getChildren().addAll(seasonsLabel, addSeriesSeasonsSpinner);

        yearSeasonsRow.getChildren().addAll(yearBox, seasonsBox);

        // Genre field
        HBox genreRow = new HBox(10);
        genreRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label genreLabel = new Label("Είδος:");
        genreLabel.setPrefWidth(120);
        addSeriesGenreComboBox = new ComboBox<>();
        addSeriesGenreComboBox.getItems().addAll(
                "Action", "Adventure", "Comedy", "Crime", "Drama", "Fantasy",
                "Horror", "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller", "Western"
        );
        addSeriesGenreComboBox.setPrefWidth(200);
        addSeriesGenreComboBox.setEditable(true);
        genreRow.getChildren().addAll(genreLabel, addSeriesGenreComboBox);

        // Director field
        HBox directorRow = new HBox(10);
        directorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label directorLabel = new Label("Σκηνοθέτης:");
        directorLabel.setPrefWidth(120);
        addSeriesDirectorComboBox = new ComboBox<>();
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

        // Actor field
        HBox actorRow = new HBox(10);
        actorRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label actorLabel = new Label("Πρωταγωνιστής:");
        actorLabel.setPrefWidth(120);
        addSeriesActorComboBox = new ComboBox<>();
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

        // Buttons
        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        Button addButton = new Button("Προσθήκη Σειράς");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        addButton.setPrefWidth(150);
        addButton.setOnAction(e -> addNewSeries());

        Button clearButton = new Button("Καθαρισμός");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        clearButton.setPrefWidth(150);
        clearButton.setOnAction(e -> clearAddSeriesForm());

        buttonRow.getChildren().addAll(addButton, clearButton);

        formPanel.getChildren().addAll(
                titleRow, yearSeasonsRow, genreRow,
                directorRow, actorRow, buttonRow
        );

        mainBox.getChildren().addAll(titleLabel, formPanel);
        return mainBox;
    }

    private void addNewMovie() {
        try {
            // Validate input fields
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

            // Create new movie
            Movie newMovie = new Movie(
                    addMovieTitleField.getText().trim(),
                    addMovieYearSpinner.getValue(),
                    addMovieGenreComboBox.getValue().trim(),
                    addMovieDurationSpinner.getValue(),
                    addMovieDirectorComboBox.getValue(),
                    addMovieImdbSpinner.getValue(),
                    addMovieActorComboBox.getValue()
            );

            // Check if IMDB rating is above 7.5 to add to director's best works
            if (addMovieImdbSpinner.getValue() > 7.5) {
                Director director = addMovieDirectorComboBox.getValue();
                director.addBestWork(newMovie.getTitle());

                showAlert("Επιτυχία",
                        "Η ταινία προστέθηκε επιτυχώς!\n" +
                                "Επίσης προστέθηκε στις καλύτερες ταινίες του σκηνοθέτη " +
                                director.getFullName() + " λόγω υψηλής βαθμολογίας IMDB (" +
                                addMovieImdbSpinner.getValue() + " > 7.5).",
                        Alert.AlertType.INFORMATION);
            } else {
                showAlert("Επιτυχία", "Η ταινία προστέθηκε επιτυχώς!", Alert.AlertType.INFORMATION);
            }

            // Add to collections
            DataLoader.movies.add(newMovie);
            allMovies.add(newMovie);

            // Sort movies by average user rating (new movie will have 0.0 initially)
            allMovies.sort(Comparator.comparingDouble(Movie::getAverageUserRating).reversed());

            // Refresh filtered movies and perform search to update display
            performMovieSearch();

            // Clear form
            clearAddMovieForm();

        } catch (IllegalArgumentException ex) {
            showAlert("Σφάλμα", "Μη έγκυρη βαθμολογία IMDB: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Σφάλμα", "Παρουσιάστηκε σφάλμα κατά την προσθήκη της ταινίας: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addNewSeries() {
        try {
            // Validate input fields
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

            // Create new series
            Series newSeries = new Series(
                    addSeriesTitleField.getText().trim(),
                    addSeriesGenreComboBox.getValue().trim()
            );

            // Add seasons to the series
            int numberOfSeasons = addSeriesSeasonsSpinner.getValue();
            for (int i = 1; i <= numberOfSeasons; i++) {
                Season season = new Season(i); // Default 10 episodes per season
                newSeries.addSeason(season);
            }

            showAlert("Επιτυχία", "Η σειρά προστέθηκε επιτυχώς με " + numberOfSeasons + " σεζόν!", Alert.AlertType.INFORMATION);

            // Add to collections
            DataLoader.seriesList.add(newSeries);
            allSeries.add(newSeries);

            // Sort series by average user rating (new series will have 0.0 initially)
            allSeries.sort(Comparator.comparingDouble(Series::getAverageUserRating).reversed());

            // Refresh filtered series and perform search to update display
            performSeriesSearch();

            // Clear form
            clearAddSeriesForm();

        } catch (Exception ex) {
            showAlert("Σφάλμα", "Παρουσιάστηκε σφάλμα κατά την προσθήκη της σειράς: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearAddMovieForm() {
        addMovieTitleField.clear();
        addMovieYearSpinner.getValueFactory().setValue(2024);
        addMovieGenreComboBox.setValue(null);
        addMovieDurationSpinner.getValueFactory().setValue(120);
        addMovieDirectorComboBox.setValue(null);
        addMovieActorComboBox.setValue(null);
        addMovieImdbSpinner.getValueFactory().setValue(7.0);
    }

    private void clearAddSeriesForm() {
        addSeriesTitleField.clear();
        addSeriesYearSpinner.getValueFactory().setValue(2024);
        addSeriesGenreComboBox.setValue(null);
        addSeriesSeasonsSpinner.getValueFactory().setValue(1);
        addSeriesDirectorComboBox.setValue(null);
        addSeriesActorComboBox.setValue(null);
    }

    private VBox createMovieSearchPanel() {
        VBox searchPanel = new VBox(10);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        Label searchLabel = new Label("🔍 Αναζήτηση Ταινιών");
        searchLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // First row: Title and Actor search
        HBox firstRow = new HBox(15);
        firstRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Τίτλος:");
        movieTitleSearchField = new TextField();
        movieTitleSearchField.setPromptText("Αναζήτηση τίτλου...");
        movieTitleSearchField.setPrefWidth(200);
        movieTitleSearchField.textProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        titleBox.getChildren().addAll(titleLabel, movieTitleSearchField);

        VBox actorBox = new VBox(5);
        Label actorLabel = new Label("Ηθοποιός:");
        movieActorSearchField = new TextField();
        movieActorSearchField.setPromptText("Αναζήτηση ηθοποιού...");
        movieActorSearchField.setPrefWidth(200);
        movieActorSearchField.textProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        actorBox.getChildren().addAll(actorLabel, movieActorSearchField);

        firstRow.getChildren().addAll(titleBox, actorBox);

        // Second row: Director and IMDB rating
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
        movieMinImdbSpinner = new Spinner<>(0.0, 10.0, 0.0, 0.1);
        movieMinImdbSpinner.setPrefWidth(120);
        movieMinImdbSpinner.setEditable(true);
        movieMinImdbSpinner.valueProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        imdbBox.getChildren().addAll(imdbLabel, movieMinImdbSpinner);

        secondRow.getChildren().addAll(directorBox, imdbBox);

        // Third row: User rating
        HBox thirdRow = new HBox(15);
        thirdRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox userRatingBox = new VBox(5);
        Label userRatingLabel = new Label("Ελάχιστη Βαθμολογία Χρηστών:");
        movieMinUserRatingSpinner = new Spinner<>(0.0, 10.0, 0.0, 0.1);
        movieMinUserRatingSpinner.setPrefWidth(120);
        movieMinUserRatingSpinner.setEditable(true);
        movieMinUserRatingSpinner.valueProperty().addListener((obs, oldVal, newVal) -> performMovieSearch());
        userRatingBox.getChildren().addAll(userRatingLabel, movieMinUserRatingSpinner);

        Button clearButton = new Button("Καθαρισμός Φίλτρων");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearMovieFilters());

        thirdRow.getChildren().addAll(userRatingBox, clearButton);

        searchPanel.getChildren().addAll(searchLabel, firstRow, secondRow, thirdRow);
        return searchPanel;
    }

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
        seriesMinUserRatingSpinner = new Spinner<>(0.0, 10.0, 0.0, 0.1);
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

    private void performMovieSearch() {
        String titleSearch = movieTitleSearchField.getText().toLowerCase().trim();
        String actorSearch = movieActorSearchField.getText().toLowerCase().trim();
        String directorSearch = movieDirectorSearchField.getText().toLowerCase().trim();
        double minImdb = movieMinImdbSpinner.getValue();
        double minUserRating = movieMinUserRatingSpinner.getValue();

        List<Movie> filtered = allMovies.stream()
                .filter(movie -> titleSearch.isEmpty() ||
                        movie.getTitle().toLowerCase().contains(titleSearch))
                .filter(movie -> actorSearch.isEmpty() ||
                        movie.getLeadActor().getFullName().toLowerCase().contains(actorSearch))
                .filter(movie -> directorSearch.isEmpty() ||
                        movie.getDirector().getFullName().toLowerCase().contains(directorSearch))
                .filter(movie -> movie.getImdbRating() >= minImdb)
                .filter(movie -> movie.getAverageUserRating() >= minUserRating)
                .collect(Collectors.toList());

        filteredMovies.clear();
        filteredMovies.addAll(filtered);
    }

    private void performSeriesSearch() {
        String titleSearch = seriesTitleSearchField.getText().toLowerCase().trim();
        double minUserRating = seriesMinUserRatingSpinner.getValue();

        List<Series> filtered = allSeries.stream()
                .filter(series -> titleSearch.isEmpty() ||
                        series.getTitle().toLowerCase().contains(titleSearch))
                .filter(series -> series.getAverageUserRating() >= minUserRating)
                .collect(Collectors.toList());

        filteredSeries.clear();
        filteredSeries.addAll(filtered);
    }

    private void clearMovieFilters() {
        movieTitleSearchField.clear();
        movieActorSearchField.clear();
        movieDirectorSearchField.clear();
        movieMinImdbSpinner.getValueFactory().setValue(0.0);
        movieMinUserRatingSpinner.getValueFactory().setValue(0.0);
    }

    private void clearSeriesFilters() {
        seriesTitleSearchField.clear();
        seriesMinUserRatingSpinner.getValueFactory().setValue(0.0);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
