package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.service.RegisterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
        model.addAttribute("user", new Client());
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
    public String registerUser(@Valid @ModelAttribute Client user,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        log.info("User '{}' attempting to register", user.getName());
        return registerService.registerUser(user, result, model, redirectAttributes);
    }
}
