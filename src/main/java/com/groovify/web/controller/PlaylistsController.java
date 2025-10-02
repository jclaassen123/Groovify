package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlaylistsController {
    @GetMapping("/playlists")
    public String playlistsPage(Model model) {
        // Looks for src/main/resources/templates/playlists.html
        model.addAttribute("pageTitle", "Playlists");
        return "playlists";
    }
}
