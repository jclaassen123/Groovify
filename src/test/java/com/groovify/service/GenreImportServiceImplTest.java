package com.groovify.service;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest(properties = {"groovify.import.genres.enabled=false"})
@Transactional
class GenreImportServiceImplTest {

    @Autowired
    private GenreImportService genreImportService;

    @Autowired
    private GenreRepo genreRepo;

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

    // ------------------ Happy Path ------------------

    @Test
    void testImportGenres() {
        List<String> genres = List.of(rock, pop, classical, tech, country, folk);
        genreImportService.importGenres(genres);

        List<Genre> saved = genreRepo.findAll();
        assertTrue("Should have 6 genres in DB", saved.size() == 6);
        for (String g : genres) {
            assertTrue(g + " should exist", genreImportService.genreExists(g));
        }
    }

    @Test
    void testSaveGenreIndividually() {
        boolean saved = genreImportService.saveGenre("Jazz");
        assertTrue("Genre should be saved successfully", saved);
        assertTrue("Genre should exist in DB", genreImportService.genreExists("Jazz"));
    }

    @Test
    void testDuplicateGenreImport() {
        genreImportService.saveGenre("Blues");
        boolean savedAgain = genreImportService.saveGenre("Blues"); // duplicate
        assertFalse("Duplicate genre should not be saved", savedAgain);

        List<Genre> saved = genreRepo.findAll();
        long count = saved.stream().filter(g -> g.getName().equals("Blues")).count();
        assertTrue("There should be only 1 Blues genre", count == 1);
    }

    @Test
    void testSaveNullGenre() {
        boolean saved = genreImportService.saveGenre(null);
        assertFalse("Saving null genre should return false", saved);
    }

    @Test
    void testSaveEmptyGenreName() {
        boolean saved = genreImportService.saveGenre("");
        assertFalse("Saving empty genre name should return false", saved);
    }

    @Test
    void testImportGenresWithNullAndEmpty() {
        genreImportService.importGenres(Arrays.asList("Metal", null, "", "Hip-Hop"));

        List<Genre> saved = genreRepo.findAll();
        assertTrue("Should save only valid genres", saved.stream().anyMatch(g -> g.getName().equals("Metal")));
        assertTrue("Should save only valid genres", saved.stream().anyMatch(g -> g.getName().equals("Hip-Hop")));
        assertTrue("Null and empty genres should not be saved", saved.stream().noneMatch(g -> g.getName() == null || g.getName().isEmpty()));
    }

    @Test
    void testImportWithDuplicatesInInputList() {
        genreImportService.importGenres(Arrays.asList("Reggae", "Reggae", "Jazz", "Jazz", "Jazz"));
        List<Genre> saved = genreRepo.findAll();

        long reggaeCount = saved.stream().filter(g -> g.getName().equals("Reggae")).count();
        long jazzCount = saved.stream().filter(g -> g.getName().equals("Jazz")).count();

        assertTrue("Should only save one Reggae", reggaeCount == 1);
        assertTrue("Should only save one Jazz", jazzCount == 1);
    }

    @Test
    void testTrimWhitespaceOnSave() {
        boolean saved = genreImportService.saveGenre("  Indie  ");
        assertTrue("Genre with surrounding whitespace should be saved", saved);
        assertTrue("Whitespace should be trimmed", genreImportService.genreExists("Indie"));
        List<Genre> savedGenres = genreRepo.findAll();
        assertTrue("Genre name in DB should be trimmed", savedGenres.stream().anyMatch(g -> g.getName().equals("Indie")));
    }
}
