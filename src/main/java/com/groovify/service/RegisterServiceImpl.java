package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final ClientRepo clientRepo;

    public RegisterServiceImpl(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

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
        return true;
    }

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
        return true;
    }

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

    @Override
    public void setDefaultValues(Client user) {
        if (user == null) return;
        if (user.getDescription() == null || user.getDescription().isBlank()) user.setDescription("");
        if (user.getImageFileName() == null || user.getImageFileName().isBlank()) user.setImageFileName("Fishing.jpg");
        log.debug("Default values applied for optional fields for '{}'", user.getName());
    }

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
