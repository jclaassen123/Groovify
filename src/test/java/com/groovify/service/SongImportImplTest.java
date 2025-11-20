package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.SongRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@Transactional
@SpringBootTest
class SongImportImplTest {

    @Autowired
    private SongService songService;

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private SongImportImpl songImportService;

    @BeforeEach
    void setUp() {
        songImportService.musicDirectory = "src/main/resources/static/songs"; // actual project path
    }

    @Test
    void importSongsShouldSkipUnknownGenre() {
        File fakeGenreFolder = new File(songImportService.musicDirectory + "/UnknownGenre");
        // If folder exists, Repo will return empty -> should skip
        songImportService.importSongs();
        verify(SongService, never()).save(any(Song.class));
    }

    @Test
    void importSongsShouldSkipExistingFile() {
        // Pick a real genre folder
        String genreName = "Rock";
        Genre genre = new Genre(genreName);
        genre.setId(1L);

        when(genreRepo.findByName(genreName)).thenReturn(Optional.of(genre));
        when(SongService.existsByFilename(any())).thenReturn(true); // All files exist

        songImportService.importSongs();

        // No new songs saved since they already exist
        verify(SongService, never()).save(any(Song.class));
    }

    @Test
    void importSongsShouldFailWhenMusicDirMissing() {
        songImportService.musicDirectory = "nonexistent-folder";
        songImportService.importSongs();
        verifyNoInteractions(SongService, genreRepo);
    }
}
