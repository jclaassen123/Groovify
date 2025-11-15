package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"groovify.import.genres.enabled=false"})
@Transactional
public class RecommendationServiceTest {

    @Autowired
    private SongRepo songRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private RecommendationService recommendationService;

    private Client testClient;
    private Client noGenreClient;
    private Genre rockGenre;
    private Genre popGenre;
    private Song song1;
    private Song song2;
    private Song song3;
    private Song song4;
    private Song song5;
    private Song song6;

    @BeforeEach
    public void setup() {
        // Create genres
        rockGenre = new Genre("Rock");
        popGenre = new Genre("Pop");
        genreRepo.save(rockGenre);
        genreRepo.save(popGenre);

        assertNotNull(rockGenre.getId(), "Rock genre should have ID assigned after save");
        assertNotNull(popGenre.getId(), "Pop genre should have ID assigned after save");

        // Create client with preferred genres
        testClient = new Client("TestUser", "password");
        testClient.setGenres(new ArrayList<>(List.of(rockGenre, popGenre)));
        clientRepo.save(testClient);

        noGenreClient = new Client("NoGenreUser", "password");
        noGenreClient.setGenres(new ArrayList<>());
        clientRepo.save(noGenreClient);

        assertNotNull(testClient.getId(), "Test client should have ID after save");
        assertNotNull(noGenreClient.getId(), "No-genre client should have ID after save");

        // Create songs
        song1 = new Song("song1.mp3", "Rock 1", "Artist A");
        song1.setGenre(rockGenre);

        song2 = new Song("song2.mp3", "Rock 2", "Artist B");
        song2.setGenre(rockGenre);

        song3 = new Song("song3.mp3", "Rock 3", "Artist C");
        song3.setGenre(rockGenre);

        song4 = new Song("song4.mp3", "Pop 1", "Artist D");
        song4.setGenre(popGenre);

        song5 = new Song("song5.mp3", "Pop 2", "Artist E");
        song5.setGenre(popGenre);

        song6 = new Song("song6.mp3", "Jazz 1", "Artist F");
        song6.setGenre(null);

        songRepo.saveAll(List.of(song1, song2, song3, song4, song5, song6));

        // Assert songs are persisted
        List<Song> persistedSongs = songRepo.findAll();
        assertEquals(6, persistedSongs.size(), "There should be exactly 6 songs in the database");
        assertTrue(persistedSongs.contains(song1), "Song1 should exist in DB");
        assertTrue(persistedSongs.contains(song6), "Song6 should exist in DB");

        // Assert client’s preferred genres
        Client persistedClient = clientRepo.findById(testClient.getId()).orElse(null);
        assertNotNull(persistedClient, "Client should exist in DB");
        assertEquals(2, persistedClient.getGenres().size(), "Client should have 2 preferred genres");
    }

    // ===============================
    // RecommendationService tests
    // ===============================

    @Test
    public void getRecommendedSongs_userHasPreferredGenres() {
        List<Song> recommendations = recommendationService.getRecommendedSongs(testClient);
        assertNotNull(recommendations, "Recommendations should not be null");
        assertFalse(recommendations.isEmpty(), "Recommendations should not be empty");
        assertTrue(recommendations.size() <= 5, "Recommendations should have at most 5 songs");

        boolean hasPreferredGenre = recommendations.stream()
                .anyMatch(s -> testClient.getGenres().contains(s.getGenre()));
        assertTrue(hasPreferredGenre, "At least one recommended song should be from a preferred genre");
    }

    @Test
    public void getRecommendedSongs_userHasNoPreferredGenres() {
        List<Song> recommendations = recommendationService.getRecommendedSongs(noGenreClient);
        assertNotNull(recommendations, "Recommendations should not be null");
        assertFalse(recommendations.isEmpty(), "Recommendations should not be empty");
        assertTrue(recommendations.size() <= 5, "Recommendations should have at most 5 songs");
    }

    @Test
    public void getRecommendedSongs_emptyDatabase() {
        songRepo.deleteAll();
        List<Song> recommendations = recommendationService.getRecommendedSongs(testClient);
        assertNotNull(recommendations, "Recommendations should not be null");
        assertTrue(recommendations.isEmpty(), "Recommendations should be empty when no songs exist");
    }

    @Test
    public void getRecommendedSongs_lessThanFiveSongsInDatabase() {
        songRepo.deleteAll();
        Song onlySong = new Song("only.mp3", "Only Song", "Solo Artist");
        songRepo.save(onlySong);

        List<Song> recommendations = recommendationService.getRecommendedSongs(testClient);
        assertEquals(1, recommendations.size(), "Should return all songs in database if fewer than 5 exist");
        assertEquals(onlySong.getTitle(), recommendations.get(0).getTitle());
    }

    @Test
    public void getRecommendedSongs_genreHasFewerSongsThanMax() {
        testClient.setGenres(new ArrayList<>(List.of(popGenre)));
        clientRepo.save(testClient);

        List<Song> recommendations = recommendationService.getRecommendedSongs(testClient);
        assertTrue(recommendations.size() <= 5, "Recommendations should not exceed 5 songs");
        assertTrue(recommendations.contains(song4) || recommendations.contains(song5),
                "Recommendations should include available Pop songs");
    }

    @Test
    public void getRecommendedSongs_noDuplicateSongs() {
        List<Song> recommendations = recommendationService.getRecommendedSongs(testClient);
        long uniqueCount = recommendations.stream().distinct().count();
        assertEquals(recommendations.size(), uniqueCount, "Recommendations should not contain duplicates");
    }

    @Test
    public void getRecommendedSongs_fallbackWhenGenreEmpty() {
        Genre emptyGenre = new Genre("EmptyGenre");
        genreRepo.save(emptyGenre);
        testClient.setGenres(new ArrayList<>(List.of(emptyGenre)));
        clientRepo.save(testClient);

        List<Song> recommendations = recommendationService.getRecommendedSongs(testClient);
        assertFalse(recommendations.isEmpty(), "Should fallback to other songs when genre has no songs");
        assertTrue(recommendations.size() <= 5, "Should not exceed 5 songs");
    }
}
