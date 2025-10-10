package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UsersRepo usersRepo;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Users user, Model model) {
        String username = user.getName();
        log.info("User '{}' attempting to register", username);

        if (usersRepo.findByName(username).isPresent()) {
            log.warn("Registration failed: username '{}' already exists", username);
            model.addAttribute("error", "Username already exists.");
            model.addAttribute("user", user); // keep typed data
            return "register";
        }

        usersRepo.save(user);
        log.info("User '{}' successfully registered", username);

        model.addAttribute("loginForm", new Users());
        model.addAttribute("success", "Registration successful! Please log in.");
        return "redirect:";
    }
}
