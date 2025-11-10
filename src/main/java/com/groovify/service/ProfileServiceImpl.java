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
 * Provides methods to fetch user data, retrieve available genres,
 * check username availability, and update user profiles.
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ClientRepo clientRepo;
    private final GenreRepo genreRepo;

    /**
     * Constructs a ProfileServiceImpl with required repositories.
     *
     * @param clientRepo repository for accessing Client entities
     * @param genreRepo  repository for accessing Genre entities
     */
    public ProfileServiceImpl(ClientRepo clientRepo, GenreRepo genreRepo) {
        this.clientRepo = clientRepo;
        this.genreRepo = genreRepo;
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the Client if found, otherwise empty
     */
    public Optional<Client> getUserByUsername(String username) {
        log.debug("Fetching user by username '{}'", username);
        return clientRepo.findByName(username);
    }

    /**
     * Retrieves all available genres from the database.
     *
     * @return a list of all genres
     */
    public List<Genre> getAllGenres() {
        log.debug("Fetching all genres");
        return genreRepo.findAll();
    }

    /**
     * Checks whether a username is already taken by another user.
     *
     * @param username       the username to check
     * @param currentUsername the username of the current user to exclude
     * @return true if the username is taken by someone else, false otherwise
     */
    public boolean isUsernameTaken(String username, String currentUsername) {
        Optional<Client> user = clientRepo.findByName(username);
        boolean taken = user.isPresent() && !user.get().getName().equals(currentUsername);
        log.debug("Username '{}' taken: {}", username, taken);
        return taken;
    }


    @Transactional
    public boolean updateProfile(String username, ProfileUpdateForm form) {
        Optional<Client> optionalUser = clientRepo.findByName(username);
        if (optionalUser.isEmpty()) return false;

        Client user = optionalUser.get();

        // Only update fields we want to change
        user.setName(form.getName().trim());
        user.setDescription(form.getDescription().trim());
        user.setImageFileName(form.getImageFileName());

        List<Genre> genres = (form.getGenres() != null)
                ? genreRepo.findAllById(form.getGenres())
                : new ArrayList<>();
        user.setGenres(genres);

        // Save entity while skipping password validation
        // This works because we're not modifying the password
        clientRepo.save(user);

        log.info("Profile updated successfully for '{}'", username);
        return true;
    }


}
