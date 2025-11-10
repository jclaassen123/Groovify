package com.groovify.web.form;

import com.groovify.jpa.model.Client;
import java.util.List;

/**
 * DTO for handling profile update form submissions.
 * Keeps form data separate from the Client entity.
 */
public class ProfileUpdateForm {

    private String name;
    private String description;
    private String imageFileName;
    private List<Long> genres;

    public ProfileUpdateForm() {}

    public ProfileUpdateForm(Client user) {
        this.name = user.getName();
        this.description = user.getDescription();
        this.imageFileName = user.getImageFileName();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageFileName() { return imageFileName; }
    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }

    public List<Long> getGenres() { return genres; }
    public void setGenres(List<Long> genres) { this.genres = genres; }
}
