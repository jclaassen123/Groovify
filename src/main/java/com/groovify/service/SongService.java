package com.groovify.service;

import com.groovify.jpa.model.Song;

import java.util.List;

/**
 * Service interface for managing and searching songs.
 * <p>
 * Provides methods to fetch all songs and perform searches by title or genre.
 */
public interface SongService {

    /**
     * Retrieves all songs in the system.
     *
     * @return a list of all Song entities
     */
    List<Song> getAllSongs();

    /**
     * Searches for songs by their title.
     *
     * @param query the search query string
     * @return a list of songs whose titles match the query
     */
    List<Song> searchSongsByTitle(String query);

    /**
     * Searches for songs by genre.
     *
     * @param genre the name of the genre to search for
     * @return a list of songs belonging to the specified genre
     */
    List<Song> searchSongsByGenre(String genre);
}
