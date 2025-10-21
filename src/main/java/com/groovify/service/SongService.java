package com.groovify.service;

import com.groovify.jpa.model.Song;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SongService {

    List<Song> getAllSongs();
}
