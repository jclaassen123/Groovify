package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.service.PlaylistService;
import com.groovify.service.SongService;
import com.groovify.web.dto.SongView;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller responsible for handling song search functionality.
 * <p>
 * Provides endpoints for displaying the search page and search results
 * based on song title or genre. Converts search results into SongView
 * DTOs for display in the frontend.
 */
@Controller
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final SongService songService;
    private final GenreRepo genreRepo;
    private final ClientRepo clientRepo;
    private final PlaylistService playlistService;

    /**
     * Constructs a SearchController with required repositories and services.
     *
     * @param songService service for querying songs
     * @param genreRepo   repository for accessing genre information
     * @param clientRepo  repository for accessing client data
     */
    public SearchController(SongService songService, GenreRepo genreRepo, ClientRepo clientRepo, PlaylistService playlistService) {
        this.songService = songService;
        this.genreRepo = genreRepo;
        this.clientRepo = clientRepo;
        this.playlistService = playlistService;
    }

    /**
     * Displays the search page.
     *
     * @param session the HTTP session containing user information
     * @param model   the Spring Model used to pass data to the view
     * @return the search page view or redirect if user is not logged in
     */
    @GetMapping("/search")
    public String searchPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            log.warn("Access to search page denied: no user logged in");
            return "redirect:/";
        }

        Client user = clientRepo.findByName(username).orElse(null);
        log.info("User '{}' accessed search page", username);

        List<Playlist> playlists = playlistService.getPlaylists(user.getId());

        // Initialize empty song list
        model.addAttribute("user", user);
        model.addAttribute("songList", List.of());
        model.addAttribute("pageTitle", "Search");
        model.addAttribute("playlists", playlists);


        return "search";
    }

    /**
     * Displays search results for a given query.
     *
     * @param query   the search term entered by the user
     * @param type    the type of search: "title" or "genre" (default is "title")
     * @param session the HTTP session containing user information
     * @param model   the Spring Model used to pass data to the view
     * @return the search results view or redirect if user is not logged in
     */
    @GetMapping("/search/results")
    public String searchResults(
            @RequestParam("query") String query,
            @RequestParam(value = "type", defaultValue = "title") String type,
            HttpSession session,
            Model model) {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            log.warn("Access to search results denied: no user logged in");
            return "redirect:/";
        }

        Client user = clientRepo.findByName(username).orElse(null);
        log.info("User '{}' performed a '{}' search with query '{}'", username, type, query);

        // Perform search by type
        List<Song> songs = "genre".equalsIgnoreCase(type)
                ? songService.searchSongsByGenre(query)
                : songService.searchSongsByTitle(query);

        log.debug("Found {} songs for query '{}' of type '{}'", songs.size(), query, type);

        // Convert songs to SongView DTOs
        List<SongView> songList = songs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenre().getId())
                    .map(g -> g.getName())
                    .orElse("Unknown");
            return new SongView(song.getId(), song.getTitle(), song.getArtist(), genreName,song.getFilename());
        }).toList();

        List<Playlist> playlists = playlistService.getPlaylists(user.getId());

        // Add attributes for rendering
        model.addAttribute("user", user);
        model.addAttribute("songList", songList);
        model.addAttribute("playlists", playlists);
        model.addAttribute("query", query);
        model.addAttribute("type", type);
        model.addAttribute("pageTitle", "Search Results");

        return "search";
    }
}
