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

class HomeControllerTest {

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void homePage_redirectsWhenUserNotLoggedIn() {
        // Arrange
        when(session.getAttribute("username")).thenReturn(null);

        // Act
        String result = homeController.homePage(session, model);

        // Assert
        assertEquals("redirect:", result);
        verifyNoInteractions(clientRepo);
        verifyNoInteractions(model);
    }

    @Test
    void homePage_loadsHomeViewWhenUserLoggedIn() {
        // Arrange
        when(session.getAttribute("username")).thenReturn("nevin");
        Client mockClient = new Client();
        mockClient.setName("nevin");

        when(clientRepo.findByName("nevin")).thenReturn(Optional.of(mockClient));

        // Act
        String result = homeController.homePage(session, model);

        // Assert
        assertEquals("home", result);
        verify(clientRepo).findByName("nevin");
        verify(model).addAttribute("user", mockClient);
        verify(model).addAttribute("pageTitle", "Home");
    }

    @Test
    void homePage_handlesMissingClientGracefully() {
        // Arrange
        when(session.getAttribute("username")).thenReturn("ghostUser");
        when(clientRepo.findByName("ghostUser")).thenReturn(Optional.empty());

        // Act
        String result = homeController.homePage(session, model);

        // Assert
        assertEquals("home", result);
        verify(model).addAttribute("user", null);
        verify(model).addAttribute("pageTitle", "Home");
    }
}
