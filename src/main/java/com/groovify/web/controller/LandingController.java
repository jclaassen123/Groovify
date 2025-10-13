package com.groovify.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import com.groovify.service.LoginService;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import com.groovify.web.form.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsible for handling landing page requests, user login,
 * and logout functionality for the Groovify application.
 * <p>
 * This class interacts with the {@link LoginService} to validate users and
 * manages user sessions using {@link HttpSession}.
 */
@Controller
public class LandingController {
    private static final Logger log = LoggerFactory.getLogger(LandingController.class);

    private final LoginService loginService;

    public LandingController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Displays the landing page containing the login form.
     *
     * @param model the {@link Model} used to pass attributes to the view
     * @return the name of the landing page template
     */
    @GetMapping("/")
    public String landingPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "landingPage";
    }

    /**
     * Processes a login attempt submitted from the landing page.
     * <ul>
     *   <li>Validates form input.</li>
     *   <li>Authenticates the user using {@link LoginService}.</li>
     *   <li>Starts a session if login is successful.</li>
     * </ul>
     *
     * @param loginForm the login form data submitted by the user
     * @param result contains validation errors if the form input is invalid
     * @param session the current {@link HttpSession}, used to store user data
     * @param attrs redirect attributes for passing messages between requests
     * @return a redirect to the home page on success, or back to the landing page on failure
     */
    @PostMapping("/")
    public String loginPost(
            @Valid @ModelAttribute LoginForm loginForm,
            BindingResult result,
            HttpSession session,    // inject session here
            RedirectAttributes attrs) {

        //
        String username = loginForm.getUsername();
        log.info("User '{}' attempting to log in", username);

        if (result.hasErrors()) {
            log.warn("Validation error during login attempt by '{}'", username);
            return "landingPage";
        }

        if (!loginService.validateUser(username, loginForm.getPassword())) {
            log.warn("Invalid login attempt for username '{}'", username);
            result.addError(new ObjectError("globalError", "Invalid username or password."));
            return "landingPage";
        }

        // Put the username (or full user object) into the session
        session.setAttribute("username", username);
        log.info("User '{}' successfully logged in", username);

        return "redirect:/home";
    }

    /**
     * Handles user logout requests.
     * <p>
     * Invalidates the current session and redirects the user to the landing page.
     *
     * @param session the current {@link HttpSession} to be invalidated
     * @return a redirect to the landing page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Object username = session.getAttribute("username");
        if (username != null) {
            log.info("User '{}' logged out", username);
        }
        session.invalidate();           // ends the session
        return "redirect:";            // goes to landing page
    }
}
