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
import java.util.List;
import java.util.Random;

@Service
public class SongImportImpl implements SongImportService {
    private static final Logger log = LoggerFactory.getLogger(SongImportImpl.class);

    private final SongRepo songRepository;
    private final GenreRepo genreRepository;
    private final Random random = new Random();

    @Value("${music.directory:src/main/resources/static/songs}")
    private String musicDirectory;

    // Lists of first and last names for generating artist names
    private static final List<String> FIRST_NAMES = List.of(
            "Liam", "Olivia", "Noah", "Emma", "Oliver",
            "Ava", "Elijah", "Sophia", "Lucas", "Isabella",
            "Mason", "Mia", "Ethan", "Charlotte", "Logan"
    );

    private static final List<String> LAST_NAMES = List.of(
            "Smith", "Johnson", "Brown", "Taylor", "Anderson",
            "Thomas", "Jackson", "White", "Harris", "Martin",
            "Thompson", "Garcia", "Martinez", "Robinson", "Clark"
    );

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
                    String artist = generateRandomArtist();

                    Song song = new Song(file.getName(), title, artist);
                    song.setGenreId(genre.getId());

                    songRepository.save(song);
                    log.info("Imported '{}': '{}', Artist='{}', Genre='{}'",
                            file.getName(), title, artist, genreName);

                } catch (Exception e) {
                    log.error("Error reading '{}' '{}'", file.getName(), e.getMessage());
                }
            }
        }
    }

    private String generateRandomArtist() {
        String first = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String last = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        return first + " " + last;
    }

    private String formatTitle(String filename) {
        return filename.replace(".mp3", "")
                .replaceAll("(?<!^)(?=[A-Z])", " ")
                .trim();
    }
}