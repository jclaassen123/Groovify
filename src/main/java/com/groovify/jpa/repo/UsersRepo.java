package com.groovify.jpa.repo;

import com.groovify.jpa.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Users} entities.
 * <p>
 * This interface extends {@link JpaRepository}, which provides built-in methods
 * for interacting with the database (such as saving, deleting, and finding users).
 * <p>
 * The custom query method {@code findByName(String name)} allows lookup of users
 * by their unique username.
 */
public interface UsersRepo extends JpaRepository<Users, Long> {

    /**
     * Finds a {@link Users} entity by its username.
     *
     * @param name the username to search for
     * @return an {@link Optional} containing the user if found, or empty if no user exists with that name
     */
    Optional<Users> findByName(String name); // Useful for fetching a user by username
}
