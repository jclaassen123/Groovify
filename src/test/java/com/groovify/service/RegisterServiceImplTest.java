package com.groovify.service;

import com.groovify.jpa.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 tests for {@link RegisterServiceImpl}.
 * <p>
 * Tests are organized into three sections:
 * <ul>
 *     <li>Happy Path: expected, standard usage of all public methods</li>
 *     <li>Crappy Path: null, empty, or invalid inputs</li>
 *     <li>Crazy Path: extreme or unusual inputs to ensure robustness</li>
 * </ul>
 * <p>
 * No external mocking framework is required; all tests are self-contained.
 */
public class RegisterServiceImplTest {

    // -------------------------------
    // Happy Path Tests
    // -------------------------------

    // -------- validatePassword --------
    /** Test normal password validation. */
    @Test
    void happyValidatePasswordNormal() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("abc123");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test password containing symbols. */
    @Test
    void happyValidatePasswordWithSymbols() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("P@ssw0rd!");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test a long password. */
    @Test
    void happyValidatePasswordLong() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("VeryLongPassword123!@#");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test numeric-only password. */
    @Test
    void happyValidatePasswordWithNumbersOnly() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("123456");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test alphabetic-only password. */
    @Test
    void happyValidatePasswordWithLettersOnly() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("abcdef");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test mixed-case password. */
    @Test
    void happyValidatePasswordWithMixedCase() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("AbCdEf123");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test maximum length password. */
    @Test
    void happyValidatePasswordWithMaxLength() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("A".repeat(100));
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test minimum length password. */
    @Test
    void happyValidatePasswordWithMinLength() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("123456");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    // -------- hashAndSetPassword --------
    /** Test that hashing a password generates a salt and changes the password. */
    @Test
    void happyHashAndSetPasswordGeneratesSalt() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client("user1", "password1");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("password1", user.getPassword());
    }

    /** Test hashing a different password. */
    @Test
    void happyHashAndSetPasswordDifferentPassword() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client("user123", "myPassword");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("myPassword", user.getPassword());
    }

    /** Test hashing a password with special characters. */
    @Test
    void happyHashAndSetPasswordWithSpecialChars() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client("userSpecial", "P@$$w0rd!");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotNull(user.getPasswordSalt());
        assertNotEquals("P@$$w0rd!", user.getPassword());
    }

    /** Test hashing a password twice produces different hashes. */
    @Test
    void happyHashAndSetPasswordTwice() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client("userRepeat", "password");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.hashAndSetPassword(user, model));
        String firstHash = user.getPassword();
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotEquals(firstHash, user.getPassword());
    }

    // -------- setDefaultValues --------
    /** Test setting default values when none exist. */
    @Test
    void happySetDefaultValues() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test that already-set values remain unchanged. */
    @Test
    void happySetDefaultValuesAlreadySet() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription("Already set");
        user.setImageFileName("custom.jpg");
        service.setDefaultValues(user);
        assertEquals("Already set", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test setting default values when only description exists. */
    @Test
    void happySetDefaultValuesWithDescriptionOnly() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription("My description");
        service.setDefaultValues(user);
        assertEquals("My description", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test setting default values when only image exists. */
    @Test
    void happySetDefaultValuesWithImageOnly() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription(null);
        user.setImageFileName("myImage.png");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test default values after password hashing. */
    @Test
    void happySetDefaultValuesAfterPasswordHashing() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("abc123");
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    // -------- addSuccessRedirect --------
    /** Test that success redirect sets the flash message. */
    @Test
    void happyAddSuccessRedirectSetsMessage() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirect, "userX");
        assertTrue(redirect.getFlashAttributes().containsKey("successMessage"));
    }

    /** Test the content of the success redirect message. */
    @Test
    void happyAddSuccessRedirectMessageContent() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirect, "userX");
        assertEquals("Registration successful! You can now log in.",
                redirect.getFlashAttributes().get("successMessage"));
    }

    /** Test calling addSuccessRedirect multiple times. */
    @Test
    void happyAddSuccessRedirectMultipleCalls() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirect, "user1");
        service.addSuccessRedirect(redirect, "user2");
        assertTrue(redirect.getFlashAttributes().containsKey("successMessage"));
    }

    // -------- validateInput --------
    /** Test validateInput with no errors. */
    @Test
    void happyValidateInputNoErrors() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validateInput(user, binding, model));
    }

    /** Test validateInput with a single error. */
    @Test
    void happyValidateInputWithSingleError() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        binding.reject("error", "test error");
        ExtendedModelMap model = new ExtendedModelMap();
        assertFalse(service.validateInput(user, binding, model));
    }

    /** Test validateInput with multiple errors. */
    @Test
    void happyValidateInputWithMultipleErrors() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        binding.reject("error1", "error 1");
        binding.reject("error2", "error 2");
        ExtendedModelMap model = new ExtendedModelMap();
        assertFalse(service.validateInput(user, binding, model));
    }

    /** Test validateInput with an empty binding. */
    @Test
    void happyValidateInputEmptyBinding() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validateInput(user, binding, model));
    }

    /** Test validateInput after hashing password. */
    @Test
    void happyValidateInputAfterHashingPassword() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("abc123");
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validateInput(user, binding, model));
    }

    // -------------------------------
    // Crappy Path Tests
    // -------------------------------

    /** Test validatePassword with null password. */
    @Test
    void crappyValidatePasswordNull() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword(null);
        ExtendedModelMap model = new ExtendedModelMap();
        assertFalse(service.validatePassword(user, model));
        assertTrue(model.containsAttribute("error"));
    }

    /** Test validatePassword with empty string password. */
    @Test
    void crappyValidatePasswordEmptyString() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("");
        ExtendedModelMap model = new ExtendedModelMap();
        assertFalse(service.validatePassword(user, model));
        assertTrue(model.containsAttribute("error"));
    }

    /** Test validatePassword with blank spaces password. */
    @Test
    void crappyValidatePasswordBlankString() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("   ");
        ExtendedModelMap model = new ExtendedModelMap();
        assertFalse(service.validatePassword(user, model));
        assertTrue(model.containsAttribute("error"));
    }

    /** Test validateInput with errors in binding. */
    @Test
    void crappyValidateInputWithErrors() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        binding.reject("error", "Invalid field");
        ExtendedModelMap model = new ExtendedModelMap();
        assertFalse(service.validateInput(user, binding, model));
        assertTrue(model.containsAttribute("user"));
    }

    /** Test hashing an empty password. */
    @Test
    void crappyHashAndSetPasswordEmptyPassword() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("");
        ExtendedModelMap model = new ExtendedModelMap();
        boolean result = service.hashAndSetPassword(user, model);
        assertTrue(result);
        assertNotNull(user.getPasswordSalt());
        assertNotNull(user.getPassword());
    }

    /** Test setDefaultValues with empty description and image. */
    @Test
    void crappySetDefaultValuesEmptyDescriptionAndImage() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription(null);
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test addSuccessRedirect with null username. */
    @Test
    void crappyAddSuccessRedirectNullUsername() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirect, null);
        assertTrue(redirect.getFlashAttributes().containsKey("successMessage"));
    }

    // -------------------------------
    // Crazy Path Tests
    // -------------------------------

    /** Test extremely long password for validatePassword. */
    @Test
    void crazyValidatePasswordExtremelyLong() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("A".repeat(1000));
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test password containing Unicode characters. */
    @Test
    void crazyValidatePasswordUnicode() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("Pāššŵøřđ🚀🔥");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test password with emoji characters only. */
    @Test
    void crazyValidatePasswordEmojiOnly() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setPassword("🔥🚀💥🌟🎶");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validatePassword(user, model));
    }

    /** Test repeated password hashing multiple times. */
    @Test
    void crazyHashAndSetPasswordRepeatedHashing() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client("userCrazy", "password123");
        ExtendedModelMap model = new ExtendedModelMap();
        for (int i = 0; i < 50; i++) {
            assertTrue(service.hashAndSetPassword(user, model));
            assertNotNull(user.getPasswordSalt());
            assertNotNull(user.getPassword());
        }
    }

    /** Test hashing a Unicode password. */
    @Test
    void crazyHashAndSetPasswordUnicode() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client("userUnicode", "Pāššŵøřđ🚀🔥");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotNull(user.getPasswordSalt());
        assertNotNull(user.getPassword());
    }

    /** Test hashing a very long password. */
    @Test
    void crazyHashAndSetPasswordLongPassword() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client("longUser", "A".repeat(1000));
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.hashAndSetPassword(user, model));
        assertNotNull(user.getPasswordSalt());
        assertNotNull(user.getPassword());
    }

    /** Test extremely long description in setDefaultValues. */
    @Test
    void crazySetDefaultValuesExtremelyLongDescription() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription("D".repeat(1000));
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("D".repeat(1000), user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test Unicode description in setDefaultValues. */
    @Test
    void crazySetDefaultValuesUnicodeDescription() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription("🔥🚀💥🌟🎶");
        user.setImageFileName(null);
        service.setDefaultValues(user);
        assertEquals("🔥🚀💥🌟🎶", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test setDefaultValues when both description and image are already set. */
    @Test
    void crazySetDefaultValuesDescriptionAndImageSet() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setDescription("Crazy description");
        user.setImageFileName("crazy.png"); // file does not exist
        service.setDefaultValues(user);
        assertEquals("Crazy description", user.getDescription());
        assertEquals("Fishing.jpg", user.getImageFileName());
    }

    /** Test addSuccessRedirect with long username. */
    @Test
    void crazyAddSuccessRedirectLongUsername() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirect, "user".repeat(100));
        assertTrue(redirect.getFlashAttributes().containsKey("successMessage"));
    }

    /** Test addSuccessRedirect with Unicode username. */
    @Test
    void crazyAddSuccessRedirectUnicodeUsername() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        service.addSuccessRedirect(redirect, "🔥🚀💥🌟🎶");
        assertTrue(redirect.getFlashAttributes().containsKey("successMessage"));
    }

    /** Test validateInput with very many errors. */
    @Test
    void crazyValidateInputManyErrors() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        for (int i = 0; i < 100; i++) {
            binding.reject("err" + i, "Error " + i);
        }
        ExtendedModelMap model = new ExtendedModelMap();
        assertFalse(service.validateInput(user, binding, model));
    }

    /** Test validateInput with maximum-length username. */
    @Test
    void crazyValidateInputMaxLengthUsername() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setName("U".repeat(32));
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validateInput(user, binding, model));
    }

    /** Test validateInput with over maximum-length username. */
    @Test
    void crazyValidateInputOverMaxLengthUsername() {
        RegisterServiceImpl service = new RegisterServiceImpl(null);
        Client user = new Client();
        user.setName("U".repeat(100));
        MapBindingResult binding = new MapBindingResult(new HashMap<>(), "user");
        ExtendedModelMap model = new ExtendedModelMap();
        assertTrue(service.validateInput(user, binding, model));
    }
}
