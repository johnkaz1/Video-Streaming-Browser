package model;

import java.util.HashMap;
import java.util.Map;

public class Movie {
    private static int counter = 1;
    private final int id;
    private String title;
    private int year;
    private String genre;
    private int duration;
    private Director director;
    private double imdbRating;
    private Actor leadActor;
    private Map<Integer, Integer> userRatings; // userId -> rating (1–10)

    public Movie(String title, int year, String genre, int duration, Director director, double imdbRating, Actor leadActor) {
        if (imdbRating < 1.0 || imdbRating > 10.0) {
            throw new IllegalArgumentException("Lathos vathmologia IMDB");
        }
        this.id = counter++;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.imdbRating = imdbRating;
        this.leadActor = leadActor;
        this.userRatings = new HashMap<>();
    }

    public void addUserRating(int userId, int rating) {
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("Vathmologia prepei na einai metaxy 1 kai 10");
        }
        userRatings.put(userId, rating);
    }

    public double getAverageUserRating() {
        return userRatings.values().stream().mapToInt(i -> i).average().orElse(0.0);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public Director getDirector() {
        return director;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public Actor getLeadActor() {
        return leadActor;
    }

    public Map<Integer, Integer> getUserRatings() {
        return userRatings;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public void setImdbRating(double imdbRating) {
        if (imdbRating < 1.0 || imdbRating > 10.0) {
            throw new IllegalArgumentException("Lathos vathmologia IMDB");
        }
        this.imdbRating = imdbRating;
    }

    public void setLeadActor(Actor leadActor) {
        this.leadActor = leadActor;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", titlos='" + title + '\'' +
                ", etos=" + year +
                ", eidos='" + genre + '\'' +
                ", diarkeia=" + duration +
                ", skinothetis=" + director.getFullName() +
                ", imdb=" + imdbRating +
                ", protagonistis=" + leadActor.getFullName() +
                ", meso_user_rating=" + getAverageUserRating() +
                '}';
    }
}