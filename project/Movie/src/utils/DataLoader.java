package utils;

import model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DataLoader {
    public static List<User> users = new ArrayList<>();
    public static List<Actor> actors = new ArrayList<>();
    public static List<Director> directors = new ArrayList<>();
    public static List<Movie> movies = new ArrayList<>();
    public static List<Series> seriesList = new ArrayList<>();

    // Method to load only users (for login)
    public static void loadUsers(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            users.clear(); // Clear existing users
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                users.add(new User(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
            }
            System.out.println("Loaded " + users.size() + " users");
        } catch (IOException e) {
            System.err.println("Error loading users from " + path + ": " + e.getMessage());
            throw e;
        }
    }

    public static void loadAllData() throws IOException {
        // Try different possible paths
        String[] possiblePaths = {
                "src/utils/",
                "Movie/src/utils/",
                "utils/",
                "" // Current directory
        };

        String basePath = findBasePath(possiblePaths);
        System.out.println("Using base path: " + basePath);

        // Only load users if not already loaded
        if (users.isEmpty()) {
            loadUsers(basePath + "Users.txt");
        }
        loadActors(basePath + "Actors.txt");
        loadDirectors(basePath + "Directors.txt");

        // IMPORTANT: Add missing actors/directors *after* loading from files
        // but *before* parsing Movies/Series, as they might be referenced there.
        addMissingActors();
        addMissingDirectors();

        loadMovies(basePath + "Movies.txt");
        loadSeries(basePath + "Series.txt");

        System.out.println("\n--- Data Loading Summary ---");
        System.out.println("Total Users: " + users.size());
        System.out.println("Total Actors: " + actors.size());
        System.out.println("Total Directors: " + directors.size());
        System.out.println("Total Movies: " + movies.size());
        System.out.println("Total Series: " + seriesList.size());
        System.out.println("--------------------------\n");
    }

    private static String findBasePath(String[] possiblePaths) {
        for (String path : possiblePaths) {
            if (Files.exists(Paths.get(path + "Users.txt"))) {
                return path;
            }
        }
        // If none found, return empty string and let the error handling deal with it
        return "";
    }

    private static void loadActors(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            actors.clear(); // Clear existing actors before loading
            for (String line : lines) {
                String[] parts = line.split(",", 5);
                if (parts.length < 5) {
                    System.err.println("Skipping malformed actor line: " + line);
                    continue;
                }
                actors.add(new Actor(
                        parts[0].trim(),
                        parts[1].trim(),
                        LocalDate.parse(parts[2].trim()),
                        parts[3].trim().charAt(0),
                        parts[4].trim()
                ));
            }
            System.out.println("Loaded " + actors.size() + " actors from " + path);
        } catch (IOException e) {
            System.err.println("Error loading actors from " + path + ": " + e.getMessage());
            // Do not throw, allow the application to try with other data if possible
        } catch (Exception e) {
            System.err.println("Error parsing actor data from " + path + ": " + e.getMessage());
        }
    }

    private static void loadDirectors(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            directors.clear(); // Clear existing directors before loading
            for (String line : lines) {
                String[] parts = line.split(",", 5);
                if (parts.length < 5) {
                    System.err.println("Skipping malformed director line: " + line);
                    continue;
                }

                String bestWorksStr = parts[4].replaceAll("\\r?\\n", "|");
                List<String> bestWorks = Arrays.stream(bestWorksStr.split("\\|"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                directors.add(new Director(
                        parts[0].trim(),
                        parts[1].trim(),
                        LocalDate.parse(parts[2].trim()),
                        parts[3].trim().charAt(0),
                        bestWorks
                ));
            }
            System.out.println("Loaded " + directors.size() + " directors from " + path);
        } catch (IOException e) {
            System.err.println("Error loading directors from " + path + ": " + e.getMessage());
            // Do not throw, allow the application to try with other data if possible
        } catch (Exception e) {
            System.err.println("Error parsing director data from " + path + ": " + e.getMessage());
        }
    }

    // Comprehensive list of actors for Series.txt (add as needed)
    private static void addMissingActors() {
        if (findActorByName("Bryan Cranston") == null) {
            actors.add(new Actor("Bryan", "Cranston", LocalDate.of(1956, 3, 7), 'M', "United States"));
            System.out.println("[DEBUG] Added missing actor: Bryan Cranston");
        }
        if (findActorByName("Adam Scott") == null) {
            actors.add(new Actor("Adam", "Scott", LocalDate.of(1973, 4, 3), 'M', "United States"));
            System.out.println("[DEBUG] Added missing actor: Adam Scott");
        }
        // Add other actors if you encounter "Actor not found" errors
    }

    // Comprehensive list of directors for Series.txt (add as needed)
    private static void addMissingDirectors() {
        // Breaking Bad Directors
        if (findDirectorByName("Vince Gilligan") == null) {
            directors.add(new Director("Vince", "Gilligan", LocalDate.of(1967, 2, 11), 'M', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Vince Gilligan");
        }
        if (findDirectorByName("Adam Bernstein") == null) {
            directors.add(new Director("Adam", "Bernstein", LocalDate.of(1960, 5, 7), 'M', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Adam Bernstein");
        }
        if (findDirectorByName("Jim McKay") == null) {
            directors.add(new Director("Jim", "McKay", LocalDate.of(1962, 1, 1), 'M', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Jim McKay");
        }
        if (findDirectorByName("Tricia Brock") == null) {
            directors.add(new Director("Tricia", "Brock", LocalDate.of(1950, 1, 1), 'F', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Tricia Brock");
        }
        if (findDirectorByName("Nelson McCormick") == null) {
            directors.add(new Director("Nelson", "McCormick", LocalDate.of(1960, 1, 1), 'M', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Nelson McCormick");
        }
        if (findDirectorByName("Bryan Spicer") == null) {
            directors.add(new Director("Bryan", "Spicer", LocalDate.of(1960, 1, 1), 'M', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Bryan Spicer");
        }
        if (findDirectorByName("Phil Abraham") == null) {
            directors.add(new Director("Phil", "Abraham", LocalDate.of(1970, 1, 1), 'M', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Phil Abraham");
        }
        if (findDirectorByName("Michelle MacLaren") == null) {
            directors.add(new Director("Michelle", "MacLaren", LocalDate.of(1965, 1, 1), 'F', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Michelle MacLaren");
        }
        if (findDirectorByName("Michael Slovis") == null) {
            directors.add(new Director("Michael", "Slovis", LocalDate.of(1956, 1, 1), 'M', Arrays.asList("Breaking Bad")));
            System.out.println("[DEBUG] Added missing director: Michael Slovis");
        }

        // Severance Directors
        if (findDirectorByName("Ben Stiller") == null) {
            directors.add(new Director("Ben", "Stiller", LocalDate.of(1965, 11, 30), 'M', Arrays.asList("Severance")));
            System.out.println("[DEBUG] Added missing director: Ben Stiller");
        }
        if (findDirectorByName("Aoife McArdle") == null) {
            directors.add(new Director("Aoife", "McArdle", LocalDate.of(1980, 1, 1), 'F', Arrays.asList("Severance")));
            System.out.println("[DEBUG] Added missing director: Aoife McArdle");
        }

        // Existing movie directors for completeness (from previous versions)
        if (findDirectorByName("Frank Darabont") == null) {
            directors.add(new Director("Frank", "Darabont", LocalDate.of(1959, 1, 28), 'M',
                    Arrays.asList("The Shawshank Redemption", "The Green Mile")));
            System.out.println("[DEBUG] Added missing director: Frank Darabont");
        }
        if (findDirectorByName("Damien Chazelle") == null) {
            directors.add(new Director("Damien", "Chazelle", LocalDate.of(1985, 1, 19), 'M',
                    Arrays.asList("La La Land", "Whiplash")));
            System.out.println("[DEBUG] Added missing director: Damien Chazelle");
        }
        if (findDirectorByName("Brett Ratner") == null) {
            directors.add(new Director("Brett", "Ratner", LocalDate.of(1969, 3, 28), 'M',
                    Arrays.asList("Rush Hour", "X-Men: The Last Stand")));
            System.out.println("[DEBUG] Added missing director: Brett Ratner");
        }
    }


    private static void loadMovies(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            movies.clear(); // Clear existing movies before loading
            for (String line : lines) {
                String fullLine = line.replaceAll("\\r?\\n", " ");
                String[] parts = fullLine.split(",", 8);
                if (parts.length < 8) {
                    System.err.println("[ERROR] Invalid movie line (not enough parts): " + line);
                    continue;
                }

                String directorName = parts[4].trim();
                String actorName = parts[6].trim();

                Director dir = findDirectorByName(directorName);
                Actor actor = findActorByName(actorName);

                if (dir == null) {
                    System.err.println("[ERROR] Movie '" + parts[0].trim() + "': Director not found: '" + directorName + "'. Skipping movie.");
                    continue;
                }
                if (actor == null) {
                    System.err.println("[ERROR] Movie '" + parts[0].trim() + "': Actor not found: '" + actorName + "'. Skipping movie.");
                    continue;
                }

                Movie movie = new Movie(
                        parts[0].trim(),
                        Integer.parseInt(parts[1].trim()),
                        parts[2].trim(),
                        Integer.parseInt(parts[3].trim()),
                        dir,
                        Double.parseDouble(parts[5].trim()),
                        actor
                );

                // Add user ratings
                String ratingsStr = parts[7].trim();
                if (!ratingsStr.isEmpty()) {
                    String[] ratings = ratingsStr.split("\\|");
                    for (String r : ratings) {
                        if (r.contains(":")) {
                            String[] idRating = r.split(":");
                            if (idRating.length == 2) {
                                try {
                                    movie.addUserRating(Integer.parseInt(idRating[0].trim()), Integer.parseInt(idRating[1].trim()));
                                } catch (NumberFormatException e) {
                                    System.err.println("[WARNING] Invalid rating format: '" + r + "' for movie: " + movie.getTitle());
                                }
                            }
                        }
                    }
                }
                movies.add(movie);
            }
            System.out.println("Loaded " + movies.size() + " movies.");
        } catch (IOException e) {
            System.err.println("Error loading movies from " + path + ": " + e.getMessage());
            // Do not throw, allow the application to try with other data if possible
        } catch (Exception e) {
            System.err.println("Error parsing movie data from " + path + ": " + e.getMessage());
        }
    }

    private static void loadSeries(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            seriesList.clear(); // Clear existing series before loading
            Series currentSeries = null;
            int seasonCounter = 0; // To keep track of season number for constructor

            for (String line : lines) {
                line = line.trim();

                // Skip empty lines or lines that start with a hash (comments) or instructional lines
                if (line.isEmpty() || line.startsWith("#") || line.contains("(τίτλος, είδος, βαθμολογία χρηστών)")) {
                    continue;
                }

                // If a line is just a number, it's likely a count or separator, skip it
                try {
                    Integer.parseInt(line);
                    continue;
                } catch (NumberFormatException e) {
                    // Not a pure number, continue
                }

                if (line.startsWith("SERIES:")) {
                    String[] parts = line.substring(7).split(",", 3);
                    if (parts.length >= 2) {
                        currentSeries = new Series(parts[0].trim(), parts[1].trim());
                        seasonCounter = 0; // Reset season counter for new series

                        // Handle user ratings if present
                        if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                            String[] ratings = parts[2].trim().split("\\|");
                            for (String r : ratings) {
                                if (r.contains(":")) {
                                    try {
                                        String[] idRating = r.split(":");
                                        if (idRating.length == 2) {
                                            currentSeries.addUserRating(
                                                    Integer.parseInt(idRating[0].trim()),
                                                    Integer.parseInt(idRating[1].trim())
                                            );
                                        }
                                    } catch (NumberFormatException ex) {
                                        System.err.println("[WARNING] Could not parse rating: '" + r + "' for series: " + currentSeries.getTitle());
                                    }
                                }
                            }
                        }
                        seriesList.add(currentSeries);
                        System.out.println("[INFO] Started loading series: " + currentSeries.getTitle());
                    } else {
                        System.err.println("[ERROR] Malformed SERIES line: " + line);
                    }
                } else if (line.startsWith("SEASON:")) {
                    if (currentSeries != null) {
                        try {
                            String[] seasonParts = line.split(",", 2);
                            if (seasonParts.length < 2) {
                                System.err.println("[ERROR] Malformed SEASON line (missing year): " + line);
                                continue;
                            }
                            String yearPart = seasonParts[1].trim().replace(":", "");
                            int year = Integer.parseInt(yearPart);
                            seasonCounter++; // Increment season counter for the new season

                            // NOW USING THE CORRECTED Season(int seasonNumber, int year) constructor
                            Season season = new Season(seasonCounter, year);
                            currentSeries.addSeason(season);
                            System.out.println("[INFO]   Added season " + season.getSeasonNumber() + " (Year: " + season.getYear() + ") for " + currentSeries.getTitle());
                        } catch (NumberFormatException e) {
                            System.err.println("[ERROR] Could not parse year in SEASON line: '" + line + "' - " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("[ERROR] Error parsing SEASON line: '" + line + "' for series " + currentSeries.getTitle() + " - " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] SEASON line found without a preceding SERIES line. Skipping: " + line);
                    }
                } else if (!line.isEmpty() && Character.isDigit(line.charAt(0)) && currentSeries != null) {
                    // Episode data
                    try {
                        String[] parts = line.split(",", 4);
                        if (parts.length >= 4) {
                            int duration = Integer.parseInt(parts[0].trim());
                            String directorName = parts[1].trim();
                            double imdb = Double.parseDouble(parts[2].trim());
                            String actorName = parts[3].trim();

                            Director dir = findDirectorByName(directorName);
                            Actor act = findActorByName(actorName);

                            if (dir == null) {
                                System.err.println("[ERROR]     Episode for '" + currentSeries.getTitle() + "'. Director not found: '" + directorName + "'. Line: " + line + ". Skipping episode.");
                                continue; // Skip episode if director not found
                            }
                            if (act == null) {
                                System.err.println("[ERROR]     Episode for '" + currentSeries.getTitle() + "'. Actor not found: '" + actorName + "'. Line: " + line + ". Skipping episode.");
                                continue; // Skip episode if actor not found
                            }

                            if (!currentSeries.getSeasons().isEmpty()) {
                                currentSeries.getSeasons().get(currentSeries.getSeasons().size() - 1)
                                        .addEpisode(new Episode(duration, dir, imdb, act));
                                // System.out.println("      Added episode to season " + currentSeries.getSeasons().size() + " for " + currentSeries.getTitle()); // Uncomment for verbose episode loading
                            } else {
                                System.err.println("[WARNING]     No season available for episode in series: '" + currentSeries.getTitle() + "'. Line: " + line + ". Skipping episode.");
                            }
                        } else {
                            System.err.println("[ERROR] Invalid episode line format (not enough parts): " + line);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("[ERROR] Error parsing numeric values in episode line: '" + line + "' for series '" + (currentSeries != null ? currentSeries.getTitle() : "Unknown") + "' - " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("[ERROR] Could not parse episode line: '" + line + "' for series '" + (currentSeries != null ? currentSeries.getTitle() : "Unknown") + "' - " + e.getMessage());
                    }
                }
            }
            System.out.println("[INFO] Finished loading all series. Total series loaded: " + seriesList.size());
        } catch (IOException e) {
            System.err.println("[CRITICAL ERROR] Error loading series from " + path + ": " + e.getMessage());
            throw e; // Re-throw critical IO exception
        }
    }

    private static Director findDirectorByName(String fullName) {
        return directors.stream()
                .filter(d -> (d.getFullName().equalsIgnoreCase(fullName)))
                .findFirst().orElse(null);
    }

    private static Actor findActorByName(String fullName) {
        return actors.stream()
                .filter(a -> (a.getFullName().equalsIgnoreCase(fullName)))
                .findFirst().orElse(null);
    }

    public static User findUserById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
}
