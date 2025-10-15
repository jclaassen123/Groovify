package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.SongRepo;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling requests related to songs.
 * <p>
 * This includes displaying the songs page with all available songs and
 * user-specific information in the topbar.
 * </p>
 */
@Controller
public class SongController {

    private final SongRepo songRepository;

    @Autowired
    private UsersRepo usersRepo;

    /**
     * Constructor for SongController.
     *
     * @param songRepository Repository for accessing songs data
     */
    public SongController(SongRepo songRepository) {
        this.songRepository = songRepository;
    }

    /**
     * Handles GET requests to "/songs".
     * <p>
     * Checks if a user is logged in via the session. If not, redirects to the landing page.
     * Otherwise, fetches all songs and user info and passes them to the Thymeleaf template.
     * </p>
     *
     * @param session HTTP session containing logged-in user info
     * @param model   Model object to pass data to the view
     * @return name of the Thymeleaf template to render
     */
    @GetMapping("/songs")
    public String songsPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:"; // Not logged in
        }

        // Fetch full user object for topbar
        Users user = usersRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);        // For topbar profile picture
        model.addAttribute("pageTitle", "Songs");
        model.addAttribute("songs", songRepository.findAll());
        return "songs";
    }
}