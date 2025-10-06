package com.groovify.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Songs")
public class Songs {
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
    @Column(name = "Length")
    private Integer length;

    /*
    TODO
    Might add
        Year
        Album
     */


    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Integer getLength() {
        return length;
    }
}
