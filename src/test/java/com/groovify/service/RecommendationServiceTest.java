package com.groovify.service;

import com.groovify.jpa.model.Client;
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

class RecommendationServiceTest {

    private SongRepo songRepo;
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        songRepo = mock(SongRepo.class);
        recommendationService = new RecommendationService(songRepo);
    }

    // ------------------ getRecommendedSongs ------------------

    @Test
    void getRecommendedSongs_ShouldReturnSongsFromUserGenre_WhenUserHasPreferredGenres() {
        Client user = new Client();
        Genre genre = new Genre("Rock");
        genre.setId(1L);
        user.setGenres(List.of(genre));

        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        s1.setGenre(genre);
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");
        s2.setGenre(genre);
        Song s3 = new Song("file3.mp3", "Song3", "Artist3");
        s3.setGenre(genre);

        when(songRepo.findByGenreId(1L)).thenReturn(Arrays.asList(s1, s2, s3));

        List<Song> recommended = recommendationService.getRecommendedSongs(user);

        assertFalse(recommended.isEmpty());
        assertTrue(recommended.size() <= 5);
        assertTrue(recommended.stream().allMatch(s -> s.getGenre().equals(genre)));
    }

    @Test
    void getRecommendedSongs_ShouldFallbackToAllSongs_WhenUserHasNoGenres() {
        Client user = new Client();
        user.setGenres(Collections.emptyList());

        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");
        Song s3 = new Song("file3.mp3", "Song3", "Artist3");
        Song s4 = new Song("file4.mp3", "Song4", "Artist4");
        Song s5 = new Song("file5.mp3", "Song5", "Artist5");
        Song s6 = new Song("file6.mp3", "Song6", "Artist6");

        List<Song> allSongs = Arrays.asList(s1, s2, s3, s4, s5, s6);
        when(songRepo.findAll()).thenReturn(allSongs);

        List<Song> recommended = recommendationService.getRecommendedSongs(user);

        assertFalse(recommended.isEmpty());
        assertTrue(recommended.size() <= 5);
        assertTrue(allSongs.containsAll(recommended));
    }

    @Test
    void getRecommendedSongs_ShouldFallbackToAllSongs_WhenGenreHasNoSongs() {
        Client user = new Client();
        Genre genre = new Genre("Jazz");
        genre.setId(2L);
        user.setGenres(List.of(genre));

        // No songs in Jazz
        when(songRepo.findByGenreId(2L)).thenReturn(Collections.emptyList());

        Song s1 = new Song("file1.mp3", "Song1", "Artist1");
        Song s2 = new Song("file2.mp3", "Song2", "Artist2");
        List<Song> allSongs = Arrays.asList(s1, s2);
        when(songRepo.findAll()).thenReturn(allSongs);

        List<Song> recommended = recommendationService.getRecommendedSongs(user);

        assertFalse(recommended.isEmpty());
        assertTrue(recommended.size() <= 5);
        assertTrue(allSongs.containsAll(recommended));
    }

    @Test
    void getRecommendedSongs_ShouldReturnEmptyList_WhenNoSongsExist() {
        Client user = new Client();
        user.setGenres(Collections.emptyList());

        when(songRepo.findAll()).thenReturn(Collections.emptyList());

        List<Song> recommended = recommendationService.getRecommendedSongs(user);
        assertTrue(recommended.isEmpty());
    }

    @Test
    void getRecommendedSongs_ShouldFail_WhenRepoThrowsException() {
        Client user = new Client();
        user.setGenres(Collections.emptyList());

        when(songRepo.findAll()).thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(RuntimeException.class,
                () -> recommendationService.getRecommendedSongs(user));
        assertEquals("DB error", exception.getMessage());
    }
}
