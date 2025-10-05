package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private UsersRepo usersRepo;

    // Display profile page
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:"; // force login
        }

        Users user = usersRepo.findByName(username).orElse(null);
        if (user == null) {
            return "redirect:"; // fallback
        }

        model.addAttribute("user", user);

        return "profile";
    }

    // Handle updates from a form
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String image_file_name) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/"; // not logged in
        }

        // Find the logged-in user by session
        Users user = usersRepo.findByName(username).orElse(null);
        if (user != null) {
            user.setName(name);
            user.setDescription(description);
            user.setImageFileName(image_file_name);
            usersRepo.save(user);
        }

        return "redirect:/profile";
    }
}
