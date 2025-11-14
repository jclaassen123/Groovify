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
 * Provides methods to fetch all songs and perform searches by title or genre.
 */
@Service
public class SongServiceImpl implements SongService {

    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);

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

    @Override
    public Song getSongById(Long songId) {
        log.debug("Fetching song by id {}", songId);
        return songRepo.findById(songId).orElse(null);
    }

    /**
     * Searches for songs by title.
     *
     * @param query the search query string
     * @return a list of songs whose titles match the query, or empty list if query is blank
     */
    @Override
    public List<Song> searchSongsByTitle(String query) {
        if (query == null || query.isBlank()) {
            log.debug("Empty or null title query provided, returning empty list");
            return List.of();
        }

        log.debug("Searching songs by title containing '{}'", query);
        return songRepo.findByTitleContainingIgnoreCase(query);
    }

    /**
     * Searches for songs by genre.
     *
     * @param genre the genre name to search for
     * @return a list of songs whose genre names match the query, or empty list if genre is blank
     */
    @Override
    public List<Song> searchSongsByGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            log.debug("Empty or null genre query provided, returning empty list");
            return List.of();
        }

        log.debug("Searching songs by genre containing '{}'", genre);
        return songRepo.findByGenreNameContainingIgnoreCase(genre);
    }

    @Override
    public boolean addSong(Song song) {
        songRepo.save(song);
        return true;
    }


}
