package model;

public class Episode {
    private static int counter = 1;
    private final int id;
    private int duration;
    private Director director;
    private double imdbRating;
    private Actor leadActor;

    public Episode(int duration, Director director, double imdbRating, Actor leadActor) {
        if (imdbRating < 1.0 || imdbRating > 10.0) {
            throw new IllegalArgumentException("Lathos vathmologia IMDB");
        }
        this.id = counter++;
        this.duration = duration;
        this.director = director;
        this.imdbRating = imdbRating;
        this.leadActor = leadActor;
    }

    @Override
    public String toString() {
        return "Episode{" +
               "id=" + id +
               ", diarkeia=" + duration +
               ", skinothetis=" + director.getFullName() +
               ", imdb=" + imdbRating +
               ", protagonistis=" + leadActor.getFullName() +
               '}';
    }
}
