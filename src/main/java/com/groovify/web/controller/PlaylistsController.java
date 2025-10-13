package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling playlist-related pages.
 * <p>
 * Ensures that only logged-in users can access the playlists page
 * and provides the full user object for UI elements such as the topbar.
 * </p>
 */
@Controller
public class PlaylistsController {

    @Autowired
    private UsersRepo usersRepo;

    /**
     * Handles GET requests to "/playlists".
     * <p>
     * Fetches the currently logged-in user's data and renders the playlists page.
     * Redirects to the landing page if the user is not logged in.
     * </p>
     *
     * @param session HTTP session containing logged-in user information
     * @param model   Model object to pass data to the view
     * @return name of the Thymeleaf template to render
     */
    @GetMapping("/playlists")
    public String playlistsPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        // Fetch full user object for topbar
        Users user = usersRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Playlists");
        return "playlists";
    }
}