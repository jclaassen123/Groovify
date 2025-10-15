package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling session-related pages.
 * <p>
 * Currently provides a mapping for the session timeout page.
 * </p>
 */
@Controller
public class SessionController {

    /**
     * Handles requests to the session timeout page.
     *
     * @return the name of the Thymeleaf template "sessionTimedOut.html"
     */
    @GetMapping("/sessionTimedOut")
    public String sessionTimedOut() {
        return "sessionTimedOut";
    }
}

