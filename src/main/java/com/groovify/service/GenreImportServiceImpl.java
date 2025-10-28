package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.repo.GenreRepo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GenreImportServiceImpl implements GenreImportService {

    private final GenreRepo genreRepo;

    public GenreImportServiceImpl(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    @Override
    public void importGenres(List<String> genreNames) {
        for (String name : genreNames) {
            if (!genreExists(name)) {
                saveGenre(name);
            }
        }
    }

    @Override
    public boolean genreExists(String name) {
        return genreRepo.findByName(name).isPresent();
    }

    @Override
    public Genre saveGenre(String name) {
        Genre genre = new Genre(name);
        return genreRepo.save(genre);
    }
}
