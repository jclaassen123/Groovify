package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UsersRepo usersRepo;

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:"; // Not logged in
        }

        // Fetch full user from database
        Users user = usersRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);       // For topbar profile image
        model.addAttribute("pageTitle", "Home"); // Optional page title
        return "home";
    }
}
