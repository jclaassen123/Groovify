package com.groovify.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.groovify.jpa.model.Song;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SongRepo extends JpaRepository<Song, Long> {
    boolean existsByFilename(String filename);

    // Search songs by title (case-insensitive)
    List<Song> findByTitleContainingIgnoreCase(String title);
    List<Song> findByGenreId(Long genreId);
    @Query("SELECT s FROM Song s JOIN Genre g ON s.genreId = g.id WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Song> findByGenreNameContainingIgnoreCase(String genre);
}

