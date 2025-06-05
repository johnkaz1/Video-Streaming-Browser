package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Series {
    private static int counter = 1;
    private final int id;
    private String title;
    private String genre;
    private List<Season> seasons;
    private Map<Integer, Integer> userRatings;

    public Series(String title, String genre) {
        this.id = counter++;
        this.title = title;
        this.genre = genre;
        this.seasons = new ArrayList<>();
        this.userRatings = new HashMap<>();
    }

    public void addSeason(Season season) {
        seasons.add(season);
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

    public String getGenre() {
        return genre;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public Map<Integer, Integer> getUserRatings() {
        return userRatings;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Series{" +
                "id=" + id +
                ", titlos='" + title + '\'' +
                ", eidos='" + genre + '\'' +
                ", season_count=" + seasons.size() +
                ", meso_user_rating=" + getAverageUserRating() +
                '}';
    }
}