package com.groovify.web.dto;

/**
 * Data Transfer Object (DTO) for representing a song in views.
 * <p>
 * Contains the song ID, title, artist, genre name, and filename.
 * Provides helper methods to generate or retrieve values for use in templates.
 */
public class SongView {
    private Long id;
    private String title;
    private String artist;
    private String genreName;
    private String filename;

    /**
     * Constructs a SongView DTO with the given ID, title, artist, genre, and filename.
     *
     * @param id        the unique ID of the song
     * @param title     the title of the song
     * @param artist    the name of the artist
     * @param genreName the name of the genre
     * @param filename  the filename of the song
     */
    public SongView(Long id, String title, String artist, String genreName, String filename) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genreName = genreName;
        this.filename = filename;
    }

    /** @return the song ID */
    public Long getId() {
        return id;
    }

    /** @return the song title */
    public String getTitle() {
        return title;
    }

    /** @return the artist name */
    public String getArtist() {
        return artist;
    }

    /** @return the genre name */
    public String getGenreName() {
        return genreName;
    }

    /**
     * Returns the filename of the song for use in templates.
     * <p>
     * Can be used in Thymeleaf as ${song.filename}.
     *
     * @return the song filename
     */
    public String getFilename() {
        return filename;
    }
}
