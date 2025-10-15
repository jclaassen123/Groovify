package com.groovify.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.groovify.jpa.model.Song;

public interface SongRepo extends JpaRepository<Song, Long> {
    boolean existsByFilename(String filename);
}

