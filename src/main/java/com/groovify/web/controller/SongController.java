package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.SongRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.service.PlaylistService;
import com.groovify.web.dto.SongView;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Controller responsible for displaying all songs to logged-in users.
 * <p>
 * Fetches all songs from the database, converts them to SongView DTOs
 * with genre names, and renders the songs page.
 */
@Controller
public class SongController {

    private static final Logger log = LoggerFactory.getLogger(SongController.class);

    private final ClientRepo clientRepo;
    private final SongRepo songRepo;
    private final GenreRepo genreRepo;
    private final PlaylistService playlistService;


    /**
     * Constructs a SongController with required repositories.
     *
     * @param clientRepo repository for accessing client data
     * @param songRepo   repository for accessing song data
     * @param genreRepo  repository for accessing genre data
     */
    public SongController(ClientRepo clientRepo, SongRepo songRepo, GenreRepo genreRepo, PlaylistService playlistService) {
        this.clientRepo = clientRepo;
        this.songRepo = songRepo;
        this.genreRepo = genreRepo;
        this.playlistService = playlistService;
    }

    /**
     * Handles GET requests to the songs page.
     * Fetches all songs, maps them to SongView objects, and adds them to the model.
     *
     * @param session the HTTP session containing user information
     * @param model   the Spring Model used to pass data to the view
     * @return the songs page view or redirect if user is not logged in
     */
    @GetMapping("/songs")
    public String songPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            log.warn("Access to songs page denied: no user logged in");
            return "redirect:";
        }

        Client user = clientRepo.findByName(username).orElse(null);
        log.info("User '{}' accessed songs page", username);

        // Fetch all songs from the database
        List<Song> songs = songRepo.findAll();
        log.debug("Fetched {} songs from database", songs.size());

        List<Playlist> playlists = playlistService.getPlaylists(user.getId());

        // Map songs to SongView DTOs with genre names
        List<SongView> songList = songs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenreId())
                    .map(genre -> genre.getName())
                    .orElse("Unknown");
            return new SongView(song.getId(), song.getTitle(), song.getArtist(), genreName);
        }).toList();
        log.debug("Converted songs to SongView list");

        // Add model attributes for rendering
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Songs");
        model.addAttribute("songList", songList);
        model.addAttribute("inPlaylist", false); // we are not in a playlist
        model.addAttribute("playlists", playlists);

        return "songs";
    }

}
