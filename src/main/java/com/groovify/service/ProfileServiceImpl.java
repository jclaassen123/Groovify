package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.ClientRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl {

    private final ClientRepo clientRepo;
    private final GenreRepo genreRepo;

    public ProfileServiceImpl(ClientRepo clientRepo, GenreRepo genreRepo) {
        this.clientRepo = clientRepo;
        this.genreRepo = genreRepo;
    }

    public Optional<Client> getUserByUsername(String username) {
        return clientRepo.findByName(username);
    }

    public List<Genre> getAllGenres() {
        return genreRepo.findAll();
    }

    public boolean isUsernameTaken(String username, String currentUsername) {
        Optional<Client> user = clientRepo.findByName(username);
        return user.isPresent() && !user.get().getName().equals(currentUsername);
    }

    @Transactional
    public void updateProfile(Client user, String name, String description, String imageFileName, List<Long> genreIds) {
        user.setName(name.trim());
        user.setDescription(description.trim());
        user.setImageFileName(imageFileName);

        List<Genre> genres = (genreIds != null) ? genreRepo.findAllById(genreIds) : new ArrayList<>();
        user.setGenres(genres);

        clientRepo.save(user);
    }
}
