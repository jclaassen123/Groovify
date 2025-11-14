package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.service.RegisterService;
import com.groovify.validation.RegexUtil;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for handling user registration requests.
 * <p>
 * Delegates business logic to {@link RegisterService}.
 */
@Controller
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private final RegisterService registerService;

    /**
     * Constructs a RegisterController with the given RegisterService.
     *
     * @param registerService service handling registration logic
     */
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    /**
     * Handles GET requests to show the registration form.
     *
     * @param model the Spring Model used to pass a new Client object to the view
     * @return the registration page view
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.debug("Displaying registration form");

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new Client());
        }

        return "register";
    }

    /**
     * Handles POST requests to register a new user.
     *
     * @param user               the submitted Client object
     * @param result             the binding result containing validation errors
     * @param model              the Spring Model for passing data back to the view
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to landing page if successful, otherwise redisplay the registration form
     */
    @PostMapping("/register")
    public String registerUser(
            @Validated({Default.class}) @ModelAttribute("user") Client user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("User '{}' attempting to register", user.getName());

        // --- NEW: Regex validation for user feedback ---
        if (!result.hasErrors()) { // only check regex if basic validation passed
            if (RegexUtil.isUsernameValid(user.getName())) {
                result.rejectValue("name", "invalid", "Username can only contain letters, numbers, dots, underscores, or hyphens.");
            }
            if (RegexUtil.isPasswordValid(user.getPassword())) {
                result.rejectValue("password", "invalid", "Password cannot contain: <>\"'%;()&+");
            }
        }

        // Handle validation errors (from @Valid/@Validated or regex)
        if (result.hasErrors()) {
            log.debug("Validation errors for user '{}': {}", user.getName(), result.getAllErrors());
            model.addAttribute("user", user);
            return "register";
        }

        // Delegate to service (pure business logic)
        boolean success = registerService.registerUser(user);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
            log.info("User '{}' successfully registered", user.getName());
            return "redirect:/"; // or redirect to home if you prefer
        } else {
            model.addAttribute("user", user);
            model.addAttribute("error", "Registration failed. Username may already exist or internal error occurred.");
            log.warn("Registration failed for user '{}'", user.getName());
            return "register";
        }
    }

}
