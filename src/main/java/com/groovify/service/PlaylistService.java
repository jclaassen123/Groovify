package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getPlaylists(Long id);
    Playlist getPlaylistById(Long id);
    List<Song> getSongs(Long id);
    void savePlaylist(Playlist playlist);
    void deletePlaylist(Long id);
    boolean addSongToPlaylist(Long playlistId, Long songId);
    void removeSongFromPlaylist(Long playlistId, Long songId);

}
