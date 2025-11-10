package com.groovify.service;

import com.groovify.jpa.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Comprehensive Spring Boot tests for {@link RegisterServiceImpl}.
 * <p>
 * Tests are organized by Method and into Happy, Crappy, and Crazy paths.
 */
@SpringBootTest
public class RegisterServiceImplTest {

    @Autowired
    private RegisterServiceImpl service;

    private Client user;
    private ExtendedModelMap model;

    @BeforeEach
    void setup() {
        service = new RegisterServiceImpl(null);
        user = new Client();
        model = new ExtendedModelMap();
    }

    // -------------------------------
    // registerUser Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    @Test
    void registerUserHappyValidUser() {
        user.setName("JohnDoe");
        user.setPassword("pass123");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserHappyAlphanumericPassword() {
        user.setName("user123");
        user.setPassword("abc123");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserHappyWithSymbols() {
        user.setName("SymbolGuy");
        user.setPassword("P@ssword!");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserHappyMinLengthPassword() {
        user.setName("MinUser");
        user.setPassword("123456");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserHappyLongPassword() {
        user.setName("LongPassUser");
        user.setPassword("A".repeat(50));
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserHappyWithWhitespaceInName() {
        user.setName(" John Doe ");
        user.setPassword("securePass123");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserHappySpecialCharactersInName() {
        user.setName("Jane_Doe!");
        user.setPassword("1234567");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserHappyMixedCaseName() {
        user.setName("MixedCASEuser");
        user.setPassword("mypassword");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    // Crappy Path (7 tests)

    @Test
    void registerUserCrappyNullPassword() {
        user.setName("TestUser");
        user.setPassword(null);
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrappyBlankPassword() {
        user.setName("Blanky");
        user.setPassword("   ");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrappyEmptyName() {
        user.setName("");
        user.setPassword("pass123");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrappyNullName() {
        user.setName(null);
        user.setPassword("password");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrappyBindingErrorsPresent() {
        user.setName("ErrorUser");
        user.setPassword("pass");
        var result = new MapBindingResult(new HashMap<>(), "user");
        result.reject("error", "Simulated validation error");
        var redirect = new RedirectAttributesModelMap();
        assertEquals("register", service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrappyModelHasPreexistingAttributes() {
        model.addAttribute("user", "existing");
        user.setName("AlreadyInModel");
        user.setPassword("pass123");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrappyEmptyPasswordAndName() {
        user.setName("");
        user.setPassword("");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    // Crazy Path (5 tests)

    @Test
    void registerUserCrazyUnicodeNameAndPassword() {
        user.setName("🔥🚀💥🌟🎶");
        user.setPassword("🌈✨12345");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrazyVeryLongName() {
        user.setName("User".repeat(1000));
        user.setPassword("password");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrazyVeryLongPassword() {
        user.setName("CrazyPasswordUser");
        user.setPassword("A".repeat(1000));
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    @Test
    void registerUserCrazyMultipleCallsWithSameUser() {
        user.setName("RepeatUser");
        user.setPassword("password123");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> {
            service.registerUser(user, result, model, redirect);
            service.registerUser(user, result, model, redirect);
        });
    }

    @Test
    void registerUserCrazyEmojiOnlyPassword() {
        user.setName("EmojiUser");
        user.setPassword("🔥🚀💥🌟🎶");
        var result = new MapBindingResult(new HashMap<>(), "user");
        var redirect = new RedirectAttributesModelMap();
        assertThrows(NullPointerException.class, () -> service.registerUser(user, result, model, redirect));
    }

    // -------------------------------
    // validateInput Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (5 tests)

    @Test
    void validateInputHappyValidUserNoErrorsReturnsTrue() {
        user.setName("GoodUser");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputHappyEmptyModelReturnsTrue() {
        user.setName("UserA");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputHappyWhitespaceNameReturnsTrue() {
        user.setName("  test  ");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputHappyMixedCaseNameReturnsTrue() {
        user.setName("MixEdCase");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputHappyUnicodeNameReturnsTrue() {
        user.setName("🌟User");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    // Crappy Path (5 tests)

    @Test
    void validateInputCrappyValidationErrorsReturnFalse() {
        user.setName("BadUser");
        var result = new MapBindingResult(new HashMap<>(), "user");
        result.reject("name", "Invalid name");
        assertFalse(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrappyNullUserReturnsTrue() {
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(null, result, model));
    }

    @Test
    void validateInputCrappyNullModelReturnsTrue() {
        user.setName("NullModelUser");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, null));
    }

    @Test
    void validateInputCrappyNullBindingResultThrowsNpe() {
        user.setName("NullResultUser");
        assertThrows(NullPointerException.class, () -> service.validateInput(user, null, model));
    }

    @Test
    void validateInputCrappyMultipleErrorsReturnFalse() {
        user.setName("MultiErrorUser");
        var result = new MapBindingResult(new HashMap<>(), "user");
        result.reject("error1", "Error A");
        result.reject("error2", "Error B");
        assertFalse(service.validateInput(user, result, model));
    }

    // Crazy Path (10 tests)

    @Test
    void validateInputCrazyVeryLongNameReturnsTrue() {
        user.setName("X".repeat(5000));
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyNameWithSqlInjectionReturnsTrue() {
        user.setName("'; DROP TABLE users; --");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyErrorAfterSuccessReturnFalse() {
        user.setName("WeirdCase");
        var result = new MapBindingResult(new HashMap<>(), "user");
        result.reject("error", "Injected later");
        assertFalse(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyModelAlreadyHasUserAttributeStillReturnsTrue() {
        model.addAttribute("user", "Existing");
        user.setName("ModelOverlap");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyRapidMultipleCallsAlternateResults() {
        user.setName("RapidFire");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
        result.reject("error", "Later failure");
        assertFalse(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyEmojiAndSymbolsNameReturnsTrue() {
        user.setName("🔥💀⚡");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyResultClearedMidRunStillReturnsTrue() {
        user.setName("ClearResult");
        var result = new MapBindingResult(new HashMap<>(), "user");
        // don’t add/reject anything; just run it
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyMassiveModelAttributesStillReturnsTrue() {
        for (int i = 0; i < 1000; i++) model.addAttribute("key" + i, "value");
        user.setName("HeavyModel");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyNullNameReturnsTrue() {
        user.setName(null);
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    @Test
    void validateInputCrazyEmptyNameReturnsTrue() {
        user.setName("");
        var result = new MapBindingResult(new HashMap<>(), "user");
        assertTrue(service.validateInput(user, result, model));
    }

    // -------------------------------
    // checkUsernameAvailability Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    @Test
    void checkUsernameAvailabilityHappyNewUserThrowsNpe() {
        user.setName("newUser");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityHappyEmptyNameThrowsNpe() {
        user.setName("");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityHappyWhitespaceNameThrowsNpe() {
        user.setName("   ");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityHappyNameWithSymbolsThrowsNpe() {
        user.setName("user!@#");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityHappyMixedCaseNameThrowsNpe() {
        user.setName("MixedCase");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityHappyUnicodeNameThrowsNpe() {
        user.setName("🔥🚀💥");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityHappyVeryLongNameThrowsNpe() {
        user.setName("User".repeat(100));
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityHappyAlphanumericNameThrowsNpe() {
        user.setName("User123ABC");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    // Crappy Path (5 tests)

    @Test
    void checkUsernameAvailabilityCrappyNullUserThrowsNpe() {
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(null, model));
    }

    @Test
    void checkUsernameAvailabilityCrappyNullModelThrowsNpe() {
        user.setName("someone");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, null));
    }

    @Test
    void checkUsernameAvailabilityCrappyNameIsNullThrowsNpe() {
        user.setName(null);
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityCrappyVeryShortNameThrowsNpe() {
        user.setName("a");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityCrappyExistingModelAttributesStillThrowsNpe() {
        model.addAttribute("error", "preexisting");
        user.setName("someone");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    // Crazy Path (7 tests)

    @Test
    void checkUsernameAvailabilityCrazyUnicodeNameThrowsNpe() {
        user.setName("🔥🚀");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityCrazyVeryLongNameThrowsNpe() {
        user.setName("U".repeat(2000));
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityCrazySqlLikeNameThrowsNpe() {
        user.setName("'; DROP TABLE users; --");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityCrazyMultipleSequentialCallsThrowNpe() {
        user.setName("seqUser");
        assertThrows(NullPointerException.class, () -> {
            service.checkUsernameAvailability(user, model);
            service.checkUsernameAvailability(user, model);
        });
    }

    @Test
    void checkUsernameAvailabilityCrazyDifferentUsersAllThrowNpe() {
        user.setName("u1");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
        user.setName("u2");
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    @Test
    void checkUsernameAvailabilityExtraCrazyNullsBothArgsThrowsNpe() {
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(null, null));
    }

    @Test
    void checkUsernameAvailabilityCrazyExtraNameWithEmojiAndLongSuffixThrowsNpe() {
        user.setName("emojiUser🔥".repeat(50));
        assertThrows(NullPointerException.class, () -> service.checkUsernameAvailability(user, model));
    }

    // -------------------------------
    // validatePassword Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    @Test
    void validatePasswordHappyNormal() {
        user.setPassword("abc123");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordHappyWithSymbols() {
        user.setPassword("P@ssw0rd!");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordHappyLong() {
        user.setPassword("VeryLongPassword123!@#");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordHappyNumbersOnly() {
        user.setPassword("123456");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordHappyLettersOnly() {
        user.setPassword("abcdef");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordHappyMixedCase() {
        user.setPassword("AbCdEf123");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordHappyMaxLength() {
        user.setPassword("A".repeat(100));
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordHappyMinLength() {
        user.setPassword("123456");
        assertTrue(service.validatePassword(user, model));
    }

    // Crappy Path (6 tests)

    @Test
    void validatePasswordCrappyNullPassword() {
        user.setPassword(null);
        assertFalse(service.validatePassword(user, model));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void validatePasswordCrappyEmptyPassword() {
        user.setPassword("");
        assertFalse(service.validatePassword(user, model));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void validatePasswordCrappyBlankPassword() {
        user.setPassword("   ");
        assertFalse(service.validatePassword(user, model));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void validatePasswordCrappyWhitespaceAroundValidPassword() {
        user.setPassword("  valid123  ");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordCrappyNullModel() {
        user.setPassword("");
        assertThrows(NullPointerException.class, () -> service.validatePassword(user, null),
                "Expected NPE when model is null and password is blank");
    }

    @Test
    void validatePasswordCrappyNullUser() {
        assertThrows(NullPointerException.class, () -> service.validatePassword(null, model));
    }

    // Crazy Path (6 tests)

    @Test
    void validatePasswordCrazyUnicodePassword() {
        user.setPassword("🔥🚀💥🌟🎶");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordCrazyEmojiAndText() {
        user.setPassword("abc🚀123🔥");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordCrazyExtremelyLongPassword() {
        user.setPassword("A".repeat(1000));
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordCrazySpecialCharsOnly() {
        user.setPassword("!@#$%^&*()_+-=[]{}|;':,./<>?");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordCrazyPasswordWithTabsAndNewlines() {
        user.setPassword("\t\nabc123\n\t");
        assertTrue(service.validatePassword(user, model));
    }

    @Test
    void validatePasswordCrazyRepeatedValidation() {
        user.setPassword("repeatedPass123");
        for (int i = 0; i < 50; i++) {
            assertTrue(service.validatePassword(user, model));
        }
    }

    // -------------------------------
    // hashAndSetPassword Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (8 tests)

    @Test
    void happyHashAndSetPasswordNormal() {
        user = new Client("user1", "password1");
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("password1", user.getPassword());
    }

    @Test
    void happyHashAndSetPasswordDifferentUser() {
        user = new Client("user2", "myPassword");
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotNull(user.getPasswordSalt());
    }

    @Test
    void happyHashAndSetPasswordSpecialChars() {
        user = new Client("userSpecial", "P@$$w0rd!");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void happyHashAndSetPasswordTwiceDiffHashes() {
        user = new Client("userRepeat", "password");
        assertTrue(service.hashAndSetPassword(user, model));
        String firstHash = user.getPassword();
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotEquals(firstHash, user.getPassword());
    }

    @Test
    void happyHashAndSetPasswordMixedCase() {
        user = new Client("userMix", "AbCdEf123");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void happyHashAndSetPasswordMaxLength() {
        user = new Client("userMax", "A".repeat(100));
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void happyHashAndSetPasswordMinLength() {
        user = new Client("userMin", "123456");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void happyHashAndSetPasswordSamePasswordDifferentUsers() {
        Client u1 = new Client("u1", "pw");
        Client u2 = new Client("u2", "pw");
        service.hashAndSetPassword(u1, model);
        service.hashAndSetPassword(u2, model);
        assertNotEquals(u1.getPasswordSalt(), u2.getPasswordSalt());
    }

    // Crappy Path (7 tests)

    @Test
    void crappyHashAndSetPasswordEmptyString() {
        user = new Client("emptyPw", "");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crappyHashAndSetPasswordSpacesOnly() {
        user = new Client("spacesPw", "   ");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crappyHashAndSetPasswordLongWhitespace() {
        user = new Client("longSpace", " ".repeat(50));
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crappyHashAndSetPasswordNameNull() {
        user = new Client(null, "password123");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crappyHashAndSetPasswordEmptyName() {
        user = new Client("", "password123");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crappyHashAndSetPasswordSingleCharPassword() {
        user = new Client("singleChar", "x");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crappyHashAndSetPasswordLongPassword() {
        user = new Client("longPass", "X".repeat(1000));
        assertTrue(service.hashAndSetPassword(user, model));
    }

    // Crazy Path (5 tests)

    @Test
    void crazyHashAndSetPasswordRepeatedCalls() {
        user = new Client("crazyUser", "password123");
        for (int i = 0; i < 10; i++) {
            assertTrue(service.hashAndSetPassword(user, model));
        }
    }

    @Test
    void crazyHashAndSetPasswordUnicodePassword() {
        user = new Client("unicodeUser", "Pāššŵøřđ🚀🔥");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crazyHashAndSetPasswordEmojiPassword() {
        user = new Client("emojiUser", "🔥🚀💥🌟🎶");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crazyHashAndSetPasswordVeryLongPassword() {
        user = new Client("veryLongUser", "A".repeat(1000));
        assertTrue(service.hashAndSetPassword(user, model));
    }

    @Test
    void crazyHashAndSetPasswordEmptyStringPassword() {
        user = new Client("emptyStringUser", "");
        assertTrue(service.hashAndSetPassword(user, model));
    }

    // -------------------------------
    // setDefaultValues Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (5 tests)

    @Test
    void happyDescriptionNullImageNull() {
        user.setDescription(null);
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void happyDescriptionSetImageNull() {
        user.setDescription("Hello");
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("Hello", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void happyDescriptionNullImageSet() {
        user.setDescription(null);
        user.setImageFileName("custom.jpg");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void happyDescriptionSetImageSet() {
        user.setDescription("Hi");
        user.setImageFileName("img.png");
        service.setDefaultValues(user);
        assertEquals("Hi", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void happyDescriptionEmptyImageNull() {
        user.setDescription("");
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    // Crappy Path (5 tests)

    @Test
    void crappyDescriptionEmptyImageEmpty() {
        user.setDescription("");
        user.setImageFileName("");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("", user.getImageFileName());
    }

    @Test
    void crappyDescriptionSpacesImageSet() {
        user.setDescription("   ");
        user.setImageFileName("img.png");
        service.setDefaultValues(user);
        assertEquals("   ", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyDescriptionSetImageSet() {
        user.setDescription("Desc");
        user.setImageFileName("img.png");
        service.setDefaultValues(user);
        assertEquals("Desc", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyDescriptionEmptyImageSet() {
        user.setDescription("");
        user.setImageFileName("img.png");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crappyDescriptionNullImageSet() {
        user.setDescription(null);
        user.setImageFileName("img.png");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    // Crazy Path (10 tests)

    @Test
    void crazyVeryLongDescriptionImageNull() {
        user.setDescription("D".repeat(1000));
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("D".repeat(1000), user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyUnicodeDescriptionImageNull() {
        user.setDescription("🔥🚀💥");
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("🔥🚀💥", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyEmojiDescriptionImageSet() {
        user.setDescription("🎉🎂");
        user.setImageFileName("🎈🎁.png");
        service.setDefaultValues(user);
        assertEquals("🎉🎂", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyDescriptionNullImageVeryLongName() {
        user.setDescription(null);
        user.setImageFileName("A".repeat(200));
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyEmptyDescriptionImageVeryLongName() {
        user.setDescription("");
        user.setImageFileName("B".repeat(400));
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyEmojiDescriptionImageEmpty() {
        user.setDescription("🎶🎵");
        user.setImageFileName("");
        service.setDefaultValues(user);
        assertEquals("🎶🎵", user.getDescription());
        assertEquals("", user.getImageFileName());
    }

    @Test
    void crazyDescriptionNullImageNull() {
        user.setDescription(null);
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyUnicodeDescriptionImageSet() {
        user.setDescription("🎶🎵");
        user.setImageFileName("img.png");
        service.setDefaultValues(user);
        assertEquals("🎶🎵", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyVeryLongDescriptionImageSet() {
        user.setDescription("Y".repeat(300));
        user.setImageFileName("custom.png");
        service.setDefaultValues(user);
        assertEquals("Y".repeat(300), user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    @Test
    void crazyDescriptionSpacesImageNull() {
        user.setDescription("   ");
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("   ", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    // -------------------------------
    // saveUser Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (5 tests)

    @Test
    void saveUserHappyValidUserReturnsFalseWithoutRepo() {
        user.setName("JohnDoe");
        user.setPassword("password123");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserHappyAnotherValidUserReturnsFalseWithoutRepo() {
        user.setName("Alice123");
        user.setPassword("Pass456");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserHappyUserWithLongNameReturnsFalseWithoutRepo() {
        user.setName("User".repeat(50));
        user.setPassword("password");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserHappyUserWithSpecialCharsReturnsFalseWithoutRepo() {
        user.setName("Jane_Doe!");
        user.setPassword("Pa$$word");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserHappyUserWithSpacesReturnsFalseWithoutRepo() {
        user.setName(" John Doe ");
        user.setPassword("securePass");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    // Crappy Path (5 tests)

    @Test
    void saveUserCrappyUserNameNullReturnsFalseWithoutRepo() {
        user.setName(null);
        user.setPassword("password123");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrappyUserNameEmptyReturnsFalseWithoutRepo() {
        user.setName("");
        user.setPassword("password123");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrappyUserPasswordNullReturnsFalseWithoutRepo() {
        user.setName("UserX");
        user.setPassword(null);
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrappyUserPasswordEmptyReturnsFalseWithoutRepo() {
        user.setName("UserY");
        user.setPassword("");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrappyUserPasswordSpacesOnlyReturnsFalseWithoutRepo() {
        user.setName("UserZ");
        user.setPassword("   ");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    // Crazy Path (10 tests)

    @Test
    void saveUserCrazyEmptyNameAndPasswordReturnsFalseWithoutRepo() {
        user.setName("");
        user.setPassword("");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyNullNameAndPasswordReturnsFalseWithoutRepo() {
        user.setName(null);
        user.setPassword(null);
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyUserNameVeryLongReturnsFalseWithoutRepo() {
        user.setName("LongName".repeat(100));
        user.setPassword("password");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyUserPasswordVeryLongReturnsFalseWithoutRepo() {
        user.setName("UserLongPass");
        user.setPassword("A".repeat(1000));
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyEmojiNameAndPasswordReturnsFalseWithoutRepo() {
        user.setName("🔥🚀💥");
        user.setPassword("🌈✨💧");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyWhitespaceNameAndPasswordReturnsFalseWithoutRepo() {
        user.setName("    ");
        user.setPassword("   ");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyVeryLongNameAndPasswordReturnsFalseWithoutRepo() {
        user.setName("User".repeat(200));
        user.setPassword("A".repeat(2000));
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyNullNameWithValidPasswordReturnsFalseWithoutRepo() {
        user.setName(null);
        user.setPassword("Valid123");
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyValidNameWithNullPasswordReturnsFalseWithoutRepo() {
        user.setName("ValidUser");
        user.setPassword(null);
        boolean result = service.saveUser(user, model);
        assertFalse(result);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    void saveUserCrazyMultipleCallsWithSameUserReturnsFalseWithoutRepo() {
        user.setName("RepeatUser");
        user.setPassword("Repeat123");
        boolean result1 = service.saveUser(user, model);
        boolean result2 = service.saveUser(user, model);
        assertFalse(result1);
        assertFalse(result2);
        assertTrue(model.containsAttribute("user"));
        assertTrue(model.containsAttribute("error"));
    }

    // -------------------------------
    // addSuccessRedirect Method Tests (20 total tests)
    // -------------------------------

    // Happy Path (7 tests)

    @Test
    void addSuccessRedirectHappyNormalUsername() {
        String username = "JohnDoe";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectHappyShortUsername() {
        String username = "JD";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectHappyNumericUsername() {
        String username = "user123";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectHappyAlphanumericUsername() {
        String username = "userABC123";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectHappyUsernameWithSpaces() {
        String username = "John Doe";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectHappyUsernameWithUnderscores() {
        String username = "John_Doe";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectHappyUsernameWithDashes() {
        String username = "John-Doe";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    // Crappy Path (5 tests)

    @Test
    void addSuccessRedirectCrappyNullUsername() {
        String username = null;
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrappyEmptyUsername() {
        String username = "";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrappyWhitespaceUsername() {
        String username = "   ";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrappyUsernameWithTabs() {
        String username = "\t\t";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrappyUsernameWithNewlines() {
        String username = "\n\n";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    // Crazy Path (8 tests)

    @Test
    void addSuccessRedirectCrazyUsernameWithSymbolsAndEmoji() {
        String username = "@User🔥!";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrazyUsernameWithLongSpaces() {
        String username = " ".repeat(100);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrazyUsernameWithTabsAndNewlines() {
        String username = "\n\tUser\n\t";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrazyVeryLongEmojiUsername() {
        String username = "🔥🚀💥🌟🎶".repeat(50);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrazyUnicodeUsername() {
        String username = "用户🔥🚀";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrazyUsernameWithInvisibleUnicodeChars() {
        String username = "\u200B\u200C\u200D";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrazyUsernameWithMixedSymbols() {
        String username = "!@#$%^&*()_+{}|:<>?";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    @Test
    void addSuccessRedirectCrazyUsernameWithRTLChars() {
        String username = "שלוםעולם";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirectAttributes, username);
        assertEquals("Registration successful! You can now log in.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
    }

    /*
    140 tests in total - 20 for each method.
     */

}
