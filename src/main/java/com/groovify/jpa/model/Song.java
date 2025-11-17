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
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "Filename")
    private String filename;

    @Column(name = "Title")
    private String title;

    @Column(name = "Artist")
    private String artist;

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
     * <p>
     * Creates an empty Song instance with no initialized fields.
     */
    public Song() {}

    /**
     * Constructs a Song with the specified filename, title, and artist.
     * <p>
     * The genre is not set by default and can be assigned later.
     *
     * @param filename the .mp3 filename of the song
     * @param title    the title of the song
     * @param artist   the first and last name of the artist
     */
    public Song(String filename, String title, String artist) {
        this.filename = filename;
        this.title = title;
        this.artist = artist;
        this.genre = null;
    }

    // ------------------ Getters ------------------

    /**
     * Returns the unique identifier for this song.
     *
     * @return the song's database ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the filename of the song.
     * <p>
     * The filename typically refers to the song's .mp3 file on disk.
     *
     * @return the filename of the song
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns the title of the song.
     * <p>
     * This is the official name or title by which the song is known.
     *
     * @return the title of the song
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the artist of the song.
     * <p>
     * The artist name is stored as the first and last name of the performer.
     *
     * @return the artist's full name
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Returns the genre associated with this song.
     * <p>
     * May return {@code null} if the song has not been assigned a genre.
     *
     * @return the genre of the song, or {@code null} if not set
     */
    public Genre getGenre() {
        return genre;
    }

    // ------------------ Setters ------------------

    /**
     * Sets the genre object for this song.
     * <p>
     * This associates the song with a specific musical genre (e.g., Rock, Pop, Jazz).
     *
     * @param genre the {@link Genre} to associate with this song
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    /**
     * Sets the unique identifier for this song.
     * <p>
     * This method is primarily used by JPA during entity persistence operations.
     *
     * @param id the unique database ID of this song
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the title for this song.
     * @param title
     */
    public void setTitle(String title) {this.title = title;}

    // ------------------ Object Overrides ------------------

    /**
     * Compares this Song to another object for equality.
     * <p>
     * Two songs are considered equal if they have the same non-null database ID.
     *
     * @param o the object to compare with
     * @return {@code true} if both objects represent the same Song; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        return id != null && id.equals(((Song) o).id);
    }

    /**
     * Returns a constant hash code for this entity.
     * <p>
     * The hash code is based on a fixed constant for JPA entity identity.
     *
     * @return a constant integer hash code
     */
    @Override
    public int hashCode() {
        return 31;
    }
}
