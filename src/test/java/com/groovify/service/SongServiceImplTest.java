package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.SongRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SongServiceImplTest {

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private SongRepo songRepo;

    @Autowired
    private SongService songService;

    @BeforeEach
    void setUp() {
        songRepo.deleteAll();
    }


}
