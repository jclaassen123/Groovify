package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.SongRepo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Service responsible for generating song recommendations for users.
 * <p>
 * Recommendations are currently generated randomly based on user-preferred genres
 * or from the full song database as a fallback.
 */
@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final SongRepo songRepo;
    private final Random random = new Random();

    /**
     * Constructs a RecommendationService with the given SongRepo.
     *
     * @param songRepo repository for accessing Song entities
     */
    public RecommendationService(SongRepo songRepo) {
        this.songRepo = songRepo;
    }

    /**
     * Generates a temporary recommended songs list for the given user.
     * <ul>
     *     <li>If the user has preferred genres, randomly chooses one of them and returns up to 5 random songs from it.</li>
     *     <li>If no preferred genres exist or genre has no songs, returns up to 5 random songs from the entire database.</li>
     * </ul>
     *
     * @param user the user for whom recommendations are generated
     * @return a list of up to 5 recommended songs
     */
    public List<Song> getRecommendedSongs(Client user) {
        log.debug("Generating recommended songs for user '{}'", user.getName());

        List<Genre> userGenres = user.getGenres();

        if (userGenres != null && !userGenres.isEmpty()) {
            // Pick one random genre from the user's preferred genres
            Genre chosenGenre = userGenres.get(random.nextInt(userGenres.size()));
            Long genreId = chosenGenre.getId();
            log.debug("Chosen genre '{}' (ID: {}) for recommendations", chosenGenre.getName(), genreId);

            // Fetch songs of that genre
            List<Song> songsOfGenre = songRepo.findByGenreId(genreId);

            if (!songsOfGenre.isEmpty()) {
                Collections.shuffle(songsOfGenre);
                List<Song> recommended = songsOfGenre.stream().limit(5).collect(Collectors.toList());
                log.info("Returning {} recommended songs from genre '{}'", recommended.size(), chosenGenre.getName());
                return recommended;
            }
            log.debug("No songs found for genre '{}', falling back to random songs", chosenGenre.getName());
        }

        // Fallback: return random 5 songs from the entire song database
        List<Song> allSongs = songRepo.findAll();
        Collections.shuffle(allSongs);
        List<Song> recommended = allSongs.stream().limit(5).collect(Collectors.toList());
        log.info("Returning {} recommended songs from full database", recommended.size());
        return recommended;
    }
}
