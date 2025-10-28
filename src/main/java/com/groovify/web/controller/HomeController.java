package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling the home page.
 * <p>
 * Ensures that only logged-in users can access the home page
 * and provides the full user object for UI elements such as the topbar.
 * </p>
 */
@Controller
public class HomeController {

    @Autowired
    private ClientRepo clientRepo;

    /**
     * Handles GET requests to "/home".
     * <p>
     * Fetches the currently logged-in user's data and renders the home page.
     * Redirects to the landing page if the user is not logged in.
     * </p>
     *
     * @param session HTTP session containing logged-in user information
     * @param model   Model object to pass data to the view
     * @return name of the Thymeleaf template to render
     */
    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        // Fetch full user from database
        Client user = clientRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Home");
        return "home";
    }
}
