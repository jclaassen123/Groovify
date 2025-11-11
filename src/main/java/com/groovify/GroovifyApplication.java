package com.groovify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main entry point for the Groovify Spring Boot application.
 * <p>
 * This class bootstraps the Spring application context and starts the embedded server.
 * </p>
 */
@SpringBootApplication
public class GroovifyApplication {

    private static final Logger log = LoggerFactory.getLogger(GroovifyApplication.class);

    /**
     * Main method used to launch the Spring Boot application.
     *
     * @param args command-line arguments (optional)
     */
    public static void main(String[] args) {
        SpringApplication.run(GroovifyApplication.class, args);
    }
}