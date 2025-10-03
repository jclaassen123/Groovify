package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String searchPage(Model model) {
        // Looks for src/main/resources/templates/search.html
        model.addAttribute("pageTitle", "Search");
        return "search";
    }
}
