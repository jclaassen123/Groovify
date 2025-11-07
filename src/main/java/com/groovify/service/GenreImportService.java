package com.groovify.service;

import com.groovify.jpa.model.Genre;

import java.util.List;

/**
 * Service interface responsible for importing and managing genres.
 * <p>
 * Provides methods to import a list of genre names, check for the existence
 * of a genre, and save a new genre to the database.
 */
public interface GenreImportService {

    /**
     * Imports a list of genres by name. Existing genres should be skipped
     * or handled according to implementation logic.
     *
     * @param genreNames the list of genre names to import
     */
    void importGenres(List<String> genreNames);

    /**
     * Checks if a genre with the given name already exists.
     *
     * @param name the genre name to check
     * @return true if the genre exists, false otherwise
     */
    boolean genreExists(String name);

    /**
     * Saves a new genre with the given name to the database.
     *
     * @param name the genre name to save
     * @return the saved Genre entity
     */
    Genre saveGenre(String name);
}
