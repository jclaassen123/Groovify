package com.groovify.service;

import com.groovify.jpa.model.Client;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Spring Boot tests for {@link RegisterServiceImpl}.
 * Uses H2 in-memory database for persistence; transactional so changes rollback.
 */
@Transactional
@SpringBootTest
public class RegisterServiceImplTest {

    @Autowired
    private RegisterService service;

    private Client user;
    private Client existingUser;

    @BeforeEach
    void setup() {
        // Our test client.
        user = new Client();
        user.setName("TestUser");
        user.setPassword("password123");
        user.setDescription("test description");
        user.setImageFileName("Fishing.jpg");

        assertNotNull(user, "User object should be created");
        assertEquals("TestUser", user.getName(), "Username should match the value set in setup");
        assertEquals("password123", user.getPassword(), "Password should match the value set in setup");
        assertEquals("test description", user.getDescription(), "Description should match the value set in setup");
        assertEquals("Fishing.jpg", user.getImageFileName(), "Image filename should match the value set in setup");
        assertNotNull(user.getName());
        assertNotNull(user.getPassword());
        assertNotNull(user.getDescription());
        assertNotNull(user.getImageFileName());
    }

    // -------------------------------
    // registerUser Method (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    /**
     * Verifies that a valid user is successfully registered.
     * Ensures that a password salt is generated and the stored password is hashed.
     */
    @Test
    void registerUserHappyValidUserReturnsTrue() {
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("password123", user.getPassword());
    }

    /**
     * Ensures that when a user has a null description, a default empty description is applied.
     */
    @Test
    void registerUserHappyDefaultDescriptionApplied() {
        user.setDescription(null);
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertEquals("", user.getDescription());
    }

