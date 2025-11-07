package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SongServiceImplTest {

    private SongRepo songRepo;
    private SongServiceImpl songService;

    @BeforeEach
    void setUp() {
        songRepo = mock(SongRepo.class);
        songService = new SongServiceImpl(songRepo);
    }

    // ------------------ getAllSongs ------------------

    @Test
    void getAllSongs_ShouldReturnAllSongs() {
        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");
        when(songRepo.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<Song> result = songService.getAllSongs();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    @Test
    void getAllSongs_ShouldReturnEmptyList_WhenNoSongsExist() {
        when(songRepo.findAll()).thenReturn(Collections.emptyList());

        List<Song> result = songService.getAllSongs();

        assertTrue(result.isEmpty());
    }

    // ------------------ searchSongsByTitle ------------------

    @Test
    void searchSongsByTitle_ShouldReturnMatchingSongs() {
        Song s1 = new Song("file1.mp3", "LoveSong", "Artist1");
        Song s2 = new Song("file2.mp3", "Lovely Day", "Artist2");

        when(songRepo.findByTitleContainingIgnoreCase("love")).thenReturn(Arrays.asList(s1, s2));

        List<Song> result = songService.searchSongsByTitle("love");

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    @Test
    void searchSongsByTitle_ShouldReturnEmptyList_WhenQueryBlank() {
        List<Song> result1 = songService.searchSongsByTitle("");
        List<Song> result2 = songService.searchSongsByTitle("  ");
        List<Song> result3 = songService.searchSongsByTitle(null);

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
    }

    @Test
    void searchSongsByTitle_ShouldReturnEmptyList_WhenNoMatches() {
        when(songRepo.findByTitleContainingIgnoreCase("unknown")).thenReturn(Collections.emptyList());

        List<Song> result = songService.searchSongsByTitle("unknown");

        assertTrue(result.isEmpty());
    }

    // ------------------ searchSongsByGenre ------------------

    @Test
    void searchSongsByGenre_ShouldReturnMatchingSongs() {
        Genre genre = new Genre("Rock");
        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        s1.setGenre(genre);
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");
        s2.setGenre(genre);

        when(songRepo.findByGenreNameContainingIgnoreCase("rock")).thenReturn(Arrays.asList(s1, s2));

        List<Song> result = songService.searchSongsByGenre("rock");

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    @Test
    void searchSongsByGenre_ShouldReturnEmptyList_WhenQueryBlank() {
        List<Song> result1 = songService.searchSongsByGenre("");
        List<Song> result2 = songService.searchSongsByGenre("  ");
        List<Song> result3 = songService.searchSongsByGenre(null);

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
    }

    @Test
    void searchSongsByGenre_ShouldReturnEmptyList_WhenNoMatches() {
        when(songRepo.findByGenreNameContainingIgnoreCase("unknown")).thenReturn(Collections.emptyList());

        List<Song> result = songService.searchSongsByGenre("unknown");

        assertTrue(result.isEmpty());
    }
}
