package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.service.ProfileServiceImpl;
import com.groovify.web.form.ProfileUpdateForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Controller responsible for handling user profile pages, updates,
 * and username availability checks.
 */
@Controller
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileServiceImpl profileService;

    public ProfileController(ProfileServiceImpl profileService) {
        this.profileService = profileService;
    }

    /**
     * Handles GET requests to the profile page.
     * Loads the logged-in user's profile information and available genres.
     */
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            log.warn("Access denied to profile page: no user logged in");
            return "redirect:/";
        }

        Optional<Client> optionalUser = profileService.getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            log.warn("User '{}' not found when accessing profile page", username);
            return "redirect:/";
        }

        Client user = optionalUser.get();
        log.info("User '{}' accessed profile page", username);

        // Add user info and form data to the model
        model.addAttribute("user", user);
        model.addAttribute("allGenres", profileService.getAllGenres());
        model.addAttribute("profileForm", new ProfileUpdateForm(user));

        return "profile";
    }

    /**
     * Handles POST requests for updating a user's profile.
     */
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session, @ModelAttribute ProfileUpdateForm form) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            log.warn("Profile update attempt with no logged-in user");
            return "redirect:/";
        }

        boolean updated = profileService.updateProfile(username, form);

        if (updated) {
            session.setAttribute("username", form.getName());
            log.info("Profile updated successfully for '{}'", form.getName());
        } else {
            log.warn("Profile update failed for '{}'", username);
        }

        return "redirect:/profile";
    }

    /**
     * Checks if a given username is already taken.
     */
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsernameExists(@RequestParam String username, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        boolean isTaken = profileService.isUsernameTaken(username, currentUsername);

        log.debug("Username '{}' checked for availability by '{}': {}", username, currentUsername, isTaken);

        return isTaken;
    }
}