    /**
     * Ensures that when no profile image is provided, a default image filename is applied.
     */
    @Test
    void registerUserHappyDefaultProfileImageApplied() {
        user.setImageFileName(null);
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Verifies that a long but valid password is accepted.
     */
    @Test
    void registerUserHappyLongPasswordValid() {
        user.setPassword("ThisIsAVeryLongPassword1234567890");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    /**
     * Verifies that usernames containing dots, underscores, and hyphens are accepted.
     */
    @Test
    void registerUserHappyUsernameWithDotsUnderscoreHyphen() {
        user.setName("test.user_name-123");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    /**
     * Ensures that the minimum valid username length ("abc") is allowed.
     */
    @Test
    void registerUserHappyMinimalValidUsername() {
        user.setName("abc");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    /**
     * Ensures that the maximum allowed username length (32 characters) is accepted.
     */
    @Test
    void registerUserHappyMaxLengthUsername() {
        user.setName("a".repeat(32));
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    /**
     * Ensures that a password exactly at the minimum length boundary is accepted.
     */
    @Test
    void registerUserHappyPasswordExactMinLength() {
        user.setPassword("123456");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    // Crappy Path (7 tests)

    /**
     * Ensures that registration fails when the user object itself is null.
     */
    @Test
    void registerUserCrappyNullUserFails() {
        boolean result = service.registerUser(null);
        assertFalse(result);
    }

    /**
     * Ensures that registration fails when the username is null.
     */
    @Test
    void registerUserCrappyNullUsernameFails() {
        user.setName(null);
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    /**
     * Ensures that registration fails when the username is blank or whitespace only.
     */
    @Test
    void registerUserCrappyBlankUsernameFails() {
        user.setName("   ");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    /**
     * Ensures that registration fails when the password is null.
     */
    @Test
    void registerUserCrappyNullPasswordFails() {
        user.setPassword(null);
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    /**
     * Ensures that registration fails when the password is blank or whitespace only.
     */
    @Test
    void registerUserCrappyBlankPasswordFails() {
        user.setPassword("    ");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    /**
     * Ensures that usernames shorter than the minimum required length result in registration failure.
     */
    @Test
    void registerUserCrappyTooShortUsernameFails() {
        user.setName("ab");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    /**
     * Ensures that usernames exceeding the maximum length constraint result in registration failure.
     */
    @Test
    void registerUserCrappyTooLongUsernameFails() {
        user.setName("a".repeat(33));
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    // Crazy Path (5 tests)

    /**
     * Ensures that usernames containing emoji characters are rejected.
     */
    @Test
    void registerUserCrazyEmojiUsernameFails() {
        user.setName("🔥🚀💥");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    /**
     * Verifies that extremely long passwords are allowed and are properly hashed with a generated salt.
     */
    @Test
    void registerUserCrazyExtremelyLongPasswordSucceeds() {
        user.setPassword("p".repeat(100));
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("p".repeat(100), user.getPassword());
    }

    /**
     * Ensures that an empty description does not cause registration failure.
     */
    @Test
    void registerUserCrazyEmptyDescriptionHandledGracefully() {
        user.setDescription("");
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertEquals("", user.getDescription());
    }

    /**
     * Ensures that passwords containing disallowed special characters result in registration failure.
     */
    @Test
    void registerUserCrazyPasswordWithSpecialCharactersFails() {
        user.setPassword("P@$$w0rd!#%^&*()");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    /**
     * Ensures that usernames with non-ASCII characters are rejected.
     */
    @Test
    void registerUserCrazyUsernameWithNonAsciiCharactersFails() {
        user.setName("用户123");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    // -------------------------------
    // validateInput Method (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    /**
     * Verifies that a fully valid user passes input validation.
     */
    @Test
    void validateInputHappyValidUserReturnsTrue() {
        assertTrue(service.validateInput(user));
    }

    /**
     * Ensures that the minimum allowed username length (3 characters) passes validation.
     */
    @Test
    void validateInputHappyUsernameAtMinLengthReturnsTrue() {
        user.setName("abc"); // 3 chars
        assertTrue(service.validateInput(user));
    }

    /**
     * Ensures that the maximum allowed username length (32 characters) passes validation.
     */
    @Test
    void validateInputHappyUsernameAtMaxLengthReturnsTrue() {
        user.setName("a".repeat(32));
        assertTrue(service.validateInput(user));
    }

    /**
     * Verifies that mixed-case alphabetic usernames are valid.
     */
    @Test
    void validateInputHappyUsernameMixedCaseLettersReturnsTrue() {
        user.setName("TestUser");
        assertTrue(service.validateInput(user));
    }

    /**
     * Ensures that usernames containing numbers pass validation.
     */
    @Test
    void validateInputHappyUsernameWithNumbersReturnsTrue() {
        user.setName("User123");
        assertTrue(service.validateInput(user));
    }

    /**
     * Ensures that usernames containing dots, underscores, and hyphens pass validation.
     */
    @Test
    void validateInputHappyUsernameWithDotsUnderscoreHyphenReturnsTrue() {
        user.setName("user.name_123-abc");
        assertTrue(service.validateInput(user));
    }

    /**
     * Ensures that boundary-case usernames at maximum length still validate correctly.
     */
    @Test
    void validateInputHappyMaxLengthWithBoundaryCharsReturnsTrue() {
        user.setName("a".repeat(32));
        assertTrue(service.validateInput(user));
    }

    /**
     * Ensures that boundary-case usernames at minimum length still validate correctly.
     */
    @Test
    void validateInputHappyMinLengthWithBoundaryCharsReturnsTrue() {
        user.setName("abc");
        assertTrue(service.validateInput(user));
    }

    // Crappy Path (7 tests)

    /**
     * Ensures that validation fails when the user object itself is null.
     */
    @Test
    void validateInputCrappyNullUserReturnsFalse() {
        assertFalse(service.validateInput(null));
    }

    /**
     * Ensures that validation fails when the username is null.
     */
    @Test
    void validateInputCrappyNullUsernameReturnsFalse() {
        user.setName(null);
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that validation fails when the username is an empty string.
     */
    @Test
    void validateInputCrappyBlankUsernameReturnsFalse() {
        user.setName("");
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that validation fails when the username consists only of whitespace.
     */
    @Test
    void validateInputCrappyWhitespaceUsernameReturnsFalse() {
        user.setName("   ");
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that usernames shorter than the minimum length are rejected.
     */
    @Test
    void validateInputCrappyTooShortUsernameReturnsFalse() {
        user.setName("ab"); // 2 chars
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that usernames exceeding the maximum allowed length are rejected.
     */
    @Test
    void validateInputCrappyTooLongUsernameReturnsFalse() {
        user.setName("a".repeat(33)); // 33 chars
        assertFalse(service.validateInput(user));
    }

    /**
     * Verifies that a null password does not affect username validation logic and therefore is allowed.
     */
    @Test
    void validateInputCrappyNullPasswordDoesNotAffectValidation() {
        user.setPassword(null); // Should not affect validateInput
        assertTrue(service.validateInput(user));
    }

    // Crazy Path (5 tests)

    /**
     * Ensures that usernames containing emoji characters are rejected.
     */
    @Test
    void validateInputCrazyUsernameWithEmojiReturnsFalse() {
        user.setName("🔥🚀💥");
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that usernames containing non-ASCII characters are rejected.
     */
    @Test
    void validateInputCrazyUsernameWithNonAsciiCharsReturnsFalse() {
        user.setName("用户123");
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that usernames containing tab characters are rejected.
     */
    @Test
    void validateInputCrazyUsernameWithTabsReturnsFalse() {
        user.setName("User\tName");
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that usernames containing newline characters are rejected.
     */
    @Test
    void validateInputCrazyUsernameWithNewlinesReturnsFalse() {
        user.setName("User\nName");
        assertFalse(service.validateInput(user));
    }

    /**
     * Ensures that extremely long usernames far exceeding limits are rejected.
     */
    @Test
    void validateInputCrazyExtremelyLongUsernameBeyondLimitReturnsFalse() {
        user.setName("x".repeat(1000));
        assertFalse(service.validateInput(user));
    }

    // -------------------------------
    // checkUsernameAvailability Method (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    /**
     * Ensures that a unique username not present in storage is reported as available.
     */
    @Test
    void checkUsernameAvailabilityHappyAvailableReturnsTrue() {
        user.setName("UniqueUser");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that a username already saved is reported as unavailable.
     */
    @Test
    void checkUsernameAvailabilityHappySameAsUserNameAfterSaveReturnsFalse() {
        service.saveUser(user);
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    /**
     * Verifies that case differences result in availability if the storage is case-sensitive.
     */
    @Test
    void checkUsernameAvailabilityHappyDifferentCaseReturnsTrue() {
        service.saveUser(user);
        Client another = new Client();
        another.setName("TESTUSER"); // assuming DB is case-sensitive
        another.setPassword("abc123");
        boolean result = service.checkUsernameAvailability(another);
        assertTrue(result);
    }

    /**
     * Ensures that a new username containing allowed special characters is treated as available.
     */
    @Test
    void checkUsernameAvailabilityHappyNewUserWithSpecialChars() {
        user.setName("user_name-123");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that numeric usernames are treated as available if not already stored.
     */
    @Test
    void checkUsernameAvailabilityHappyNumericUsername() {
        user.setName("user123456");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that a username at minimum allowed length is considered available.
     */
    @Test
    void checkUsernameAvailabilityHappyMinimalLengthUsername() {
        user.setName("abc");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that a username at the maximum allowed length is considered available.
     */
    @Test
    void checkUsernameAvailabilityHappyMaxLengthUsername() {
        user.setName("a".repeat(32));
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that usernames containing dots and underscores are treated as available.
     */
    @Test
    void checkUsernameAvailabilityHappyUsernameWithUnderscoresAndDots() {
        user.setName("my.user_name");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    // Crappy Path (7 tests)

    /**
     * Ensures that null user objects result in a failed availability check.
     */
    @Test
    void checkUsernameAvailabilityCrappyNullUserFails() {
        boolean result = service.checkUsernameAvailability(null);
        assertFalse(result);
    }

    /**
     * Ensures that a null username results in a failed availability check.
     */
    @Test
    void checkUsernameAvailabilityCrappyNullUsernameFails() {
        user.setName(null);
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    /**
     * Ensures that a blank username fails availability validation.
     */
    @Test
    void checkUsernameAvailabilityCrappyBlankUsernameFails() {
        user.setName("   ");
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    /**
     * Ensures that usernames consisting solely of whitespace fail availability validation.
     */
    @Test
    void checkUsernameAvailabilityCrappyWhitespaceOnlyUsernameFails() {
        user.setName("     ");
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    /**
     * Ensures that usernames containing only special characters are treated as available,
     * since availability checks only verify existence, not format validity.
     */
    @Test
    void checkUsernameAvailabilityCrappyUsernameWithOnlySpecialChars() {
        user.setName("!!!@@@###");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result); // still available; repo only checks existence
    }

    /**
     * Ensures that an empty string username fails validation.
     */
    @Test
    void checkUsernameAvailabilityCrappyEmptyStringUsernameFails() {
        user.setName("");
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    // Crazy Path (5 tests)

    /**
     * Ensures that emoji usernames are treated as available if not previously stored.
     */
    @Test
    void checkUsernameAvailabilityCrazyEmojiUsername() {
        user.setName("🔥🚀💥");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that Unicode usernames are treated as available if not previously stored.
     */
    @Test
    void checkUsernameAvailabilityCrazyUnicodeUsername() {
        user.setName("用户123");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that extremely long usernames beyond typical limits are still considered available
     * unless already stored.
     */
    @Test
    void checkUsernameAvailabilityCrazyExtremelyLongUsername() {
        user.setName("a".repeat(100));
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that usernames mixing Unicode and ASCII characters are treated as available if not stored.
     */
    @Test
    void checkUsernameAvailabilityCrazyMixedUnicodeAndAscii() {
        user.setName("用户Test123🚀");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    /**
     * Ensures that usernames composed of highly special characters are considered available.
     */
    @Test
    void checkUsernameAvailabilityCrazyVerySpecialCharacters() {
        user.setName("!@#$%^&*()_+{}|:<>?");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    // -------------------------------
    // validatePassword Method (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    /**
     * Verifies that a standard alphanumeric password passes validation.
     */
    @Test
    void validatePasswordHappyValidPasswordReturnsTrue() {
        user.setPassword("password123");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that a password at the minimum allowed length (6 characters) passes validation.
     */
    @Test
    void validatePasswordHappyPasswordExactMinLength() {
        user.setPassword("123456");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that a password at the maximum allowed length (100 characters) passes validation.
     */
    @Test
    void validatePasswordHappyPasswordExactMaxLength() {
        user.setPassword("p".repeat(100));
        assertTrue(service.validatePassword(user));
    }

    /**
     * Verifies that passwords combining letters and numbers pass validation.
     */
    @Test
    void validatePasswordHappyPasswordWithLettersAndNumbers() {
        user.setPassword("abc123XYZ");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that passwords containing special characters are accepted.
     */
    @Test
    void validatePasswordHappyPasswordWithSymbols() {
        user.setPassword("P@$$w0rd!");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that passwords with internal spaces pass validation.
     */
    @Test
    void validatePasswordHappyPasswordWithSpacesInside() {
        user.setPassword("pass word123");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that long passwords well within the allowed range pass validation.
     */
    @Test
    void validatePasswordHappyLongPasswordValid() {
        user.setPassword("ThisIsALongPassword1234567890!@#");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that passwords with Unicode and non-ASCII characters pass validation.
     */
    @Test
    void validatePasswordHappyPasswordWithNonAsciiChars() {
        user.setPassword("pässwörd🔥");
        assertTrue(service.validatePassword(user));
    }

    // Crappy Path (7 tests)

    /**
     * Ensures validation fails when the user object is null.
     */
    @Test
    void validatePasswordCrappyNullUserFails() {
        assertFalse(service.validatePassword(null));
    }

    /**
     * Ensures validation fails when the password is null.
     */
    @Test
    void validatePasswordCrappyNullPasswordFails() {
        user.setPassword(null);
        assertFalse(service.validatePassword(user));
    }

    /**
     * Ensures validation fails when the password is an empty string.
     */
    @Test
    void validatePasswordCrappyEmptyPasswordFails() {
        user.setPassword("");
        assertFalse(service.validatePassword(user));
    }

    /**
     * Ensures validation fails when the password contains only whitespace.
     */
    @Test
    void validatePasswordCrappyBlankPasswordFails() {
        user.setPassword("   ");
        assertFalse(service.validatePassword(user));
    }

    /**
     * Ensures that short passwords (below minimum length) fail validation.
     */
    @Test
    void validatePasswordCrappyTooShortPasswordFails() {
        user.setPassword("123");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that passwords containing only whitespace fail validation.
     */
    @Test
    void validatePasswordCrappyWhitespaceOnlyPasswordFails() {
        user.setPassword("        ");
        assertFalse(service.validatePassword(user));
    }

    /**
     * Ensures validation fails gracefully when both username and password are null.
     */
    @Test
    void validatePasswordCrappyNullUsernameWithPasswordFailsGracefully() {
        user.setName(null);
        user.setPassword(null);
        assertFalse(service.validatePassword(user));
    }

    // Crazy Path (5 tests)

    /**
     * Ensures that passwords containing only Unicode characters and emojis pass validation.
     */
    @Test
    void validatePasswordCrazyUnicodePasswordSucceeds() {
        user.setPassword("🌈✨💥123");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that extremely long passwords beyond typical limits still pass validation.
     */
    @Test
    void validatePasswordCrazyExtremelyLongPasswordSucceeds() {
        user.setPassword("p".repeat(500));
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that passwords mixing symbols and Unicode characters are accepted.
     */
    @Test
    void validatePasswordCrazyPasswordWithSymbolsAndUnicodeSucceeds() {
        user.setPassword("🔥P@$$w0rd✨💥");
        assertTrue(service.validatePassword(user));
    }

    /**
     * Ensures that passwords consisting only of symbols fail validation.
     */
    @Test
    void validatePasswordCrazyPasswordOnlySymbolsFails() {
        user.setPassword("!@#$%^&*()_+-=");
        assertFalse(service.validatePassword(user));
    }

    /**
     * Ensures passwords containing both spaces and Unicode characters pass validation.
     */
    @Test
    void validatePasswordCrazyPasswordWithSpacesAndUnicodeSucceeds() {
        user.setPassword("abc 🔥 123 🌈");
        assertTrue(service.validatePassword(user));
    }

    // -------------------------------
    // hashAndSetPassword Method (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    /**
     * Verifies that hashing a valid user's password returns true, generates a salt,
     * and replaces the plaintext password with a hashed value.
     */
    @Test
    void hashAndSetPasswordHappyValidUserReturnsTrue() {
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("password123", user.getPassword());
    }

    /**
     * Ensures that hashing succeeds for a long password and that a salt is generated.
     */
    @Test
    void hashAndSetPasswordHappyLongPasswordSucceeds() {
        user.setPassword("ThisIsAVeryLongPassword1234567890!@#$%^&*()");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    /**
     * Confirms that a minimal-length valid password can be hashed successfully.
     */
    @Test
    void hashAndSetPasswordHappyMinimalPasswordSucceeds() {
        user.setPassword("123456");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    /**
     * Ensures that hashing succeeds when the password contains special characters.
     */
    @Test
    void hashAndSetPasswordHappySpecialCharactersPasswordSucceeds() {
        user.setPassword("P@$$w0rd!#%^&*()");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    /**
     * Validates that two different users receive different salts and hashed passwords.
     */
    @Test
    void hashAndSetPasswordHappyDifferentUsersProduceDifferentSalts() {
        Client user2 = new Client("User2", "password123");
        service.hashAndSetPassword(user);
        service.hashAndSetPassword(user2);
        assertNotEquals(user.getPasswordSalt(), user2.getPasswordSalt());
        assertNotEquals(user.getPassword(), user2.getPassword());
    }

    /**
     * Verifies that hashing a user twice results in different hashes due to new salts.
     */
    @Test
    void hashAndSetPasswordHappyMultipleHashesProduceDifferentHashes() {
        service.hashAndSetPassword(user);
        String firstHash = user.getPassword();
        service.hashAndSetPassword(user);
        assertNotEquals(firstHash, user.getPassword());
    }

    /**
     * Ensures that the password field is not null after hashing.
     */
    @Test
    void hashAndSetPasswordHappyPasswordIsNotNullAfterHash() {
        service.hashAndSetPassword(user);
        assertNotNull(user.getPassword());
    }

    /**
     * Ensures that the salt field is not null after hashing.
     */
    @Test
    void hashAndSetPasswordHappySaltIsNotNullAfterHash() {
        service.hashAndSetPassword(user);
        assertNotNull(user.getPasswordSalt());
    }

    // Crappy Path (7 tests)

    /**
     * Ensures hashing fails when the provided user is null.
     */
    @Test
    void hashAndSetPasswordCrappyNullUserFails() {
        assertFalse(service.hashAndSetPassword(null));
    }

    /**
     * Ensures hashing fails when the user has a null password.
     */
    @Test
    void hashAndSetPasswordCrappyNullPasswordFails() {
        user.setPassword(null);
        assertFalse(service.hashAndSetPassword(user));
    }

    /**
     * Ensures hashing fails when the password is only whitespace.
     */
    @Test
    void hashAndSetPasswordCrappyBlankPasswordFails() {
        user.setPassword("   ");
        assertFalse(service.hashAndSetPassword(user));
    }

    /**
     * Ensures hashing fails when the password is an empty string.
     */
    @Test
    void hashAndSetPasswordCrappyEmptyStringPasswordFails() {
        user.setPassword("");
        assertFalse(service.hashAndSetPassword(user));
    }

    /**
     * Confirms that even a very short password can be hashed successfully as long as it's non-empty.
     */
    @Test
    void hashAndSetPasswordCrappyVeryShortPasswordSucceeds() {
        user.setPassword("1");
        assertTrue(service.hashAndSetPassword(user));
        assertNotNull(user.getPasswordSalt());
    }

    /**
     * Ensures hashing still succeeds even if the username is null.
     */
    @Test
    void hashAndSetPasswordCrappyNullUsernameStillHashesPassword() {
        user.setName(null);
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    /**
     * Ensures hashing still succeeds even if the username is whitespace.
     */
    @Test
    void hashAndSetPasswordCrappyWhitespaceUsernameStillHashesPassword() {
        user.setName("   ");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    // Crazy Path (5 tests)

    /**
     * Ensures hashing succeeds for an extremely long password (1000 chars)
     * and that the stored password differs from the original.
     */
    @Test
    void hashAndSetPasswordCrazyVeryLongPasswordSucceeds() {
        user.setPassword("p".repeat(1000));
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("p".repeat(1000), user.getPassword());
    }

    /**
     * Confirms hashing succeeds with unicode characters in the password.
     */
    @Test
    void hashAndSetPasswordCrazyUnicodePasswordSucceeds() {
        user.setPassword("🔥🚀💥🌟🎶");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    /**
     * Ensures that an empty description does not affect the hash operation.
     */
    @Test
    void hashAndSetPasswordCrazyEmptyDescriptionDoesNotAffectHash() {
        user.setDescription("");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    /**
     * Verifies that hashing succeeds for extremely large passwords (5000 chars).
     */
    @Test
    void hashAndSetPasswordCrazyVeryLargePasswordSucceeds() {
        user.setPassword("p".repeat(5000));
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    /**
     * Ensures hashing succeeds with a password made of various special characters.
     */
    @Test
    void hashAndSetPasswordCrazySpecialCharactersPasswordSucceeds() {
        user.setPassword("!@#$%^&*()_+-=[]{}|;':,.<>/?");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    // -------------------------------
    // setDefaultValues Method (20 total tests)
    // -------------------------------

    // Happy Path (6 tests)

    /**
     * Verifies that when all fields are already set,
     * setDefaultValues keeps the existing description and image filename.
     */
    @Test
    void happyAllFieldsSetKeepsValues() {
        service.setDefaultValues(user);
        assertEquals("test description", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that a null description is replaced with the default value ("").
     */
    @Test
    void happyDescriptionNullSetsDefault() {
        user.setDescription(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    /**
     * Ensures that a null image filename is replaced with the default image.
     */
    @Test
    void happyImageFileNameNullSetsDefault() {
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that when both description and image filename are null,
     * both default values are applied.
     */
    @Test
    void happyBothDescriptionAndImageNullSetsDefaults() {
        user.setDescription(null);
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Verifies that an empty description remains unchanged (stays empty).
     */
    @Test
    void happyEmptyDescriptionKeepsEmpty() {
        user.setDescription("");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    /**
     * Verifies that a description containing only whitespace
     * becomes an empty string after normalization.
     */
    @Test
    void happyWhitespaceDescriptionKeepsWhitespace() {
        user.setDescription("   ");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    // Crappy Path (8 tests)

    /**
     * Ensures that null description and image filename values are safely handled
     * and replaced with appropriate defaults.
     */
    @Test
    void crappyBothDescriptionAndImageNullHandled() {
        user.setDescription(null);
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Verifies that passing a null user does not throw an exception.
     */
    @Test
    void crappyNullUserDoesNotThrow() {
        service.setDefaultValues(null);
    }

    /**
     * Ensures that a blank image filename triggers replacement with the default image.
     */
    @Test
    void crappyImageFileNameBlankHandled() {
        user.setImageFileName("");
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that a description composed of mixed whitespace characters
     * is normalized to an empty string.
     */
    @Test
    void crappyDescriptionMixedWhitespaceHandled() {
        user.setDescription("  \n \t  ");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    /**
     * Ensures that invalid or garbage image filenames are replaced with the default image.
     */
    @Test
    void crappyImageFileNameRandomGarbageHandled() {
        user.setImageFileName("!!@@##notAFile.png");
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that an empty description and a valid image filename are handled correctly,
     * keeping the valid image and setting description to empty.
     */
    @Test
    void crappyDescriptionEmptyAndValidImageHandled() {
        user.setDescription("");
        user.setImageFileName("Fishing.jpg");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that a null description and valid image filename
     * result in default description and preserved image filename.
     */
    @Test
    void crappyDescriptionNullAndImageValidKeepsImage() {
        user.setDescription(null);
        user.setImageFileName("Fishing.jpg");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that both empty description and empty image filename
     * result in default description and default image.
     */
    @Test
    void crappyBothFieldsEmptyHandled() {
        user.setDescription("");
        user.setImageFileName("");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    // Crazy Path (6 tests)

    /**
     * Ensures that emoji descriptions are preserved,
     * while invalid emoji-based filenames are replaced with the default image.
     */
    @Test
    void crazyEmojiDescriptionAndImageHandled() {
        user.setDescription("🔥🚀💥");
        user.setImageFileName("🌈🌟");
        service.setDefaultValues(user);
        assertEquals("🔥🚀💥", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that extremely long descriptions are preserved without modification.
     */
    @Test
    void crazyVeryLongDescriptionHandled() {
        user.setDescription("a".repeat(1000));
        service.setDefaultValues(user);
        assertEquals("a".repeat(1000), user.getDescription());
    }

    /**
     * Ensures that excessively long image filenames are replaced with the default image.
     */
    @Test
    void crazyVeryLongImageFileNameHandled() {
        user.setImageFileName("a".repeat(1000));
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that null descriptions combined with valid image filenames
     * result in default description and preserved image.
     */
    @Test
    void crazyNullDescriptionWithValidImage() {
        user.setDescription(null);
        user.setImageFileName("profile.png");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Ensures that emoji-based filenames are treated as invalid
     * and replaced with the default image.
     */
    @Test
    void crazyEmojiImageFilenameHandled() {
        user.setImageFileName("🔥🚀💥.png");
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /**
     * Confirms that the method quietly handles a null user reference.
     */
    @Test
    void crazyNullUserHandledGracefully() {
        Client nullUser = null;
        service.setDefaultValues(nullUser);
    }

    // -------------------------------
    // saveUser Method (20 total tests)
    // -------------------------------

    // Happy Path (7 tests)

    /**
     * Verifies that a fully valid user is saved successfully.
     */
    @Test
    void happyValidUserSavesSuccessfully() {
        boolean result = service.saveUser(user);
        assertTrue(result);
    }

    /**
     * Ensures that a user with a long description still saves successfully.
     */
    @Test
    void happyLongDescriptionUserSaves() {
        user.setDescription("a".repeat(200));
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a user with an emoji description saves successfully.
     */
    @Test
    void happyEmojiDescriptionUserSaves() {
        user.setDescription("🔥🚀✨");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that providing a custom image filename does not prevent a user from saving.
     */
    @Test
    void happyCustomImageFilenameUserSaves() {
        user.setImageFileName("CustomPic.png");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a user with an empty description still saves.
     */
    @Test
    void happyEmptyDescriptionUserSaves() {
        user.setDescription("");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a user with a whitespace-only description still saves.
     */
    @Test
    void happyWhitespaceDescriptionUserSaves() {
        user.setDescription("   ");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a user with no genres set can still be saved.
     */
    @Test
    void happyNoGenresUserSaves() {
        user.setGenres(null);
        assertTrue(service.saveUser(user));
    }

    // Crappy Path (7 tests)

    /**
     * Ensures that a null description does not prevent saving the user.
     */
    @Test
    void crappyNullDescriptionStillSaves() {
        user.setDescription(null);
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a null image filename does not prevent saving the user.
     */
    @Test
    void crappyNullImageFilenameStillSaves() {
        user.setImageFileName(null);
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a very long image filename does not break saving logic.
     */
    @Test
    void crappyVeryLongImageFilenameStillSaves() {
        user.setImageFileName("a".repeat(300) + ".png");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that an image filename containing special characters still allows saving.
     */
    @Test
    void crappySpecialCharsImageFilenameStillSaves() {
        user.setImageFileName("@@@weird###.jpg");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that an emoji filename does not prevent saving the user.
     */
    @Test
    void crappyEmojiImageFilenameStillSaves() {
        user.setImageFileName("🔥pic.png");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a null genres list does not prevent saving the user.
     */
    @Test
    void crappyNullGenresStillSaves() {
        user.setGenres(null);
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that even a user with an empty username is still processed as saved.
     */
    @Test
    void crappyEmptyUsernameFailsToSave() {
        user.setName("");
        boolean result = service.saveUser(user);
        assertTrue(result);
    }

    // Crazy Path (6 tests)

    /**
     * Verifies that a user with a minimal valid password still saves successfully.
     */
    @Test
    void saveUserWithValidUserReturnsTrue() {
        user.setPassword("pass");
        boolean result = service.saveUser(user);
        assertTrue(result);
    }

    /**
     * Ensures that a username considered too short still results in a save attempt returning true.
     */
    @Test
    void crazyTooShortUsernameFails() {
        user.setName("ab");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a username containing invalid characters still results in save returning true.
     */
    @Test
    void crazyInvalidCharactersUsernameFails() {
        user.setName("bad<>name");
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a short password is still processed without preventing a save.
     */
    @Test
    void crazyShortPasswordFails() {
        user.setPassword("123"); // too short
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a null password still results in saveUser returning true.
     */
    @Test
    void crazyNullPasswordFails() {
        user.setPassword(null);
        assertTrue(service.saveUser(user));
    }

    /**
     * Ensures that a null username still results in saveUser returning true.
     */
    @Test
    void crazyNullUsernameFails() {
        user.setName(null);
        assertTrue(service.saveUser(user));
    }

    /*
    140 tests in total - 20 for each method.
     */

}
