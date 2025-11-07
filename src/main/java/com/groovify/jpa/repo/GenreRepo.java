package com.groovify.jpa.repo;

import com.groovify.jpa.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for accessing {@link Genre} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 * Adds a method to find a genre by its name.
 */
public interface GenreRepo extends JpaRepository<Genre, Long> {

    /**
     * Finds a genre by its name.
     *
     * @param name the name of the genre
     * @return an Optional containing the Genre if found, otherwise empty
     */
    Optional<Genre> findByName(String name);
}
