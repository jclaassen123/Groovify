package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.SongRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SongServiceImplTest {

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private SongRepo songRepo;

    @Autowired
    private SongService songService;

    @BeforeEach
    void setUp() {
        songRepo.deleteAll();
    }

    // ------------------ getAllSongs ------------------

    // Happy Tests

    @Test
    void getAllSongsShouldReturnAllSongs() {
        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");

        songRepo.save(s1);
        songRepo.save(s2);

        List<Song> result = songService.getAllSongs();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    @Test
    void getAllSongsShouldReturnEmptyListWhenNoSongsExist() {
        songRepo.deleteAll();

        List<Song> result = songService.getAllSongs();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllSongsShouldReturnOne() {
        Song s1 = new Song("file1.mp3", "Song1", "Artist1");

        songRepo.save(s1);

        List<Song> result = songService.getAllSongs();

        assertEquals(1, result.size());
        assertTrue(result.contains(s1));
    }

    @Test
    void getAllSongsShouldReturnZeroThanOne() {
        List<Song> result = songService.getAllSongs();

        assertEquals(0, result.size());

        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        songRepo.save(s1);

        List<Song> result2 = songService.getAllSongs();
        assertEquals(1, result2.size());
        assertTrue(result2.contains(s1));
    }

    @Test
    void getAllSongsShouldReturnOneThanZero() {
        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        songRepo.save(s1);

        List<Song> result = songService.getAllSongs();

        assertEquals(1, result.size());
        assertTrue(result.contains(s1));

        songRepo.delete(s1);

        List<Song> result2 = songService.getAllSongs();
        assertEquals(0, result2.size());
    }

    // ------------------ searchSongsByTitle ------------------

    // Happy Tests

    @Test
    void searchSongsByTitleShouldReturnMatchingSongs() {
        Song s1 = new Song("file1.mp3", "LoveSong", "Artist1");
        Song s2 = new Song("file2.mp3", "Lovely Day", "Artist2");

        songRepo.save(s1);
        songRepo.save(s2);

        List<Song> result = songService.searchSongsByTitle("love");

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    @Test
    void searchSongsByTitleWithSpecialCharactersShouldReturnMatchingSongs() {
        Song s1 = new Song("file1.mp3", "Love @ Song!", "Artist1");
        Song s2 = new Song("file2.mp3", "Lovely Day$!", "Artist2");

        songRepo.save(s1);
        songRepo.save(s2);

        List<Song> result = songService.searchSongsByTitle("Song!");

        assertEquals(1, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1)));
    }

    @Test
    void searchSongsByTitleWithDifferentCasesShouldReturnMatchingSongs() {
        Song s1 = new Song("file1.mp3", "Love Song", "Artist1");
        Song s2 = new Song("file2.mp3", "lovely Day", "Artist2");

        songRepo.save(s1);
        songRepo.save(s2);

        List<Song> result = songService.searchSongsByTitle("Love");

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    // Crappy Test

    @Test
    void searchSongsByTitleShouldReturnEmptyListWhenNoMatches() {
        List<Song> result = songService.searchSongsByTitle("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByTitleShouldReturnEmptyListWhenQueryBlank() {
        List<Song> result = songService.searchSongsByTitle("");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByTitleShouldReturnEmptyListWhenQuerySpace() {
        List<Song> result = songService.searchSongsByTitle("  ");

        assertTrue(result.isEmpty());    }

    @Test
    void searchSongsByTitleShouldReturnEmptyListWhenQueryNull() {
        List<Song> result = songService.searchSongsByTitle(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByTitleShouldReturnEmptyListWhenLongQuery() {
        List<Song> result = songService.searchSongsByTitle("A".repeat(1000));

        assertTrue(result.isEmpty());
    }


    // ------------------ searchSongsByGenre ------------------

    @Test
    void searchSongsByGenreShouldReturnMatchingSongs() {
        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        s1.setGenre(genreRepo.findById(1L).get());
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");
        s2.setGenre(genreRepo.findById(1L).get());

        songRepo.save(s1);
        songRepo.save(s2);

        List<Song> result = songService.searchSongsByGenre("Rock");

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    @Test
    void searchSongsByGenreShouldReturnMatchingSongsDifferentCase() {
        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        s1.setGenre(genreRepo.findById(1L).get());
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");
        s2.setGenre(genreRepo.findById(1L).get());

        songRepo.save(s1);
        songRepo.save(s2);

        List<Song> result = songService.searchSongsByGenre("rock");

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(s1, s2)));
    }

    // Crappy Test

    @Test
    void searchSongsByGenreShouldReturnEmptyListWhenQueryBlank() {
        List<Song> result = songService.searchSongsByGenre("");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByGenreShouldReturnEmptyListWhenQuerySpace() {
        List<Song> result = songService.searchSongsByGenre("  ");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByGenreShouldReturnEmptyListWhenQueryNull() {
        List<Song> result = songService.searchSongsByGenre(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByGenreShouldReturnEmptyListWhenNoMatches() {
        List<Song> result = songService.searchSongsByGenre("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByGenreShouldReturnEmptyListWhenQueryIsToLong() {
        List<Song> result = songService.searchSongsByGenre("A".repeat(1000));

        assertTrue(result.isEmpty());
    }

    @Test
    void searchSongsByGenreShouldReturnEmptyListWhenSpecialCharactersArePresent() {
        List<Song> result = songService.searchSongsByGenre("R!o#c$k%%(#)!>{}:");

        assertTrue(result.isEmpty());
    }


}
