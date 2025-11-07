package com.groovify.jpa.repo;

import com.groovify.jpa.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepo extends JpaRepository<Playlist, Long> {
    List<Playlist> getUserPlaylists(Long clientID);
}
