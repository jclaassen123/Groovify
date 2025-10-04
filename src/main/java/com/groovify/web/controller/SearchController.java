package com.groovify.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            // No session? Kick back to login
            return "redirect:/";
        }
        model.addAttribute("pageTitle", "Search");
        model.addAttribute("username", username);
        return "search";
    }
}
