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
/**
 * Test suite for {@link GenreImportService} validating import, save,
 * and existence-checking behaviors for music genres.
 */
class GenreImportServiceImplTest {

    @Autowired
    private GenreImportService genreImportService;

    private String rock;
    private String pop;
    private String classical;
    private String tech;
    private String country;
    private String folk;

    /**
     * Initializes common genre strings before each test.
     */
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
    /**
     * Ensures valid genres are imported and persisted correctly.
     */
    @Test
    void importGenresValidTest() {
        List<String> genres = List.of(rock, pop, classical, tech, country, folk);

        genreImportService.importGenres(genres);

        for (String g : genres) {
            assertTrue("Genre should exist after import: " + g,
                    genreImportService.genreExists(g));
        }
    }

    /**
     * Ensures importing the same list twice does not break or duplicate entries.
     */
    @Test
    void importGenresValidTwiceTest() {
        List<String> genres = List.of(rock, pop);

        genreImportService.importGenres(genres);
        genreImportService.importGenres(genres);

        assertTrue("Rock should exist", genreImportService.genreExists(rock));
        assertTrue("Pop should exist", genreImportService.genreExists(pop));
    }

    /**
     * Verifies that genres with surrounding whitespace are trimmed before import.
     */
    @Test
    void importGenresTrimsWhitespaceTest() {
        genreImportService.importGenres(List.of("  Disco  "));

        assertTrue("Trimmed Disco should exist", genreImportService.genreExists("Disco"));
    }

    /**
     * Ensures that valid genres are imported even when mixed with null/empty values.
     */
    @Test
    void importGenresMixedValidInvalidTest() {
        List<String> input = Arrays.asList("Metal", null, "", "Blues", "  ");

        genreImportService.importGenres(input);

        assertTrue("Metal should exist", genreImportService.genreExists("Metal"));
        assertTrue("Blues should exist", genreImportService.genreExists("Blues"));
    }

    /**
     * Ensures exact duplicates in the input list are ignored.
     */
    @Test
    void importGenresIgnoresExactDuplicatesTest() {
        List<String> input = Arrays.asList("Reggae", "Reggae", "Reggae");

        genreImportService.importGenres(input);

        assertTrue("Reggae should exist", genreImportService.genreExists("Reggae"));
    }

    /**
     * Ensures importing an empty list results in no changes.
     */
    @Test
    void importGenresEmptyListDoesNothingTest() {
        genreImportService.importGenres(List.of());

        assertFalse("Rock should not exist (not imported)", genreImportService.genreExists("Rock"));
    }

    // ---------- Crappy Path (Expected Fail Tests) ----------
    /**
     * Ensures importing a null list does nothing.
     */
    @Test
    void importGenresWithNullListTest() {
        genreImportService.importGenres(null);

        assertFalse("No genres should be imported", genreImportService.genreExists("Anything"));
    }

    /**
     * Ensures a list containing only invalid values results in no saved genres.
     */
    @Test
    void importGenresWithOnlyInvalidValuesTest() {
        List<String> input = Arrays.asList(null, "", "  ");

        genreImportService.importGenres(input);

        assertFalse("Nothing valid should be saved", genreImportService.genreExists(""));
    }

    /**
     * Ensures whitespace-only strings do not create genre records.
     */
    @Test
    void importGenresDoesNotCreateWhitespaceGenreRecordTest() {
        genreImportService.importGenres(Arrays.asList("   "));

        assertFalse("Whitespace should not exist", genreImportService.genreExists("   "));
    }

    /**
     * Confirms that case differences do not count as duplicates.
     */
    @Test
    void importGenresDuplicateWithDifferentCaseDoesNotExistTest() {
        genreImportService.importGenres(List.of("Rock"));

        assertFalse("rock (lowercase) should not count as existing", genreImportService.genreExists("rock"));
    }

    /**
     * Ensures case-sensitive duplicates are allowed.
     */
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
    /**
     * Tests saving a valid genre.
     */
    @Test
    void saveGenreValidTest() {
        assertTrue("Saving valid genre should succeed", genreImportService.saveGenre("Jazz"));
        assertTrue("Jazz should exist", genreImportService.genreExists("Jazz"));
    }

    /**
     * Ensures saving a genre with whitespace results in a trimmed saved value.
     */
    @Test
    void saveGenreWithWhitespaceTest() {
        assertTrue("Saving genre with whitespace should succeed", genreImportService.saveGenre("  Indie  "));
        assertTrue("Trimmed Indie should exist", genreImportService.genreExists("Indie"));
    }

