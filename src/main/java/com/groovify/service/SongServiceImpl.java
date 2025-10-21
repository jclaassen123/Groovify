package com.groovify.service;

import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;

import java.util.List;

public class SongServiceImpl implements SongService {

    private final SongRepo songRepository;

    SongServiceImpl(SongRepo songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }
}
