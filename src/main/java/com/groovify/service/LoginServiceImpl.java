package com.groovify.service;

import org.springframework.stereotype.Service;
import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.LoginRepository;
import com.groovify.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of {@link LoginService} that provides user authentication functionality.
 * <p>
 * This service validates user credentials against the database using
 * {@link LoginRepository} and verifies passwords with {@link PasswordUtil}.
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final LoginRepository loginRepo;

    /**
     * Constructs a LoginServiceImpl with the given LoginRepository.
     *
     * @param loginRepo repository for accessing Client entities
     */
    public LoginServiceImpl(LoginRepository loginRepo) {
        this.loginRepo = loginRepo;
    }

    /**
     * Validates a client's credentials by username and password.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return true if the username exists and password matches; false otherwise
     */
    @Override
    public boolean validateClient(String username, String password) {
        log.debug("Validating login for username '{}'", username);

        // Fetch users by username (case-insensitive)
        List<Client> users = loginRepo.findByNameIgnoreCase(username);
        if (users.isEmpty()) {
            log.warn("Login failed: username '{}' not found", username);
            return false;
        }

        Client user = users.get(0);

        // Verify the password using salt and hash
        boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordSalt(), user.getPassword());
        if (valid) {
            log.info("User '{}' successfully validated", username);
        } else {
            log.warn("Login failed: invalid password for username '{}'", username);
        }

        return valid;
    }
}
