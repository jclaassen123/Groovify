package com.groovify.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Entity
@Table(name = "Client")
public class Client {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "image_file_name")
    private String image_file_name;

    @Column(name = "description")
    private String description;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "User_genres",
            joinColumns = @JoinColumn(name = "Users_ID"),
            inverseJoinColumns = @JoinColumn(name = "Genres_ID")
    )
    private List<Genre> genres;

    public Client() {}
    public Client(String name, String password) { this.name = name; this.password = password; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getImageFileName() {
        String defaultImage = "Fishing.jpg";
        Path imagesFolder = Paths.get("src/main/resources/static/images");

        if (image_file_name == null) return defaultImage;

        Path imagePath = imagesFolder.resolve(image_file_name);
        return Files.exists(imagePath) ? image_file_name : defaultImage;
    }
    public void setImageFileName(String image_file_name) { this.image_file_name = image_file_name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }
}
