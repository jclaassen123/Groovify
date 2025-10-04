package com.groovify.web.controller;

import com.groovify.jpa.repo.SongsRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SongController {
    private final SongsRepo songsRepository;

    public SongController(SongsRepo songsRepository) {
        this.songsRepository = songsRepository;
    }

    @GetMapping("/songs")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            // No session? Kick back to login
            return "redirect:/";
        }
        model.addAttribute("pageTitle", "Songs");
        model.addAttribute("username", username);
        return "songs";
    }

}
