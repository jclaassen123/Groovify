package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link RecommendationService}, validating the expected
 * behavior of the recommendation algorithm using an in-memory database.
 *
 * <p>The recommendation logic verified by this suite follows these rules:</p>
 *
 * <ol>
 *     <li><b>Maximum of 5 recommendations:</b> All returned lists contain no more than 5 songs.</li>
 *     <li><b>Preferred genres prioritized:</b> Users with genre preferences receive songs matching those genres first.</li>
 *     <li><b>Fill remaining slots from any genre:</b> If fewer than 5 preferred-genre songs exist, the system fills the rest with other songs from the database.</li>
 *     <li><b>Users with no genres (or null genres):</b>
 *          Still receive up to 5 songs; recommendations are unrestricted but genres must be non-null.</li>
 *     <li><b>No duplicates:</b> The algorithm returns only unique songs in the recommendation list.</li>
 *     <li><b>Database-sized limits:</b> If the database contains fewer than 5 songs total, only available songs are returned.</li>
 *     <li><b>Empty database:</b> Users receive an empty list if no songs exist.</li>
 * </ol>
 *
 * <p>This class ensures correctness across all edge cases, including multi-genre preference,
 * missing genre matches, partial availability, and null genre lists.</p>
 */
@SpringBootTest
@Transactional
class RecommendationServiceTest {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private GenreImportService genreImportService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private SongService songService;

    private Genre rock;
    private Genre jazz;
    private Client userWithGenres;
    private Client userWithoutGenres;

    @BeforeEach
    void setUp() {
        importGenres("Rock", "Jazz");
        rock = getGenre("Rock");
        jazz = getGenre("Jazz");

        userWithGenres = createUser("userWithGenres", rock, jazz);
        userWithoutGenres = createUser("userWithoutGenres");
    }

    // ========================================================================
    // Tests
    // ========================================================================

    /**
     * Verifies that users with genres receive recommendations matching
     * at least one preferred genre and no more than 5 songs.
     */
    @Test
    void testRecommendationForUserWithGenres() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", rock);
        addSong("song3.mp3", "Song Three", "Artist C", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        assertThat(recommendations).anyMatch(song -> userWithGenres.getGenres().contains(song.getGenre()));
    }

