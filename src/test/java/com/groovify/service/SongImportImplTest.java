package com.groovify.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

/**
 * Unit tests for {@link SongImportImpl}, validating behavior of the song import
 * process under various folder and file configurations.
 */
@Transactional
@SpringBootTest
class SongImportImplTest {

    @Autowired
    private GenreImportService genreImportService;

    @Autowired
    private SongImportImpl songImportService;

    @Autowired
    private SongService songService;

    /**
     * Prepares the test environment by importing valid genres and assigning
     * the music directory path used by the song importer.
     */
    @BeforeEach
    void setUp() {
        genreImportService.importGenres(List.of("Rock", "Pop", "Classical", "Tech", "Country", "Folk")); // So song import works, needs genres for songs
        songImportService.musicDirectory = "src/main/resources/static/songs"; // actual project path
    }

    // Happy Path

    /**
     * Tests that songs import successfully when the directory is valid
     * and all required genres exist.
     */
    @Test
    public void songImportSuccessTest() {
        assertTrue("0 songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("Should be no errors with import", songImportService.importSongs());
        assertTrue("60 songs should exist", songService.getAllSongs().size() == 60);
    }

    // Crappy Path

    /**
     * Tests that the song importer fails when the provided music directory
     * does not exist.
     */
    @Test
    public void songImportInvalidFolderTest() {
        songImportService.musicDirectory = "src/main/resources/static/DoesNotExist";

        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertFalse("Should fail to import", songImportService.importSongs());
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests that the importer fails when the provided music directory
     * is actually a file path instead of a directory.
     */
    @Test
    public void songImportFilePathTest() {
        songImportService.musicDirectory = "src/main/resources/static/Classical/Albumleaf.mp3";

        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertFalse("Should fail to import", songImportService.importSongs());
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests that the importer fails when the folder contains genres
     * that do not match the ones imported into the system.
     */
    @Test
    public void songImportInvalidGenreFoldersTest() {
        songImportService.musicDirectory = "src/test/resources/static/songs";
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertFalse("Should fail to import", songImportService.importSongs());
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
    }
}
