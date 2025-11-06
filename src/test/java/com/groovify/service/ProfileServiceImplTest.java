package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    private ClientRepo clientRepo;
    private GenreRepo genreRepo;
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        clientRepo = mock(ClientRepo.class);
        genreRepo = mock(GenreRepo.class);
        profileService = new ProfileServiceImpl(clientRepo, genreRepo);
    }

    @Test
    void getUserByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(clientRepo.findByName("unknown")).thenReturn(java.util.Optional.empty());
        assertTrue(profileService.getUserByUsername("unknown").isEmpty());
    }

    @Test
    void getUserByUsername_ShouldReturnClient_WhenUserExists() {
        Client user = new Client();
        user.setName("Jace");
        when(clientRepo.findByName("Jace")).thenReturn(java.util.Optional.of(user));

        var result = profileService.getUserByUsername("Jace");
        assertTrue(result.isPresent());
        assertEquals("Jace", result.get().getName());
    }

    @Test
    void getAllGenres_ShouldReturnEmptyList_WhenNoGenresExist() {
        when(genreRepo.findAll()).thenReturn(List.of());
        var genres = profileService.getAllGenres();
        assertNotNull(genres);
        assertTrue(genres.isEmpty());
    }

    @Test
    void getAllGenres_ShouldReturnAllGenres() {
        Genre g1 = new Genre("Rock");
        Genre g2 = new Genre("Pop");
        when(genreRepo.findAll()).thenReturn(List.of(g1, g2));

        var genres = profileService.getAllGenres();
        assertNotNull(genres);
        assertEquals(2, genres.size());
        assertTrue(genres.stream().anyMatch(g -> g.getName().equals("Rock")));
        assertTrue(genres.stream().anyMatch(g -> g.getName().equals("Pop")));
    }

    @Test
    void isUsernameTaken_ShouldReturnFalse_WhenUsernameSameAsCurrent() {
        Client user = new Client();
        user.setName("Jace");
        when(clientRepo.findByName("Jace")).thenReturn(java.util.Optional.of(user));

        assertFalse(profileService.isUsernameTaken("Jace", "Jace"));
    }

    @Test
    void isUsernameTaken_ShouldReturnFalse_WhenUsernameNotTaken() {
        when(clientRepo.findByName("Jace")).thenReturn(java.util.Optional.empty());
        assertFalse(profileService.isUsernameTaken("Jace", "SomeoneElse"));
    }

    @Test
    void isUsernameTaken_ShouldReturnTrue_WhenUsernameTakenByOtherUser() {
        Client user = new Client();
        user.setName("Jace");
        when(clientRepo.findByName("Jace")).thenReturn(java.util.Optional.of(user));

        assertTrue(profileService.isUsernameTaken("Jace", "SomeoneElse"));
    }
}
