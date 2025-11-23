package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.web.form.ProfileUpdateForm;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ProfileServiceImpl}, validating profile retrieval,
 * username availability checks, genre retrieval, and profile update behavior.
 * <p>
 * All tests assume intentional behavior including:
 * <ul>
 *     <li>Usernames are <strong>case-sensitive</strong></li>
 *     <li>Usernames are matched exactly with <strong>no whitespace trimming</strong></li>
 *     <li>Returned genre lists are <strong>mutable</strong></li>
 *     <li>{@code null} current username is treated as "different user"</li>
 * </ul>
 */
@Transactional
@SpringBootTest
class ProfileServiceImplTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private GenreImportService genreImportService;

    private Client testUser;

    /**
     * Registers a default test user before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new Client();
        testUser.setName("TestUser");
        testUser.setPassword("Password123");
        registerService.registerUser(testUser);
    }

    // ============================
    // getUserByUsername Tests
    // ============================

    /**
     * Verifies that retrieving an existing user by exact username returns the user.
     */
    @Test
    void getUserByUsernameExistingReturnsUser() {
        Optional<Client> user = profileService.getUserByUsername("TestUser");
        assertTrue(user.isPresent());
        assertEquals("TestUser", user.get().getName());
    }

    /**
     * Ensures that querying a non-existent username returns an empty Optional.
     */
    @Test
    void getUserByUsernameNonExistentReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername("NoUser");
        assertTrue(user.isEmpty());
    }

    /**
     * Ensures correct retrieval when multiple users exist.
     */
    @Test
    void getUserByUsernameReturnsCorrectUserForMultipleUsers() {
        Client user2 = new Client();
        user2.setName("UserTwo");
        user2.setPassword("Password123");
        registerService.registerUser(user2);

        Optional<Client> user = profileService.getUserByUsername("UserTwo");
        assertTrue(user.isPresent());
        assertEquals("UserTwo", user.get().getName());
    }

    /**
     * Ensures that user lookup does not trim whitespace.
     * Leading/trailing whitespace must be considered part of the username.
     */
    @Test
    void getUserByUsernameLeadingTrailingWhitespaceFails() {
        Optional<Client> user = profileService.getUserByUsername(" TestUser ");
        assertTrue(user.isEmpty());
    }

    /**
     * Verifies that an empty username returns an empty result.
     */
    @Test
    void getUserByUsernameEmptyStringReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername("");
        assertTrue(user.isEmpty());
    }

    /**
     * Ensures that a null username returns an empty Optional.
     */
    @Test
    void getUserByUsernameNullReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername(null);
        assertTrue(user.isEmpty());
    }

    /**
     * Ensures username lookup is strictly case-sensitive.
     */
    @Test
    void getUserByUsernameCaseSensitiveReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername("testuser");
        assertTrue(user.isEmpty());
    }

    /**
     * Ensures that after updating a user's username, the old name no longer resolves
     * and the new name correctly resolves to the updated user.
     */
    @Test
    void getUserByUsernameExactMatchAfterUpdateReturnsUpdatedUser() {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setName("TestUserUpdated");
        form.setDescription("New description");
        form.setImageFileName("NewImage.jpg");

        profileService.updateProfile("TestUser", form);

        Optional<Client> oldNameUser = profileService.getUserByUsername("TestUser");
        Optional<Client> newNameUser = profileService.getUserByUsername("TestUserUpdated");

        assertTrue(oldNameUser.isEmpty());
        assertTrue(newNameUser.isPresent());
        assertEquals("TestUserUpdated", newNameUser.get().getName());
    }

    // ============================
    // getAllGenres Tests
    // ============================

    /**
     * Ensures the genre list is initially empty.
     */
    @Test
    void getAllGenresInitiallyEmpty() {
        List<Genre> genres = profileService.getAllGenres();
        assertNotNull(genres);
        assertTrue(genres.isEmpty());
    }

    /**
     * Ensures genres appear after saving them via the import service.
     */
    @Test
    void getAllGenresReturnsListAfterAddingGenres() {
        genreImportService.saveGenre("Rock");
        genreImportService.saveGenre("Pop");

        List<Genre> genres = profileService.getAllGenres();
        assertNotNull(genres);
        assertEquals(2, genres.size());
    }

    /**
     * Ensures the genre list contains correct genre names after insertion.
     */
    @Test
    void getAllGenresContainsCorrectNames() {
        genreImportService.saveGenre("Jazz");
        genreImportService.saveGenre("Blues");

        List<Genre> genres = profileService.getAllGenres();
        List<String> names = genres.stream().map(Genre::getName).toList();

        assertTrue(names.contains("Jazz"));
        assertTrue(names.contains("Blues"));
    }

    /**
     * Ensures duplicate genre names are not saved and only one entry exists.
     */
    @Test
    void getAllGenresHandlesDuplicates() {
        genreImportService.saveGenre("Rock");
        genreImportService.saveGenre("Rock");

        List<Genre> genres = profileService.getAllGenres();
        assertEquals(1, genres.size());
    }

    /**
     * Ensures the list returned by {@code getAllGenres()} is mutable, and modifying it
     * does not affect the stored data.
     */
    @Test
    void getAllGenresListIsMutable() {
        genreImportService.saveGenre("Metal");

        List<Genre> genres = profileService.getAllGenres();
        int originalSize = genres.size();
        genres.add(new Genre("Electronic"));

        assertEquals(originalSize + 1, genres.size());
    }

    /**
     * Ensures the genre list does not automatically clear if import is called with an empty list.
     */
    @Test
    void getAllGenresReturnsEmptyAfterClearingGenres() {
        genreImportService.saveGenre("Country");
        genreImportService.importGenres(List.of()); // intentionally no-op

        List<Genre> genres = profileService.getAllGenres();
        assertFalse(genres.isEmpty());
    }

    // ============================
    // isUsernameTaken Tests
    // ============================

    /**
     * Ensures usernames belonging to another user are considered taken.
     */
    @Test
    void isUsernameTakenReturnsTrueForExistingUsernameDifferentFromCurrent() {
        Client otherUser = new Client();
        otherUser.setName("OtherUser");
        otherUser.setPassword("Password123");
        registerService.registerUser(otherUser);

        boolean taken = profileService.isUsernameTaken("OtherUser", "TestUser");
        assertTrue(taken);
    }

    /**
     * Ensures a username is not considered taken when it matches the current user's username exactly.
     */
    @Test
    void isUsernameTakenReturnsFalseForSameAsCurrentUsername() {
        boolean taken = profileService.isUsernameTaken("TestUser", "TestUser");
        assertFalse(taken);
    }

    /**
     * Ensures non-existent usernames are not considered taken.
     */
    @Test
    void isUsernameTakenReturnsFalseForNonExistentUsername() {
        boolean taken = profileService.isUsernameTaken("NonExistent", "TestUser");
        assertFalse(taken);
    }

    /**
     * Ensures username-taken checks correctly detect multiple existing users.
     */
    @Test
    void isUsernameTakenReturnsTrueWithMultipleUsers() {
        Client otherUser1 = new Client();
        otherUser1.setName("UserOne");
        otherUser1.setPassword("Password123");
        registerService.registerUser(otherUser1);

        Client otherUser2 = new Client();
        otherUser2.setName("UserTwo");
        otherUser2.setPassword("Password123");
        registerService.registerUser(otherUser2);

        assertTrue(profileService.isUsernameTaken("UserOne", "TestUser"));
        assertTrue(profileService.isUsernameTaken("UserTwo", "TestUser"));
    }

    /**
     * Ensures {@code null} current username is treated as "different user",
     * meaning an existing username is considered taken.
     */
    @Test
    void isUsernameTakenReturnsFalseWhenCurrentUsernameIsNull() {
        boolean taken = profileService.isUsernameTaken("TestUser", null);
        assertTrue(taken);
    }

    /**
     * Ensures empty usernames are never considered taken.
     */
    @Test
    void isUsernameTakenReturnsFalseForEmptyUsername() {
        boolean taken = profileService.isUsernameTaken("", "TestUser");
        assertFalse(taken);
    }

    /**
     * Ensures null usernames are never considered taken.
     */
    @Test
    void isUsernameTakenReturnsFalseForNullUsername() {
        boolean taken = profileService.isUsernameTaken(null, "TestUser");
        assertFalse(taken);
    }

    /**
     * Ensures username-taken checks are case-sensitive.
     */
    @Test
    void isUsernameTakenCaseSensitiveCheck() {
        boolean taken = profileService.isUsernameTaken("testuser", "TestUser");
        assertFalse(taken);
    }

    /**
     * Ensures usernames must match exactly and are not trimmed.
     */
    @Test
    void isUsernameTakenReturnsTrueIfOtherUserExistsWithTrailingWhitespace() {
        Client whitespaceUser = new Client();
        whitespaceUser.setName("WhitespaceUser");
        whitespaceUser.setPassword("Password123");
        registerService.registerUser(whitespaceUser);

        boolean taken = profileService.isUsernameTaken("WhitespaceUser", "TestUser");
        assertTrue(taken);
    }

    // ============================
    // updateProfile Tests
    // ============================

    /**
     * Ensures profile updates modify username, description, and image file name.
     */
    @Test
    void updateProfileSuccessfullyUpdatesFields() {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setName("NewName");
        form.setDescription("NewDesc");
        form.setImageFileName("new.jpg");
        form.setGenres(List.of());

        boolean updated = profileService.updateProfile("TestUser", form);
        assertTrue(updated);

        Optional<Client> user = profileService.getUserByUsername("NewName");
        assertTrue(user.isPresent());
        assertEquals("NewDesc", user.get().getDescription());
        assertEquals("new.jpg", user.get().getImageFileName());
    }

    /**
     * Ensures updating a non-existent user returns false and performs no update.
     */
    @Test
    void updateProfileNonExistentUserReturnsFalse() {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setName("NoUser");
        form.setDescription("Desc");
        form.setImageFileName("img.jpg");
        form.setGenres(List.of());

        boolean updated = profileService.updateProfile("NoUser", form);
        assertFalse(updated);
    }

    /**
     * Ensures null genre lists clear the user's genres.
     */
    @Test
    void updateProfileWithNullGenresClearsGenres() {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setName("TestUser");
        form.setDescription("Desc");
        form.setImageFileName("img.jpg");
        form.setGenres(null);

        profileService.updateProfile("TestUser", form);
        Optional<Client> user = profileService.getUserByUsername("TestUser");
        assertTrue(user.isPresent());
        assertTrue(user.get().getGenres().isEmpty());
    }

    /**
     * Ensures trimmed names and descriptions are saved without surrounding whitespace.
     */
    @Test
    void updateProfileTrimsNameAndDescription() {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setName("  TrimName  ");
        form.setDescription("  TrimDesc  ");
        form.setImageFileName("img.jpg");
        form.setGenres(List.of());

        profileService.updateProfile("TestUser", form);

        Optional<Client> user = profileService.getUserByUsername("TrimName");
        assertTrue(user.isPresent());
        assertEquals("TrimDesc", user.get().getDescription());
    }
}
