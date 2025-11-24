package com.groovify.service;

/**
 * Service interface responsible for validating user login attempts.
 * <p>
 * Provides authentication logic to verify whether the supplied
 * username and password match an existing client in the system.
 */
public interface LoginService {

    /**
     * Given a loginForm, determine if the information provided is valid,
     * and the user exists in the system.
     *
     * @param username - Username of the person attempting to login
     * @param password - Raw password provided by the user logging in
     * @return true if data exists and matches what's on record, false otherwise
     */
    boolean validateClient(String username, String password);
}
