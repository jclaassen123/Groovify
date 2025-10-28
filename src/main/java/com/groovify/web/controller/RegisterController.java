package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsible for handling user registration requests.
 * <p>
 * This controller manages both displaying the registration form and processing
 * form submissions. It validates new user data, ensures usernames are unique,
 * saves valid user accounts to the database, and logs all registration activity.
 */
@Controller
public class RegisterController {

    /** Logger instance for tracking registration events. */
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    /** Repository used to perform CRUD operations on {@link Client} entities. */
    @Autowired
    private UsersRepo usersRepo;

    /**
     * Displays the registration page where a user can create a new account.
     *
     * @param model the {@link Model} used to pass data to the view
     * @return the name of the Thymeleaf template for registration
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Client());
        return "register";
    }

    /**
     * Handles form submission for new user registration.
     * <p>
     * If the chosen username is already taken, an error message is displayed
     * and the user is prompted to try again. If registration is successful,
     * the user is redirected to the landing page.
     *
     * @param user  the {@link Client} object populated from form input
     * @param model the {@link Model} used to store messages for the view
     * @return redirect to landing page upon success, or the registration form on failure
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute Client user,
                               BindingResult result,
                               Model model) {
        String username = user.getName();
        log.info("User '{}' attempting to register", username);

        // Return to form if validation fails
        if (result.hasErrors()) {
            log.warn("Registration failed for user '{}': validation errors", username);
            model.addAttribute("user", user); // ✅ must add back the user object
            return "register";
        }

        // Check for duplicate username
        if (usersRepo.findByName(username).isPresent()) {
            log.warn("Registration failed: username '{}' already exists", username);
            model.addAttribute("user", user);  // ✅ add user back
            model.addAttribute("error", "Username already exists.");
            return "register";
        }

        // Save the new user
        usersRepo.save(user);
        log.info("User '{}' successfully registered", username);

        return "redirect:/"; // landing page after registration
    }

}
