package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.service.SongService;
import com.groovify.web.dto.SongView;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private final SongService songService;
    private final GenreRepo genreRepo;
    private final ClientRepo clientRepo;

    public SearchController(SongService songService, GenreRepo genreRepo, ClientRepo clientRepo) {
        this.songService = songService;
        this.genreRepo = genreRepo;
        this.clientRepo = clientRepo;
    }

    @GetMapping("/search")
    public String searchPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        Client user = clientRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("songList", List.of());
        model.addAttribute("pageTitle", "Search");

        return "search";
    }

    @GetMapping("/search/results")
    public String searchResults(
            @RequestParam("query") String query,
            @RequestParam(value = "type", defaultValue = "title") String type,
            HttpSession session,
            Model model) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        Client user = clientRepo.findByName(username).orElse(null);

        List<Song> songs = "genre".equalsIgnoreCase(type)
                ? songService.searchSongsByGenre(query)
                : songService.searchSongsByTitle(query);

        List<SongView> songList = songs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenreId())
                    .map(g -> g.getName())
                    .orElse("Unknown");
            return new SongView(song.getTitle(), song.getArtist(), genreName);
        }).toList();

        model.addAttribute("user", user);
        model.addAttribute("songList", songList);
        model.addAttribute("query", query);
        model.addAttribute("type", type);
        model.addAttribute("pageTitle", "Search Results");

        return "search";
    }
}