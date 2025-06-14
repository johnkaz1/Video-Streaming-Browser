package model;

import java.util.ArrayList;
import java.util.List;

public class Season {
    // Removed 'counter' and 'id' if they are not explicitly used elsewhere
    // If 'id' is important for unique identification, you might keep it and manage it differently,
    // but for linking episodes to seasons, 'seasonNumber' is more direct.
    private int seasonNumber; // New field to store the explicit season number
    private int year;
    private List<Episode> episodes;

    // Updated constructor to accept both seasonNumber and year
    public Season(int seasonNumber, int year) {
        this.seasonNumber = seasonNumber; // Initialize the new seasonNumber field
        this.year = year;
        this.episodes = new ArrayList<>();
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    // New getter for seasonNumber, which is crucial for DataLoader
    public int getSeasonNumber() {
        return seasonNumber;
    }

    // Existing getter for year (renamed for clarity from 'etos' in toString to 'year' in getter)
    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Season{" +
                "seasonNumber=" + seasonNumber + // Use seasonNumber in toString
                ", year=" + year +
                ", episodes=" + episodes.size() +
                '}';
    }
}
