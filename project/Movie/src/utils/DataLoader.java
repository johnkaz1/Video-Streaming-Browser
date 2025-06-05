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
                ""
        };

        String basePath = findBasePath(possiblePaths);
        System.out.println("Using base path: " + basePath);

        // Only load users if not already loaded
        if (users.isEmpty()) {
            loadUsers(basePath + "Users.txt");
        }
        loadActors(basePath + "Actors.txt");
        loadDirectors(basePath + "Directors.txt");
        loadMovies(basePath + "Movies.txt");
        loadSeries(basePath + "Series.txt");

        System.out.println("Data loading completed:");
        System.out.println("Users: " + users.size());
        System.out.println("Actors: " + actors.size());
        System.out.println("Directors: " + directors.size());
        System.out.println("Movies: " + movies.size());
        System.out.println("Series: " + seriesList.size());
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
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                actors.add(new Actor(
                        parts[0].trim(),
                        parts[1].trim(),
                        LocalDate.parse(parts[2].trim()),
                        parts[3].trim().charAt(0),
                        parts[4].trim()
                ));
            }
            System.out.println("Loaded " + actors.size() + " actors");
        } catch (IOException e) {
            System.err.println("Error loading actors from " + path + ": " + e.getMessage());
            throw e;
        }
    }

    private static void loadDirectors(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                // Handle multiline entries by joining lines that don't start with a name
                String fullLine = line;
                String[] parts = fullLine.split(",", 5);
                if (parts.length < 5) continue;

                // Clean up best works - handle pipe separation and line breaks
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

            // Add missing directors from Movies.txt
            addMissingDirectors();

            System.out.println("Loaded " + directors.size() + " directors");
        } catch (IOException e) {
            System.err.println("Error loading directors from " + path + ": " + e.getMessage());
            throw e;
        }
    }

    private static void addMissingDirectors() {
        // Add directors that are referenced in Movies.txt but missing from Directors.txt
        if (findDirectorByName("Frank Darabont") == null) {
            directors.add(new Director("Frank", "Darabont", LocalDate.of(1959, 1, 28), 'M',
                    Arrays.asList("The Shawshank Redemption", "The Green Mile")));
        }
        if (findDirectorByName("Damien Chazelle") == null) {
            directors.add(new Director("Damien", "Chazelle", LocalDate.of(1985, 1, 19), 'M',
                    Arrays.asList("La La Land", "Whiplash")));
        }
        if (findDirectorByName("Brett Ratner") == null) {
            directors.add(new Director("Brett", "Ratner", LocalDate.of(1969, 3, 28), 'M',
                    Arrays.asList("Rush Hour", "X-Men: The Last Stand")));
        }
    }

    private static void loadMovies(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                // Handle multiline entries
                String fullLine = line.replaceAll("\\r?\\n", " ");
                String[] parts = fullLine.split(",", 8);
                if (parts.length < 8) {
                    System.err.println("Invalid movie line (not enough parts): " + line);
                    continue;
                }

                String directorName = parts[4].trim();
                String actorName = parts[6].trim();

                System.out.println("Processing movie: " + parts[0].trim());
                System.out.println("Looking for director: '" + directorName + "'");
                System.out.println("Looking for actor: '" + actorName + "'");

                Director dir = findDirectorByName(directorName);
                Actor actor = findActorByName(actorName);

                if (dir == null) {
                    System.err.println("Director not found: '" + directorName + "'");
                    System.err.println("Available directors:");
                    directors.forEach(d -> System.err.println("  - '" + d.getFullName() + "'"));
                    continue;
                }
                if (actor == null) {
                    System.err.println("Actor not found: '" + actorName + "'");
                    System.err.println("Available actors:");
                    actors.forEach(a -> System.err.println("  - '" + a.getFullName() + "'"));
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
                                    movie.addUserRating(Integer.parseInt(idRating[0]), Integer.parseInt(idRating[1]));
                                } catch (NumberFormatException e) {
                                    System.err.println("Invalid rating format: " + r);
                                }
                            }
                        }
                    }
                }

                movies.add(movie);
            }
            System.out.println("Loaded " + movies.size() + " movies");
        } catch (IOException e) {
            System.err.println("Error loading movies from " + path + ": " + e.getMessage());
            throw e;
        }
    }

    private static void loadSeries(String path) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            Series currentSeries = null;

            for (String line : lines) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Skip header lines or comment lines
                if (line.contains("(τίτλος, είδος, βαθμολογία χρηστών)") ||
                        line.startsWith("#") ||
                        line.matches("^\\d+\\s*\\(.*\\)$")) {
                    continue;
                }

                // Skip pure number lines (count lines)
                try {
                    Integer.parseInt(line);
                    continue; // This is just a count line, skip it
                } catch (NumberFormatException e) {
                    // Not a pure number, continue processing
                }

                if (line.startsWith("SERIES:")) {
                    String[] parts = line.substring(7).split(",", 3);
                    if (parts.length >= 2) {
                        currentSeries = new Series(parts[0].trim(), parts[1].trim());

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
                                        System.err.println("Could not parse rating: " + r + " for series: " + currentSeries.getTitle());
                                    }
                                }
                            }
                        }
                        seriesList.add(currentSeries);
                    }
                } else if (line.startsWith("SEASON:")) {
                    if (currentSeries != null) {
                        try {
                            String yearPart = line.split(",")[1].trim().replace(":", "");
                            int year = Integer.parseInt(yearPart);
                            Season season = new Season(year);
                            currentSeries.addSeason(season);
                        } catch (Exception e) {
                            System.err.println("Could not parse season line: " + line);
                        }
                    }
                } else if (!line.isEmpty() && Character.isDigit(line.charAt(0)) && currentSeries != null) {
                    // Episode data
                    try {
                        String[] parts = line.split(",", 4);
                        if (parts.length >= 4) {
                            int duration = Integer.parseInt(parts[0].trim());
                            Director dir = findDirectorByName(parts[1].trim());
                            double imdb = Double.parseDouble(parts[2].trim());
                            Actor act = findActorByName(parts[3].trim());

                            if (dir != null && act != null && !currentSeries.getSeasons().isEmpty()) {
                                currentSeries.getSeasons().get(currentSeries.getSeasons().size() - 1)
                                        .addEpisode(new Episode(duration, dir, imdb, act));
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Could not parse episode line: " + line + " - " + e.getMessage());
                    }
                }
            }

            System.out.println("Loaded " + seriesList.size() + " series.");
        } catch (IOException e) {
            System.err.println("Error loading series from " + path + ": " + e.getMessage());
            throw e;
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