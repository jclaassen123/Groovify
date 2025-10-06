package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UsersRepo usersRepo;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Users user, Model model) {
        if (usersRepo.findByName(user.getName()).isPresent()) {
            model.addAttribute("error", "Username already exists.");
            model.addAttribute("user", user); // keep typed data
            return "register";
        }

        usersRepo.save(user);

        model.addAttribute("loginForm", new Users());
        model.addAttribute("success", "Registration successful! Please log in.");
        return "redirect:";
    }
}

