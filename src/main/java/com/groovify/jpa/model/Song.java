package com.groovify.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Songs")
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
    @Column(name = "Album")
    private String album;
    @Column(name = "Year")
    private Integer year;
    @Column(name = "Genre_ID")
    private Long genreId;

    // TODO Images

    public Song() {}

    public Song(String filename, String title, String artist, String album, int year) {
        this.filename = filename;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
    }

    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() { return album; }

    public int getYear() { return year; }

    public long getGenreId() { return genreId; }

}
