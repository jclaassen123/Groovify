package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.util.PasswordUtil;
import com.groovify.validation.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Service implementation responsible for handling user registration logic.
 * This includes validation, password hashing, default field population,
 * and persistence of the {@link Client} entity.
 */
@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final ClientRepo clientRepo;

    /**
     * Constructs a new {@code RegisterServiceImpl} with the given repository.
     *
     * @param clientRepo the repository used to persist and query {@link Client} entities
     */
    public RegisterServiceImpl(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    /**
     * Registers a new user by validating input, checking username availability,
     * verifying the password, hashing and salting it, applying default values,
     * and saving the user to the database.
     *
     * @param user the client attempting to register
     * @return {@code true} if registration succeeds, otherwise {@code false}
     */
    @Override
    public boolean registerUser(Client user) {

        if (user == null) {
            log.warn("Cannot register null user");
            return false;
        }

        String username = user.getName();
        log.info("User '{}' attempting to register", username);

        if (!validateInput(user)) return false;
        if (!checkUsernameAvailability(user)) return false;
        if (!validatePassword(user)) return false;
        if (!hashAndSetPassword(user)) return false;

        setDefaultValues(user);

        if (!saveUser(user)) return false;

        log.info("Registration completed successfully for '{}'", username);
        return true;
    }

    /**
     * Validates the required input fields for user registration.
     * Ensures the user object and username are non-null and meet length constraints.
     *
     * @param user the client being validated
     * @return {@code true} if input is valid, otherwise {@code false}
     */
    @Override
    public boolean validateInput(Client user) {
        if (user == null) {
            log.warn("User object is null");
            return false;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Username is null or blank");
            return false;
        }
        if (user.getName().length() < 3 || user.getName().length() > 32) {
            log.warn("Username '{}' is too short or too long", user.getName());
            return false;
        }
        if (RegexUtil.isUsernameValid(user.getName())) {
            log.warn("Username '{}' contains invalid characters", user.getName());
            return false;
        }
        return true;
    }

    /**
     * Checks whether the provided username is available for registration.
     *
     * @param user the client whose username should be checked
     * @return {@code true} if the username is not already in use, otherwise {@code false}
     */
    @Override
    public boolean checkUsernameAvailability(Client user) {
        if (user == null || user.getName() == null || user.getName().isBlank()) {
            log.warn("Cannot check username availability: user or username is null/blank");
            return false;
        }

        Optional<Client> existing = clientRepo.findByName(user.getName());
        if (existing.isPresent()) {
            log.warn("Username '{}' already exists", user.getName());
            return false;
        }
        return true;
    }

    /**
     * Validates that the user has provided a non-null, non-empty password.
     *
     * @param user the client whose password should be validated
     * @return {@code true} if the password is valid, otherwise {@code false}
     */
    @Override
    public boolean validatePassword(Client user) {
        if (user == null) {
            log.warn("User object is null");
            return false;
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            log.warn("Password is null or blank for user '{}'", user.getName());
            return false;
        }
        if (RegexUtil.isPasswordValid(user.getPassword())) {
            log.warn("Password contains invalid characters for user '{}'", user.getName());
            return false;
        }
        return true;
    }

    /**
     * Hashes and salts the user's password using {@link PasswordUtil}.
     * The resulting hash and salt are stored in the user object.
     *
     * @param user the client whose password is being hashed
     * @return {@code true} if hashing succeeds, otherwise {@code false}
     */
    @Override
    public boolean hashAndSetPassword(Client user) {
        if (user == null) {
            log.warn("Cannot hash password: user is null");
            return false;
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            log.warn("Cannot hash password: password is null or blank for user '{}'", user.getName());
            return false;
        }

        try {
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt);
            user.setPasswordSalt(salt);
            user.setPassword(hashedPassword);
            log.debug("Password hashed successfully for '{}'", user.getName());
            return true;
        } catch (Exception e) {
            log.error("Error hashing password for '{}': {}", user.getName(), e.getMessage());
            return false;
        }
    }

    /**
     * Applies default values to optional user fields such as description and image filename
     * if they were not provided.
     *
     * @param user the client whose optional fields are being defaulted
     */
    @Override
    public void setDefaultValues(Client user) {
        if (user == null) return;

        if (user.getDescription() == null || user.getDescription().isBlank())
            user.setDescription("");

        if (user.getImageFileName() == null || user.getImageFileName().isBlank())
            user.setImageFileName("Fishing.jpg");
        else {
            Path imagesFolder = Paths.get("src/main/resources/static/images/profile/");
            Path imagePath = imagesFolder.resolve(user.getImageFileName());

            // If file doesn't exist, set default
            if (!Files.exists(imagePath)) {
                user.setImageFileName("Fishing.jpg");
            }
        }

        log.debug("Default values applied for optional fields for '{}'", user.getName());
    }


    /**
     * Attempts to persist the user to the database.
     *
     * @param user the client being saved
     * @return {@code true} if saving succeeds, otherwise {@code false}
     */
    @Override
    public boolean saveUser(Client user) {
        try {
            clientRepo.save(user);
            log.info("User '{}' successfully saved to database", user.getName());
            return true;
        } catch (Exception e) {
            log.error("Error saving user '{}': {}", user.getName(), e.getMessage());
            return false;
        }
    }
}
