package com.groovify.web.controller;

import jakarta.servlet.http.HttpSession;
import com.groovify.service.LoginService;
import com.groovify.web.form.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

/**
 * Controller responsible for handling landing page requests, user login,
 * and logout functionality for the Groovify application.
 * <p>
 * This class interacts with the {@link LoginService} to validate users and
 * manages user sessions using {@link HttpSession}.
 */
@Controller
public class LandingController {

    // Logger for tracking user interactions and events
    private static final Logger log = LoggerFactory.getLogger(LandingController.class);

    // Service responsible for validating login credentials
    private final LoginService loginService;

    /**
     * Constructs a LandingController with the provided LoginService.
     *
     * @param loginService the service used for validating user credentials
     */
    public LandingController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Handles GET requests to the landing page.
     * Initializes the login form model attribute.
     *
     * @param model the Spring Model used to pass data to the view
     * @return the name of the landing page view
     */
    @GetMapping("/")
    public String landingPage(Model model) {
        log.debug("Serving landing page view");
        model.addAttribute("loginForm", new LoginForm());
        return "landingPage";
    }

    /**
     * Handles POST requests for user login.
     * Validates the login form and authenticates the user.
     *
     * @param loginForm the login form submitted by the user
     * @param result the binding result holding validation errors
     * @param session the HTTP session for managing logged-in user state
     * @param attrs redirect attributes for passing flash messages
     * @return redirect to home page if successful, otherwise returns to landing page
     */
    @PostMapping("/")
    public String loginPost(
            @Valid @ModelAttribute LoginForm loginForm,
            BindingResult result,
            HttpSession session,
            RedirectAttributes attrs) {

        String username = loginForm.getUsername();
        log.info("User '{}' attempting to log in", username);

        // Check for validation errors in the submitted form
        if (result.hasErrors()) {
            log.warn("Validation error during login attempt by '{}'", username);
            result.getAllErrors().forEach(error ->
                    log.debug("Validation error: {}", error.getDefaultMessage())
            );
            return "redirect:";
        }

        // Validate credentials via the login service
        boolean isValid = loginService.validateClient(username, loginForm.getPassword());
        log.debug("Login validation result for user '{}': {}", username, isValid);

        if (!isValid) {
            log.warn("Invalid login attempt for username '{}'", username);
            result.addError(new ObjectError("globalError", "Invalid username or password."));
            return "redirect:";
        }

        // Set username in session to maintain login state
        session.setAttribute("username", username);
        log.info("User '{}' successfully logged in", username);

        return "redirect:/home";
    }

    /**
     * Handles user logout requests.
     * Invalidates the session and adds a logout flash message.
     *
     * @param session the HTTP session to invalidate
     * @param redirectAttributes the redirect attributes for flash messages
     * @return redirect to the landing page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        Object username = session.getAttribute("username");

        if (username != null) {
            log.info("User '{}' logged out", username);
        } else {
            log.debug("Logout requested with no active session user");
        }

        // Invalidate the session to clear any stored attributes
        session.invalidate();
        log.debug("Session invalidated successfully");

        // Add a flash attribute to notify the user of successful logout
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been logged out successfully.");
        log.debug("Logout flash message added");

        return "redirect:";
    }
}
