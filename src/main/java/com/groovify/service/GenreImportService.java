package com.groovify.service;

import java.util.List;

/**
 * Service interface for importing and managing music genres.
 * <p>
 * Provides operations for bulk genre import, checking for existing genres,
 * and saving new genre entries while ensuring data integrity.
 */
public interface GenreImportService {

    /**
     * Imports a list of genres by name.
     * Existing genres or invalid names are skipped.
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
     * Null, empty, or duplicate names are ignored.
     *
     * @param name the genre name to save
     * @return true if the genre was saved, false if skipped
     */
    boolean saveGenre(String name);
}
