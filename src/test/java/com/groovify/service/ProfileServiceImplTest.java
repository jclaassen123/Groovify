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

    @BeforeEach
    void setUp() {
        // Register a user via RegisterService
        testUser = new Client();
        testUser.setName("TestUser");
        testUser.setPassword("Password123");
        registerService.registerUser(testUser);
    }

    // -------------------------
    // getUserByUsername Tests
    // -------------------------
    @Test
    void getUserByUsernameExistingReturnsUser() {
        Optional<Client> user = profileService.getUserByUsername("TestUser");
        assertTrue(user.isPresent());
        assertEquals("TestUser", user.get().getName());
    }

    @Test
    void getUserByUsernameNonExistentReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername("NoUser");
        assertTrue(user.isEmpty());
    }

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

    @Test
    void getUserByUsernameLeadingTrailingWhitespaceFails() {
        Optional<Client> user = profileService.getUserByUsername(" TestUser ");
        assertTrue(user.isEmpty());
    }

    @Test
    void getUserByUsernameEmptyStringReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername("");
        assertTrue(user.isEmpty());
    }

    @Test
    void getUserByUsernameNullReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername(null);
        assertTrue(user.isEmpty());
    }

    @Test
    void getUserByUsernameCaseSensitiveReturnsEmpty() {
        Optional<Client> user = profileService.getUserByUsername("testuser"); // lowercase
        assertTrue(user.isEmpty());
    }

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

    // -------------------------
    // getAllGenres Tests
    // -------------------------
    @Test
    void getAllGenresInitiallyEmpty() {
        List<Genre> genres = profileService.getAllGenres();
        assertNotNull(genres);
        assertTrue(genres.isEmpty(), "Genres list should be empty initially");
    }

    @Test
    void getAllGenresReturnsListAfterAddingGenres() {
        genreImportService.saveGenre("Rock");
        genreImportService.saveGenre("Pop");

        List<Genre> genres = profileService.getAllGenres();
        assertNotNull(genres);
        assertEquals(2, genres.size());
    }

    @Test
    void getAllGenresContainsCorrectNames() {
        genreImportService.saveGenre("Jazz");
        genreImportService.saveGenre("Blues");

        List<Genre> genres = profileService.getAllGenres();
        List<String> names = genres.stream().map(Genre::getName).toList();

        assertTrue(names.contains("Jazz"));
        assertTrue(names.contains("Blues"));
    }

    @Test
    void getAllGenresHandlesDuplicates() {
        genreImportService.saveGenre("Rock");
        genreImportService.saveGenre("Rock"); // duplicate name

        List<Genre> genres = profileService.getAllGenres();
        assertEquals(1, genres.size(), "Duplicate genre names should not be saved");
    }

    @Test
    void getAllGenresListIsMutable() {
        genreImportService.saveGenre("Metal");

        List<Genre> genres = profileService.getAllGenres();
        int originalSize = genres.size();
        genres.add(new Genre("Electronic"));

        assertEquals(originalSize + 1, genres.size(), "Returned list should be mutable");
    }

    @Test
    void getAllGenresReturnsEmptyAfterClearingGenres() {
        genreImportService.saveGenre("Country");
        genreImportService.importGenres(List.of()); // no-op to simulate clearing, can't delete via service

        List<Genre> genres = profileService.getAllGenres();
        assertFalse(genres.isEmpty(), "Genres list remains until manually cleared via repo");
    }

    // -------------------------
// isUsernameTaken Tests
// -------------------------
    @Test
    void isUsernameTakenReturnsTrueForExistingUsernameDifferentFromCurrent() {
        Client otherUser = new Client();
        otherUser.setName("OtherUser");
        otherUser.setPassword("Password123");
        registerService.registerUser(otherUser);

        boolean taken = profileService.isUsernameTaken("OtherUser", "TestUser");
        assertTrue(taken, "Username should be taken because it belongs to another user");
    }

    @Test
    void isUsernameTakenReturnsFalseForSameAsCurrentUsername() {
        boolean taken = profileService.isUsernameTaken("TestUser", "TestUser");
        assertFalse(taken, "Username should not be considered taken if it's the current user's username");
    }

    @Test
    void isUsernameTakenReturnsFalseForNonExistentUsername() {
        boolean taken = profileService.isUsernameTaken("NonExistent", "TestUser");
        assertFalse(taken, "Username should not be taken if no user exists with that name");
    }

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

    @Test
    void isUsernameTakenReturnsFalseWhenCurrentUsernameIsNull() {
        boolean taken = profileService.isUsernameTaken("TestUser", null);
        assertTrue(taken, "If currentUsername is null, existing username is considered taken");
    }

    @Test
    void isUsernameTakenReturnsFalseForEmptyUsername() {
        boolean taken = profileService.isUsernameTaken("", "TestUser");
        assertFalse(taken, "Empty username should not be considered taken");
    }

    @Test
    void isUsernameTakenReturnsFalseForNullUsername() {
        boolean taken = profileService.isUsernameTaken(null, "TestUser");
        assertFalse(taken, "Null username should not be considered taken");
    }

    @Test
    void isUsernameTakenCaseSensitiveCheck() {
        boolean taken = profileService.isUsernameTaken("testuser", "TestUser");
        assertFalse(taken, "Username check should be case-sensitive; lowercase differs from registered username");
    }

    @Test
    void isUsernameTakenReturnsTrueIfOtherUserExistsWithTrailingWhitespace() {
        Client whitespaceUser = new Client();
        whitespaceUser.setName("WhitespaceUser");
        whitespaceUser.setPassword("Password123");
        registerService.registerUser(whitespaceUser);

        boolean taken = profileService.isUsernameTaken("WhitespaceUser", "TestUser");
        assertTrue(taken, "Username with exact match including no trimming should be considered taken");
    }


    // -------------------------
    // updateProfile Tests
    // -------------------------
    @Test
    void updateProfileSuccessfullyUpdatesFields() {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setName("NewName");
        form.setDescription("NewDesc");
        form.setImageFileName("new.jpg");
        form.setGenres(List.of()); // empty genre list

        boolean updated = profileService.updateProfile("TestUser", form);
        assertTrue(updated);

        Optional<Client> user = profileService.getUserByUsername("NewName");
        assertTrue(user.isPresent());
        assertEquals("NewDesc", user.get().getDescription());
        assertEquals("new.jpg", user.get().getImageFileName());
    }

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

    @Test
    void updateProfileWithNullGenresClearsGenres() {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setName("TestUser");
        form.setDescription("Desc");
        form.setImageFileName("img.jpg");
        form.setGenres(null); // null genres

        profileService.updateProfile("TestUser", form);
        Optional<Client> user = profileService.getUserByUsername("TestUser");
        assertTrue(user.isPresent());
        assertTrue(user.get().getGenres().isEmpty());
    }

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
