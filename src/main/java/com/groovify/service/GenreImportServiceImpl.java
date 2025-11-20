package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.GenreRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for importing and managing music genres in the database.
 * <p>
 * This service provides methods to import a list of genres, check for the existence
 * of a genre, and save individual genres. It ensures that genres are not duplicated.
 * </p>
 */
@Service
public class GenreImportServiceImpl implements GenreImportService {

    private static final Logger log = LoggerFactory.getLogger(GenreImportServiceImpl.class);

    private final GenreRepo genreRepo;

    /**
     * Constructs a new {@code GenreImportServiceImpl} with the given {@link GenreRepo}.
     *
     * @param genreRepo the repository used to persist and query genres
     */
    public GenreImportServiceImpl(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    @Override
    public boolean genreExists(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return false;

        return genreRepo.findByName(trimmed).isPresent();
    }

    @Override
    public boolean saveGenre(String name) {
        if (name == null) return false;

        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            log.warn("Cannot save genre: name is null or empty");
            return false;
        }

        if (genreExists(trimmed)) {
            log.info("Genre '{}' already exists, skipping save", trimmed);
            return false;
        }

        Genre genre = new Genre(trimmed);
        genreRepo.save(genre);
        log.info("Saved new genre '{}'", trimmed);
        return true;
    }

    @Override
    public void importGenres(List<String> genreNames) {
        if (genreNames == null || genreNames.isEmpty()) return;

        for (String name : genreNames) {
            if (name == null) {
                log.debug("Skipping null genre name");
                continue;
            }

            String trimmed = name.trim();
            if (trimmed.isEmpty()) {
                log.debug("Skipping empty or whitespace-only genre name");
                continue;
            }

            if (!genreExists(trimmed)) {
                saveGenre(trimmed);
            } else {
                log.debug("Genre '{}' already exists, skipping import", trimmed);
            }
        }
    }

}
