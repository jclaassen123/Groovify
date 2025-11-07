package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.SongRepo;
import com.mpatric.mp3agic.Mp3File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SongImportImplTest {

    private SongRepo songRepo;
    private GenreRepo genreRepo;
    private SongImportImpl songImportService;

    @BeforeEach
    void setUp() {
        songRepo = mock(SongRepo.class);
        genreRepo = mock(GenreRepo.class);
        songImportService = new SongImportImpl(songRepo, genreRepo);
        songImportService.musicDirectory = "src/main/resources/static/songs"; // actual project path
    }

    @Test
    void importSongs_ShouldSkipUnknownGenre() {
        File fakeGenreFolder = new File(songImportService.musicDirectory + "/UnknownGenre");
        // If folder exists, Repo will return empty -> should skip
        songImportService.importSongs();
        verify(songRepo, never()).save(any(Song.class));
    }

    @Test
    void importSongs_ShouldSkipExistingFile() {
        // Pick a real genre folder
        String genreName = "Rock";
        Genre genre = new Genre(genreName);
        genre.setId(1L);

        when(genreRepo.findByName(genreName)).thenReturn(Optional.of(genre));
        when(songRepo.existsByFilename(any())).thenReturn(true); // All files exist

        songImportService.importSongs();

        // No new songs saved since they already exist
        verify(songRepo, never()).save(any(Song.class));
    }

    @Test
    void importSongs_ShouldFailWhenMusicDirMissing() {
        songImportService.musicDirectory = "nonexistent-folder";
        songImportService.importSongs();
        verifyNoInteractions(songRepo, genreRepo);
    }
}
