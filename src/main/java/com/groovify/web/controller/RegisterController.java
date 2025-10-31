package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for handling user registration requests.
 * <p>
 * This controller manages both displaying the registration form and processing
 * form submissions. It validates new user data, ensures usernames are unique,
 * saves valid user accounts to the database, and logs all registration activity.
 */
@Controller
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final ClientRepo clientRepo;

    public RegisterController(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Client());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute Client user,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        String username = user.getName();
        log.info("User '{}' attempting to register", username);

        // Validation errors
        if (result.hasErrors()) {
            log.warn("Registration failed for user '{}': validation errors", username);
            model.addAttribute("user", user);
            return "register";
        }

        // Username already exists
        if (clientRepo.findByName(username).isPresent()) {
            log.warn("Registration failed: username '{}' already exists", username);
            model.addAttribute("user", user);
            model.addAttribute("error", "Username already exists.");
            return "register";
        }

        // Password validation
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            log.warn("Registration failed for user '{}': password is null or blank", username);
            model.addAttribute("user", user);
            model.addAttribute("error", "Password cannot be empty.");
            return "register";
        }

        // Hash password safely
        try {
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt);
            user.setPasswordSalt(salt);
            user.setPassword(hashedPassword);
        } catch (Exception e) {
            log.error("Error hashing password for user '{}': {}", username, e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("error", "An internal error occurred while processing your password.");
            return "register";
        }

        // Default values for optional fields
        if (user.getDescription() == null) user.setDescription("");
        if (user.getImageFileName() == null) user.setImageFileName("Fishing.jpg");

        // Save user with DB safety
        try {
            clientRepo.save(user);
            log.info("User '{}' successfully registered", username);
        } catch (Exception e) {
            log.error("Error saving user '{}': {}", username, e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("error", "An error occurred while saving your account. Please try again.");
            return "register";
        }

        // Success — add flash message and redirect
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
        return "redirect:";
    }
}
