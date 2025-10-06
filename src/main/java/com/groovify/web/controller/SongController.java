package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.SongsRepo;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SongController {

    private final SongsRepo songsRepository;

    @Autowired
    private UsersRepo usersRepo;

    public SongController(SongsRepo songsRepository) {
        this.songsRepository = songsRepository;
    }

    @GetMapping("/songs")
    public String songsPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:"; // Not logged in
        }

        // Fetch full user object for topbar
        Users user = usersRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);        // For topbar profile picture
        model.addAttribute("pageTitle", "Songs");
        model.addAttribute("songs", songsRepository.findAll());
        return "songs";
    }
}