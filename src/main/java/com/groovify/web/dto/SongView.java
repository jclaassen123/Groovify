package com.groovify.web.dto;

/**
 * Data Transfer Object (DTO) for representing a song in views.
 * <p>
 * Contains the song ID, title, artist, genre name, and filename.
 * Provides helper methods to generate or retrieve values for use in templates.
 */
public record SongView(Long id, String title, String artist, String genreName, String filename) {
    /**
     * Constructs a SongView DTO with the given ID, title, artist, genre, and filename.
     *
     * @param id        the unique ID of the song
     * @param title     the title of the song
     * @param artist    the name of the artist
     * @param genreName the name of the genre
     * @param filename  the filename of the song
     */
    public SongView {
    }

    /**
     * @return the song ID
     */
    @Override
    public Long id() {
        return id;
    }

    /**
     * @return the song title
     */
    @Override
    public String title() {
        return title;
    }

    /**
     * Returns the filename of the song for use in templates.
     * <p>
     * Can be used in Thymeleaf as ${song.filename}.
     *
     * @return the song filename
     */
    @Override
    public String filename() {
        return filename;
    }
}
