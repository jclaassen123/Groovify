package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    @Autowired
    private UsersRepo usersRepo;

    // Display profile page
    @GetMapping("/profile")
    public String profilePage(Model model) {
        // For now, fetch a user with ID = 1 as a placeholder
        Users user = usersRepo.findById(1L).orElse(null);

        model.addAttribute("user", user);
        return "profile"; // corresponds to profile.html
    }

    // Handle updates from a form
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description
            // @RequestParam MultipartFile imageFile  <-- optional for file uploads
    ) {
        Users user = usersRepo.findById(id).orElse(null);
        if (user != null) {
            user.setName(name);
            user.setDescription(description);
            // TODO: handle image update
            usersRepo.save(user);
        }

        return "redirect:/profile";
    }
}
