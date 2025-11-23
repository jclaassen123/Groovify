package com.groovify.service;

import org.springframework.stereotype.Service;
import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.LoginRepo;
import com.groovify.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of {@link LoginService} that provides user authentication functionality.
 * <p>
 * This service validates user credentials against the database using
 * {@link LoginRepo} and verifies passwords with {@link PasswordUtil}.
 * <p>
 * Features:
 * <ul>
 *     <li>Case-insensitive username lookup</li>
 *     <li>Password verification using salt and hash</li>
 *     <li>Logging for authentication attempts</li>
 * </ul>
 */
@Service
public class LoginServiceImpl implements LoginService {

    // Logger for recording authentication attempts and warnings
    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    // Repository for accessing client (user) records in the database
    private final LoginRepo loginRepo;

    /**
     * Constructs a new {@code LoginServiceImpl} with the specified {@link LoginRepo}.
     *
     * @param loginRepo the repository used to query client records
     */
    public LoginServiceImpl(LoginRepo loginRepo) {
        this.loginRepo = loginRepo;
    }

    /**
     * Validates a client's credentials.
     * <p>
     * Checks if the provided username and password match a registered user.
     * Trims whitespace from the username and performs a case-insensitive lookup.
     * Passwords are verified using the stored salt and hashed password.
     *
     * @param username the username provided by the client
     * @param password the password provided by the client
     * @return {@code true} if the credentials are valid; {@code false} otherwise
     */
    @Override
    public boolean validateClient(String username, String password) {
        // Null check for username or password
        if (username == null || password == null) {
            log.warn("Login failed: username or password is null");
            return false;
        }

        // Trim leading/trailing whitespace
        String trimmedUsername = username.trim();
        if (trimmedUsername.isEmpty()) {
            log.warn("Login failed: username is empty after trimming");
            return false;
        }

        log.debug("Validating login for username '{}'", trimmedUsername);

        // Fetch users by username (case-insensitive)
        List<Client> users = loginRepo.findByNameIgnoreCase(trimmedUsername);
        if (users.isEmpty()) {
            log.warn("Login failed: username '{}' not found", trimmedUsername);
            return false;
        }

        // Pick the first user (should be unique if RegisterService prevents duplicates)
        Client user = users.get(0);

        // Verify the password using salt and hash
        boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordSalt(), user.getPassword());
        if (valid) {
            log.info("User '{}' successfully validated", trimmedUsername);
        } else {
            log.warn("Login failed: invalid password for username '{}'", trimmedUsername);
        }

        return valid;
    }
}
