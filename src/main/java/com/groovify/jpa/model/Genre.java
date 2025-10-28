package com.groovify.jpa.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Genre")
public class Genre {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "Genre")
    private List<Client> clients;

    public Genre() {}
    public Genre(String name) { this.name = name; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Client> getClient() { return clients; }
    public void setClients(List<Client> users) { this.clients = users; }
}