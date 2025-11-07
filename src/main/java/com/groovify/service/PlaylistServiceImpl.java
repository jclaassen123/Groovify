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

    @Transactional
    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found: " + playlistId));
        Song song = songRepo.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found: " + songId));

        if (playlist.getSongs() == null) playlist.setSongs(new ArrayList<>());

        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
            playlistRepo.save(playlist);
        }
    }

    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
        Song song = songRepo.findById(songId).orElse(null);

        if (playlist != null && song != null && playlist.getSongs() != null) {
            playlist.getSongs().remove(song);
            playlistRepo.save(playlist);
        }
    }
}
