package com.groovify.web.dto;

/**
 * Data Transfer Object (DTO) for representing a song in views.
 * <p>
 * Contains the song title, artist, genre name, and provides a helper
 * method to generate the filename for the song.
 */
public class SongView {
    private Long id;
    private String title;
    private String artist;
    private String genreName;

    /**
     * Constructs a SongView DTO with the given title, artist, and genre.
     *
     * @param title     the title of the song
     * @param artist    the name of the artist
     * @param genreName the name of the genre
     */
    public SongView(Long id, String title, String artist, String genreName) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genreName = genreName;
    }

    /**
     *
     * @return the song id
     */
    public Long getId() {
        return id;
    }

    /** @return the song title */
    public String getTitle() {
        return title;
    }
}
