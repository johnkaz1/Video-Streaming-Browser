package model;

import java.util.ArrayList;
import java.util.List;

public class Season {
    private static int counter = 1;
    private final int id;
    private int year;
    private List<Episode> episodes;

    public Season(int year) {
        this.id = counter++;
        this.year = year;
        this.episodes = new ArrayList<>();
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    @Override
    public String toString() {
        return "Season{" +
               "id=" + id +
               ", etos=" + year +
               ", episodes=" + episodes.size() +
               '}';
    }
}
