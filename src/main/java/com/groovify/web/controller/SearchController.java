package com.groovify.web.controller;

import com.groovify.jpa.model.Song;
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

    public SearchController(SongService songService, GenreRepo genreRepo) {
        this.songService = songService;
        this.genreRepo = genreRepo;
    }

    // Show search page with all songs
    @GetMapping("/search")
    public String searchPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        model.addAttribute("songList", List.of());
        model.addAttribute("pageTitle", "Search");

        return "search"; // src/main/resources/templates/search.html
    }

    // Show filtered search results
    @GetMapping("/search/results")
    public String searchResults(@RequestParam("query") String query, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        List<Song> songs = songService.searchSongsByTitle(query);
        List<SongView> songList = songs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenreId())
                    .map(g -> g.getName())
                    .orElse("Unknown");
            return new SongView(song.getTitle(), song.getArtist(), genreName);
        }).toList();

        model.addAttribute("songList", songList);
        model.addAttribute("query", query);
        model.addAttribute("pageTitle", "Search Results");

        return "search"; // reuse the same template
    }
}