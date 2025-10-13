package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller responsible for handling user profile-related pages and actions.
 * <p>
 * Includes viewing the profile, updating profile information, and
 * checking username availability.
 * </p>
 */
@Controller
public class ProfileController {

    @Autowired
    private UsersRepo usersRepo;

    /**
     * Handles GET requests to "/profile".
     * <p>
     * Displays the profile page for the currently logged-in user.
     * Redirects to the landing page if the user is not logged in
     * or the user cannot be found in the database.
     * </p>
     *
     * @param session HTTP session containing logged-in user info
     * @param model   Model object to pass data to the view
     * @return name of the Thymeleaf template to render
     */
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        Users user = usersRepo.findByName(username).orElse(null);
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Handles POST requests to "/profile/update".
     * <p>
     * Updates the logged-in user's profile information: username, description, and profile image.
     * Also updates the session username if the user changed their username.
     * </p>
     *
     * @param session        HTTP session containing logged-in user info
     * @param name           new username
     * @param description    new description
     * @param image_file_name new profile image file name
     * @return redirect to the profile page
     */
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String image_file_name) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        // Find the logged-in user by session
        Users user = usersRepo.findByName(username).orElse(null);
        if (user != null) {
            user.setName(name);
            user.setDescription(description);
            user.setImageFileName(image_file_name);
            usersRepo.save(user);

            // Update session username if they changed it
            session.setAttribute("username", name);
        }

        return "redirect:/profile";
    }

    /**
     * Handles GET requests to "/check-username".
     * <p>
     * Returns true if the given username already exists in the database,
     * excluding the currently logged-in user's username.
     * Used for real-time username validation in forms.
     * </p>
     *
     * @param username the username to check
     * @param session  HTTP session containing logged-in user info
     * @return true if the username is taken, false otherwise
     */
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsernameExists(@RequestParam String username, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");

        // If they're keeping their own username, it's fine
        if (currentUsername != null && currentUsername.equalsIgnoreCase(username)) {
            return false;
        }

        // Otherwise check if anyone else has it
        Optional<Users> existing = usersRepo.findByName(username);
        return existing.isPresent();
    }
}
