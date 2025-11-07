package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PlaylistControllerTest {

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private PlaylistsController playlistsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* --------------------------------------------------
     * GET /playlists
     * -------------------------------------------------- */

    @Test
    void playlistsPage_redirectsWhenUserNotLoggedIn() {
        when(session.getAttribute("username")).thenReturn(null);

        String result = playlistsController.playlistsPage(session, model);

        assertEquals("redirect:", result);
        verifyNoInteractions(clientRepo, model);
    }

    @Test
    void playlistsPage_loadsPlaylistsPageWhenUserLoggedIn() {
        Client user = new Client();
        user.setName("nevin");

        when(session.getAttribute("username")).thenReturn("nevin");
        when(clientRepo.findByName("nevin")).thenReturn(Optional.of(user));

        String result = playlistsController.playlistsPage(session, model);

        assertEquals("playlists", result);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("pageTitle", "Playlists");
    }

    @Test
    void playlistsPage_handlesMissingUserGracefully() {
        when(session.getAttribute("username")).thenReturn("ghostUser");
        when(clientRepo.findByName("ghostUser")).thenReturn(Optional.empty());

        String result = playlistsController.playlistsPage(session, model);

        assertEquals("playlists", result);
        verify(model).addAttribute("user", null);
        verify(model).addAttribute("pageTitle", "Playlists");
    }
}
