package com.groovify.service;

/**
 * Service interface responsible for importing songs from a file system or other sources.
 * <p>
 * Implementations should handle reading song files, extracting metadata,
 * and saving songs to the database.
 */
public interface SongImportService {

    /**
     * Imports songs into the system.
     * <p>
     * The exact source of songs (file system, API, etc.) is implementation-dependent.
     */
    boolean importSongs();
}
