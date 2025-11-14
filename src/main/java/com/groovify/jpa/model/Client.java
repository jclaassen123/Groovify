package com.groovify.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a client (user) in the system.
 * <p>
 * Maps to the "Client" table in the database. Each client has a username,
 * password (with salt), profile image, description, and preferred genres.
 */
@Entity
@Table(name = "Client")
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "imageFileName")
    private String imageFileName = "Fishing.jpg";

    @Column(name = "description")
    private String description = "";

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters.")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters.")
    @Column(nullable = false)
    private String password;

    /** Salt used for password hashing (stored separately). */
    @Column(name = "password_salt")
    private String passwordSalt;

    @ManyToMany
    @JoinTable(
            name = "Client_Genre",
            joinColumns = @JoinColumn(name = "Client_ID"),
            inverseJoinColumns = @JoinColumn(name = "Genre_ID")
    )
    private List<Genre> genres;

    /**
     * Default constructor required by JPA.
     */
    public Client() {}

    /**
     * Constructs a Client with the given username and password.
     *
     * @param name     the username
     * @param password the password (plain text; should be hashed before storing)
     */
    public Client(String name, String password) {
        this.name = name;
        this.password = password;
    }

    /**
     * Returns the unique ID of the client.
     *
     * @return the client ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the client.
     *
     * @param id the client ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the username of the client.
     *
     * @return the username
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the username of the client.
     *
     * @param name the new username
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the stored (hashed) password of the client.
     *
     * @return the hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the stored (hashed) password of the client.
     *
     * @param password the hashed password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the password salt associated with the client.
     *
     * @return the password salt
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * Sets the password salt used for hashing the client’s password.
     *
     * @param passwordSalt the password salt
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * Returns the validated profile image file name.
     * If the stored file name does not exist on disk, the default image name is returned.
     *
     * @return the valid profile image file name
     */
    public String getImageFileName() {
        String defaultImage = "Fishing.jpg";
        Path imagesFolder = Paths.get("src/main/resources/static/images/profile/");

        if (imageFileName == null) return defaultImage;

        Path imagePath = imagesFolder.resolve(imageFileName);
        return Files.exists(imagePath) ? imageFileName : defaultImage;
    }

    /**
     * Sets the file name of the client's profile image.
     *
     * @param imageFileName the profile image file name
     */
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    /**
     * Returns the description of the client.
     *
     * @return the client description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the client.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of genres associated with the client.
     *
     * @return the list of genres
     */
    public List<Genre> getGenres() {
        return genres;
    }

    /**
     * Sets the list of genres preferred by the client.
     *
     * @param genres the list of genres
     */
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    /**
     * Checks whether this client is equal to another object.
     * Two clients are considered equal if all core fields match.
     *
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(imageFileName, client.imageFileName) &&
                Objects.equals(description, client.description) &&
                Objects.equals(name, client.name) &&
                Objects.equals(password, client.password) &&
                Objects.equals(passwordSalt, client.passwordSalt) &&
                Objects.equals(genres, client.genres);
    }

    /**
     * Computes the hash code for the client.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(imageFileName, description, name, password, passwordSalt, genres);
    }
}
