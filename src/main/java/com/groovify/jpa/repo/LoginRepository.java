package com.groovify.jpa.repo;

import java.util.List;

import com.groovify.jpa.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for handling login-related database operations for {@link Users}.
 * <p>
 * This interface extends {@link JpaRepository}, providing basic CRUD functionality.
 * It includes a custom query method for case-insensitive username lookup, which
 * supports user authentication without requiring exact case matching.
 */
public interface LoginRepository extends JpaRepository<Users, Integer> {
    /**
     * Finds users by username, ignoring case sensitivity.
     * <p>
     * Although usernames are expected to be unique, this method returns a {@link List}
     * to safely handle situations where no users or multiple users are found, avoiding
     * potential exceptions.
     *
     * @param name the username to search for (case-insensitive)
     * @return a {@link List} of matching {@link Users} entities, which will typically
     *         contain either zero or one element
     */
    List<Users> findByNameIgnoreCase(String name);
}
