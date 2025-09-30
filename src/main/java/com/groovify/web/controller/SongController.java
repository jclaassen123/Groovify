package com.groovify.web.controller;

import com.groovify.jpa.model.Songs;
import com.groovify.jpa.repository.SongsRepository;
import com.groovify.web.form.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SongController {
    private final SongsRepository songsRepository;

    public SongController(SongsRepository songsRepository) {
        this.songsRepository = songsRepository;
    }

    @GetMapping("/songs")
    public String songs(Model model) {
        return "songs";
    }



}
