package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.PlaylistRepo;
import com.groovify.service.PlaylistService;
import com.groovify.service.PlaylistServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller responsible for handling playlist-related pages.
 * <p>
 * Ensures that only logged-in users can access the playlists page
 * and provides the full user object for UI elements such as the topbar.
 * </p>
 */
@Controller
public class PlaylistsController {

    private final ClientRepo clientRepo;
    private final PlaylistService playlistService;

    public PlaylistsController(ClientRepo clientRepo, PlaylistService playlistService) {
        this.clientRepo = clientRepo;
        this.playlistService = playlistService;
    }

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
        Client user = clientRepo.findByName(username).orElse(null);

        // Fetch all playlists for this user
        List<Playlist> playlists = playlistService.getPlaylists(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Playlists");
        model.addAttribute("playlists", playlists);

        return "playlists";
    }

    @PostMapping("/playlists/create")
    public String createPlaylist(HttpSession session,
                                 @RequestParam("name") String name,
                                 Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) {
            return "redirect:";
        }

        // Create and save playlist
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setClientID(user.getId()); // link playlist to logged-in user
        playlistService.savePlaylist(playlist);

        // Redirect back to playlists page
        return "redirect:/playlists";
    }
}