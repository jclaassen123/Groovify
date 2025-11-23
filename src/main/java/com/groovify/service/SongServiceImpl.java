package com.groovify.service;

import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of {@link SongService} for managing and searching songs.
 * <p>
 * Provides methods to:
 * <ul>
 *     <li>Fetch all songs in the system</li>
 *     <li>Fetch a song by its ID</li>
 *     <li>Search songs by title or genre</li>
 *     <li>Add a new song to the repository</li>
 * </ul>
 * This service interacts directly with {@link SongRepo} for database operations
 * and logs all actions for debugging and monitoring purposes.
 */
@Service
public class SongServiceImpl implements SongService {

    // Logger to trace service activity
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);

    // Repository for Song entity database access
    private final SongRepo songRepo;

    /**
     * Constructs a SongServiceImpl with the given SongRepo.
     *
     * @param songRepo repository for accessing Song entities
     */
    public SongServiceImpl(SongRepo songRepo) {
        this.songRepo = songRepo;
    }

    /**
     * Retrieves all songs in the system.
     *
     * @return a list of all Song entities
     */
    @Override
    public List<Song> getAllSongs() {
        log.debug("Fetching all songs");
        return songRepo.findAll();
    }

    /**
     * Retrieves a song by its database ID.
     *
     * @param songId the unique identifier of the song
     * @return the {@link Song} if found; {@code null} otherwise
     */
    @Override
    public Song getSongById(Long songId) {

        if (songId == null) {
            log.error("Null song id provided");
            return null;
        }

        log.debug("Fetching song by id {}", songId);
        return songRepo.findById(songId).orElse(null);
    }

    /**
     * Searches for songs whose title contains the given query string (case-insensitive).
     *
     * @param query the search query string
     * @return a list of songs matching the title query, or empty list if query is blank
     */
    @Override
    public List<Song> searchSongsByTitle(String query) {
        // Return empty list for null or blank queries
        if (query == null || query.isBlank()) {
            log.debug("Empty or null title query provided, returning empty list");
            return List.of();
        }

        log.debug("Searching songs by title containing '{}'", query);
        return songRepo.findByTitleContainingIgnoreCase(query);
    }

    /**
     * Searches for songs whose genre contains the given query string (case-insensitive).
     *
     * @param genre the genre name to search for
     * @return a list of songs matching the genre query, or empty list if genre is blank
     */
    @Override
    public List<Song> searchSongsByGenre(String genre) {
        // Return empty list for null or blank genre queries
        if (genre == null || genre.isBlank()) {
            log.debug("Empty or null genre query provided, returning empty list");
            return List.of();
        }

        log.debug("Searching songs by genre containing '{}'", genre);
        return songRepo.findByGenreNameContainingIgnoreCase(genre);
    }

    /**
     * Check if filename exists in song table
     * @param filename to search
     * @return Whether filename is in song table
     */
    @Override
    public boolean searchSongByFilename(String filename) {
        log.info("Searching songs by filename containing '{}'", filename);
        if (filename == null || filename.isBlank()) {
            log.error("Filename is null or empty");
            return false;
        }

        try {
            filename = filename.trim();
            return songRepo.existsByFilename(filename);
        } catch (Exception e) {
            log.error("Error finding song by filename {}", e.getMessage());
            return false;
        }
    }

    /**
     * Adds a new song to the database.
     *
     * @param song the {@link Song} entity to persist
     * @return {@code true} if the song is successfully saved
     */
    @Override
    public boolean addSong(Song song) {
        if (song == null) {
            log.error("Null song provided");
            return false;
        }

        if (song.getArtist() == null) {
            log.error("Null artist provided");
            return false;
        }

        if (song.getTitle() == null) {
            log.error("Null title provided");
            return false;
        }

        if (song.getGenre() == null) {
            log.error("Null genre provided");
            return false;
        }

        if (song.getFilename() == null) {
            log.error("Null filename provided");
            return false;
        }

        if (!song.getFilename().endsWith(".mp3")) {
            log.error("Song is not a mp3 file");
            return false;
        }

        if (searchSongByFilename(song.getFilename())) {
            log.error("Song already exists in the database");
            return false;
        }

        log.debug("Saving song {} into database", song);
        // Save song entity using repository
        songRepo.save(song);
        return true;
    }
}
