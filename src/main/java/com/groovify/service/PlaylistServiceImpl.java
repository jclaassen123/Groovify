package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.PlaylistRepo;
import com.groovify.jpa.repo.SongRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepo playlistRepo;
    private final SongRepo songRepo;

    public PlaylistServiceImpl(PlaylistRepo playlistRepo, SongRepo songRepo) {
        this.playlistRepo = playlistRepo;
        this.songRepo = songRepo;
    }

    @Override
    public List<Playlist> getPlaylists(Long clientID) {
        return playlistRepo.findByClientID(clientID);
    }

    @Override
    public List<Song> getSongs(Long playlistId) {
        Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
        return playlist != null ? playlist.getSongs() : List.of();
    }

    @Override
    public void savePlaylist(Playlist playlist) {
        playlistRepo.save(playlist);
    }

    @Override
    public void deletePlaylist(Long id) {
        playlistRepo.deleteById(id);
    }

    @Override
    public Playlist getPlaylistById(Long id) {
        return playlistRepo.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public boolean addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found: " + playlistId));
        Song song = songRepo.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found: " + songId));

        if (playlist.getSongs() == null) playlist.setSongs(new ArrayList<>());

        if (playlist.getSongs().contains(song)) {
            return false; // Already in playlist
        }

        playlist.getSongs().add(song);
        playlistRepo.save(playlist);
        return true;
    }

    @Transactional
    @Override
    public boolean removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
        Song song = songRepo.findById(songId).orElse(null);

        if (playlist != null && song != null && playlist.getSongs() != null) {
            boolean removed = playlist.getSongs().remove(song);
            if (removed) playlistRepo.save(playlist);
            return removed;
        }
        return false;
    }


}

