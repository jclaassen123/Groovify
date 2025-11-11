package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.web.form.ProfileUpdateForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing user profiles.
 * <p>
 * Provides concrete implementations of {@link ProfileService} methods
 * for retrieving user information, listing genres, checking username availability,
 * and updating user profile data.
 * </p>
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ClientRepo clientRepo;
    private final GenreRepo genreRepo;

    /**
     * Constructs a {@code ProfileServiceImpl} with the required repositories.
     *
     * @param clientRepo repository for accessing {@link Client} entities
     * @param genreRepo  repository for accessing {@link Genre} entities
     */
    public ProfileServiceImpl(ClientRepo clientRepo, GenreRepo genreRepo) {
        this.clientRepo = clientRepo;
        this.genreRepo = genreRepo;
    }

    /**
     * Retrieves a user by their username.
     * <p>
     * Delegates to the {@link ClientRepo} to fetch a {@link Client} entity
     * matching the provided username.
     * </p>
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the {@link Client} if found,
     *         otherwise an empty {@link Optional}
     */
    @Override
    public Optional<Client> getUserByUsername(String username) {
        log.debug("Fetching user by username '{}'", username);
        return clientRepo.findByName(username);
    }

    /**
     * Retrieves all available genres from the database.
     * <p>
     * Useful for populating dropdowns or selection lists where users
     * can choose their favorite genres.
     * </p>
     *
     * @return a {@link List} of all {@link Genre} entities
     */
    @Override
    public List<Genre> getAllGenres() {
        log.debug("Fetching all genres");
        return genreRepo.findAll();
    }

    /**
     * Checks whether a username is already taken by another user.
     * <p>
     * This method ensures username uniqueness across all clients.
     * The currently logged-in user's own username is excluded from the check.
     * </p>
     *
     * @param username        the username to verify
     * @param currentUsername the username of the current user (excluded from comparison)
     * @return {@code true} if the username is already in use by another user,
     *         {@code false} otherwise
     */
    @Override
    public boolean isUsernameTaken(String username, String currentUsername) {
        Optional<Client> user = clientRepo.findByName(username);
        boolean taken = user.isPresent() && !user.get().getName().equals(currentUsername);
        log.debug("Username '{}' taken: {}", username, taken);
        return taken;
    }

    /**
     * Updates the profile information for the specified user.
     * <p>
     * Applies the updated values from the {@link ProfileUpdateForm}
     * to the user's {@link Client} entity. Fields such as name, description,
     * profile image, and preferred genres are updated. Passwords are not modified
     * as part of this operation.
     * </p>
     *
     * <p>
     * The method is annotated with {@link Transactional} to ensure that
     * all updates are applied atomically. If any operation fails,
     * changes will be rolled back automatically.
     * </p>
     *
     * @param username the username of the profile being updated
     * @param form     the form containing updated profile data
     * @return {@code true} if the update succeeded, {@code false} if the user was not found
     */
    @Override
    @Transactional
    public boolean updateProfile(String username, ProfileUpdateForm form) {
        log.debug("Attempting to update profile for '{}'", username);

        Optional<Client> optionalUser = clientRepo.findByName(username);
        if (optionalUser.isEmpty()) {
            log.warn("User '{}' not found — profile update aborted", username);
            return false;
        }

        Client user = optionalUser.get();

        // Update relevant profile fields
        user.setName(form.getName().trim());
        user.setDescription(form.getDescription().trim());
        user.setImageFileName(form.getImageFileName());

        // Map selected genre IDs to actual Genre entities
        List<Genre> genres = (form.getGenres() != null)
                ? genreRepo.findAllById(form.getGenres())
                : new ArrayList<>();
        user.setGenres(genres);

        // Save updated entity
        clientRepo.save(user);

        log.info("Profile updated successfully for '{}'", username);
        return true;
    }
}
