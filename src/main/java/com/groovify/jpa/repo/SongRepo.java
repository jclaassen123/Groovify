package com.groovify.jpa.repo;

import com.groovify.jpa.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for accessing {@link Song} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 * Adds methods to check for existing songs and search by title or genre.
 */
public interface SongRepo extends JpaRepository<Song, Long> {

    /**
     * Checks if a song exists with the given filename.
     *
     * @param filename the filename of the song
     * @return true if a song with the filename exists, false otherwise
     */
    boolean existsByFilename(String filename);

    /**
     * Finds songs whose titles contain the given string (case-insensitive).
     *
     * @param title the title substring to search for
     * @return a list of songs matching the title
     */
    List<Song> findByTitleContainingIgnoreCase(String title);

    /**
     * Finds songs belonging to a specific genre by its ID.
     *
     * @param genreId the ID of the genre
     * @return a list of songs for the given genre
     */
    List<Song> findByGenreId(Long genreId);

    /**
     * Finds songs whose genre names contain the given string (case-insensitive).
     * <p>
     * Uses a JPQL query to join Song and Genre entities for the search.
     *
     * @param genre the genre substring to search for
     * @return a list of songs matching the genre name
     */
    @Query("SELECT s FROM Song s JOIN Genre g ON s.genreId = g.id " +
            "WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Song> findByGenreNameContainingIgnoreCase(String genre);
}
