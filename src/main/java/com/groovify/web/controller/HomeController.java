package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.service.PlaylistService;
import com.groovify.web.dto.SongView;
import com.groovify.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller responsible for rendering the home page and providing
 * personalized song recommendations to the logged-in user.
 */
@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final ClientRepo clientRepo;
    private final GenreRepo genreRepo;
    private final RecommendationService recommendationService;
    private final PlaylistService playlistService;

    /**
     * Constructs a HomeController with required repositories and services.
     *
     * @param clientRepo            repository for accessing client data
     * @param genreRepo             repository for accessing genre data
     * @param recommendationService service to generate song recommendations
     */
    public HomeController(ClientRepo clientRepo, GenreRepo genreRepo, RecommendationService recommendationService, PlaylistService playlistService) {
        this.clientRepo = clientRepo;
        this.genreRepo = genreRepo;
        this.recommendationService = recommendationService;
        this.playlistService = playlistService;
    }

    /**
     * Handles GET requests to the home page.
     * <p>
     * Checks for a logged-in user and retrieves personalized song recommendations.
     *
     * @param session the HTTP session containing user information
     * @param model   the Spring Model used to pass data to the view
     * @return the home page view if user is logged in, otherwise redirects to landing page
     */
    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        // If no username is in session, redirect to landing page
        if (username == null) {
            log.warn("Access to /home denied: no user logged in");
            return "redirect:/";
        }
        log.info("User '{}' accessed home page", username);

        // Fetch user from the repository
        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) {
            log.warn("User '{}' not found in database", username);
            return "redirect:/";
        }
        log.debug("User '{}' retrieved from database: {}", username, user);

        // Retrieve recommended songs for the user
        List<Song> recommendedSongs = recommendationService.getRecommendedSongs(user);
        log.debug("Retrieved {} recommended songs for user '{}'", recommendedSongs.size(), username);

        // Convert Song entities to SongView DTOs for display
        List<SongView> songList = recommendedSongs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenre().getId())
                    .map(g -> g.getName())
                    .orElse("Unknown");
            return new SongView(song.getId(), song.getTitle(), song.getArtist(), genreName,song.getFilename());
        }).toList();
        log.debug("Converted recommended songs to SongView list for user '{}'", username);

        List<Playlist> playlists = playlistService.getPlaylistsByClientId(user.getId());

        // Add attributes to model for rendering in the view
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("songList", songList);
        model.addAttribute("playlists", playlists);
        log.info("Model attributes set for home page of user '{}'", username);

        return "home";
    }
}
