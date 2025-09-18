package com.groovify;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    /**
     * In order to connect the React front end with our Java backend, a CrossOrigin annotation
     * is required. This enables CORS (Cross-Origin Resource Sharing) and allows a web application
     * running on one domain (in this case our React front end running on localhost:5173) to connect
     * to another domain (our Java backend running on localhost:8080).
     */

    @CrossOrigin
    @GetMapping("/")
    public String index() {
        return "Groovify";
    }

}