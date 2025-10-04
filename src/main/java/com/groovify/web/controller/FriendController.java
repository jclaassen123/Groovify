package com.groovify.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FriendController {

    @GetMapping("/friends")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            // No session? Kick back to login
            return "redirect:/";
        }
        model.addAttribute("pageTitle", "Friends");
        model.addAttribute("username", username);
        return "friends";
    }
}
