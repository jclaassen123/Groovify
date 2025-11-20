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

@SpringBootTest
@Transactional
class RecommendationServiceIntegrationTest {

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


    // =================== Tests ===================

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

    @Test
    void testRecommendationForUserWithoutGenres() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithoutGenres);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        recommendations.forEach(song -> assertThat(song.getGenre()).isNotNull());
    }

    @Test
    void testRecommendationLimitsToDatabaseSize() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", rock);
        addSong("song3.mp3", "Song Three", "Artist C", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(3);
    }

    @Test
    void testRecommendationEmptyDatabase() {
        Client newUser = createUser("newUser");
        List<Song> recommendations = recommendationService.getRecommendedSongs(newUser);
        assertThat(recommendations).isEmpty();
    }

    @Test
    void testRecommendationFillsWithOtherSongsIfGenreNotEnough() {
        addSong("soloRock.mp3", "Solo Rock", "Artist D", rock);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        assertThat(recommendations).anyMatch(song -> song.getTitle().equals("Solo Rock"));
    }

    @Test
    void testRecommendationsDoNotRepeatSongs() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        long distinctCount = recommendations.stream().distinct().count();
        assertThat(distinctCount).isEqualTo(recommendations.size());
    }

    @Test
    void testRecommendationReturnsUpToFiveSongs() {
        for (int i = 1; i <= 10; i++) {
            addSong("extra" + i + ".mp3", "Extra " + i, "Artist X", rock);
        }

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
    }

    @Test
    void testRecommendationWithSingleGenre() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", rock);

        Client singleGenreUser = createUser("singleGenreUser", rock);
        List<Song> recommendations = recommendationService.getRecommendedSongs(singleGenreUser);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).allMatch(song -> song.getGenre().equals(rock));
    }

    @Test
    void testRecommendationWithNoSongsInPreferredGenre() {
        addSong("song1.mp3", "Song One", "Artist A", rock);
        addSong("song2.mp3", "Song Two", "Artist B", rock);

        Client jazzUser = createUser("jazzUser", jazz);
        List<Song> recommendations = recommendationService.getRecommendedSongs(jazzUser);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).noneMatch(song -> song.getGenre().equals(jazz));
    }

    @Test
    void testRecommendationWithExactFiveSongsInGenre() {
        for (int i = 1; i <= 5; i++) {
            addSong("rock" + i + ".mp3", "Rock Song " + i, "Artist R", rock);
        }

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isLessThanOrEqualTo(5);
        assertThat(recommendations).allMatch(song -> userWithGenres.getGenres().contains(song.getGenre()));
    }

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

    @Test
    void testRecommendationWithNoGenresAtAll() {
        addSong("rock1.mp3", "Rock 1", "Artist R", rock);
        addSong("jazz1.mp3", "Jazz 1", "Artist J", jazz);

        Client noGenreUser = createUser("noGenreUser");
        List<Song> recommendations = recommendationService.getRecommendedSongs(noGenreUser);
        assertThat(recommendations.size()).isLessThanOrEqualTo(2);
        assertThat(recommendations).allMatch(song -> song.getGenre() != null);
    }

    @Test
    void testRecommendationWithMultiplePreferredGenres() {
        addSong("rock.mp3", "Rock Song", "Artist R", rock);
        addSong("jazz.mp3", "Jazz Song", "Artist J", jazz);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).anyMatch(song -> userWithGenres.getGenres().contains(song.getGenre()));
    }

    @Test
    void testRecommendationWithSingleSongInDatabase() {
        addSong("only.mp3", "The Only Song", "Artist O", rock);

        List<Song> recommendations = recommendationService.getRecommendedSongs(userWithGenres);
        assertThat(recommendations.size()).isEqualTo(1);
        assertThat(recommendations.get(0).getTitle()).isEqualTo("The Only Song");
    }

    @Test
    void testRecommendationHandlesNullGenresGracefully() {
        Client nullGenreUser = createUser("nullGenreUser");
        nullGenreUser.setGenres(null);

        addSong("rock1.mp3", "Rock 1", "Artist R", rock);

        List<Song> recommendations = recommendationService.getRecommendedSongs(nullGenreUser);
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations).allMatch(song -> song.getGenre() != null);
    }

    @Test
    void testRecommendationWithAllSongsFromSingleGenre() {
        for (int i = 1; i <= 4; i++) {
            addSong("jazz" + i + ".mp3", "Jazz " + i, "Artist J", jazz);
        }

        Client jazzOnlyUser = createUser("jazzOnly", jazz);
        List<Song> recommendations = recommendationService.getRecommendedSongs(jazzOnlyUser);
        assertThat(recommendations).allMatch(song -> song.getGenre().equals(jazz));
    }

    // =================== Helper Methods ===================

    private void importGenres(String... genreNames) {
        genreImportService.importGenres(List.of(genreNames));
        List<Genre> allGenres = profileService.getAllGenres();
        for (String name : genreNames) {
            assertThat(allGenres.stream().map(Genre::getName)).contains(name);
        }
    }

    private Genre getGenre(String name) {
        return profileService.getAllGenres().stream()
                .filter(g -> g.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Genre not found: " + name));
    }

    private Client createUser(String username, Genre... genres) {
        Client user = new Client(username, "password");
        if (genres.length > 0) user.setGenres(List.of(genres));
        registerService.registerUser(user);
        assertThat(profileService.getUserByUsername(username)).isPresent();
        return user;
    }

    private Song addSong(String fileName, String title, String artist, Genre genre) {
        Song song = new Song(fileName, title, artist);
        song.setGenre(genre);
        songService.addSong(song);
        return song;
    }
}
