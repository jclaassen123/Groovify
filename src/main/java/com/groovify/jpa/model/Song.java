package com.groovify.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Filename")
    private String filename;
    @Column(name = "Title")
    private String title;
    @Column(name = "Artist")
    private String artist;
    @Column(name = "Genre_ID")
    private Long genreId;

    public Song() {}

    public Song(String filename, String title, String artist) {
        this.filename = filename;
        this.title = title;
        this.artist = artist;
        this.genreId = null;
    }

    // Getters
    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Long getGenreId() {
        return genreId;
    }

    // Setter for Genre ID
    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
}