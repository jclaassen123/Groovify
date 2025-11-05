package com.groovify.web.dto;

/**
 * Data Transfer Object (DTO) for representing a song in views.
 * <p>
 * Contains the song title, artist, genre name, and provides a helper
 * method to generate the filename for the song.
 */
public class SongView {

    private final String title;
    private final String artist;
    private final String genreName;

    /**
     * Constructs a SongView DTO with the given title, artist, and genre.
     *
     * @param title     the title of the song
     * @param artist    the name of the artist
     * @param genreName the name of the genre
     */
    public SongView(String title, String artist, String genreName) {
        this.title = title;
        this.artist = artist;
        this.genreName = genreName;
    }

    /** @return the song title */
    public String getTitle() {
        return title;
    }

    /** @return the song artist */
    public String getArtist() {
        return artist;
    }

    /** @return the genre name */
    public String getGenreName() {
        return genreName;
    }

    /**
     * Generates a filename for the song by removing all whitespace
     * from the title and appending ".mp3".
     *
     * @return the song filename
     */
    public String getFilename() {
        // Remove all spaces for filename
        return title.replaceAll("\\s+", "") + ".mp3";
    }
}
