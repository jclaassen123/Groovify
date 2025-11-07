package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.service.ProfileServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Controller responsible for handling user profile pages, updates, and
 * username availability checks.
 */
@Controller
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileServiceImpl profileService;

    /**
     * Constructs a ProfileController with the given ProfileServiceImpl.
     *
     * @param profileService service for managing profile-related operations
     */
    public ProfileController(ProfileServiceImpl profileService) {
        this.profileService = profileService;
    }

    /**
     * Handles GET requests to the profile page.
     * Loads the logged-in user's profile information and available genres.
     *
     * @param session the HTTP session containing user information
     * @param model   the Spring Model used to pass data to the view
     * @return the profile page view or redirect if user is not logged in
     */
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            log.warn("Access denied to profile page: no user logged in");
            return "redirect:";
        }

        Optional<Client> optionalUser = profileService.getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            log.warn("User '{}' not found when accessing profile page", username);
            return "redirect:";
        }

        Client user = optionalUser.get();
        log.info("User '{}' accessed profile page", username);

        // Add user and all available genres to the model
        model.addAttribute("user", user);
        model.addAttribute("allGenres", profileService.getAllGenres());

        return "profile";
    }

    /**
     * Handles POST requests for updating a user's profile.
     *
     * @param session        the HTTP session containing user information
     * @param name           the updated username
     * @param description    the updated user description
     * @param image_file_name the updated profile image filename
     * @param genres         the list of genre IDs selected by the user
     * @return redirect to the profile page
     */
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String image_file_name,
                                @RequestParam(required = false) List<Long> genres) {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            log.warn("Profile update attempt with no logged-in user");
            return "redirect:/";
        }

        Optional<Client> optionalUser = profileService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            Client user = optionalUser.get();

            log.info("Updating profile for user '{}'", username);
            profileService.updateProfile(user, name, description, image_file_name, genres);

            // Update session username in case it was changed
            session.setAttribute("username", name);
            log.debug("Session username updated to '{}'", name);
        } else {
            log.warn("Profile update attempted for non-existent user '{}'", username);
        }

        return "redirect:/profile";
    }

    /**
     * Checks if a given username is already taken.
     *
     * @param username the username to check
     * @param session  the HTTP session to get current logged-in username
     * @return true if the username is taken by someone else, false otherwise
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
