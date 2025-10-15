package com.groovify;

import com.groovify.service.MusicImport;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;


@SpringBootApplication
@Controller
public class GroovifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroovifyApplication.class, args);
    }

    /**
     * Runs automatically right before the web server starts listening for requests.
     */
    @Bean
    public ApplicationRunner runMusicImport(MusicImport importService) {
        return args -> {
            System.out.println("Starting MP3 import before web server startup...");
            importService.importSongs();
            System.out.println("MP3 import complete. Continuing startup...");
        };
    }
}