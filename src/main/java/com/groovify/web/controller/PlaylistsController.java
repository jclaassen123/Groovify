package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.PlaylistRepo;
import com.groovify.service.PlaylistService;
import com.groovify.service.PlaylistServiceImpl;
import com.groovify.web.dto.SongView;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final GenreRepo genreRepo;

    public PlaylistsController(ClientRepo clientRepo, PlaylistService playlistService, GenreRepo genreRepo) {
        this.clientRepo = clientRepo;
        this.playlistService = playlistService;
        this.genreRepo = genreRepo;
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

    @GetMapping("/playlists/{id}")
    public String viewPlaylist(@PathVariable("id") Long playlistId, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:";

        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) return "redirect:";

        Playlist playlist = playlistService.getPlaylistById(playlistId);

        if (playlist == null || !playlist.getClientID().equals(user.getId())) {
            return "redirect:/playlists"; // redirect if playlist not found or belongs to another user
        }

        List<Song> songs = playlistService.getSongs(playlistId);

        List<SongView> songList = songs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenreId())
                    .map(genre -> genre.getName())
                    .orElse("Unknown");
            return new SongView(song.getTitle(), song.getArtist(), genreName);
        }).toList();

        model.addAttribute("user", user);
        model.addAttribute("playlist", playlist);
        model.addAttribute("songList", songList);
        model.addAttribute("pageTitle", playlist.getName());

        return "playlistSongs"; // single reusable template
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

    @GetMapping("/playlists/{playlistId}/removeSong/{songId}")
    public String removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return "redirect:/playlists/" + playlistId; // back to playlist view
    }

}