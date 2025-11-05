package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.GenreRepo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of {@link GenreImportService} for importing and managing genres.
 * <p>
 * Handles checking for existing genres, saving new genres, and batch importing
 * a list of genre names.
 */
@Service
public class GenreImportServiceImpl implements GenreImportService {

    private static final Logger log = LoggerFactory.getLogger(GenreImportServiceImpl.class);

    private final GenreRepo genreRepo;

    /**
     * Constructs a GenreImportServiceImpl with the given GenreRepo.
     *
     * @param genreRepo repository for accessing and saving Genre entities
     */
    public GenreImportServiceImpl(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    /**
     * Imports a list of genres, saving any that do not already exist.
     *
     * @param genreNames the list of genre names to import
     */
    @Override
    public void importGenres(List<String> genreNames) {
        for (String name : genreNames) {
            if (!genreExists(name)) {
                log.info("Importing new genre '{}'", name);
                saveGenre(name);
            } else {
                log.debug("Genre '{}' already exists, skipping import", name);
            }
        }
    }

    /**
     * Checks if a genre with the given name already exists in the database.
     *
     * @param name the genre name to check
     * @return true if the genre exists, false otherwise
     */
    @Override
    public boolean genreExists(String name) {
        boolean exists = genreRepo.findByName(name).isPresent();
        log.debug("Genre '{}' exists: {}", name, exists);
        return exists;
    }

    /**
     * Saves a new genre with the specified name to the database.
     *
     * @param name the genre name to save
     * @return the saved Genre entity
     */
    @Override
    public Genre saveGenre(String name) {
        Genre genre = new Genre(name);
        Genre savedGenre = genreRepo.save(genre);
        log.info("Saved new genre '{}'", name);
        return savedGenre;
    }
}
