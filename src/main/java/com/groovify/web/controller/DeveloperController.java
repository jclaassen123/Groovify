package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsible for rendering developer profile pages.
 * <p>
 * Each developer page is accessible only to logged-in users and displays
 * the developer's name, image, and bio.
 */
@Controller
public class DeveloperController {

    private static final Logger log = LoggerFactory.getLogger(DeveloperController.class);

    private final ClientRepo clientRepo;

    /**
     * Constructs a DeveloperController with the provided ClientRepo.
     *
     * @param clientRepo repository for accessing client data
     */
    public DeveloperController(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    /**
     * Handles GET requests for the Jace developer page.
     *
     * @param session the HTTP session to check for logged-in user
     * @param model   the Spring Model used to pass data to the view
     * @return the developer page view or redirect if user is not logged in
     */
    @GetMapping("/jace")
    public String jacePage(HttpSession session, Model model) {
        log.info("Accessing Jace developer page");
        return loadDeveloperPage(session, model, "Jace Claassen", "/images/developer/Jace.jpg",
                "Hi, I'm Jace — I’m an aspiring software engineer at Carroll College and have lived in Helena " +
                        "my whole life. I love building projects that challenge me to learn and create, but when I’m " +
                        "not coding, you’ll probably find me snowboarding, hunting, or playing a round of folf. I " +
                        "enjoy tackling new challenges, both on and off the computer, and I’m always looking for ways " +
                        "to grow as a developer.");
    }

    /**
     * Handles GET requests for the Zack developer page.
     *
     * @param session the HTTP session to check for logged-in user
     * @param model   the Spring Model used to pass data to the view
     * @return the developer page view or redirect if user is not logged in
     */
    @GetMapping("/zack")
    public String zackPage(HttpSession session, Model model) {
        log.info("Accessing Zack developer page");
        return loadDeveloperPage(session, model, "Zack Gacnik", "/images/developer/Zack.jpg",
                "Hi, I'm Zack - I like hiking in the woods looking for cool stuff.");
    }

    /**
     * Handles GET requests for the Nevin developer page.
     *
     * @param session the HTTP session to check for logged-in user
     * @param model   the Spring Model used to pass data to the view
     * @return the developer page view or redirect if user is not logged in
     */
    @GetMapping("/nevin")
    public String nevinPage(HttpSession session, Model model) {
        log.info("Accessing Nevin developer page");
        return loadDeveloperPage(session, model, "Nevin Fullerton", "/images/developer/Nevin.jpg",
                "Hi im nevin - I like reading sci-fi and coding.");
    }

    /**
     * Helper method to load a developer page for a specific developer.
     *
     * @param session   the HTTP session to verify logged-in user
     * @param model     the Spring Model used to pass data to the view
     * @param devName   the developer's name
     * @param imagePath the path to the developer's image
     * @param bio       the developer's bio
     * @return the developer page view or redirect if user is not logged in
     */
    private String loadDeveloperPage(HttpSession session, Model model, String devName,
                                     String imagePath, String bio) {
        String username = (String) session.getAttribute("username");

        // Redirect to landing page if user is not logged in
        if (username == null) {
            log.warn("Access denied to developer page '{}': no user logged in", devName);
            return "redirect:/";
        }
        log.info("User '{}' accessing developer page '{}'", username, devName);

        // Fetch logged-in user from database
        Client user = clientRepo.findByName(username).orElse(null);
        if (user == null) {
            log.warn("Logged-in user '{}' not found in database", username);
        } else {
            log.debug("User '{}' retrieved from database: {}", username, user);
        }

        // Set model attributes for the view
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", devName);
        model.addAttribute("name", devName);
        model.addAttribute("image", imagePath);
        model.addAttribute("bio", bio);

        return "developer";
    }
}
