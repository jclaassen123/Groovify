package com.groovify.service;

import com.groovify.jpa.model.Songs;
import com.groovify.jpa.repo.SongsRepo;
import com.mpatric.mp3agic.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class MusicImport {

    private final SongsRepo songRepository;

    @Value("${music.directory:src/main/resources/static/songs}")
    private String musicDirectory;

    public MusicImport(SongsRepo songRepository) {
        this.songRepository = songRepository;
    }

    public void importSongs() {
        File folder = new File(musicDirectory);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Music folder not found: " + folder.getAbsolutePath());
            return;
        }

        for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".mp3")))) {
            try {
                Mp3File mp3 = new Mp3File(file);
                String title = file.getName();
                String artist = "Unknown";
                String album = "Unknown";
                int duration = (int) mp3.getLengthInSeconds();

                if (mp3.hasId3v2Tag()) {
                    ID3v2 tag = mp3.getId3v2Tag();
                    title = tag.getTitle() != null ? tag.getTitle() : title;
                    artist = tag.getArtist() != null ? tag.getArtist() : artist;
                    album = tag.getAlbum() != null ? tag.getAlbum() : album;
                } else if (mp3.hasId3v1Tag()) {
                    ID3v1 tag = mp3.getId3v1Tag();
                    title = tag.getTitle() != null ? tag.getTitle() : title;
                    artist = tag.getArtist() != null ? tag.getArtist() : artist;
                    album = tag.getAlbum() != null ? tag.getAlbum() : album;
                }

                Songs song = new Songs(file.getName(), title, artist, album, duration);
                songRepository.save(song);
                System.out.println("✅ Imported: " + title + " by " + artist);

            } catch (Exception e) {
                System.err.println("❌ Error reading " + file.getName() + ": " + e.getMessage());
            }
        }
    }
}

