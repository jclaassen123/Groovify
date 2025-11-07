package com.groovify.web.dto;

public class SongView {
    private Long id;
    private String title;
    private String artist;
    private String genreName;

    public SongView(Long id, String title, String artist, String genreName) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genreName = genreName;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getGenreName() { return genreName; }
    public String getFilename() {
        // remove all spaces for filename
        return title.replaceAll("\\s+", "") + ".mp3";
    }
}
