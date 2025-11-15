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

    /**
     * Imports a list of genre names into the database.
     * <p>
     * Skips null or empty names and does not re-import genres that already exist.
     * </p>
     *
     * @param genreNames a list of genre names to import
     */
    @Override
    public void importGenres(List<String> genreNames) {
        if (genreNames == null || genreNames.isEmpty()) return;

        for (String name : genreNames) {
            if (name == null || name.trim().isEmpty()) {
                log.debug("Skipping null or empty genre name");
                continue;
            }
            if (!genreExists(name)) {
                saveGenre(name);
            } else {
                log.debug("Genre '{}' already exists, skipping import", name);
            }
        }
    }

    /**
     * Checks if a genre with the given name exists in the database.
     *
     * @param name the genre name to check
     * @return {@code true} if the genre exists, {@code false} otherwise
     */
    @Override
    public boolean genreExists(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return genreRepo.findByName(name).isPresent();
    }

    /**
     * Saves a single genre into the database.
     * <p>
     * Does nothing and returns {@code false} if the name is null, empty, or
     * the genre already exists.
     * </p>
     *
     * @param name the genre name to save
     * @return {@code true} if the genre was successfully saved, {@code false} otherwise
     */
    @Override
    public boolean saveGenre(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Cannot save genre: name is null or empty");
            return false;
        }

        if (genreExists(name)) {
            log.info("Genre '{}' already exists, skipping save", name);
            return false;
        }

        Genre genre = new Genre(name.trim());
        genreRepo.save(genre);
        log.info("Saved new genre '{}'", name);
        return true;
    }

}
