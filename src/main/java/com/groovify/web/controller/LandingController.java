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
    public String loginPost(
            @Valid @ModelAttribute LoginForm loginForm,
            BindingResult result,
            HttpSession session,    // inject session here
            RedirectAttributes attrs) {

        if (result.hasErrors()) {
            return "landingPage";
        }

        if (!loginService.validateUser(loginForm.getUsername(), loginForm.getPassword())) {
            result.addError(new ObjectError("globalError", "Username and password do not match known users"));
            return "loginFailure";
        }

        // ✅ Put the username (or full user object) into the session
        session.setAttribute("username", loginForm.getUsername());

        return "redirect:/home";
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