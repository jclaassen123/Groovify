package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsible for handling user registration requests.
 * <p>
 * Manages both displaying the registration form and processing submissions.
 * Validates new user data, ensures unique usernames, safely hashes passwords,
 * saves valid users to the database, and logs registration activity.
 */
@Controller
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final ClientRepo clientRepo;

    /**
     * Constructs a RegisterController with the given ClientRepo.
     *
     * @param clientRepo repository for managing Client entities
     */
    public RegisterController(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    /**
     * Handles GET requests to show the registration form.
     *
     * @param model the Spring Model used to pass a new Client object to the view
     * @return the registration page view
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.debug("Displaying registration form");
        model.addAttribute("user", new Client());
        return "register";
    }

    /**
     * Handles POST requests to register a new user.
     *
     * @param user               the submitted Client object
     * @param result             the binding result containing validation errors
     * @param model              the Spring Model for passing data back to the view
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to landing page if successful, otherwise redisplay the registration form
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute Client user,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        String username = user.getName();
        log.info("User '{}' attempting to register", username);

        // Handle validation errors from @Valid
        if (result.hasErrors()) {
            log.warn("Registration failed for user '{}': validation errors", username);
            model.addAttribute("user", user);
            return "register";
        }

        // Check if username is already taken
        if (clientRepo.findByName(username).isPresent()) {
            log.warn("Registration failed: username '{}' already exists", username);
            model.addAttribute("user", user);
            model.addAttribute("error", "Username already exists.");
            return "register";
        }

        // Validate password presence
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            log.warn("Registration failed for user '{}': password is null or blank", username);
            model.addAttribute("user", user);
            model.addAttribute("error", "Password cannot be empty.");
            return "register";
        }

        // Hash password securely
        try {
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt);
            user.setPasswordSalt(salt);
            user.setPassword(hashedPassword);
            log.debug("Password hashed successfully for user '{}'", username);
        } catch (Exception e) {
            log.error("Error hashing password for user '{}': {}", username, e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("error", "An internal error occurred while processing your password.");
            return "register";
        }

        // Set default values for optional fields if missing
        if (user.getDescription() == null) user.setDescription("");
        if (user.getImageFileName() == null) user.setImageFileName("Fishing.jpg");
        log.debug("Default values applied for optional fields for user '{}'", username);

        // Save user to database
        try {
            clientRepo.save(user);
            log.info("User '{}' successfully registered", username);
        } catch (Exception e) {
            log.error("Error saving user '{}': {}", username, e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("error", "An error occurred while saving your account. Please try again.");
            return "register";
        }

        // Registration success — add flash message and redirect to landing page
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
        log.info("Registration completed successfully for user '{}'", username);

        return "redirect:";
    }
}
