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
     *     <li>If fewer than 5 songs exist in the chosen genre, fills the remaining slots with random songs from the full database.</li>
     *     <li>If no preferred genres exist or genre has no songs, returns up to 5 random songs from the entire database.</li>
     *     <li>Ensures that the total number of recommended songs does not exceed the total number of songs in the database.</li>
     * </ul>
     *
     * @param user the user for whom recommendations are generated
     * @return a list of up to 5 recommended songs
     */
    public List<Song> getRecommendedSongs(Client user) {
        log.debug("Generating recommended songs for user '{}'", user.getName());

        List<Song> allSongs = getAllSongs();
        if (allSongs.isEmpty()) {
            log.info("No songs in database, returning empty list");
            return Collections.emptyList();
        }

        int maxRecommendations = Math.min(5, allSongs.size());

        if (userHasPreferredGenres(user)) {
            Genre chosenGenre = pickRandomGenre(user);
            List<Song> recommended = getSongsFromGenre(chosenGenre, maxRecommendations);

            int remaining = maxRecommendations - recommended.size();
            if (remaining > 0) {
                recommended.addAll(getRandomSongsExcluding(allSongs, recommended, remaining));
            }

            log.info("Returning {} recommended songs ({} from genre '{}')",
                    recommended.size(), recommended.size() - remaining, chosenGenre.getName());
            return recommended;
        }

        // Fallback
        return getRandomSongs(allSongs, maxRecommendations);
    }

    /**
     * Fetches all songs from the repository.
     *
     * @return a list of all songs in the database
     */
    private List<Song> getAllSongs() {
        return songRepo.findAll();
    }

    /**
     * Checks whether the user has any preferred genres.
     *
     * @param user the user to check
     * @return true if the user has preferred genres, false otherwise
     */
    private boolean userHasPreferredGenres(Client user) {
        List<Genre> genres = user.getGenres();
        return genres != null && !genres.isEmpty();
    }

    /**
     * Picks a random genre from the user's preferred genres.
     *
     * @param user the user whose genres are considered
     * @return a randomly chosen genre
     */
    private Genre pickRandomGenre(Client user) {
        List<Genre> genres = user.getGenres();
        Genre chosenGenre = genres.get(random.nextInt(genres.size()));
        log.debug("Chosen genre '{}' (ID: {}) for recommendations", chosenGenre.getName(), chosenGenre.getId());
        return chosenGenre;
    }

    /**
     * Returns a list of songs from the specified genre, shuffled and limited to the given number.
     *
     * @param genre the genre from which to fetch songs
     * @param limit the maximum number of songs to return
     * @return a list of songs from the genre
     */
    private List<Song> getSongsFromGenre(Genre genre, int limit) {
        List<Song> songs = songRepo.findByGenreId(genre.getId());
        Collections.shuffle(songs);
        return songs.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Returns a list of random songs from the full song list, excluding those in the provided exclusion list.
     *
     * @param allSongs the list of all available songs
     * @param exclude  the list of songs to exclude
     * @param limit    the maximum number of songs to return
     * @return a list of random songs excluding the specified songs
     */
    private List<Song> getRandomSongsExcluding(List<Song> allSongs, List<Song> exclude, int limit) {
        List<Song> remaining = allSongs.stream()
                .filter(song -> !exclude.contains(song))
                .collect(Collectors.toList());
        Collections.shuffle(remaining);
        return remaining.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Returns a list of random songs from the provided list, limited to the given number.
     *
     * @param songs the list of songs to choose from
     * @param limit the maximum number of songs to return
     * @return a list of random songs
     */
    private List<Song> getRandomSongs(List<Song> songs, int limit) {
        Collections.shuffle(songs);
        return songs.stream().limit(limit).collect(Collectors.toList());
    }
}
