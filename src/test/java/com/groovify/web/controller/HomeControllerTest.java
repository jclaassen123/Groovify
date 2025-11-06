package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.service.RecommendationService;
import com.groovify.web.dto.SongView;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    @Mock private ClientRepo clientRepo;
    @Mock private GenreRepo genreRepo;
    @Mock private RecommendationService recommendationService;
    @Mock private HttpSession session;
    @Mock private org.springframework.ui.Model model;

    @InjectMocks private HomeController homeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void homePage_redirectsWhenUserNotLoggedIn() {
        when(session.getAttribute("username")).thenReturn(null);

        String result = homeController.homePage(session, model);

        assertEquals("redirect:", result);
        verifyNoInteractions(clientRepo, recommendationService, genreRepo, model);
    }

    @Test
    void homePage_redirectsWhenClientNotFound() {
        when(session.getAttribute("username")).thenReturn("ghostUser");
        when(clientRepo.findByName("ghostUser")).thenReturn(Optional.empty());

        String result = homeController.homePage(session, model);

        assertEquals("redirect:", result);
        verify(clientRepo).findByName("ghostUser");
        verifyNoInteractions(recommendationService, genreRepo);
    }

    @Test
    void homePage_loadsHomeViewWithRecommendations() {
        // Mock session and user
        when(session.getAttribute("username")).thenReturn("nevin");
        Client mockClient = new Client();
        mockClient.setName("nevin");

        when(clientRepo.findByName("nevin")).thenReturn(Optional.of(mockClient));

        // Mock recommended songs
        Song song1 = new Song("file1.mp3", "SongOne", "ArtistA");
        song1.setGenreId(1L);
        Song song2 = new Song("file2.mp3", "SongTwo", "ArtistB");
        song2.setGenreId(2L);

        when(recommendationService.getRecommendedSongs(mockClient)).thenReturn(List.of(song1, song2));

        // Mock genreRepo
        when(genreRepo.findById(1L)).thenReturn(Optional.of(new Genre("Rock")));
        when(genreRepo.findById(2L)).thenReturn(Optional.of(new Genre("Pop")));

        String result = homeController.homePage(session, model);

        assertEquals("home", result);

        verify(clientRepo).findByName("nevin");
        verify(recommendationService).getRecommendedSongs(mockClient);
        verify(model).addAttribute(eq("user"), eq(mockClient));
        verify(model).addAttribute(eq("pageTitle"), eq("Home"));
        verify(model).addAttribute(eq("songList"), any(List.class));
    }

    @Test
    void homePage_loadsHomeViewWithUnknownGenreIfMissing() {
        when(session.getAttribute("username")).thenReturn("nevin");
        Client mockClient = new Client();
        mockClient.setName("nevin");
        when(clientRepo.findByName("nevin")).thenReturn(Optional.of(mockClient));

        Song song = new Song("file1.mp3", "SongOne", "ArtistA");
        song.setGenreId(999L); // genre not found

        when(recommendationService.getRecommendedSongs(mockClient)).thenReturn(List.of(song));
        when(genreRepo.findById(999L)).thenReturn(Optional.empty());

        String result = homeController.homePage(session, model);

        assertEquals("home", result);
        verify(model).addAttribute(eq("user"), eq(mockClient));
        verify(model).addAttribute(eq("pageTitle"), eq("Home"));
        verify(model).addAttribute(eq("songList"), any(List.class));
    }
}
