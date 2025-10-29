package com.groovify.service;

import com.groovify.jpa.model.Song;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.SongRepo;
import com.groovify.jpa.repo.GenreRepo;
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
    private final GenreRepo genreRepository;

    @Value("${music.directory:src/main/resources/static/songs}")
    private String musicDirectory;

    public SongImportImpl(SongRepo songRepository, GenreRepo genreRepository) {
        this.songRepository = songRepository;
        this.genreRepository = genreRepository;
    }

    public void importSongs() {
        File songsRoot = new File(musicDirectory);
        if (!songsRoot.exists() || !songsRoot.isDirectory()) {
            log.error("Music directory not found: '{}'", songsRoot.getAbsolutePath());
            return;
        }

        // Iterate over genre folders
        File[] genreFolders = songsRoot.listFiles(File::isDirectory);
        if (genreFolders == null || genreFolders.length == 0) {
            log.warn("No genre folders found in '{}'", songsRoot.getAbsolutePath());
            return;
        }

        for (File genreFolder : genreFolders) {
            String genreName = genreFolder.getName();
            Genre genre = genreRepository.findByName(genreName).orElse(null);
            if (genre == null) {
                log.warn("Genre '{}' not found in database, skipping folder", genreName);
                continue;
            }

            File[] mp3Files = genreFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
            if (mp3Files == null || mp3Files.length == 0) {
                log.warn("No MP3 files found in '{}'", genreFolder.getAbsolutePath());
                continue;
            }

            for (File file : mp3Files) {
                if (songRepository.existsByFilename(file.getName())) {
                    log.warn("File '{}' already exists", file.getName());
                    continue;
                }

                try {
                    Mp3File mp3 = new Mp3File(file);
                    String title = formatTitle(file.getName());
                    String artist = "Unknown";
                    String album = "Unknown";
                    int year = 0;

                    if (mp3.hasId3v2Tag()) {
                        ID3v2 tag = mp3.getId3v2Tag();
                        title = safeString(tag.getTitle(), title);
                        artist = safeString(tag.getArtist(), artist);
                        album = safeString(tag.getAlbum(), album);
                        year = parseYear(tag.getYear());
                    } else if (mp3.hasId3v1Tag()) {
                        ID3v1 tag = mp3.getId3v1Tag();
                        title = safeString(tag.getTitle(), title);
                        artist = safeString(tag.getArtist(), artist);
                        album = safeString(tag.getAlbum(), album);
                        year = parseYear(tag.getYear());
                    }

                    Song song = new Song(file.getName(), title, artist, album, year);
                    song.setGenreId(genre.getId()); // Set genre ID from folder match
                    songRepository.save(song);
                    log.info("Imported '{}': '{}', Genre='{}'", file.getName(), title, genreName);

                } catch (Exception e) {
                    log.error("Error reading '{}' '{}'", file.getName(), e.getMessage());
                }
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

    private String formatTitle(String filename) {
        // Remove .mp3
        String name = filename.replace(".mp3", "");
        // Insert spaces before capital letters
        return name.replaceAll("(?<!^)(?=[A-Z])", " ").trim();
    }
}