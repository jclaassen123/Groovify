package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.LoginRepository;
import com.groovify.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

    private LoginRepository loginRepo;
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        loginRepo = mock(LoginRepository.class);
        loginService = new LoginServiceImpl(loginRepo);
    }

    // ------------------ validateClient ------------------

    @Test
    void validateClient_ShouldReturnTrue_WhenCredentialsAreValid() {
        String username = "jace";
        String password = "password123";
        Client client = new Client();
        client.setName(username);
        client.setPassword("hashedPassword");
        client.setPasswordSalt("salt");

        when(loginRepo.findByNameIgnoreCase(username)).thenReturn(List.of(client));

        // Mock static PasswordUtil.verifyPassword
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.verifyPassword(password, client.getPasswordSalt(), client.getPassword()))
                    .thenReturn(true);

            boolean result = loginService.validateClient(username, password);
            assertTrue(result);
        }
    }

    @Test
    void validateClient_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        String username = "unknown";
        String password = "password123";

        when(loginRepo.findByNameIgnoreCase(username)).thenReturn(Collections.emptyList());

        boolean result = loginService.validateClient(username, password);
        assertFalse(result);
    }

    @Test
    void validateClient_ShouldReturnFalse_WhenPasswordIsInvalid() {
        String username = "jace";
        String password = "wrongPassword";
        Client client = new Client();
        client.setName(username);
        client.setPassword("hashedPassword");
        client.setPasswordSalt("salt");

        when(loginRepo.findByNameIgnoreCase(username)).thenReturn(List.of(client));

        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.verifyPassword(password, client.getPasswordSalt(), client.getPassword()))
                    .thenReturn(false);

            boolean result = loginService.validateClient(username, password);
            assertFalse(result);
        }
    }

    @Test
    void validateClient_ShouldFail_WhenRepoThrowsException() {
        String username = "jace";
        String password = "password123";

        when(loginRepo.findByNameIgnoreCase(username)).thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(RuntimeException.class, () -> loginService.validateClient(username, password));
        assertEquals("DB error", exception.getMessage());
    }
}
