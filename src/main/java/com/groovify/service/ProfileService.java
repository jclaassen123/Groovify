package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.web.form.ProfileUpdateForm;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing user profiles.
 */
public interface ProfileService {

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the Client if found, otherwise empty
     */
    Optional<Client> getUserByUsername(String username);

    /**
     * Retrieves all available genres from the database.
     *
     * @return a list of all genres
     */
    List<Genre> getAllGenres();

    /**
     * Checks whether a username is already taken by another user.
     *
     * @param username        the username to check
     * @param currentUsername the username of the current user to exclude
     * @return true if the username is taken by someone else, false otherwise
     */
    boolean isUsernameTaken(String username, String currentUsername);


    boolean updateProfile(String name, ProfileUpdateForm form);
}
