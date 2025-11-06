package com.groovify.jpa.model;

import jakarta.persistence.*;

/**
 * Entity representing a Song in the system.
 * <p>
 * Maps to the "Song" table in the database. Each song has a filename, title,
 * artist, and an optional reference to a genre.
 */
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

    @Column(name = "Genre_ID", insertable = false, updatable = false)
    private Long genreId;

    /**
     * Many-to-one relationship to Genre.
     * <p>
     * Optional: a song may belong to a genre.
     */
    @ManyToOne
    @JoinColumn(name = "Genre_ID")
    private Genre genre;

    /**
     * Default constructor required by JPA.
     */
    public Song() {}

    /**
     * Constructs a Song with the specified filename, title, and artist.
     * <p>
     * The genre is left null and can be set later.
     *
     * @param filename the filename of the song
     * @param title    the title of the song
     * @param artist   the artist of the song
     */
    public Song(String filename, String title, String artist) {
        this.filename = filename;
        this.title = title;
        this.artist = artist;
        this.genre = null;
    }

    // ------------------ Getters ------------------

    public Long getId() {
        return id;
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

    public Long getGenreId() {
        return genreId;
    }

    public Genre getGenre() {
        return genre;
    }

    // ------------------ Setters ------------------

    /**
     * Sets the genre ID for this song.
     *
     * @param genreId the ID of the genre to associate with this song
     */
    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    /**
     * Sets the genre object for this song.
     *
     * @param genre the Genre to associate with this song
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
