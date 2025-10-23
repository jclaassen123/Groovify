package com.groovify.jpa.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "genre")
public class Genre {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Users> users;

    public Genre() {}
    public Genre(String name) { this.name = name; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Users> getUsers() { return users; }
    public void setUsers(List<Users> users) { this.users = users; }
}