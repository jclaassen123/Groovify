package com.groovify.web.dto;

public class SongView {
    private String title;
    private String artist;
    private String genreName;

    public SongView(String title, String artist, String genreName) {
        this.title = title;
        this.artist = artist;
        this.genreName = genreName;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getGenreName() { return genreName; }
}
