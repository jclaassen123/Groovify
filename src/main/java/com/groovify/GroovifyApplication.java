package com.groovify;

import com.groovify.service.GenreImportService;
import com.groovify.service.SongImportImpl;
import com.groovify.web.controller.LandingController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Main entry point for the Groovify Spring Boot application.
 * <p>
 * This class bootstraps the Spring application context and starts the embedded server.
 * </p>
 */
@SpringBootApplication
@Controller
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

    /**
     * Runs automatically right before the web server starts listening for requests.
     */
    @Bean
    public ApplicationRunner runMusicImport(SongImportImpl importService) {
        return args -> {
            log.info("Starting MP3 import before web server startup...");
            importService.importSongs();
            log.info("MP3 import complete. Continuing startup...");
        };
    }

    /**
     * Runs automatically before the web server starts.
     * Imports predefined genres.
     */
    @Bean
    public ApplicationRunner runGenreImport(GenreImportService genreImportService) {
        return args -> {
            log.info("Starting genre import before web server startup...");
            List<String> genres = List.of(
                    "Rock", "Pop","Classical",
                    "Tech", "Country", "Folk"
            );
            genreImportService.importGenres(genres);
            log.info("Genre import complete. Continuing startup...");
        };
    }


}