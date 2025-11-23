package com.groovify.service;

import com.groovify.jpa.model.Client;

/**
 * Core service for handling user registration logic.
 *
 * Handles validation, password hashing, default value management,
 * and persistence logic for registering new users — no web-layer dependencies.
 */
public interface RegisterService {

    /**
     * Registers a new user.
     *
     * @param user the submitted Client object
     * @return true if registration succeeded, false otherwise
     */
    boolean registerUser(Client user);

    /**
     * Validates that the provided user data is correct.
     *
     * @param user the Client being registered
     * @return true if valid, false otherwise
     */
    boolean validateInput(Client user);

    /**
     * Checks if the username already exists in the database.
     *
     * @param user the Client being registered
     * @return true if available, false otherwise
     */
    boolean checkUsernameAvailability(Client user);

    /**
     * Validates that the user’s password is not null or blank.
     *
     * @param user the Client being registered
     * @return true if valid, false otherwise
     */
    boolean validatePassword(Client user);

    /**
     * Generates a salt, hashes the user’s password, and sets them on the entity.
     *
     * @param user the Client being registered
     * @return true if hashing succeeded, false otherwise
     */
    boolean hashAndSetPassword(Client user);

    /**
     * Sets default values for optional user fields if not provided.
     *
     * @param user the Client being registered
     */
    void setDefaultValues(Client user);

    /**
     * Persists the new user to the database.
     *
     * @param user the Client being registered
     * @return true if save succeeded, false otherwise
     */
    boolean saveUser(Client user);
}