package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeveloperController {

    @Autowired
    private ClientRepo clientRepo;

    @GetMapping("/jace")
    public String jacePage(HttpSession session, Model model) {
        return loadDeveloperPage(session, model, "Jace Claassen", "/images/developer/Jace.jpg", "Hi, I'm Jace — I'm nasty at rocket league.");
    }

    @GetMapping("/zack")
    public String zackPage(HttpSession session, Model model) {
        return loadDeveloperPage(session, model, "Zack Gaz", "/images/developer/Zack.jpg","Hi im zack");
    }

    @GetMapping("/nevin")
    public String nevinPage(HttpSession session, Model model) {
        return loadDeveloperPage(session, model, "Nevin F", "/images/developer/Nevin.jpg","Hi im nevin");
    }

    private String loadDeveloperPage(HttpSession session, Model model, String devName, String imagePath, String bio) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        Client user = clientRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", devName);
        model.addAttribute("name", devName);
        model.addAttribute("image", imagePath);
        model.addAttribute("bio", bio);  // use the provided bio

        return "developer";
    }
}

