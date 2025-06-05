package model;

import java.time.LocalDate;

public class Actor {
    private static int counter = 1;
    private final int id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private char gender; // M or F
    private String race;

    public Actor(String firstName, String lastName, LocalDate birthDate, char gender, String race) {
        this.id = counter++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.race = race;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters & setters omitted for brevity (tha ta valoume an theleis)

    @Override
    public String toString() {
        return "Actor{" +
               "id=" + id +
               ", onoma='" + firstName + '\'' +
               ", eponymo='" + lastName + '\'' +
               ", birthDate=" + birthDate +
               ", gender=" + gender +
               ", fyli='" + race + '\'' +
               '}';
    }
}
