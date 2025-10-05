package com.groovify.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "image_file_name")
    private String image_file_name;

    @Column(name = "Description")
    private String Description;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getImageFileName() {
        // Default image
        String defaultImage = "Fishing.jpg";

        // Folder where your images are stored
        Path imagesFolder = Paths.get("src/main/resources/static/images");

        if (image_file_name == null) {
            return defaultImage;
        }

        // Check if the file exists in the folder
        Path imagePath = imagesFolder.resolve(image_file_name);
        if (Files.exists(imagePath)) {
            return image_file_name;
        } else {
            return defaultImage;
        }
    }
    public void setImageFileName(String image_file_name) { this.image_file_name = image_file_name; }

    public String getDescription() { return Description; }
    public void setDescription(String description) { this.Description = description; }

}
