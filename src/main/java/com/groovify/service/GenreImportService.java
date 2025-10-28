package com.groovify.service;

import com.groovify.jpa.model.Genre;
import java.util.List;

public interface GenreImportService {
    void importGenres(List<String> genreNames);
    boolean genreExists(String name);
    Genre saveGenre(String name);
}
