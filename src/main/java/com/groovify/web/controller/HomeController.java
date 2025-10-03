package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String homePage(@RequestParam String username, Model model) {
        // Looks for src/main/resources/templates/home.html
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("username", username);
        return "home";
    }
}
