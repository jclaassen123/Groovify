package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.SongRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.web.dto.SongView;
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
    private ClientRepo clientRepo;

    @Mock
    private SongRepo songRepo;

    @Mock
    private GenreRepo genreRepo;

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
        verifyNoInteractions(clientRepo, songRepo, genreRepo, model);
    }

    @Test
    void songsPage_loadsSongsPageWhenUserLoggedIn() {
        Client user = new Client();
        user.setName("nevin");

        Song song1 = new Song("file1.mp3", "Song1", "Artist1");
        song1.setGenreId(1L);
        Song song2 = new Song("file2.mp3", "Song2", "Artist2");
        song2.setGenreId(2L);

        Genre genre1 = new Genre("Rock"); genre1.setId(1L);
        Genre genre2 = new Genre("Pop"); genre2.setId(2L);

        when(session.getAttribute("username")).thenReturn("nevin");
        when(clientRepo.findByName("nevin")).thenReturn(Optional.of(user));
        when(songRepo.findAll()).thenReturn(List.of(song1, song2));
        when(genreRepo.findById(1L)).thenReturn(Optional.of(genre1));
        when(genreRepo.findById(2L)).thenReturn(Optional.of(genre2));

        String result = songController.songPage(session, model);

        assertEquals("songs", result);
        verify(model).addAttribute(eq("user"), eq(user));
        verify(model).addAttribute("pageTitle", "Songs");
        verify(model).addAttribute(eq("songList"), anyList());
    }

    @Test
    void songsPage_handlesMissingUserGracefully() {
        Song song1 = new Song("fileA.mp3", "SongA", "ArtistA");
        song1.setGenreId(1L);
        Song song2 = new Song("fileB.mp3", "SongB", "ArtistB");
        song2.setGenreId(2L);

        Genre genre1 = new Genre("Rock"); genre1.setId(1L);
        Genre genre2 = new Genre("Pop"); genre2.setId(2L);

        when(session.getAttribute("username")).thenReturn("ghost"); // user not in DB
        when(clientRepo.findByName("ghost")).thenReturn(Optional.empty());
        when(songRepo.findAll()).thenReturn(List.of(song1, song2));
        when(genreRepo.findById(1L)).thenReturn(Optional.of(genre1));
        when(genreRepo.findById(2L)).thenReturn(Optional.of(genre2));

        String result = songController.songPage(session, model);

        assertEquals("songs", result);
        verify(model).addAttribute("user", null);
        verify(model).addAttribute("pageTitle", "Songs");
        verify(model).addAttribute(eq("songList"), anyList());
    }
}
