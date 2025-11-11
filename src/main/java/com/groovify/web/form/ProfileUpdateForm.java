package com.groovify.web.form;

import com.groovify.jpa.model.Client;
import java.util.List;

/**
 * Data Transfer Object (DTO) for handling profile update form submissions.
 * <p>
 * This class keeps form data separate from the {@link Client} entity, allowing
 * safe transfer of user input from the web layer to the service layer.
 * </p>
 */
public class ProfileUpdateForm {

    private String name;
    private String description;
    private String imageFileName;
    private List<Long> genres;

    /**
     * Default constructor.
     * <p>
     * Required for form binding and serialization.
     * </p>
     */
    public ProfileUpdateForm() {}

    /**
     * Constructs a {@code ProfileUpdateForm} pre-filled with the current
     * user data from a {@link Client}.
     *
     * @param user the {@link Client} whose data will prepopulate the form
     */
    public ProfileUpdateForm(Client user) {
        this.name = user.getName();
        this.description = user.getDescription();
        this.imageFileName = user.getImageFileName();
    }

    // ------------------ Getters and Setters ------------------

    /**
     * Returns the name entered in the form.
     *
     * @return the form's name value
     */
    public String getName() { return name; }

    /**
     * Sets the name in the form.
     *
     * @param name the new name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the description entered in the form.
     *
     * @return the form's description value
     */
    public String getDescription() { return description; }

    /**
     * Sets the description in the form.
     *
     * @param description the new description to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the image file name associated with the user profile.
     *
     * @return the form's image file name
     */
    public String getImageFileName() { return imageFileName; }

    /**
     * Sets the image file name in the form.
     *
     * @param imageFileName the new image file name to set
     */
    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }

    /**
     * Returns the list of selected genre IDs.
     *
     * @return a list of genre IDs chosen in the form
     */
    public List<Long> getGenres() { return genres; }

    /**
     * Sets the list of selected genre IDs.
     *
     * @param genres a list of genre IDs to associate with the user profile
     */
    public void setGenres(List<Long> genres) { this.genres = genres; }
}
