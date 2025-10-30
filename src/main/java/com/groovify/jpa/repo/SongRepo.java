package com.groovify.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.groovify.jpa.model.Song;

import java.util.List;

public interface SongRepo extends JpaRepository<Song, Long> {
    boolean existsByFilename(String filename);

    // Search songs by title (case-insensitive)
    List<Song> findByTitleContainingIgnoreCase(String title);
}

