package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.util.PasswordUtil;
import com.groovify.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final ClientRepo clientRepo;

    public RegisterServiceImpl(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    @Override
    public String registerUser(Client user,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        String username = user.getName();
        log.info("User '{}' attempting to register", username);

        if (!validateInput(user, result, model)) return "register";
        if (!checkUsernameAvailability(user, model)) return "register";
        if (!validatePassword(user, model)) return "register";
        if (!hashAndSetPassword(user, model)) return "register";

        setDefaultValues(user);

        if (!saveUser(user, model)) return "register";

        addSuccessRedirect(redirectAttributes, username);
        return "redirect:";
    }

    // --- Private Helper Methods ---

    /**
     * Validate @Valid binding result errors.
     */
    public boolean validateInput(Client user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            log.warn("Validation failed for user '{}'", user.getName());
            model.addAttribute("user", user);
            return false;
        }
        return true;
    }

    /**
     * Ensure the username is not already taken.
     */
    public boolean checkUsernameAvailability(Client user, Model model) {
        Optional<Client> existing = clientRepo.findByName(user.getName());
        if (existing.isPresent()) {
            log.warn("Username '{}' already exists", user.getName());
            model.addAttribute("user", user);
            model.addAttribute("error", "Username already exists.");
            return false;
        }
        return true;
    }

    /**
     * Ensure password is not null or blank.
     */
    public boolean validatePassword(Client user, Model model) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            log.warn("Password is null or blank for user '{}'", user.getName());
            model.addAttribute("user", user);
            model.addAttribute("error", "Password cannot be empty.");
            return false;
        }
        return true;
    }

    /**
     * Securely hash and set password with salt.
     */
    public boolean hashAndSetPassword(Client user, Model model) {
        try {
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt);
            user.setPasswordSalt(salt);
            user.setPassword(hashedPassword);
            log.debug("Password hashed successfully for '{}'", user.getName());
            return true;
        } catch (Exception e) {
            log.error("Error hashing password for '{}': {}", user.getName(), e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("error", "An internal error occurred while processing your password.");
            return false;
        }
    }

    /**
     * Set defaults for optional fields if missing.
     */
    public void setDefaultValues(Client user) {
        if (user.getDescription() == null) user.setDescription("");
        if (user.getImageFileName() == null) user.setImageFileName("Fishing.jpg");
        log.debug("Default values applied for optional fields for '{}'", user.getName());
    }

    /**
     * Persist user to the database.
     */
    public boolean saveUser(Client user, Model model) {
        try {
            clientRepo.save(user);
            log.info("User '{}' successfully registered", user.getName());
            return true;
        } catch (Exception e) {
            log.error("Error saving user '{}': {}", user.getName(), e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("error", "An error occurred while saving your account. Please try again.");
            return false;
        }
    }

    /**
     * Add success flash message and log.
     */
    public void addSuccessRedirect(RedirectAttributes redirectAttributes, String username) {
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
        log.info("Registration completed successfully for '{}'", username);
    }
}
