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
 * Each subfolder in the configured music directory represents a genre.
 * MP3 files within those subfolders are imported into the database,
 * assigned a random artist, and linked to the corresponding genre.
 */
@Service
public class SongImportImpl implements SongImportService {

    private static final Logger log = LoggerFactory.getLogger(SongImportImpl.class);

    private final SongService songService;
    private final GenreRepo genreRepository;
    private final Random random = new Random();

    /**
     * Path to the root music directory (configurable in application.properties).
     * Defaults to {@code src/main/resources/static/songs}.
     */
    @Value("${music.directory:src/main/resources/static/songs}")
    String musicDirectory;

    // Predefined lists used to generate random artist names
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
     * Constructs a {@code SongImportImpl} with required repositories.
     *
     * @param songService  repository for accessing {@link Song} entities
     * @param genreRepository repository for accessing {@link Genre} entities
     */
    public SongImportImpl(SongService songService, GenreRepo genreRepository) {
        this.songService = songService;
        this.genreRepository = genreRepository;
    }

    /**
     * Imports songs from the configured music directory.
     * <ul>
     *     <li>Validates the directory structure.</li>
     *     <li>Iterates through genre folders.</li>
     *     <li>Processes MP3 files and saves them in the database.</li>
     * </ul>
     *
     * @return true if import completes successfully, false otherwise
     */
    @Override
    public boolean importSongs() {
        File songsRoot = new File(musicDirectory);

        // Step 1: Validate the music root directory
        if (!isValidMusicDirectory(songsRoot)) {
            return false;
        }

        // Step 2: Retrieve all genre subfolders
        File[] genreFolders = getGenreFolders(songsRoot);
        if (genreFolders == null || genreFolders.length == 0) {
            log.warn("No genre folders found in '{}'", songsRoot.getAbsolutePath());
            return false;
        }

        // Step 3: Process each genre folder
        for (File genreFolder : genreFolders) {
            processGenreFolder(genreFolder);
        }

        log.info("Song import completed successfully.");
        return true;
    }

    // -------------------------------------------------------
    // HELPER METHODS
    // -------------------------------------------------------

    /**
     * Validates that the given music directory exists and is a directory.
     */
    private boolean isValidMusicDirectory(File songsRoot) {
        if (!songsRoot.exists() || !songsRoot.isDirectory()) {
            log.error("Music directory not found or invalid: '{}'", songsRoot.getAbsolutePath());
            return false;
        }
        log.info("Located music directory: '{}'", songsRoot.getAbsolutePath());
        return true;
    }

    /**
     * Retrieves subdirectories (each representing a genre) within the root music directory.
     */
    private File[] getGenreFolders(File songsRoot) {
        return songsRoot.listFiles(File::isDirectory);
    }

    /**
     * Processes a single genre folder:
     * <ul>
     *     <li>Looks up the genre in the database.</li>
     *     <li>Finds MP3 files in the folder.</li>
     *     <li>Imports each song into the system.</li>
     * </ul>
     *
     * @param genreFolder the folder representing a specific genre
     */
    private void processGenreFolder(File genreFolder) {
        String genreName = genreFolder.getName();
        Genre genre = genreRepository.findByName(genreName).orElse(null);

        if (genre == null) {
            log.warn("Genre '{}' not found in database — skipping folder '{}'", genreName, genreFolder.getName());
            return;
        }

        File[] mp3Files = genreFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        if (mp3Files == null || mp3Files.length == 0) {
            log.warn("No MP3 files found in '{}'", genreFolder.getAbsolutePath());
            return;
        }

        for (File file : mp3Files) {
            processSongFile(file, genre);
        }
    }

    /**
     * Processes an individual MP3 file:
     * <ul>
     *     <li>Skips files already in the database.</li>
     *     <li>Attempts to read MP3 metadata.</li>
     *     <li>Creates and saves a {@link Song} entity.</li>
     * </ul>
     *
     * @param file  the MP3 file to import
     * @param genre the genre to associate with the song
     */
    private void processSongFile(File file, Genre genre) {
        // Skip if song already exists
        if (songService.searchSongByFilename(file.getName())) {
            log.debug("Skipping '{}': already exists in database.", file.getName());
            return;
        }

        try {
            // Parse MP3 file (could later be used for metadata extraction)
            new Mp3File(file);

            // Derive song details
            String title = formatTitle(file.getName());
            String artist = generateRandomArtist();

            // Build and save song entity
            Song song = new Song(file.getName(), title, artist);
            song.setGenre(genre);
            songService.addSong(song);

            log.info("Imported '{}': Title='{}', Artist='{}', Genre='{}'",
                    file.getName(), title, artist, genre.getName());
        } catch (Exception e) {
            log.error("Failed to read MP3 file '{}': {}", file.getName(), e.getMessage(), e);
        }
    }

    /**
     * Generates a random artist name using a random first and last name.
     */
    private String generateRandomArtist() {
        String first = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String last = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        return first + " " + last;
    }

    /**
     * Formats a song title from the file name by:
     * <ul>
     *     <li>Removing the .mp3 extension</li>
     *     <li>Adding spaces before capital letters</li>
     * </ul>
     */
    private String formatTitle(String filename) {
        return filename.replace(".mp3", "")
                .replaceAll("(?<!^)(?=[A-Z])", " ")
                .trim();
    }
}
