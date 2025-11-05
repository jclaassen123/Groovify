package com.groovify.service;

import com.groovify.jpa.model.Song;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.SongRepo;
import com.groovify.jpa.repo.GenreRepo;
import com.mpatric.mp3agic.Mp3File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * Service implementation for importing songs from a directory structure.
 * <p>
 * Each subfolder in the music directory represents a genre. MP3 files in these folders
 * are imported into the database, with random artist names generated for each song.
 */
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

    /**
     * Constructs a SongImportImpl with required repositories.
     *
     * @param songRepository  repository for accessing Song entities
     * @param genreRepository repository for accessing Genre entities
     */
    public SongImportImpl(SongRepo songRepository, GenreRepo genreRepository) {
        this.songRepository = songRepository;
        this.genreRepository = genreRepository;
    }

    /**
     * Imports songs from the configured music directory.
     * <ul>
     *     <li>Each subfolder is treated as a genre.</li>
     *     <li>MP3 files are read, assigned a random artist, and saved in the database.</li>
     *     <li>Existing files or missing genres are skipped with logging.</li>
     * </ul>
     */
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
                    log.warn("File '{}' already exists, skipping", file.getName());
                    continue;
                }

                try {
                    Mp3File mp3 = new Mp3File(file); // Can be used for future metadata extraction
                    String title = formatTitle(file.getName());
                    String artist = generateRandomArtist();

                    Song song = new Song(file.getName(), title, artist);
                    song.setGenreId(genre.getId());

                    songRepository.save(song);
                    log.info("Imported '{}': '{}', Artist='{}', Genre='{}'",
                            file.getName(), title, artist, genreName);

                } catch (Exception e) {
                    log.error("Error reading '{}': {}", file.getName(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Generates a random artist name using predefined first and last name lists.
     *
     * @return a random artist name
     */
    private String generateRandomArtist() {
        String first = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String last = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        return first + " " + last;
    }

    /**
     * Formats a song title from a filename by removing the extension
     * and inserting spaces before capital letters.
     *
     * @param filename the filename of the song
     * @return the formatted title
     */
    private String formatTitle(String filename) {
        return filename.replace(".mp3", "")
                .replaceAll("(?<!^)(?=[A-Z])", " ")
                .trim();
    }
}
