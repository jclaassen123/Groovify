package com.groovify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionController {
    @GetMapping("/sessionTimedOut")
    public String sessionTimedOut() {
        return "sessionTimedOut"; // corresponds to sessionTimedOut.html
    }
}

