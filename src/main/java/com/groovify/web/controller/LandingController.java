package com.groovify.web.controller;

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

@Controller
public class LandingController {
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
    public String loginPost(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, RedirectAttributes attrs) {
        if (result.hasErrors()) {
            return "landingPage";
        }

        if (!loginService.validateUser(loginForm.getUsername(), loginForm.getPassword())) {
            result.addError(new ObjectError("globalError", "Username and password do not match known users"));
            return "loginFailure";
        }
        attrs.addAttribute("username", loginForm.getUsername());
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(@RequestParam String username, Model model) {
        // Looks for src/main/resources/templates/home.html
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("username", username);
        return "home";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess() {
        return "loginSuccess";
    }

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "loginFailure";
    }
}