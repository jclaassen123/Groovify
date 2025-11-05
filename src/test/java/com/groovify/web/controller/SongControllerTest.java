package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.service.SongService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SongControllerTest {

    @Mock
    private SongService songService;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private SongController songController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* --------------------------------------------------
     * GET /songs
     * -------------------------------------------------- */

    @Test
    void songsPage_redirectsWhenUserNotLoggedIn() {
        when(session.getAttribute("username")).thenReturn(null);

        String result = songController.songPage(session, model);

        assertEquals("redirect:", result);
        verifyNoInteractions(clientRepo, songService, model);
    }

    @Test
    void songsPage_loadsSongsPageWhenUserLoggedIn() {
        Client user = new Client();
        user.setName("nevin");

        Song song1 = new Song("file1.mp3", "Song1", "Artist1");
        Song song2 = new Song("file2.mp3", "Song2", "Artist2");

        when(session.getAttribute("username")).thenReturn("nevin");
        when(clientRepo.findByName("nevin")).thenReturn(Optional.of(user));
        when(songService.getAllSongs()).thenReturn(List.of(song1, song2));

        String result = songController.songPage(session, model);

        assertEquals("songs", result);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("pageTitle", "Songs");
        verify(model).addAttribute("songs", List.of(song1, song2));
    }


    @Test
    void songsPage_handlesMissingUserGracefully() {
        // Arrange
        when(session.getAttribute("username")).thenReturn("ghost"); // user not in DB
        when(clientRepo.findByName("ghost")).thenReturn(Optional.empty());

        // Create dummy Song objects
        Song song1 = new Song("fileA.mp3", "SongA", "ArtistA");
        Song song2 = new Song("fileB.mp3", "SongB", "ArtistB");
        when(songService.getAllSongs()).thenReturn(List.of(song1, song2));

        // Act
        String result = songController.songPage(session, model);

        // Assert
        assertEquals("songs", result);
        verify(model).addAttribute("user", null); // user missing
        verify(model).addAttribute("pageTitle", "Songs");
        verify(model).addAttribute("songs", List.of(song1, song2));
    }

}
