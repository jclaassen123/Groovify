package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RegisterControllerTest {

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* --------------------------------------------------
     * GET /register
     * -------------------------------------------------- */

    @Test
    void showRegistrationForm_addsEmptyUserToModel() {
        String result = registerController.showRegistrationForm(model);

        assertEquals("register", result);
        verify(model).addAttribute(eq("user"), any(Client.class));
    }

    /* --------------------------------------------------
     * POST /register
     * -------------------------------------------------- */

    @Test
    void registerUser_returnsRegisterViewOnValidationErrors() {
        Client user = new Client();
        user.setName("badUser");

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = registerController.registerUser(user, bindingResult, model);

        assertEquals("register", result);
        verify(model).addAttribute("user", user);
        verifyNoInteractions(clientRepo);
    }

    @Test
    void registerUser_returnsRegisterViewWhenUsernameExists() {
        Client user = new Client();
        user.setName("existingUser");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(clientRepo.findByName("existingUser")).thenReturn(Optional.of(new Client()));

        String result = registerController.registerUser(user, bindingResult, model);

        assertEquals("register", result);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("error", "Username already exists.");
        verify(clientRepo, never()).save(any());
    }

    @Test
    void registerUser_savesNewUserAndRedirects() {
        Client user = new Client();
        user.setName("newUser");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(clientRepo.findByName("newUser")).thenReturn(Optional.empty());

        String result = registerController.registerUser(user, bindingResult, model);

        assertEquals("redirect:/", result);
        verify(clientRepo).save(user);
    }
}
