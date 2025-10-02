package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SongsController {
    @GetMapping("/songs")
    public String songsPage(Model model) {
        // Looks for src/main/resources/templates/songs.html
        model.addAttribute("pageTitle", "Songs");
        return "songs";
    }
}
