package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.PlaylistRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepo playlistRepo;

    public PlaylistServiceImpl(PlaylistRepo playlistRepo) {this.playlistRepo = playlistRepo;}

    @Override
    public List<Playlist> getPlaylists(Long clientID) {
        return playlistRepo.getUserPlaylists(clientID);
    }

    @Override
    public List<Song> getSongs(Long id) {
        return List.of();
    }
}
