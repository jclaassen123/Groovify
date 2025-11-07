package com.groovify.jpa.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Playlist")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "Client_ID")
    private Long clientID;

    @ManyToMany
    @JoinTable(
            name = "Playlist_Song",
            joinColumns = @JoinColumn(name = "Playlist_ID"),
            inverseJoinColumns =  @JoinColumn(name = "Song_ID")
    )
    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setClientID(Long id) {
        this.clientID = id;
    }

    public Long getClientID() {
        return clientID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
