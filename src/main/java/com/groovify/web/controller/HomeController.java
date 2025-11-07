package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.web.dto.SongView;
import com.groovify.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ClientRepo clientRepo;
    private final GenreRepo genreRepo;
    private final RecommendationService recommendationService;

    public HomeController(ClientRepo clientRepo, GenreRepo genreRepo, RecommendationService recommendationService) {
        this.clientRepo = clientRepo;
        this.genreRepo = genreRepo;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) {
            return "redirect:";
        }

        // Get recommended songs
        List<Song> recommendedSongs = recommendationService.getRecommendedSongs(user);

        // Convert to SongView objects
        List<SongView> songList = recommendedSongs.stream().map(song -> {
            String genreName = genreRepo.findById(song.getGenreId())
                    .map(g -> g.getName())
                    .orElse("Unknown");
            return new SongView(song.getId(), song.getTitle(), song.getArtist(), genreName);
        }).toList();

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("songList", songList);

        return "home";
    }
}