    /**
     * Ensures case-sensitive genre names are treated as distinct.
     */
    @Test
    void saveGenreCaseSensitiveDifferentNamesTest() {
        assertTrue("Saving Soul should succeed", genreImportService.saveGenre("Soul"));
        assertTrue("Saving soul should succeed", genreImportService.saveGenre("soul"));

        assertTrue("Soul should exist", genreImportService.genreExists("Soul"));
        assertTrue("soul should exist", genreImportService.genreExists("soul"));
    }

    /**
     * Ensures saving a genre succeeds even if others already exist.
     */
    @Test
    void saveGenreSuccessAfterOtherGenresExistTest() {
        genreImportService.saveGenre("Rock");
        assertTrue("Should still save different genre", genreImportService.saveGenre("Pop"));
    }

    /**
     * Ensures trimming occurs before duplicate checking.
     */
    @Test
    void saveGenreTrimsBeforeCheckingTest() {
        genreImportService.saveGenre("House");
        assertFalse("Trimmed duplicate should fail", genreImportService.saveGenre("  House  "));
    }

    // ---------- Crappy Path (Expected Fail Tests) ----------
    /**
     * Ensures saving null returns false.
     */
    @Test
    void saveGenreNullTest() {
        assertFalse("Saving null should return false", genreImportService.saveGenre(null));
    }

    /**
     * Ensures empty strings cannot be saved.
     */
    @Test
    void saveGenreEmptyStringTest() {
        assertFalse("Saving empty string should return false", genreImportService.saveGenre(""));
    }

    /**
     * Ensures whitespace-only values cannot be saved.
     */
    @Test
    void saveGenreWhitespaceOnlyTest() {
        assertFalse("Saving whitespace only should return false", genreImportService.saveGenre("   "));
    }

    /**
     * Ensures duplicate saves fail.
     */
    @Test
    void saveDuplicateGenreTest() {
        assertTrue("First save should succeed", genreImportService.saveGenre("Blues"));
        assertFalse("Duplicate save should fail", genreImportService.saveGenre("Blues"));
    }

    /**
     * Ensures duplicates with different spacing are still duplicates.
     */
    @Test
    void saveGenreFailsIfAlreadyExistsDifferentSpacingTest() {
        genreImportService.saveGenre("Techno");
        assertFalse("Duplicate with spaces should fail", genreImportService.saveGenre("  Techno "));
    }

    /**
     * Ensures case differences allow distinct genre entries.
     */
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
    /**
     * Ensures genreExists returns true for saved genres.
     */
    @Test
    void genreExistsValidTest() {
        genreImportService.saveGenre("Opera");

        assertTrue("Opera should exist", genreImportService.genreExists("Opera"));
    }

    /**
     * Ensures genre names with surrounding whitespace are trimmed for lookup.
     */
    @Test
    void genreExistsIgnoreWhitespaceTest() {
        genreImportService.saveGenre("Drumstep");

        assertTrue("Trimmed Drumstep should exist", genreImportService.genreExists("  Drumstep  "));
    }

    /**
     * Verifies case-sensitive behavior in genre existence checks.
     */
    @Test
    void genreExistsCaseSensitiveTest() {
        genreImportService.saveGenre("Trance");

        assertFalse("trance is different case and should not exist", genreImportService.genreExists("trance"));
    }

    /**
     * Ensures nonexistent genres return false.
     */
    @Test
    void genreExistsFalseWhenNotSavedTest() {
        assertFalse("Genre should not exist", genreImportService.genreExists("Nonexistent"));
    }

    /**
     * Ensures imported genres are recognized by existence check.
     */
    @Test
    void genreExistsTrueAfterImportTest() {
        genreImportService.importGenres(List.of("Ambient"));
        assertTrue("Ambient should exist", genreImportService.genreExists("Ambient"));
    }

    // ---------- Crappy Path (Expected Fail Tests) ----------
    /**
     * Ensures null input returns false.
     */
    @Test
    void genreExistsNullTest() {
        assertFalse("Null should not exist", genreImportService.genreExists(null));
    }

    /**
     * Ensures empty strings return false.
     */
    @Test
    void genreExistsEmptyTest() {
        assertFalse("Empty string should not exist", genreImportService.genreExists(""));
    }

    /**
     * Ensures whitespace-only input returns false.
     */
    @Test
    void genreExistsWhitespaceOnlyTest() {
        assertFalse("Whitespace should not exist", genreImportService.genreExists("   "));
    }

    /**
     * Ensures genreExists returns false before a genre is saved.
     */
    @Test
    void genreExistsBeforeSavingTest() {
        assertFalse("Genre should not exist before saving", genreImportService.genreExists("Reggaeton"));
    }

    /**
     * Ensures trimmed values that were not saved return false.
     */
    @Test
    void genreExistsFailsOnLeadingTrailingWhitespaceUnstoredTest() {
        assertFalse("Trimmed value does not exist", genreImportService.genreExists("  Salsa  "));
    }
}
