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

class DeveloperControllerTest {

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private DeveloperController developerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void jacePage_redirectsIfNotLoggedIn() {
        when(session.getAttribute("username")).thenReturn(null);

        String result = developerController.jacePage(session, model);

        assertEquals("redirect:/", result);
        verifyNoInteractions(clientRepo);
    }

    @Test
    void jacePage_loadsDeveloperPage_whenUserLoggedIn() {
        // Arrange
        when(session.getAttribute("username")).thenReturn("testUser");
        Client mockClient = new Client();
        mockClient.setName("testUser");
        when(clientRepo.findByName("testUser")).thenReturn(Optional.of(mockClient));

        // Act
        String result = developerController.jacePage(session, model);

        // Assert
        assertEquals("developer", result);
        verify(model).addAttribute("user", mockClient);
        verify(model).addAttribute("pageTitle", "Jace Claassen");
        verify(model).addAttribute("name", "Jace Claassen");
        verify(model).addAttribute("image", "/images/developer/Jace.jpg");
        verify(model).addAttribute("bio", "Hi, I'm Jace — I'm nasty at rocket league.");
    }

    @Test
    void zackPage_loadsCorrectDeveloperData() {
        when(session.getAttribute("username")).thenReturn("user");
        when(clientRepo.findByName("user")).thenReturn(Optional.empty());

        String result = developerController.zackPage(session, model);

        assertEquals("developer", result);
        verify(model).addAttribute("pageTitle", "Zack Gacnik");
        verify(model).addAttribute("name", "Zack Gacnik");
        verify(model).addAttribute("image", "/images/developer/Zack.jpg");
        verify(model).addAttribute("bio", "Hi, I'm Zack - I like hiking in the woods looking for cool stuff.");
    }

    @Test
    void nevinPage_loadsCorrectDeveloperData() {
        when(session.getAttribute("username")).thenReturn("nevin");
        when(clientRepo.findByName("nevin")).thenReturn(Optional.empty());

        String result = developerController.nevinPage(session, model);

        assertEquals("developer", result);
        verify(model).addAttribute("pageTitle", "Nevin F");
        verify(model).addAttribute("name", "Nevin F");
        verify(model).addAttribute("image", "/images/developer/Nevin.jpg");
        verify(model).addAttribute("bio", "Hi im nevin");
    }
}
