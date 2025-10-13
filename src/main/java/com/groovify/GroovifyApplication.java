package com.groovify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

/**
 * Main entry point for the Groovify Spring Boot application.
 * <p>
 * This class bootstraps the Spring application context and starts the embedded server.
 * </p>
 */
@SpringBootApplication
@Controller
public class GroovifyApplication {

    /**
     * Main method used to launch the Spring Boot application.
     *
     * @param args command-line arguments (optional)
     */
    public static void main(String[] args) {
        SpringApplication.run(GroovifyApplication.class, args);
    }
}