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
     * =====================================================
     * importGenres Tests
     * =====================================================
     */

    // ---------- Happy Path (Good Tests) ----------
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
        genreImportService.importGenres(genres);

        assertTrue("Rock should exist", genreImportService.genreExists(rock));
        assertTrue("Pop should exist", genreImportService.genreExists(pop));
    }

    @Test
    void importGenresTrimsWhitespaceTest() {
        genreImportService.importGenres(List.of("  Disco  "));

        assertTrue("Trimmed Disco should exist", genreImportService.genreExists("Disco"));
    }

    @Test
    void importGenresMixedValidInvalidTest() {
        List<String> input = Arrays.asList("Metal", null, "", "Blues", "  ");

        genreImportService.importGenres(input);

        assertTrue("Metal should exist", genreImportService.genreExists("Metal"));
        assertTrue("Blues should exist", genreImportService.genreExists("Blues"));
    }

    @Test
    void importGenresIgnoresExactDuplicatesTest() {
        List<String> input = Arrays.asList("Reggae", "Reggae", "Reggae");

        genreImportService.importGenres(input);

        assertTrue("Reggae should exist", genreImportService.genreExists("Reggae"));
    }

    @Test
    void importGenresEmptyListDoesNothingTest() {
        genreImportService.importGenres(List.of());

        assertFalse("Rock should not exist (not imported)", genreImportService.genreExists("Rock"));
    }

    // ---------- Crappy Path (Expected Fail Tests) ----------
    @Test
    void importGenresWithNullListTest() {
        genreImportService.importGenres(null);

        assertFalse("No genres should be imported", genreImportService.genreExists("Anything"));
    }

    @Test
    void importGenresWithOnlyInvalidValuesTest() {
        List<String> input = Arrays.asList(null, "", "  ");

        genreImportService.importGenres(input);

        assertFalse("Nothing valid should be saved", genreImportService.genreExists(""));
    }

    @Test
    void importGenresDoesNotCreateWhitespaceGenreRecordTest() {
        genreImportService.importGenres(Arrays.asList("   "));

        assertFalse("Whitespace should not exist", genreImportService.genreExists("   "));
    }

    @Test
    void importGenresDuplicateWithDifferentCaseDoesNotExistTest() {
        genreImportService.importGenres(List.of("Rock"));

        assertFalse("rock (lowercase) should not count as existing", genreImportService.genreExists("rock"));
    }

    @Test
    void importGenresCaseSensitiveDuplicateTest() {
        genreImportService.importGenres(List.of("EDM", "edm"));

        assertTrue("EDM should exist", genreImportService.genreExists("EDM"));
        assertTrue("edm should exist as separate entry", genreImportService.genreExists("edm"));
    }


    /**
     * =====================================================
     * saveGenre Tests
     * =====================================================
     */

    // ---------- Happy Path (Good Tests) ----------
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

    @Test
    void saveGenreCaseSensitiveDifferentNamesTest() {
        assertTrue("Saving Soul should succeed", genreImportService.saveGenre("Soul"));
        assertTrue("Saving soul should succeed", genreImportService.saveGenre("soul"));

        assertTrue("Soul should exist", genreImportService.genreExists("Soul"));
        assertTrue("soul should exist", genreImportService.genreExists("soul"));
    }

    @Test
    void saveGenreSuccessAfterOtherGenresExistTest() {
        genreImportService.saveGenre("Rock");
        assertTrue("Should still save different genre", genreImportService.saveGenre("Pop"));
    }

    @Test
    void saveGenreTrimsBeforeCheckingTest() {
        genreImportService.saveGenre("House");
        assertFalse("Trimmed duplicate should fail", genreImportService.saveGenre("  House  "));
    }

    // ---------- Crappy Path (Expected Fail Tests) ----------
    @Test
    void saveGenreNullTest() {
        assertFalse("Saving null should return false", genreImportService.saveGenre(null));
    }

    @Test
    void saveGenreEmptyStringTest() {
        assertFalse("Saving empty string should return false", genreImportService.saveGenre(""));
    }

    @Test
    void saveGenreWhitespaceOnlyTest() {
        assertFalse("Saving whitespace only should return false", genreImportService.saveGenre("   "));
    }

    @Test
    void saveDuplicateGenreTest() {
        assertTrue("First save should succeed", genreImportService.saveGenre("Blues"));
        assertFalse("Duplicate save should fail", genreImportService.saveGenre("Blues"));
    }

    @Test
    void saveGenreFailsIfAlreadyExistsDifferentSpacingTest() {
        genreImportService.saveGenre("Techno");
        assertFalse("Duplicate with spaces should fail", genreImportService.saveGenre("  Techno "));
    }

    @Test
    void saveGenreFailsIfExistsDifferentCaseTest() {
        genreImportService.saveGenre("Funk");
        assertTrue("Different case should be considered distinct", genreImportService.saveGenre("funk"));
    }


    /**
     * =====================================================
     * genreExists Tests
     * =====================================================
     */

    // ---------- Happy Path (Good Tests) ----------
    @Test
    void genreExistsValidTest() {
        genreImportService.saveGenre("Opera");

        assertTrue("Opera should exist", genreImportService.genreExists("Opera"));
    }

    @Test
    void genreExistsIgnoreWhitespaceTest() {
        genreImportService.saveGenre("Drumstep");

        assertTrue("Trimmed Drumstep should exist", genreImportService.genreExists("  Drumstep  "));
    }

    @Test
    void genreExistsCaseSensitiveTest() {
        genreImportService.saveGenre("Trance");

        assertFalse("trance is different case and should not exist", genreImportService.genreExists("trance"));
    }

    @Test
    void genreExistsFalseWhenNotSavedTest() {
        assertFalse("Genre should not exist", genreImportService.genreExists("Nonexistent"));
    }

    @Test
    void genreExistsTrueAfterImportTest() {
        genreImportService.importGenres(List.of("Ambient"));
        assertTrue("Ambient should exist", genreImportService.genreExists("Ambient"));
    }

    // ---------- Crappy Path (Expected Fail Tests) ----------
    @Test
    void genreExistsNullTest() {
        assertFalse("Null should not exist", genreImportService.genreExists(null));
    }

    @Test
    void genreExistsEmptyTest() {
        assertFalse("Empty string should not exist", genreImportService.genreExists(""));
    }

    @Test
    void genreExistsWhitespaceOnlyTest() {
        assertFalse("Whitespace should not exist", genreImportService.genreExists("   "));
    }

    @Test
    void genreExistsBeforeSavingTest() {
        assertFalse("Genre should not exist before saving", genreImportService.genreExists("Reggaeton"));
    }

    @Test
    void genreExistsFailsOnLeadingTrailingWhitespaceUnstoredTest() {
        assertFalse("Trimmed value does not exist", genreImportService.genreExists("  Salsa  "));
    }
}
