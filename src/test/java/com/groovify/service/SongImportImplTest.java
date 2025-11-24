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
    private SongImportImpl songImportService;

    @BeforeEach
    void setUp() {
        songImportService.musicDirectory = "src/main/resources/static/songs"; // actual project path
    }

   @Test
    public void testSongImport() {}

}
