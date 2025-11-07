package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getPlaylists(Long id);
    List<Song> getSongs(Long id);
    Playlist savePlaylist(Playlist playlist);

}
