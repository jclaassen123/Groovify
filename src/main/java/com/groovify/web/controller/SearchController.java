package com.groovify.web.controller;

import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.UsersRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling requests related to the search page.
 * <p>
 * Ensures that only logged-in users can access the search page and
 * passes user-specific information to the view for the topbar.
 * </p>
 */
@Controller
public class SearchController {

    @Autowired
    private UsersRepo usersRepo;

    /**
     * Handles GET requests to "/search".
     * <p>
     * Checks if a user is logged in via the session. If not, redirects to the landing page.
     * Otherwise, fetches the user object and passes it along with page title to the view.
     * </p>
     *
     * @param session HTTP session containing logged-in user info
     * @param model   Model object to pass data to the view
     * @return name of the Thymeleaf template to render
     */
    @GetMapping("/search")
    public String searchPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:";
        }

        // Fetch full user object for topbar
        Users user = usersRepo.findByName(username).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Search");
        return "search";
    }
}