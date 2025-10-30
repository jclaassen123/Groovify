package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.service.ProfileServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Mock
    private ProfileServiceImpl profileService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* --------------------------------------------------
     * GET /profile
     * -------------------------------------------------- */

    @Test
    void profilePage_redirectsWhenNotLoggedIn() {
        when(session.getAttribute("username")).thenReturn(null);

        String result = profileController.profilePage(session, model);

        assertEquals("redirect:/", result);
        verifyNoInteractions(profileService);
    }

    @Test
    void profilePage_redirectsWhenUserNotFound() {
        when(session.getAttribute("username")).thenReturn("unknown");
        when(profileService.getUserByUsername("unknown")).thenReturn(Optional.empty());

        String result = profileController.profilePage(session, model);

        assertEquals("redirect:/", result);
    }

    @Test
    void profilePage_loadsProfileWhenUserFound() {
        Client user = new Client();
        user.setName("nevin");

        Genre genre1 = new Genre("Rock");
        Genre genre2 = new Genre("Pop");

        when(session.getAttribute("username")).thenReturn("nevin");
        when(profileService.getUserByUsername("nevin")).thenReturn(Optional.of(user));
        when(profileService.getAllGenres()).thenReturn(List.of(genre1, genre2));

        String result = profileController.profilePage(session, model);

        assertEquals("profile", result);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("allGenres", List.of(genre1, genre2));
    }


    /* --------------------------------------------------
     * POST /profile/update
     * -------------------------------------------------- */

    @Test
    void updateProfile_redirectsWhenNotLoggedIn() {
        when(session.getAttribute("username")).thenReturn(null);

        String result = profileController.updateProfile(session,
                "name", "desc", "img.png", List.of(1L, 2L));

        assertEquals("redirect:/", result);
        verifyNoInteractions(profileService);
    }

    @Test
    void updateProfile_updatesWhenUserExists() {
        when(session.getAttribute("username")).thenReturn("oldUser");

        Client user = new Client();
        when(profileService.getUserByUsername("oldUser")).thenReturn(Optional.of(user));

        String result = profileController.updateProfile(session,
                "newName", "New bio", "photo.png", List.of(3L, 5L));

        assertEquals("redirect:/profile", result);
        verify(profileService).updateProfile(user, "newName", "New bio", "photo.png", List.of(3L, 5L));
        verify(session).setAttribute("username", "newName");
    }

    @Test
    void updateProfile_doesNothingWhenUserMissing() {
        when(session.getAttribute("username")).thenReturn("missingUser");
        when(profileService.getUserByUsername("missingUser")).thenReturn(Optional.empty());

        String result = profileController.updateProfile(session,
                "any", "desc", "img.png", null);

        assertEquals("redirect:/profile", result);
        verify(profileService, never()).updateProfile(any(), any(), any(), any(), any());
    }

    /* --------------------------------------------------
     * GET /check-username
     * -------------------------------------------------- */

    @Test
    void checkUsernameExists_returnsTrueWhenTaken() {
        when(session.getAttribute("username")).thenReturn("current");
        when(profileService.isUsernameTaken("target", "current")).thenReturn(true);

        boolean result = profileController.checkUsernameExists("target", session);

        assertEquals(true, result);
        verify(profileService).isUsernameTaken("target", "current");
    }

    @Test
    void checkUsernameExists_returnsFalseWhenAvailable() {
        when(session.getAttribute("username")).thenReturn("current");
        when(profileService.isUsernameTaken("newUser", "current")).thenReturn(false);

        boolean result = profileController.checkUsernameExists("newUser", session);

        assertEquals(false, result);
        verify(profileService).isUsernameTaken("newUser", "current");
    }
}
