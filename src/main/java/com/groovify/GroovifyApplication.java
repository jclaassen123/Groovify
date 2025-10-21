package com.groovify;

import com.groovify.service.SongImportImpl;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

    /**
     * Runs automatically right before the web server starts listening for requests.
     */
    @Bean
    public ApplicationRunner runMusicImport(SongImportImpl importService) {
        return args -> {
            System.out.println("Starting MP3 import before web server startup...");
            importService.importSongs();
            System.out.println("MP3 import complete. Continuing startup...");
        };
    }
}