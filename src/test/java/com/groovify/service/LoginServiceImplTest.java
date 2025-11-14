package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.LoginRepo;
import com.groovify.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Spring Boot tests for {@link LoginServiceImpl}.
 * Uses H2 in-memory database for persistence; transactional so changes rollback.
 */
@Transactional
@SpringBootTest
public class LoginServiceImplTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginRepo loginRepo;

    private Client testUser;
    private Client existingUser;

    @BeforeEach
    void setup() {
        // User to test login
        testUser = new Client();
        testUser.setName("TestUser");
        testUser.setPasswordSalt("salt123");
        testUser.setPassword(PasswordUtil.hashPassword("password123", "salt123"));
        loginRepo.save(testUser);

        // Pre-existing user
        existingUser = new Client();
        existingUser.setName("ExistingUser");
        existingUser.setPasswordSalt("salt456");
        existingUser.setPassword(PasswordUtil.hashPassword("secret", "salt456"));
        loginRepo.save(existingUser);
    }

    // -------------------------------
    // validateClient Method (20 tests)
    // -------------------------------

    @Test
    void validateClientReturnsTrueForValidUser() {
        boolean result = loginService.validateClient("TestUser", "password123");
        assertTrue(result);
    }

    @Test
    void validateClientReturnsFalseForInvalidPassword() {
        boolean result = loginService.validateClient("TestUser", "wrongPassword");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForNonexistentUser() {
        boolean result = loginService.validateClient("NonExistentUser", "password123");
        assertFalse(result);
    }

    @Test
    void validateClientHandlesCaseInsensitiveUsername() {
        boolean result = loginService.validateClient("testuser", "password123");
        assertTrue(result);
    }

    @Test
    void validateClientReturnsTrueForExistingUser() {
        boolean result = loginService.validateClient("ExistingUser", "secret");
        assertTrue(result);
    }

    @Test
    void validateClientReturnsFalseForWrongPasswordExistingUser() {
        boolean result = loginService.validateClient("ExistingUser", "wrongSecret");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForNullUsername() {
        boolean result = loginService.validateClient(null, "password123");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForEmptyUsername() {
        boolean result = loginService.validateClient("", "password123");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForWhitespaceUsername() {
        boolean result = loginService.validateClient("   ", "password123");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForNullPassword() {
        boolean result = loginService.validateClient("TestUser", null);
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForEmptyPassword() {
        boolean result = loginService.validateClient("TestUser", "");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForWhitespacePassword() {
        boolean result = loginService.validateClient("TestUser", "   ");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForBothNullUsernameAndPassword() {
        boolean result = loginService.validateClient(null, null);
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForBothEmptyUsernameAndPassword() {
        boolean result = loginService.validateClient("", "");
        assertFalse(result);
    }

    @Test
    void validateClientReturnsFalseForBothWhitespaceUsernameAndPassword() {
        boolean result = loginService.validateClient("   ", "   ");
        assertFalse(result);
    }

    @Test
    void validateClientIsCaseInsensitiveWithMultipleUsers() {
        Client duplicate = new Client();
        duplicate.setName("testuser");
        duplicate.setPasswordSalt("saltABC");
        duplicate.setPassword(PasswordUtil.hashPassword("password123", "saltABC"));
        loginRepo.save(duplicate);

        boolean result = loginService.validateClient("TESTUSER", "password123");
        assertTrue(result);
    }
}
