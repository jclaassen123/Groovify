package com.groovify.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

@Transactional
@SpringBootTest
class GenreImportServiceImplTest {

    @Autowired
    private GenreImportService genreImportService;

    private String rock;
    private String pop;
    private String classical;
    private String tech;
    private String country;
    private String folk;

    @BeforeEach
    void setUp() {
        rock = "Rock";
        pop = "Pop";
        classical = "Classical";
        tech = "Tech";
        country = "Country";
        folk = "Folk";
    }

    /**
     * importGenres
     */

    // Happy Path
    @Test
    void importGenresValidTest() {
        List<String> genres = List.of(rock, pop, classical, tech, country, folk);

        genreImportService.importGenres(genres);

        for (String g : genres) {
            assertTrue("Genre should exist after import: " + g,
                    genreImportService.genreExists(g));
        }
    }

    @Test
    void importGenresValidTwiceTest() {
        List<String> genres = List.of(rock, pop);

        genreImportService.importGenres(genres);
        genreImportService.importGenres(genres); // duplicates should be ignored

        assertTrue("Rock should exist", genreImportService.genreExists(rock));
        assertTrue("Pop should exist", genreImportService.genreExists(pop));
    }

    // Crappy Path
    @Test
    void importGenresWithNullAndEmptyTest() {
        genreImportService.importGenres(Arrays.asList("Metal", null, "", "Hip-Hop"));

        assertTrue("Metal should exist", genreImportService.genreExists("Metal"));
        assertTrue("Hip-Hop should exist", genreImportService.genreExists("Hip-Hop"));
        assertFalse("Null should not exist", genreImportService.genreExists(null));
        assertFalse("Empty string should not exist", genreImportService.genreExists(""));
    }

    @Test
    void importGenresDuplicateInputTest() {
        genreImportService.importGenres(Arrays.asList("Reggae", "Reggae", "Jazz", "Jazz"));

        assertTrue("Reggae should exist", genreImportService.genreExists("Reggae"));
        assertTrue("Jazz should exist", genreImportService.genreExists("Jazz"));
    }

    /**
     * saveGenre
     */

    // Happy Path
    @Test
    void saveGenreValidTest() {
        assertTrue("Saving valid genre should succeed", genreImportService.saveGenre("Jazz"));
        assertTrue("Jazz should exist", genreImportService.genreExists("Jazz"));
    }

    @Test
    void saveGenreWithWhitespaceTest() {
        assertTrue("Saving genre with whitespace should succeed", genreImportService.saveGenre("  Indie  "));
        assertTrue("Trimmed Indie should exist", genreImportService.genreExists("Indie"));
    }

    // Crappy Path
    @Test
    void saveGenreNullTest() {
        assertFalse("Saving null should return false", genreImportService.saveGenre(null));
    }

    @Test
    void saveGenreEmptyStringTest() {
        assertFalse("Saving empty string should return false", genreImportService.saveGenre(""));
    }

    @Test
    void saveDuplicateGenreTest() {
        assertTrue("First save should succeed", genreImportService.saveGenre("Blues"));
        assertFalse("Duplicate save should fail", genreImportService.saveGenre("Blues"));
        assertTrue("Blues should still exist", genreImportService.genreExists("Blues"));
    }
}
