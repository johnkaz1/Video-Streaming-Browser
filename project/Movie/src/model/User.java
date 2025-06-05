package model;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    public User(String firstName, String lastName, String username, String email) {
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Lathos morfi email");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    public User(String firstName, String lastName, String username, String email, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Lathos morfi email");
        }
        this.email = email;
    }

    public boolean verifyCredentials(String username, String email) {
        return this.username.equals(username) && this.email.equals(email);
    }


    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", onoma='" + firstName + '\'' +
               ", eponymo='" + lastName + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
