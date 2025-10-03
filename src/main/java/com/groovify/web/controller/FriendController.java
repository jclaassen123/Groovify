package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FriendController {

    @GetMapping("/friends")
    public String friendsPage(Model model) {
        // Looks for src/main/resources/templates/friends.html
        model.addAttribute("pageTitle", "Friends");
        return "friends";
    }
}
