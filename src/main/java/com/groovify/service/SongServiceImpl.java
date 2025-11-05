package com.groovify.service;

import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepo songRepo;

    // Constructor injection
    public SongServiceImpl(SongRepo songRepo) {
        this.songRepo = songRepo;
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepo.findAll();
    }

    @Override
    public List<Song> searchSongsByTitle(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return songRepo.findByTitleContainingIgnoreCase(query);
    }

    @Override
    public List<Song> searchSongsByGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            return List.of();
        }
        return songRepo.findByGenreNameContainingIgnoreCase(genre);
    }
}