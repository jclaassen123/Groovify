package com.groovify.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Genres", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Constructors
    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

    // Getters & Setters
    public Long getID() { return ID; }
    public void setID(Long ID) { this.ID = ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
