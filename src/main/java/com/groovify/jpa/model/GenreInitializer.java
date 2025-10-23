package com.groovify.jpa.model;

import com.groovify.jpa.repo.GenreRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GenreInitializer implements CommandLineRunner {

    private final GenreRepo genreRepo;

    public GenreInitializer(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    @Override
    public void run(String... args) {
        List<String> genres = List.of(
                "Rock", "Pop", "Hip-Hop", "Jazz", "Classical",
                "Electronic", "Country", "Reggae", "Blues", "Metal"
        );

        for (String genreName : genres) {
            // Check if the genre already exists
            if (genreRepo.findByName(genreName).isEmpty()) {
                genreRepo.save(new Genre(genreName));
            }
        }
    }
}
