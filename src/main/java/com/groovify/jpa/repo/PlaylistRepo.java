package com.groovify.jpa.repo;

import com.groovify.jpa.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepo extends JpaRepository<Playlist, Long> {
}
