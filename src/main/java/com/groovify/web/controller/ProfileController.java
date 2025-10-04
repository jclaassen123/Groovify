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

@Controller
public class ProfileController {

    @Autowired
    private UsersRepo usersRepo;

    // Display profile page
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/"; // force login if not in session
        }

        Users user = usersRepo.findByName(username).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }



    // Handle updates from a form
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String image_file_name

    ) {
        Users user = usersRepo.findById(id).orElse(null);
        if (user != null) {
            user.setName(name);
            user.setDescription(description);
            user.setImageFileName(image_file_name);
            usersRepo.save(user);
        }

        return "redirect:/profile";
    }
}
