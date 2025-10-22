package com.groovify.service;

import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;
import com.groovify.web.controller.LandingController;
import com.mpatric.mp3agic.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

@Service
public class SongImportImpl implements SongImportService {
    private static final Logger log = LoggerFactory.getLogger(SongImportImpl.class);

    private final SongRepo songRepository;

    // The folder where your MP3 files live
    @Value("${music.directory:src/main/resources/static/songs}")
    private String musicDirectory;

    public SongImportImpl(SongRepo songRepository) {
        this.songRepository = songRepository;
    }

    public void importSongs() {
        File folder = new File(musicDirectory);
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Music directory not found: '{}'", folder.getAbsolutePath());
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        if (files == null || files.length == 0) {
            log.warn("No MP3 files found in '{}'", folder.getAbsolutePath());
            return;
        }

        for (File file : Objects.requireNonNull(files)) {
            if (songRepository.existsByFilename(file.getName())) {
                log.warn("File '{}' already exists", file.getName());
                continue;
            }

            try {
                log.info("Importing '{}'", file.getName());
                Mp3File mp3 = new Mp3File(file);
                String title = file.getName();
                String artist = "Unknown";
                String album = "Unknown";
                int year = 0;

                if (mp3.hasId3v2Tag()) {
                    log.debug("Found ID3v2Tag: {}", mp3.getId3v2Tag());
                    ID3v2 tag = mp3.getId3v2Tag();
                    title = safeString(tag.getTitle(), title);
                    artist = safeString(tag.getArtist(), artist);
                    album = safeString(tag.getAlbum(), album);
                    year = parseYear(tag.getYear());
                } else if (mp3.hasId3v1Tag()) {
                    log.debug("Found ID3v1Tag: {}", mp3.getId3v1Tag());
                    ID3v1 tag = mp3.getId3v1Tag();
                    title = safeString(tag.getTitle(), title);
                    artist = safeString(tag.getArtist(), artist);
                    album = safeString(tag.getAlbum(), album);
                    year = parseYear(tag.getYear());
                }

                log.info("Song info: '{}': '{}'", title, artist);
                Song song = new Song(file.getName(), title, artist, album, year);
                songRepository.save(song);
                log.info("Imported: '{}'", file.getName());

            } catch (Exception e) {
                log.error("Error reading '{}' '{}'", file.getName(), e.getMessage());
            }
        }
    }

    private String safeString(String value, String fallback) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : fallback;
    }

    private int parseYear(String yearString) {
        try {
            return (yearString != null && !yearString.isBlank())
                    ? Integer.parseInt(yearString.replaceAll("\\D+", ""))
                    : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}