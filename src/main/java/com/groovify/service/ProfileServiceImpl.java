package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Users;
import com.groovify.jpa.repo.GenreRepo;
import com.groovify.jpa.repo.UsersRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl {

    private final UsersRepo usersRepo;
    private final GenreRepo genreRepo;

    public ProfileServiceImpl(UsersRepo usersRepo, GenreRepo genreRepo) {
        this.usersRepo = usersRepo;
        this.genreRepo = genreRepo;
    }

    public Optional<Users> getUserByUsername(String username) {
        return usersRepo.findByName(username);
    }

    public List<Genre> getAllGenres() {
        return genreRepo.findAll();
    }

    public boolean isUsernameTaken(String username, String currentUsername) {
        Optional<Users> user = usersRepo.findByName(username);
        return user.isPresent() && !user.get().getName().equals(currentUsername);
    }

    @Transactional
    public void updateProfile(Users user, String name, String description, String imageFileName, List<Long> genreIds) {
        user.setName(name.trim());
        user.setDescription(description.trim());
        user.setImageFileName(imageFileName);

        if (genreIds != null) {
            List<Genre> genres = genreRepo.findAllById(genreIds);
            user.setGenres(genres);
        }

        usersRepo.save(user);
    }
}
