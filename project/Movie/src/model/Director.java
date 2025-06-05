package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Director {
    private static int counter = 1;
    private final int id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private char gender;
    private List<String> bestWorks;

    public Director(String firstName, String lastName, LocalDate birthDate, char gender, List<String> bestWorks) {
        this.id = counter++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.bestWorks = new ArrayList<>(bestWorks);
    }

    public void addBestWork(String title) {
        if (!bestWorks.contains(title)) {
            bestWorks.add(title);
        }
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters/setters as needed...

    @Override
    public String toString() {
        return "Director{" +
               "id=" + id +
               ", onoma='" + firstName + '\'' +
               ", eponymo='" + lastName + '\'' +
               ", birthDate=" + birthDate +
               ", gender=" + gender +
               ", bestWorks=" + bestWorks +
               '}';
    }
}
