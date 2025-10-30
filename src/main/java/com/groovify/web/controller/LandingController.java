package com.groovify.web.controller;

import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/")
    public String landingPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "landingPage";
    }

    @PostMapping("/")
    public String loginPost(
            @Valid @ModelAttribute LoginForm loginForm,
            BindingResult result,
            HttpSession session,
            RedirectAttributes attrs) {

        String username = loginForm.getUsername();
        log.info("User '{}' attempting to log in", username);

        if (result.hasErrors()) {
            log.warn("Validation error during login attempt by '{}'", username);
            return "landingPage";
        }

        if (!loginService.validateClient(username, loginForm.getPassword())) {
            log.warn("Invalid login attempt for username '{}'", username);
            result.addError(new ObjectError("globalError", "Invalid username or password."));
            return "landingPage";
        }

        session.setAttribute("username", username);
        log.info("User '{}' successfully logged in", username);

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        Object username = session.getAttribute("username");
        if (username != null) {
            log.info("User '{}' logged out", username);
        }

        // Invalidate the session
        session.invalidate();

        // Add a flash attribute for logout success
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been logged out successfully.");

        return "redirect:/";
    }

}
