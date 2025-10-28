package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.service.ProfileServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ProfileController {

    private final ProfileServiceImpl profileService;

    public ProfileController(ProfileServiceImpl profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/";

        Optional<Client> optionalUser = profileService.getUserByUsername(username);
        if (optionalUser.isEmpty()) return "redirect:/";

        Client user = optionalUser.get();
        model.addAttribute("user", user);
        model.addAttribute("allGenres", profileService.getAllGenres());
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String image_file_name,
                                @RequestParam(required = false) List<Long> genres) {

        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/";

        Optional<Client> optionalUser = profileService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            Client user = optionalUser.get();
            profileService.updateProfile(user, name, description, image_file_name, genres);
            session.setAttribute("username", name);
        }

        return "redirect:/profile";
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsernameExists(@RequestParam String username, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        return profileService.isUsernameTaken(username, currentUsername);
    }
}
