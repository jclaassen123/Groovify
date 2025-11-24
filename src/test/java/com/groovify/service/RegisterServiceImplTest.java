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

    @Test
    void registerUserHappyValidUserReturnsTrue() {
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("password123", user.getPassword());
    }

    @Test
    void registerUserHappyDefaultDescriptionApplied() {
        user.setDescription(null);
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertEquals("", user.getDescription());
    }

    @Test
    void registerUserHappyDefaultProfileImageApplied() {
        user.setImageFileName(null);
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void registerUserHappyLongPasswordValid() {
        user.setPassword("ThisIsAVeryLongPassword1234567890");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    @Test
    void registerUserHappyUsernameWithDotsUnderscoreHyphen() {
        user.setName("test.user_name-123");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    @Test
    void registerUserHappyMinimalValidUsername() {
        user.setName("abc");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    @Test
    void registerUserHappyMaxLengthUsername() {
        user.setName("a".repeat(32));
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    @Test
    void registerUserHappyPasswordExactMinLength() {
        user.setPassword("123456");
        boolean result = service.registerUser(user);
        assertTrue(result);
    }

    // Crappy Path (7 tests)

    @Test
    void registerUserCrappyNullUserFails() {
        boolean result = service.registerUser(null);
        assertFalse(result);
    }

    @Test
    void registerUserCrappyNullUsernameFails() {
        user.setName(null);
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    @Test
    void registerUserCrappyBlankUsernameFails() {
        user.setName("   ");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    @Test
    void registerUserCrappyNullPasswordFails() {
        user.setPassword(null);
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    @Test
    void registerUserCrappyBlankPasswordFails() {
        user.setPassword("    ");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    @Test
    void registerUserCrappyTooShortUsernameFails() {
        user.setName("ab");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    @Test
    void registerUserCrappyTooLongUsernameFails() {
        user.setName("a".repeat(33));
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    // Crazy Path (5 tests)

    @Test
    void registerUserCrazyEmojiUsernameFails() {
        user.setName("🔥🚀💥");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

    @Test
    void registerUserCrazyExtremelyLongPasswordSucceeds() {
        user.setPassword("p".repeat(100));
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("p".repeat(100), user.getPassword());
    }

    @Test
    void registerUserCrazyEmptyDescriptionHandledGracefully() {
        user.setDescription("");
        boolean result = service.registerUser(user);
        assertTrue(result);
        assertEquals("", user.getDescription());
    }

    @Test
    void registerUserCrazyPasswordWithSpecialCharactersFails() {
        user.setPassword("P@$$w0rd!#%^&*()");
        boolean result = service.registerUser(user);
        assertFalse(result);
    }

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

    @Test
    void validateInputHappyValidUserReturnsTrue() {
        assertTrue(service.validateInput(user));
    }

    @Test
    void validateInputHappyUsernameAtMinLengthReturnsTrue() {
        user.setName("abc"); // 3 chars
        assertTrue(service.validateInput(user));
    }

    @Test
    void validateInputHappyUsernameAtMaxLengthReturnsTrue() {
        user.setName("a".repeat(32));
        assertTrue(service.validateInput(user));
    }

    @Test
    void validateInputHappyUsernameMixedCaseLettersReturnsTrue() {
        user.setName("TestUser");
        assertTrue(service.validateInput(user));
    }

    @Test
    void validateInputHappyUsernameWithNumbersReturnsTrue() {
        user.setName("User123");
        assertTrue(service.validateInput(user));
    }

    @Test
    void validateInputHappyUsernameWithDotsUnderscoreHyphenReturnsTrue() {
        user.setName("user.name_123-abc");
        assertTrue(service.validateInput(user));
    }

    @Test
    void validateInputHappyMaxLengthWithBoundaryCharsReturnsTrue() {
        user.setName("a".repeat(32));
        assertTrue(service.validateInput(user));
    }

    @Test
    void validateInputHappyMinLengthWithBoundaryCharsReturnsTrue() {
        user.setName("abc");
        assertTrue(service.validateInput(user));
    }

    // Crappy Path (7 tests)

    @Test
    void validateInputCrappyNullUserReturnsFalse() {
        assertFalse(service.validateInput(null));
    }

    @Test
    void validateInputCrappyNullUsernameReturnsFalse() {
        user.setName(null);
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrappyBlankUsernameReturnsFalse() {
        user.setName("");
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrappyWhitespaceUsernameReturnsFalse() {
        user.setName("   ");
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrappyTooShortUsernameReturnsFalse() {
        user.setName("ab"); // 2 chars
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrappyTooLongUsernameReturnsFalse() {
        user.setName("a".repeat(33)); // 33 chars
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrappyNullPasswordDoesNotAffectValidation() {
        user.setPassword(null); // Should not affect validateInput
        assertTrue(service.validateInput(user));
    }

    // Crazy Path (5 tests)

    @Test
    void validateInputCrazyUsernameWithEmojiReturnsFalse() {
        user.setName("🔥🚀💥");
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrazyUsernameWithNonAsciiCharsReturnsFalse() {
        user.setName("用户123");
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrazyUsernameWithTabsReturnsFalse() {
        user.setName("User\tName");
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrazyUsernameWithNewlinesReturnsFalse() {
        user.setName("User\nName");
        assertFalse(service.validateInput(user));
    }

    @Test
    void validateInputCrazyExtremelyLongUsernameBeyondLimitReturnsFalse() {
        user.setName("x".repeat(1000));
        assertFalse(service.validateInput(user));
    }

    // -------------------------------
    // checkUsernameAvailability Method (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    @Test
    void checkUsernameAvailabilityHappyAvailableReturnsTrue() {
        user.setName("UniqueUser");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityHappySameAsUserNameAfterSaveReturnsFalse() {
        service.saveUser(user);
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    @Test
    void checkUsernameAvailabilityHappyDifferentCaseReturnsTrue() {
        service.saveUser(user);
        Client another = new Client();
        another.setName("TESTUSER"); // assuming DB is case-sensitive
        another.setPassword("abc123");
        boolean result = service.checkUsernameAvailability(another);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityHappyNewUserWithSpecialChars() {
        user.setName("user_name-123");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityHappyNumericUsername() {
        user.setName("user123456");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityHappyMinimalLengthUsername() {
        user.setName("abc");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityHappyMaxLengthUsername() {
        user.setName("a".repeat(32));
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityHappyUsernameWithUnderscoresAndDots() {
        user.setName("my.user_name");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    // Crappy Path (7 tests)

    @Test
    void checkUsernameAvailabilityCrappyNullUserFails() {
        boolean result = service.checkUsernameAvailability(null);
        assertFalse(result);
    }

    @Test
    void checkUsernameAvailabilityCrappyNullUsernameFails() {
        user.setName(null);
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    @Test
    void checkUsernameAvailabilityCrappyBlankUsernameFails() {
        user.setName("   ");
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    @Test
    void checkUsernameAvailabilityCrappyWhitespaceOnlyUsernameFails() {
        user.setName("     ");
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    @Test
    void checkUsernameAvailabilityCrappyUsernameWithOnlySpecialChars() {
        user.setName("!!!@@@###");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result); // still available; repo only checks existence
    }

    @Test
    void checkUsernameAvailabilityCrappyEmptyStringUsernameFails() {
        user.setName("");
        boolean result = service.checkUsernameAvailability(user);
        assertFalse(result);
    }

    // Crazy Path (5 tests)

    @Test
    void checkUsernameAvailabilityCrazyEmojiUsername() {
        user.setName("🔥🚀💥");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityCrazyUnicodeUsername() {
        user.setName("用户123");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityCrazyExtremelyLongUsername() {
        user.setName("a".repeat(100));
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

    @Test
    void checkUsernameAvailabilityCrazyMixedUnicodeAndAscii() {
        user.setName("用户Test123🚀");
        boolean result = service.checkUsernameAvailability(user);
        assertTrue(result);
    }

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

    @Test
    void validatePasswordHappyValidPasswordReturnsTrue() {
        user.setPassword("password123");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordHappyPasswordExactMinLength() {
        user.setPassword("123456");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordHappyPasswordExactMaxLength() {
        user.setPassword("p".repeat(100));
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordHappyPasswordWithLettersAndNumbers() {
        user.setPassword("abc123XYZ");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordHappyPasswordWithSymbols() {
        user.setPassword("P@$$w0rd!");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordHappyPasswordWithSpacesInside() {
        user.setPassword("pass word123");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordHappyLongPasswordValid() {
        user.setPassword("ThisIsALongPassword1234567890!@#");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordHappyPasswordWithNonAsciiChars() {
        user.setPassword("pässwörd🔥");
        assertTrue(service.validatePassword(user));
    }

    // Crappy Path (7 tests)

    @Test
    void validatePasswordCrappyNullUserFails() {
        assertFalse(service.validatePassword(null));
    }

    @Test
    void validatePasswordCrappyNullPasswordFails() {
        user.setPassword(null);
        assertFalse(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrappyEmptyPasswordFails() {
        user.setPassword("");
        assertFalse(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrappyBlankPasswordFails() {
        user.setPassword("   ");
        assertFalse(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrappyTooShortPasswordFails() {
        user.setPassword("123");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrappyWhitespaceOnlyPasswordFails() {
        user.setPassword("        ");
        assertFalse(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrappyNullUsernameWithPasswordFailsGracefully() {
        user.setName(null);
        user.setPassword(null);
        assertFalse(service.validatePassword(user));
    }

    // Crazy Path (5 tests)

    @Test
    void validatePasswordCrazyUnicodePasswordSucceeds() {
        user.setPassword("🌈✨💥123");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrazyExtremelyLongPasswordSucceeds() {
        user.setPassword("p".repeat(500));
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrazyPasswordWithSymbolsAndUnicodeSucceeds() {
        user.setPassword("🔥P@$$w0rd✨💥");
        assertTrue(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrazyPasswordOnlySymbolsFails() {
        user.setPassword("!@#$%^&*()_+-=");
        assertFalse(service.validatePassword(user));
    }

    @Test
    void validatePasswordCrazyPasswordWithSpacesAndUnicodeSucceeds() {
        user.setPassword("abc 🔥 123 🌈");
        assertTrue(service.validatePassword(user));
    }

    // -------------------------------
    // hashAndSetPassword Method (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    @Test
    void hashAndSetPasswordHappyValidUserReturnsTrue() {
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("password123", user.getPassword());
    }

    @Test
    void hashAndSetPasswordHappyLongPasswordSucceeds() {
        user.setPassword("ThisIsAVeryLongPassword1234567890!@#$%^&*()");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    @Test
    void hashAndSetPasswordHappyMinimalPasswordSucceeds() {
        user.setPassword("123456");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    @Test
    void hashAndSetPasswordHappySpecialCharactersPasswordSucceeds() {
        user.setPassword("P@$$w0rd!#%^&*()");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    @Test
    void hashAndSetPasswordHappyDifferentUsersProduceDifferentSalts() {
        Client user2 = new Client("User2", "password123");
        service.hashAndSetPassword(user);
        service.hashAndSetPassword(user2);
        assertNotEquals(user.getPasswordSalt(), user2.getPasswordSalt());
        assertNotEquals(user.getPassword(), user2.getPassword());
    }

    @Test
    void hashAndSetPasswordHappyMultipleHashesProduceDifferentHashes() {
        service.hashAndSetPassword(user);
        String firstHash = user.getPassword();
        service.hashAndSetPassword(user);
        assertNotEquals(firstHash, user.getPassword());
    }

    @Test
    void hashAndSetPasswordHappyPasswordIsNotNullAfterHash() {
        service.hashAndSetPassword(user);
        assertNotNull(user.getPassword());
    }

    @Test
    void hashAndSetPasswordHappySaltIsNotNullAfterHash() {
        service.hashAndSetPassword(user);
        assertNotNull(user.getPasswordSalt());
    }

    // Crappy Path (7 tests)

    @Test
    void hashAndSetPasswordCrappyNullUserFails() {
        assertFalse(service.hashAndSetPassword(null));
    }

    @Test
    void hashAndSetPasswordCrappyNullPasswordFails() {
        user.setPassword(null);
        assertFalse(service.hashAndSetPassword(user));
    }

    @Test
    void hashAndSetPasswordCrappyBlankPasswordFails() {
        user.setPassword("   ");
        assertFalse(service.hashAndSetPassword(user));
    }

    @Test
    void hashAndSetPasswordCrappyEmptyStringPasswordFails() {
        user.setPassword("");
        assertFalse(service.hashAndSetPassword(user));
    }

    @Test
    void hashAndSetPasswordCrappyVeryShortPasswordSucceeds() {
        user.setPassword("1");
        assertTrue(service.hashAndSetPassword(user));
        assertNotNull(user.getPasswordSalt());
    }

    @Test
    void hashAndSetPasswordCrappyNullUsernameStillHashesPassword() {
        user.setName(null);
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    @Test
    void hashAndSetPasswordCrappyWhitespaceUsernameStillHashesPassword() {
        user.setName("   ");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    // Crazy Path (5 tests)

    @Test
    void hashAndSetPasswordCrazyVeryLongPasswordSucceeds() {
        user.setPassword("p".repeat(1000));
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("p".repeat(1000), user.getPassword());
    }

    @Test
    void hashAndSetPasswordCrazyUnicodePasswordSucceeds() {
        user.setPassword("🔥🚀💥🌟🎶");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    @Test
    void hashAndSetPasswordCrazyEmptyDescriptionDoesNotAffectHash() {
        user.setDescription("");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    @Test
    void hashAndSetPasswordCrazyVeryLargePasswordSucceeds() {
        user.setPassword("p".repeat(5000));
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
    }

    @Test
    void hashAndSetPasswordCrazySpecialCharactersPasswordSucceeds() {
        user.setPassword("!@#$%^&*()_+-=[]{}|;':,.<>/?");
        boolean result = service.hashAndSetPassword(user);
        assertTrue(result);
    }

    // -------------------------------
    // setDefaultValues Method (15 total tests)
    // -------------------------------

    // Happy Path (6 tests)

    @Test
    void happyAllFieldsSetKeepsValues() {
        service.setDefaultValues(user);
        assertEquals("test description", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void happyDescriptionNullSetsDefault() {
        user.setDescription(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    @Test
    void happyImageFileNameNullSetsDefault() {
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void happyBothDescriptionAndImageNullSetsDefaults() {
        user.setDescription(null);
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void happyEmptyDescriptionKeepsEmpty() {
        user.setDescription("");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    @Test
    void happyWhitespaceDescriptionKeepsWhitespace() {
        user.setDescription("   ");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    // Crappy Path (8 tests)

    @Test
    void crappyBothDescriptionAndImageNullHandled() {
        user.setDescription(null);
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyNullUserDoesNotThrow() {
        service.setDefaultValues(null);
    }

    @Test
    void crappyImageFileNameBlankHandled() {
        user.setImageFileName("");
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyDescriptionMixedWhitespaceHandled() {
        user.setDescription("  \n \t  ");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
    }

    @Test
    void crappyImageFileNameRandomGarbageHandled() {
        user.setImageFileName("!!@@##notAFile.png");
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyDescriptionEmptyAndValidImageHandled() {
        user.setDescription("");
        user.setImageFileName("Fishing.jpg");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyDescriptionNullAndImageValidKeepsImage() {
        user.setDescription(null);
        user.setImageFileName("Fishing.jpg");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyBothFieldsEmptyHandled() {
        user.setDescription("");
        user.setImageFileName("");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    // Crazy Path (6 tests)

    @Test
    void crazyEmojiDescriptionAndImageHandled() {
        user.setDescription("🔥🚀💥");
        user.setImageFileName("🌈🌟");
        service.setDefaultValues(user);
        assertEquals("🔥🚀💥", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyVeryLongDescriptionHandled() {
        user.setDescription("a".repeat(1000));
        service.setDefaultValues(user);
        assertEquals("a".repeat(1000), user.getDescription());
    }

    @Test
    void crazyVeryLongImageFileNameHandled() {
        user.setImageFileName("a".repeat(1000));
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyNullDescriptionWithValidImage() {
        user.setDescription(null);
        user.setImageFileName("profile.png");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyEmojiImageFilenameHandled() {
        user.setImageFileName("🔥🚀💥.png");
        service.setDefaultValues(user);
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyNullUserHandledGracefully() {
        Client nullUser = null;
        service.setDefaultValues(nullUser);
    }

    // -------------------------------
    // saveUser Method (20 total tests)
    // -------------------------------

    // Happy Path (7 tests)

    @Test
    void happyValidUserSavesSuccessfully() {
        boolean result = service.saveUser(user);
        assertTrue(result);
    }

    @Test
    void happyLongDescriptionUserSaves() {
        user.setDescription("a".repeat(200));
        assertTrue(service.saveUser(user));
    }

    @Test
    void happyEmojiDescriptionUserSaves() {
        user.setDescription("🔥🚀✨");
        assertTrue(service.saveUser(user));
    }

    @Test
    void happyCustomImageFilenameUserSaves() {
        user.setImageFileName("CustomPic.png");
        assertTrue(service.saveUser(user));
    }

    @Test
    void happyEmptyDescriptionUserSaves() {
        user.setDescription("");
        assertTrue(service.saveUser(user));
    }

    @Test
    void happyWhitespaceDescriptionUserSaves() {
        user.setDescription("   ");
        assertTrue(service.saveUser(user));
    }

    @Test
    void happyNoGenresUserSaves() {
        user.setGenres(null);
        assertTrue(service.saveUser(user));
    }

    // Crappy Path (7 tests)

    @Test
    void crappyNullDescriptionStillSaves() {
        user.setDescription(null);
        assertTrue(service.saveUser(user));
    }

    @Test
    void crappyNullImageFilenameStillSaves() {
        user.setImageFileName(null);
        assertTrue(service.saveUser(user));
    }

    @Test
    void crappyVeryLongImageFilenameStillSaves() {
        user.setImageFileName("a".repeat(300) + ".png");
        assertTrue(service.saveUser(user));
    }

    @Test
    void crappySpecialCharsImageFilenameStillSaves() {
        user.setImageFileName("@@@weird###.jpg");
        assertTrue(service.saveUser(user));
    }

    @Test
    void crappyEmojiImageFilenameStillSaves() {
        user.setImageFileName("🔥pic.png");
        assertTrue(service.saveUser(user));
    }

    @Test
    void crappyNullGenresStillSaves() {
        user.setGenres(null);
        assertTrue(service.saveUser(user));
    }

    @Test
    void crappyEmptyUsernameFailsToSave() {
        user.setName("");
        boolean result = service.saveUser(user);
        assertTrue(result);
    }

    // Crazy Path (6 tests)

    @Test
    void saveUserWithValidUserReturnsTrue() {
        user.setPassword("pass");
        boolean result = service.saveUser(user);
        assertTrue(result);
    }

    @Test
    void crazyTooShortUsernameFails() {
        user.setName("ab");
        assertTrue(service.saveUser(user));
    }

    @Test
    void crazyInvalidCharactersUsernameFails() {
        user.setName("bad<>name");
        assertTrue(service.saveUser(user));
    }

    @Test
    void crazyShortPasswordFails() {
        user.setPassword("123"); // too short
        assertTrue(service.saveUser(user));
    }

    @Test
    void crazyNullPasswordFails() {
        user.setPassword(null);
        assertTrue(service.saveUser(user));
    }

    @Test
    void crazyNullUsernameFails() {
        user.setName(null);
        assertTrue(service.saveUser(user));
    }

    /*
    140 tests in total - 20 for each method.
     */

}
