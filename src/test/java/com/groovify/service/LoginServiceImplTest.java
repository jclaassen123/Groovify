package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.LoginRepo;
import com.groovify.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link LoginServiceImpl} using H2 database and real PasswordUtil.
 */
@Transactional
@SpringBootTest
public class LoginServiceImplTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginRepo loginRepo;

    private Client existingUser;
    private String rawPassword;

    @BeforeEach
    void setup() {
        // Real password setup
        rawPassword = "MySecurePassword123";
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(rawPassword, salt);

        existingUser = new Client();
        existingUser.setName("ExistingUser");
        existingUser.setPasswordSalt(salt);
        existingUser.setPassword(hashedPassword);
        loginRepo.save(existingUser);
    }

    // -------------------------------
    // Happy Path Tests
    // -------------------------------

    @Test
    void validateClientHappyValidUserReturnsTrue() {
        boolean result = loginService.validateClient("ExistingUser", rawPassword);
        assertTrue(result);
    }

    @Test
    void validateClientHappyUsernameCaseInsensitive() {
        boolean result = loginService.validateClient("existinguser", rawPassword);
        assertTrue(result);
    }

    // -------------------------------
    // Crappy Path Tests
    // -------------------------------

    @Test
    void validateClientCrappyInvalidPasswordReturnsFalse() {
        boolean result = loginService.validateClient("ExistingUser", "WrongPassword");
        assertFalse(result);
    }

    @Test
    void validateClientCrappyNonExistentUserReturnsFalse() {
        boolean result = loginService.validateClient("NoUser", "AnyPassword");
        assertFalse(result);
    }

    @Test
    void validateClientCrappyNullUsernameReturnsFalse() {
        boolean result = loginService.validateClient(null, rawPassword);
        assertFalse(result);
    }

    @Test
    void validateClientCrappyBlankUsernameReturnsFalse() {
        boolean result = loginService.validateClient("   ", rawPassword);
        assertFalse(result);
    }

    @Test
    void validateClientCrappyNullPasswordReturnsFalse() {
        boolean result = loginService.validateClient("ExistingUser", null);
        assertFalse(result);
    }

    @Test
    void validateClientCrappyBlankPasswordReturnsFalse() {
        boolean result = loginService.validateClient("ExistingUser", "   ");
        assertFalse(result);
    }
}
