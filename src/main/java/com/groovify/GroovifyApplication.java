package com.groovify;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents the web controller for our Groovify project. Since we are using a React front end,
 * we added a RestController annotation.
 */

@SpringBootApplication
@RestController
public class GroovifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroovifyApplication.class, args);
    }

    @GetMapping("/")
    public String landingPage() {
        return "Groovify";
    }

}