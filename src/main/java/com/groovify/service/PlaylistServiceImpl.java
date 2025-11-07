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
        return playlistRepo.findByClientID(clientID);
    }

    @Override
    public List<Song> getSongs(Long playlistId) {
        Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
        if (playlist == null) return List.of();
        return playlist.getSongs();
    }


    @Override
    public Playlist savePlaylist(Playlist playlist) {
        return playlistRepo.save(playlist);
    }

    @Override
    public Playlist getPlaylistById(Long id) {
        return playlistRepo.findById(id).orElse(null);
    }
}
