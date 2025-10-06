package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private UsersRepo usersRepo;

    // Display profile page
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/"; // force login
        }

        Users user = usersRepo.findByName(username).orElse(null);
        if (user == null) {
            return "redirect:/"; // fallback
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

            // Update session username if they changed it
            session.setAttribute("username", name);
        }

        return "redirect:/profile";
    }

    // ✅ Check if username already exists (excluding current user)
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsernameExists(@RequestParam String username, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");

        // If they're keeping their own username, it's fine
        if (currentUsername != null && currentUsername.equalsIgnoreCase(username)) {
            return false;
        }

        // Otherwise check if anyone else has it
        Optional<Users> existing = usersRepo.findByName(username);
        return existing.isPresent();
    }
}
