package com.groovify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@SpringBootApplication
@Controller
public class GroovifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroovifyApplication.class, args);
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        // Looks for src/main/resources/templates/home.html
        model.addAttribute("pageTitle", "Home");
        return "home";
    }

    @GetMapping("/playlists")
    public String playlistsPage(Model model) {
        // Looks for src/main/resources/templates/playlists.html
        model.addAttribute("pageTitle", "Playlists");
        return "playlists";
    }

    @GetMapping("/search")
    public String searchPage(Model model) {
        // Looks for src/main/resources/templates/search.html
        model.addAttribute("pageTitle", "Search");
        return "search";
    }

    @GetMapping("/friends")
    public String friendsPage(Model model) {
        // Looks for src/main/resources/templates/friends.html
        model.addAttribute("pageTitle", "Friends");
        return "friends";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
}