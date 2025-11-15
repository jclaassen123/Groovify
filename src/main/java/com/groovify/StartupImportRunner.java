package com.groovify;

import com.groovify.service.GenreImportService;
import com.groovify.service.SongImportImpl;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for importing genres and songs immediately after application startup.
 * <p>
 * Uses {@link PostConstruct} to trigger the import logic as soon as the bean is initialized
 * and dependencies are injected.
 * </p>
 */
@Component
@ConditionalOnProperty(
        name = "groovify.import.genres.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class StartupImportRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupImportRunner.class);

    private final GenreImportService genreImportService;
    private final SongImportImpl songImportService;

    /**
     * Constructs the {@code StartupImportRunner} with required import services.
     *
     * @param genreImportService service for importing music genres
     * @param songImportService  service for importing MP3 song files
     */
    public StartupImportRunner(GenreImportService genreImportService, SongImportImpl songImportService) {
        this.genreImportService = genreImportService;
        this.songImportService = songImportService;
    }

    /**
     * Executes import operations immediately after the bean is initialized.
     * <p>
     * This ensures that genres and songs are imported automatically
     * as soon as the application starts up.
     * </p>
     */
    @PostConstruct
    public void runImports() {
        log.info("Starting genre import immediately after bean initialization...");
        genreImportService.importGenres(
                List.of("Rock", "Pop", "Classical", "Tech", "Country", "Folk")
        );
        log.info("Genre import complete.");

        log.info("Starting MP3 import immediately after bean initialization...");
        songImportService.importSongs();
        log.info("MP3 import complete.");
    }
}
