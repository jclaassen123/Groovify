package com.groovify.service;

import org.springframework.stereotype.Service;
import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.LoginRepository;
import java.util.List;

/**
 * Implementation of {@link LoginService} that provides user authentication functionality.
 * <p>
 * This service is responsible for validating user credentials against the database.
 * It uses {@link LoginRepository} to perform queries on the {@link Users} entity.
 * </p>
 */
@Service
public class LoginServiceImpl implements LoginService {

    /** Repository used to query user data from the database. */
    private final LoginRepository loginRepo;

    /**
     * Constructs a {@code LoginServiceImpl} with the given {@link LoginRepository}.
     *
     * @param loginRepo the repository used for accessing user data
     */
    public LoginServiceImpl(LoginRepository loginRepo) {
        this.loginRepo = loginRepo;
    }

    /**
     * Validates a user's login credentials.
     *
     * <p>This method checks if a user exists with the given username (case-insensitive)
     * and verifies that the provided password matches the stored password.</p>
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return {@code true} if the username exists and the password matches; {@code false} otherwise
     */
    @Override
    public boolean validateUser(String username, String password) {
        // Find users matching the username (case-insensitive)
        List<Users> users = loginRepo.findByNameIgnoreCase(username);

        if (users.isEmpty()) {
            return false;
        }

        Users u = users.get(0);
        return u.getPassword().equals(password);
    }
}

