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
 * This service provides methods to:
 * <ul>
 *     <li>Import a list of genres</li>
 *     <li>Check for the existence of a genre</li>
 *     <li>Save individual genres</li>
 * </ul>
 * It ensures that genres are not duplicated in the database and trims whitespace
 * from names before saving.
 * </p>
 * <p>
 * Logging is performed for important events, such as skipped duplicates, null names,
 * and successful saves.
 * </p>
 */
@Service
public class GenreImportServiceImpl implements GenreImportService {

    // Logger for recording service events and debugging information
    private static final Logger log = LoggerFactory.getLogger(GenreImportServiceImpl.class);

    // Repository used for CRUD operations on Genre entities
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
     * Checks whether a genre with the given name exists in the database.
     *
     * @param name the genre name to check
     * @return {@code true} if the genre exists; {@code false} if the name is null, empty, or not found
     */
    @Override
    public boolean genreExists(String name) {
        if (name == null) return false;

        // Trim whitespace to avoid false negatives
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return false;

        // Query the repository for existence
        return genreRepo.findByName(trimmed).isPresent();
    }

    /**
     * Saves a new genre with the given name if it does not already exist.
     *
     * @param name the genre name to save
     * @return {@code true} if the genre was saved; {@code false} if the name is null, empty, or already exists
     */
    @Override
    public boolean saveGenre(String name) {
        if (name == null) return false;

        // Trim whitespace for clean storage
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            log.warn("Cannot save genre: name is null or empty");
            return false;
        }

        // Skip saving if the genre already exists
        if (genreExists(trimmed)) {
            log.info("Genre '{}' already exists, skipping save", trimmed);
            return false;
        }

        // Create and save a new Genre entity
        Genre genre = new Genre(trimmed);
        genreRepo.save(genre);
        log.info("Saved new genre '{}'", trimmed);
        return true;
    }

    /**
     * Imports a list of genre names into the database, saving only those
     * that are non-null, non-empty, and do not already exist.
     *
     * @param genreNames the list of genre names to import
     */
    @Override
    public void importGenres(List<String> genreNames) {
        if (genreNames == null || genreNames.isEmpty()) return;

        for (String name : genreNames) {
            if (name == null) {
                log.debug("Skipping null genre name");
                continue; // Skip null entries
            }

            // Trim whitespace-only names
            String trimmed = name.trim();
            if (trimmed.isEmpty()) {
                log.debug("Skipping empty or whitespace-only genre name");
                continue;
            }

            // Save genre only if it doesn't already exist
            if (!genreExists(trimmed)) {
                saveGenre(trimmed);
            } else {
                log.debug("Genre '{}' already exists, skipping import", trimmed);
            }
        }
    }
}
