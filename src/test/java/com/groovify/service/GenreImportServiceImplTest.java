package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreImportServiceImplTest {

    private GenreRepo genreRepo;
    private GenreImportServiceImpl genreService;

    @BeforeEach
    void setUp() {
        genreRepo = mock(GenreRepo.class);
        genreService = new GenreImportServiceImpl(genreRepo);
    }

    // ------------------ genreExists ------------------

    @Test
    void genreExists_ShouldReturnTrue_WhenGenreExists() {
        String name = "Rock";
        when(genreRepo.findByName(name)).thenReturn(Optional.of(new Genre(name)));

        assertTrue(genreService.genreExists(name));
        verify(genreRepo).findByName(name);
    }

    @Test
    void genreExists_ShouldReturnFalse_WhenGenreDoesNotExist() {
        String name = "Jazz";
        when(genreRepo.findByName(name)).thenReturn(Optional.empty());

        assertFalse(genreService.genreExists(name));
        verify(genreRepo).findByName(name);
    }

    // ------------------ saveGenre ------------------

    @Test
    void saveGenre_ShouldReturnSavedGenre_WhenValidName() {
        String name = "Pop";
        Genre genre = new Genre(name);
        when(genreRepo.save(any(Genre.class))).thenReturn(genre);

        Genre saved = genreService.saveGenre(name);

        assertNotNull(saved);
        assertEquals(name, saved.getName());
        verify(genreRepo).save(any(Genre.class));
    }

    @Test
    void saveGenre_ShouldFail_WhenRepoThrowsException() {
        String name = "Metal";
        when(genreRepo.save(any(Genre.class))).thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(RuntimeException.class, () -> genreService.saveGenre(name));
        assertEquals("DB error", exception.getMessage());
        verify(genreRepo).save(any(Genre.class));
    }

    // ------------------ importGenres ------------------

    @Test
    void importGenres_ShouldSaveNewGenres_AndSkipExistingOnes() {
        List<String> genres = Arrays.asList("Hip-Hop", "Classical", "Rock");

        // Rock exists, Hip-Hop and Classical do not
        when(genreRepo.findByName("Rock")).thenReturn(Optional.of(new Genre("Rock")));
        when(genreRepo.findByName("Hip-Hop")).thenReturn(Optional.empty());
        when(genreRepo.findByName("Classical")).thenReturn(Optional.empty());

        genreService.importGenres(genres);

        // Verify saves only for new genres
        verify(genreRepo, times(1)).save(argThat(g -> g.getName().equals("Hip-Hop")));
        verify(genreRepo, times(1)).save(argThat(g -> g.getName().equals("Classical")));
        verify(genreRepo, never()).save(argThat(g -> g.getName().equals("Rock")));
    }

    @Test
    void importGenres_ShouldFail_WhenRepoSaveThrowsException() {
        List<String> genres = Arrays.asList("Electronic");
        when(genreRepo.findByName("Electronic")).thenReturn(Optional.empty());
        when(genreRepo.save(any(Genre.class))).thenThrow(new RuntimeException("DB failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> genreService.importGenres(genres));
        assertEquals("DB failure", exception.getMessage());
        verify(genreRepo).save(any(Genre.class));
    }
}
