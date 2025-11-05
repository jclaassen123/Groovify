package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final SongRepo songRepo;
    private final Random random = new Random();

    public RecommendationService(SongRepo songRepo) {
        this.songRepo = songRepo;
    }

    /**
     * Generates a temporary recommended songs list for the given user.
     * <ul>
     *     <li>If the user has preferred genres, randomly choose ONE of them and return 5 random songs from it.</li>
     *     <li>If no preferred genres exist, return 5 random songs from the entire database.</li>
     * </ul>
     */
    public List<Song> getRecommendedSongs(Client user) {
        List<Genre> userGenres = user.getGenres();

        if (userGenres != null && !userGenres.isEmpty()) {
            // Pick one random Genre object
            Genre chosenGenre = userGenres.get(random.nextInt(userGenres.size()));
            Long genreId = chosenGenre.getId();

            // Fetch songs of that genre
            List<Song> songsOfGenre = songRepo.findByGenreId(genreId);

            if (!songsOfGenre.isEmpty()) {
                Collections.shuffle(songsOfGenre);
                return songsOfGenre.stream().limit(5).collect(Collectors.toList());
            }
        }

        // Fallback: random 5 songs from all songs
        List<Song> allSongs = songRepo.findAll();
        Collections.shuffle(allSongs);
        return allSongs.stream().limit(5).collect(Collectors.toList());
    }
}
