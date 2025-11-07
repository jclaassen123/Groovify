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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class SongController {

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private SongRepo songRepo;

    @Autowired
    private GenreRepo genreRepo;

    private final PlaylistService playlistService;

    public SongController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/songs")
    public String songPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        Client user = clientRepo.findByName(username).orElse(null);

        // Fetch all songs
        List<Song> songs = songRepo.findAll();

        List<Playlist> playlists = playlistService.getPlaylists(user.getId());

        // Map songs to SongView with genre name
        List<SongView> songList = songs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenreId())
                    .map(genre -> genre.getName())
                    .orElse("Unknown");
            return new SongView(song.getId(), song.getTitle(), song.getArtist(), genreName);
        }).toList();

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Songs");
        model.addAttribute("songList", songList);
        model.addAttribute("inPlaylist", false); // we are not in a playlist
        model.addAttribute("playlists", playlists);

        return "songs";
    }

}