package com.groovify.web.controller;

import com.groovify.jpa.repo.SongsRepo;
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
    public String songs(Model model) {
        model.addAttribute("pageTitle", "Songs");
        model.addAttribute("songs", songsRepository.findAll());
        return "songs";
    }


}
