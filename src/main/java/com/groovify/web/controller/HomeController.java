package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String homePage(Model model) {
        // Looks for src/main/resources/templates/home.html
        model.addAttribute("pageTitle", "Home");
        return "home";
    }
}
