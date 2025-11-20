package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LoginServiceImpl using H2 database and RegisterService for user creation.
 */
@SpringBootTest
@Transactional
class LoginServiceImplTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RegisterService registerService;

    private Client user1;
    private Client user2;
    private String password1;
    private String password2;

    @BeforeEach
    void setUp() {
        password1 = "Password123!";
        password2 = "AnotherPass456#";

        // Create user1
        user1 = new Client();
        user1.setName("UserOne");
        String salt1 = PasswordUtil.generateSalt();
        user1.setPasswordSalt(salt1);
        user1.setPassword(PasswordUtil.hashPassword(password1, salt1));
        assertTrue(registerService.saveUser(user1), "UserOne should be saved successfully");

        // Create user2
        user2 = new Client();
        user2.setName("UserTwo");
        String salt2 = PasswordUtil.generateSalt();
        user2.setPasswordSalt(salt2);
        user2.setPassword(PasswordUtil.hashPassword(password2, salt2));
        assertTrue(registerService.saveUser(user2), "UserTwo should be saved successfully");
    }

    // -------------------------------
    // Happy Path Tests (Good)
    // -------------------------------

    @Test
    void validateClientWithCorrectCredentialsReturnsTrue() {
        assertTrue(loginService.validateClient("UserOne", password1));
        assertTrue(loginService.validateClient("UserTwo", password2));
    }

    @Test
    void validateClientUsernameCaseInsensitiveReturnsTrue() {
        assertTrue(loginService.validateClient("userone", password1));
        assertTrue(loginService.validateClient("usertwo", password2));
    }

    @Test
    void validateClientWithLeadingTrailingWhitespaceReturnsTrue() {
        assertTrue(loginService.validateClient("  UserOne  ", password1));
    }

    @Test
    void validateClientMultipleUsersValidCredentialsTest() {
        assertTrue(loginService.validateClient("UserOne", password1));
        assertTrue(loginService.validateClient("UserTwo", password2));
    }

    @Test
    void validateClientAfterRegisteringAdditionalUserReturnsTrue() {
        Client user3 = new Client();
        user3.setName("UserThree");
        String salt = PasswordUtil.generateSalt();
        user3.setPasswordSalt(salt);
        user3.setPassword(PasswordUtil.hashPassword("Pass123!", salt));
        assertTrue(registerService.saveUser(user3));
        assertTrue(loginService.validateClient("UserThree", "Pass123!"));
    }

    @Test
    void validateClientDoesNotFailAfterMultipleSuccessfulLogins() {
        for (int i = 0; i < 5; i++) {
            assertTrue(loginService.validateClient("UserOne", password1));
        }
    }

    // -------------------------------
    // Crappy Path Tests (Expected Failures)
    // -------------------------------

    @Test
    void validateClientWithWrongPasswordReturnsFalse() {
        assertFalse(loginService.validateClient("UserOne", "WrongPass"));
    }

    @Test
    void validateClientWithNonExistentUserReturnsFalse() {
        assertFalse(loginService.validateClient("NoUser", "AnyPass"));
    }

    @Test
    void validateClientWithBlankUsernameReturnsFalse() {
        assertFalse(loginService.validateClient("   ", password1));
    }

    @Test
    void validateClientWithBlankPasswordReturnsFalse() {
        assertFalse(loginService.validateClient("UserOne", "   "));
    }

    @Test
    void validateClientWithWrongUsernameCorrectPasswordReturnsFalse() {
        assertFalse(loginService.validateClient("UserThree", password1));
    }

    @Test
    void validateClientAfterFailedLoginDoesNotAffectNextLogin() {
        assertFalse(loginService.validateClient("UserOne", "Incorrect"));
        assertTrue(loginService.validateClient("UserOne", password1));
    }

    @Test
    void validateClientAfterRegisteringUserWithDuplicateNameFailsIfPasswordDiffers() {
        Client duplicateUser = new Client();
        duplicateUser.setName("UserOne");
        String salt = PasswordUtil.generateSalt();
        duplicateUser.setPasswordSalt(salt);
        duplicateUser.setPassword(PasswordUtil.hashPassword("NewPass!", salt));
        assertFalse(registerService.registerUser(duplicateUser));
        assertTrue(loginService.validateClient("UserOne", password1));
    }
}
