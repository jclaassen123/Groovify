package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.service.PlaylistService;
import com.groovify.web.dto.SongView;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spring MVC controller responsible for managing playlist-related web pages and actions.
 *
 * <p>This controller handles all playlist operations accessible through the web interface,
 * including viewing, creating, deleting playlists, and adding or removing songs from them.
 * It ensures that only authenticated users can access their playlists.</p>
 *
 * <p>The controller communicates with {@link PlaylistService} for business logic,
 * and uses repositories such as {@link ClientRepo} and {@link GenreRepo} for
 * retrieving user and genre information.</p>
 *
 * <p>All responses are mapped to Thymeleaf templates for rendering the user interface.</p>
 *
 * @author Nevin, Jace, Zack
 * @version 1.0
 * @see PlaylistService
 * @see ClientRepo
 * @see GenreRepo
 */
@Controller
public class PlaylistsController {

    private final ClientRepo clientRepo;
    private final PlaylistService playlistService;
    private final GenreRepo genreRepo;

    /**
     * Constructs a new {@code PlaylistsController} with the required dependencies.
     *
     * @param clientRepo repository for retrieving {@link Client} data
     * @param playlistService service for managing playlists
     * @param genreRepo repository for retrieving genre information
     */
    public PlaylistsController(ClientRepo clientRepo, PlaylistService playlistService, GenreRepo genreRepo) {
        this.clientRepo = clientRepo;
        this.playlistService = playlistService;
        this.genreRepo = genreRepo;
    }

    /**
     * Handles GET requests to the playlists page.
     *
     * <p>Retrieves the currently logged-in user's playlists and renders the
     * {@code playlists.html} template. If no user is logged in, the request
     * is redirected to the landing page.</p>
     *
     * @param session the current HTTP session containing the logged-in user's username
     * @param model the {@link Model} used to pass attributes to the view
     * @return the name of the Thymeleaf template to render, or a redirect string
     */
    @GetMapping("/playlists")
    public String playlistsPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        // Fetch full user object for topbar
        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) {
            return "redirect:";
        }

        // Fetch all playlists for this user
        List<Playlist> playlists = playlistService.getPlaylistsByClientId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Playlists");
        model.addAttribute("playlists", playlists);

        return "playlists";
    }

    /**
     * Handles GET requests to view a specific playlist.
     *
     * <p>Ensures the logged-in user owns the playlist before rendering
     * the {@code playlistSongs.html} view. If the playlist does not exist
     * or belongs to another user, redirects back to the playlists page.</p>
     *
     * @param playlistId the ID of the playlist to view
     * @param session the current HTTP session containing the user's credentials
     * @param model the {@link Model} for passing attributes to the view
     * @return the name of the view template to render, or a redirect instruction
     */
    @GetMapping("/playlists/{id}")
    public String viewPlaylist(@PathVariable("id") Long playlistId, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/";

        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) return "redirect:/";

        Playlist playlist = playlistService.getPlaylistById(playlistId);

        if (playlist == null || !playlist.getClientID().equals(user.getId())) {
            return "redirect:/playlists";
        }

        List<Song> songs = playlistService.getSongs(playlistId);

        List<SongView> songList = songs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenre().getId())
                    .map(genre -> genre.getName())
                    .orElse("Unknown");
            return new SongView(song.getId(), song.getTitle(), song.getArtist(), genreName, song.getFilename());
        }).toList();

        model.addAttribute("user", user);
        model.addAttribute("playlist", playlist);
        model.addAttribute("songList", songList);
        model.addAttribute("pageTitle", playlist.getName());
        model.addAttribute("inPlaylist", true);
        model.addAttribute("playlistId", playlistId);

        return "playlistSongs";
    }

    /**
     * Handles POST requests to create a new playlist for the logged-in user.
     *
     * <p>Creates a new {@link Playlist} entity with the specified name and description,
     * associates it with the logged-in client, and persists it to the database.</p>
     *
     * @param session the current HTTP session containing the logged-in user
     * @param name the name of the new playlist
     * @param description an optional description of the playlist
     * @param model the {@link Model} used for view rendering
     * @return a redirect instruction back to the playlists page
     */
    @PostMapping("/playlists/create")
    public String createPlaylist(HttpSession session,
                                 @RequestParam("name") String name,
                                 @RequestParam(value = "description", required = false) String description,
                                 Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/";

        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) return "redirect:/";

        boolean hasError = false;

        name = name != null ? name.trim() : "";
        description = description != null ? description.trim() : "";

        if (name.isEmpty() || name.length() > 20) {
            model.addAttribute("nameError", "Playlist name must be 1–30 characters.");
            hasError = true;
        }

        if (description.length() > 100) {
            model.addAttribute("descriptionError", "Description cannot exceed 100 characters.");
            hasError = true;
        }

        if (hasError) {
            // Re-populate the playlists list and user for the page
            List<Playlist> playlists = playlistService.getPlaylistsByClientId(user.getId());
            model.addAttribute("playlists", playlists);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Playlists");
            return "playlists"; // render the same page with error messages
        }

        // Create playlist if validation passed
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setClientID(user.getId());
        playlistService.savePlaylist(playlist);

        return "redirect:/playlists";
    }

    /**
     * Handles POST requests to delete a playlist.
     *
     * <p>Ensures the playlist belongs to the logged-in user before deletion.
     * Redirects to the playlists page after completion.</p>
     *
     * @param playlistId the ID of the playlist to delete
     * @param session the current HTTP session containing user info
     * @return a redirect instruction back to the playlists page
     */

    @PostMapping("/playlists/{playlistId}/delete")
    public String deletePlaylist(@PathVariable Long playlistId, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/";

        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) return "redirect:/";

        Playlist playlist = playlistService.getPlaylistById(playlistId);
        if (playlist == null || !playlist.getClientID().equals(user.getId())) {
            return "redirect:/playlists";
        }

        playlistService.deletePlaylist(playlistId);
        return "redirect:/playlists";
    }

    /**
     * Handles POST requests to add a song to a playlist.
     *
     * <p>Delegates the operation to {@link PlaylistService#addSongToPlaylist(Long, Long)}.
     * After completion, redirects the user back to the songs page.</p>
     *
     * @param playlistId the ID of the playlist to modify
     * @param songId the ID of the song to add
     * @return a redirect instruction to the songs page
     */
    @PostMapping("/playlists/{playlistId}/addSong/{songId}")
    @ResponseBody
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable Long playlistId,
                                                  @PathVariable Long songId) {
        boolean added = playlistService.addSongToPlaylist(playlistId, songId);
        if (added) {
            return ResponseEntity.ok().build(); // Song successfully added
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Already in playlist
        }
    }

    /**
     * Handles GET requests to remove a song from a playlist.
     *
     * <p>Delegates the removal to {@link PlaylistService#removeSongFromPlaylist(Long, Long)}
     * and redirects the user back to the playlist view page.</p>
     *
     * @param playlistId the ID of the playlist
     * @param songId the ID of the song to remove
     * @return a redirect instruction back to the playlist view page
     */
    @GetMapping("/playlists/{playlistId}/removeSong/{songId}")
    public String removeSongFromPlaylist(@PathVariable Long playlistId,
                                         @PathVariable Long songId) {
        boolean removed = playlistService.removeSongFromPlaylist(playlistId, songId);

        if (removed) {
            // Redirect with success query param
            return "redirect:/playlists/" + playlistId + "?removed=true";
        } else {
            // Redirect with failure query param
            return "redirect:/playlists/" + playlistId + "?removed=false";
        }
    }

}