    /**
     * Ensures users without any genres still receive up to 5 songs from the database.
     */
    @Test
    void testRecommendationForUserWithoutGenres() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithoutGenres);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        recommendations.forEach(song -> assertThat(song.getGenre()).isNotNull());
    }

    /**
     * Confirms recommendations are limited by the actual number of songs in the database.
     */
    @Test
    void testRecommendationLimitsToDatabaseSize() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", rock);
        addSong("song3.mp3", "Song Three", "Artist C", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(3);
    }

    /**
     * Ensures an empty database yields an empty recommendation list.
     */
    @Test
    void testRecommendationEmptyDatabase() {
        Client newUser = createUser("newUser");
        List<Song> recommendations = recommendationService.getRecommendedSongs(newUser);
        assertThat(recommendations).isEmpty();
    }

    /**
     * Validates that when a user’s preferred genres don't have enough songs,
     * the service fills remaining slots with non-preferred songs.
     */
    @Test
    void testRecommendationFillsWithOtherSongsIfGenreNotEnough() {
        addSong("soloRock.mp3", "Solo Rock", "Artist D", rock);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        assertThat(recommendations).anyMatch(song -> song.getTitle().equals("Solo Rock"));
    }

    /**
     * Ensures that no duplicated songs appear in the recommendation list.
     */
    @Test
    void testRecommendationsDoNotRepeatSongs() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        long distinctCount = recommendations.stream().distinct().count();
        assertThat(distinctCount).isEqualTo(recommendations.size());
    }

    /**
     * Confirms that even if more than 5 songs match the user's genres,
     * the recommendation list still caps at 5.
     */
    @Test
    void testRecommendationReturnsUpToFiveSongs() {
        for (int i = 1; i <= 10; i++) {
            addSong("extra" + i + ".mp3", "Extra " + i, "Artist X", rock);
        }

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
    }

    /**
     * Verifies correct behavior for users with a single preferred genre.
     */
    @Test
    void testRecommendationWithSingleGenre() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", rock);

        Client singleGenreUser = createUser("singleGenreUser", rock);
        List<Song> recommendations = recommendationService.getRecommendedSongs(singleGenreUser);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).allMatch(song -> song.getGenre().equals(rock));
    }

    /**
     * Ensures fallback recommendations occur when the user’s preferred genre has no matching songs.
     */
    @Test
    void testRecommendationWithNoSongsInPreferredGenre() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", rock);

        Client jazzUser = createUser("jazzUser", jazz);
        List<Song> recommendations = recommendationService.getRecommendedSongs(jazzUser);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).noneMatch(song -> song.getGenre().equals(jazz));
    }

    /**
     * Ensures that exactly 5 matching songs still results in a list of up to 5 preferred songs only.
     */
    @Test
    void testRecommendationWithExactFiveSongsInGenre() {
        for (int i = 1; i <= 5; i++) {
            addSong("rock" + i + ".mp3", "Rock Song " + i, "Artist R", rock);
        }

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        assertThat(recommendations).allMatch(song -> userWithGenres.getGenres().contains(song.getGenre()));
    }

    /**
     * Ensures at least preferred songs appear when both preferred and other-genre songs exist.
     */
    @Test
    void testRecommendationWithLessThanFiveSongsInGenreAndMoreInDatabase() {
        addSong("rock1.mp3", "Rock 1", "Artist R", rock);
        addSong("rock2.mp3", "Rock 2", "Artist R", rock);
        for (int i = 1; i <= 3; i++) {
            addSong("jazz" + i + ".mp3", "Jazz " + i, "Artist J", jazz);
        }

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        assertThat(recommendations).anyMatch(song -> song.getTitle().equals("Rock 1"));
        assertThat(recommendations).anyMatch(song -> song.getTitle().equals("Rock 2"));
    }

    /**
     * Ensures users with no genre preferences still receive valid recommendations.
     */
    @Test
    void testRecommendationWithNoGenresAtAll() {
        addSong("rock1.mp3", "Rock 1", "Artist R", rock);
        addSong("jazz1.mp3", "Jazz 1", "Artist J", jazz);

        Client noGenreUser = createUser("noGenreUser");
        List<Song> recommendations = recommendationService.getRecommendedSongs(noGenreUser);
        assertThat(recommendations.size()).isLessThanOrEqualTo(2);
        assertThat(recommendations).allMatch(song -> song.getGenre() != null);
    }

    /**
     * Verifies recommendations for users with multiple preferred genres return songs matching any of them.
     */
    @Test
    void testRecommendationWithMultiplePreferredGenres() {
        addSong("rock.mp3", "Rock Song", "Artist R", rock);
        addSong("jazz.mp3", "Jazz Song", "Artist J", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).anyMatch(song -> userWithGenres.getGenres().contains(song.getGenre()));
    }

    /**
     * Ensures correct behavior when only one song exists in the entire database.
     */
    @Test
    void testRecommendationWithSingleSongInDatabase() {
        addSong("only.mp3", "The Only Song", "Artist O", rock);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isEqualTo(1);
        assertThat(recommendations.get(0).getTitle()).isEqualTo("The Only Song");
    }

    /**
     * Validates that null genre lists are handled gracefully without throwing.
     */
    @Test
    void testRecommendationHandlesNullGenresGracefully() {
        Client nullGenreUser = createUser("nullGenreUser");
        nullGenreUser.setGenres(null);

        addSong("rock1.mp3", "Rock 1", "Artist R", rock);

        List<Song> recommendations = recommendationService.getRecommendedSongs(nullGenreUser);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).allMatch(song -> song.getGenre() != null);
    }

    /**
     * Ensures genre-specific users receive only songs from their single preferred genre.
     */
    @Test
    void testRecommendationWithAllSongsFromSingleGenre() {
        for (int i = 1; i <= 4; i++) {
            addSong("jazz" + i + ".mp3", "Jazz " + i, "Artist J", jazz);
        }

        Client jazzOnlyUser = createUser("jazzOnly", jazz);
        List<Song> recommendations = recommendationService.getRecommendedSongs(jazzOnlyUser);
        assertThat(recommendations).allMatch(song -> song.getGenre().equals(jazz));
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Imports the specified genres into the database and validates their presence.
     *
     * @param genreNames names of genres to import
     */
    private void importGenres(String... genreNames) {
        genreImportService.importGenres(List.of(genreNames));
        List<Genre> allGenres = profileService.getAllGenres();
        for (String name : genreNames) {
            assertThat(allGenres.stream().map(Genre::getName)).contains(name);
        }
    }

    /**
     * Retrieves a genre by name or throws if not found.
     *
     * @param name the genre name
     * @return the matching {@link Genre}
     */
    private Genre getGenre(String name) {
        return profileService.getAllGenres().stream()
                .filter(g -> g.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Genre not found: " + name));
    }

    /**
     * Creates and registers a user with optional assigned genres.
     *
     * @param username the username
     * @param genres   any genres to assign to the user
     * @return the persisted client
     */
    private Client createUser(String username, Genre... genres) {
        Client user = new Client(username, "password");
        if (genres.length > 0) user.setGenres(List.of(genres));
        registerService.registerUser(user);
        assertThat(profileService.getUserByUsername(username)).isPresent();
        return user;
    }

    /**
     * Creates, assigns a genre to, and saves a song to the database.
     *
     * @param fileName audio filename
     * @param title    song title
     * @param artist   artist name
     * @param genre    song genre
     * @return the saved {@link Song}
     */
    private Song addSong(String fileName, String title, String artist, Genre genre) {
        Song song = new Song(fileName, title, artist);
        song.setGenre(genre);
        songService.addSong(song);
        return song;
    }
}
