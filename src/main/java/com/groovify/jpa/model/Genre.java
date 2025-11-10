package com.groovify.jpa.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing a music genre.
 * <p>
 * Maps to the "Genre" table in the database. Each genre has a unique name
 * and can be associated with multiple clients (users) via a many-to-many relationship.
 */
@Entity
@Table(name = "Genre")
public class Genre {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Client> clients;

    /**
     * Default constructor required by JPA.
     */
    public Genre() {}

    /**
     * Constructs a Genre with the given name.
     *
     * @param name the name of the genre
     */
    public Genre(String name) {
        this.name = name;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of clients associated with this genre.
     *
     * @return list of clients
     */
    public List<Client> getClient() {
        return clients;
    }
}
