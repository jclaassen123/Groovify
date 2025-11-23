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
 * Integration tests for {@link LoginService} and {@link RegisterService},
 * verifying login validation behavior with a real H2 database.
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

    /**
     * Sets up two test users before each test, ensuring they are saved
     * with hashed passwords and unique salts.
     */
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

    /**
     * Verifies correct credentials return true for multiple users.
     */
    @Test
    void validateClientWithCorrectCredentialsReturnsTrue() {
        assertTrue(loginService.validateClient("UserOne", password1));
        assertTrue(loginService.validateClient("UserTwo", password2));
    }

    /**
     * Ensures username lookup is case-insensitive.
     */
    @Test
    void validateClientUsernameCaseInsensitiveReturnsTrue() {
        assertTrue(loginService.validateClient("userone", password1));
        assertTrue(loginService.validateClient("usertwo", password2));
    }

    /**
     * Ensures login succeeds when username contains surrounding whitespace.
     */
    @Test
    void validateClientWithLeadingTrailingWhitespaceReturnsTrue() {
        assertTrue(loginService.validateClient("  UserOne  ", password1));
    }

    /**
     * Confirms multiple users can authenticate successfully.
     */
    @Test
    void validateClientMultipleUsersValidCredentialsTest() {
        assertTrue(loginService.validateClient("UserOne", password1));
        assertTrue(loginService.validateClient("UserTwo", password2));
    }

    /**
     * Ensures login works for newly registered users beyond the initial setup.
     */
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

    /**
     * Ensures repeated successful logins do not cause state issues.
     */
    @Test
    void validateClientDoesNotFailAfterMultipleSuccessfulLogins() {
        for (int i = 0; i < 5; i++) {
            assertTrue(loginService.validateClient("UserOne", password1));
        }
    }

    // -------------------------------
    // Crappy Path Tests (Expected Failures)
    // -------------------------------

    /**
     * Verifies incorrect passwords cause login failure.
     */
    @Test
    void validateClientWithWrongPasswordReturnsFalse() {
        assertFalse(loginService.validateClient("UserOne", "WrongPass"));
    }

    /**
     * Ensures login fails for non-existent users.
     */
    @Test
    void validateClientWithNonExistentUserReturnsFalse() {
        assertFalse(loginService.validateClient("NoUser", "AnyPass"));
    }

    /**
     * Ensures blank usernames are rejected.
     */
    @Test
    void validateClientWithBlankUsernameReturnsFalse() {
        assertFalse(loginService.validateClient("   ", password1));
    }

    /**
     * Ensures blank passwords are rejected.
     */
    @Test
    void validateClientWithBlankPasswordReturnsFalse() {
        assertFalse(loginService.validateClient("UserOne", "   "));
    }

    /**
     * Ensures wrong usernames with correct passwords still fail.
     */
    @Test
    void validateClientWithWrongUsernameCorrectPasswordReturnsFalse() {
        assertFalse(loginService.validateClient("UserThree", password1));
    }

    /**
     * Ensures failed login attempts do not block subsequent correct logins.
     */
    @Test
    void validateClientAfterFailedLoginDoesNotAffectNextLogin() {
        assertFalse(loginService.validateClient("UserOne", "Incorrect"));
        assertTrue(loginService.validateClient("UserOne", password1));
    }

    /**
     * Ensures registering a duplicate username fails and does not
     * overwrite the existing valid user for login.
     */
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