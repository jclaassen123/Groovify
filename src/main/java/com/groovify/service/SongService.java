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
     * Retrieves song by ID
     * @param songId ID of song
     * @return Song corresponding to ID or null
     */
    Song getSongById(Long songId);

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

    /**
     * Checks if filename is in database
     * @param filename to search
     * @return Whether filename is in database or not
     */
    boolean searchSongByFilename(String filename);

    /**
     * Adds song to database
     * @param song Song to be added to Song table
     * @return If insertion was successful
     */
    boolean addSong(Song song);


}
