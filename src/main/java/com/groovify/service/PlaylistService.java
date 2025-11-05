package com.groovify.service;

import com.groovify.jpa.model.Song;

import java.util.List;

public interface PlaylistService {
    List<Song> getSongs(Long id);
}
