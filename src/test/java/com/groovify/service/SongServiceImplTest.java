package com.groovify.service;

import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.util.AssertionErrors.*;


/**
 * Spring Boot unit tests for {@link SongServiceImpl}.
 * Uses H2 in-memory database for persistence; transactional so changes rollback.
 */
@Transactional
@SpringBootTest
class SongServiceImplTest {

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private SongService songService;

    private Long genreId1;
    private Long genreId2;
    private Long genreId3;
    private Long genreId4;

    @BeforeEach
    /**
     * Initializes test data by creating four genres and storing the IDs
     * for reuse in song creation during tests.
     */
    void setUp() {
        Genre genre = new Genre("Rock");
        Genre genre2 = new Genre("Pop");
        Genre genre3 = new Genre("Electronic Rock");
        Genre genre4 = new Genre("rock!");
        genreRepo.save(genre);
        genreRepo.save(genre2);
        genreRepo.save(genre3);
        genreRepo.save(genre4);
        genreId1 = genre.getId();
        genreId2 = genre2.getId();
        genreId3 = genre3.getId();
        genreId4 = genre4.getId();
    }

    /**
     * Tests that getAllSongs returns one song after adding exactly one song.
     */
    @Test
    public void getAllSongsOneSongTest() {
        Song song = createSong("test.mp3");

        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should exist", songService.getAllSongs().isEmpty());

    }

    /**
     * Tests that getAllSongs correctly returns two songs after adding two.
     */
    @Test
    public void getAllSongsTwoSongTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3");

        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should exist", songService.getAllSongs().size() == 1);
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("One song should exist", songService.getAllSongs().size() == 2);
    }

    /**
     * Tests getAllSongs when no songs exist (should return empty list).
     */
    @Test
    public void getAllSongsNoSongTest() {
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
    }

    /**
     * Verifies getAllSongs returns empty list on repeated calls when no songs exist.
     */
    @Test
    public void getAllSongsNoSongTwiceTest() {
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests getSongById with one valid song added.
     */
    @Test
    public void getSongByIdOneSongTest() {
        Song song = createSong("test.mp3");

        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        Long songId = song.getId();
        assertNotNull("Song should exist", songService.getSongById(songId));
    }

    /**
     * Tests getSongById with two songs added, retrieving each by ID.
     */
    @Test
    public void getSongByIdTwoSongTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3");


        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        Long songId = song.getId();
        assertNotNull("Song one should exist", songService.getSongById(songId));

        assertTrue("Song two should be added", songService.addSong(song2));
        Long songId2 = song2.getId();
        assertNotNull("Song two should exist", songService.getSongById(songId2));
    }

    /**
     * Tests getSongById with an invalid (nonexistent) ID.
     */
    @Test
    public void getSongByIdInvalidSongTest() {
        assertNull("No songs should exist", songService.getSongById(1000L));
    }

    /**
     * Tests getSongById twice using two different nonexistent IDs.
     */
    @Test
    public void getSongByIdInvalidSongTwiceTest() {
        assertNull("No songs should exist", songService.getSongById(1000L));
        assertNull("No songs should exist", songService.getSongById(1001L));
    }

    /**
     * Ensures invalid IDs return null even when valid songs exist.
     */
    @Test
    public void getSongByIdInvalidSongWithValidInDatabaseTest() {
        Song song = createSong("test.mp3");

        assertTrue("Song should be added", songService.addSong(song));
        assertNull("No song should be retrieved", songService.getSongById(1000L));
        assertNull("No songs should be retrieved", songService.getSongById(1001L));
    }

    /**
     * Ensures getSongById(null) returns null.
     */
    @Test
    public void getSongByIdNullSongTest() {
        assertNull("No songs should exist", songService.getSongById(null));
    }

    /**
     * Tests searching by title when the song exists.
     */
    @Test
    public void searchSongsByTitleValidSongTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.searchSongsByTitle("test").isEmpty());
    }

    /**
     * Tests searching by title where two songs share matching names.
     */
    @Test
    public void searchSongsByTitleValidTwoSameNameSongTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;

        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("test").size() == 1);
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("Two songs should be found", songService.searchSongsByTitle("test").size() == 2);

    }

    /**
     * Tests searching by title where titles differ but contain same keyword.
     */
    @Test
    public void searchSongsByTitleValidTwoDifferentButContainSameWordNameSongTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;
        song2.setTitle("test2");

        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("test").size() == 1);
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("Two songs should be found", songService.searchSongsByTitle("test").size() == 2);
    }

    /**
     * Tests searching by title when songs contain different, unrelated words.
     */
    @Test
    public void searchSongsByTitleValidTwoDifferentWithCDifferentWordsInNameSongTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;
        song2.setTitle("unit");

        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
        assertTrue("No song should be found", songService.searchSongsByTitle("unit").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("test").size() == 1);
        assertTrue("No song should be found", songService.searchSongsByTitle("unit").isEmpty());
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("One songs should be found", songService.searchSongsByTitle("test").size() == 1);
        assertTrue("One song should be found", songService.searchSongsByTitle("unit").size() == 1);
    }

    /**
     * Tests title search with different cases (case-insensitive matching).
     */
    @Test
    public void searchSongsByTitleValidSameWordDifferentCaseSongTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;
        song2.setTitle("Test");

        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
        assertTrue("No song should be found", songService.searchSongsByTitle("Test").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("test").size() == 1);
        assertTrue("One song should be found", songService.searchSongsByTitle("Test").size() == 1);
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("Two songs should be found", songService.searchSongsByTitle("test").size() == 2);
        assertTrue("Two song should be found", songService.searchSongsByTitle("Test").size() == 2);
    }

    /**
     * Tests searching titles that contain spaces.
     */
    @Test
    public void searchSongsByTitleValidSpaceInTitleTest() {
        Song song = createSong("test.mp3");
        song.setTitle("Test Testing");

        assertTrue("No song should be found", songService.searchSongsByTitle("Test Testing").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("Test Testing").size() == 1);
    }

    /**
     * Tests searching titles with special characters.
     */
    @Test
    public void searchSongsByTitleValidSpecialCharacterInTitleTest() {
        Song song = createSong("test.mp3");
        song.setTitle("Test@Groovify.com!!!?!??!");

        assertTrue("No song should be found", songService.searchSongsByTitle("Test@Groovify.com!!!?!??!").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("Test@Groovify.com!!!?!??!").size() == 1);
    }

    /**
     * Tests searching titles when no songs exist (invalid title).
     */
    @Test
    public void searchSongsByTitleInvalidTitleTest() {
        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
    }

    /**
     * Tests searching titles that do not match any existing song.
     */
    @Test
    public void searchSongsByTitleWrongTitleTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByTitle("Tsettt").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("No song should be found", songService.searchSongsByTitle("Tsettt").isEmpty());
    }

    /**
     * Tests searching title using null (should return empty list).
     */
    @Test
    public void searchSongsByTitleNullTitleTest() {
        assertTrue("No song should be found", songService.searchSongsByTitle(null).isEmpty());
    }

    /**
     * Tests searching by genre when valid song exists.
     */
    @Test
    public void searchSongsByGenreValidGenreTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.searchSongsByGenre("Rock").isEmpty());
    }

    /**
     * Tests searching by genre where two songs share the same genre.
     */
    @Test
    public void searchSongsByGenreValidTwoSameGenreTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;

        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("Rock").size() == 1);
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("Two songs should be found", songService.searchSongsByGenre("Rock").size() == 2);

    }

    /**
     * Tests searching by genre where two different genres contain a common keyword.
     */
    @Test
    public void searchSongsByGenreValidTwoDifferentButContainSameWordInGenreTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;
        song2.setGenre(genreRepo.findById(genreId3).get());

        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("Rock").size() == 1);
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("Two songs should be found", songService.searchSongsByGenre("Rock").size() == 2);
    }

    /**
     * Tests searching by two completely different genre names.
     */
    @Test
    public void searchSongsByGenreValidTwoDifferentSongWithDifferentWordsInGenreTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;
        song2.setGenre(genreRepo.findById(genreId2).get());

        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
        assertTrue("No song should be found", songService.searchSongsByGenre("Pop").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("Rock").size() == 1);
        assertTrue("No song should be found", songService.searchSongsByGenre("Pop").isEmpty());
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("One songs should be found", songService.searchSongsByGenre("Rock").size() == 1);
        assertTrue("One song should be found", songService.searchSongsByGenre("Pop").size() == 1);
    }

    /**
     * Tests searching genres using case-insensitive matching.
     */
    @Test
    public void searchSongsByGenreValidSameWordDifferentCaseGenreTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;
        song2.setGenre(genreRepo.findById(genreId4).get());

        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
        assertTrue("No song should be found", songService.searchSongsByGenre("rock").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("Rock").size() == 1);
        assertTrue("One song should be found", songService.searchSongsByGenre("rock").size() == 1);
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("Two songs should be found", songService.searchSongsByGenre("Rock").size() == 2);
        assertTrue("Two song should be found", songService.searchSongsByGenre("rock").size() == 2);
    }

    /**
     * Tests searching by genre containing a space.
     */
    @Test
    public void searchSongsByGenreValidSpaceInGenreTest() {
        Song song = createSong("test.mp3");
        song.setGenre(genreRepo.findById(genreId3).get());

        assertTrue("No song should be found", songService.searchSongsByGenre("Electronic Rock").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("Electronic Rock").size() == 1);
    }

    /**
     * Tests searching genre names with special characters.
     */
    @Test
    public void searchSongsByGenreValidSpecialCharacterInGenreTest() {
        Song song = createSong("test.mp3");
        song.setGenre(genreRepo.findById(genreId4).get());

        assertTrue("No song should be found", songService.searchSongsByGenre("rock!").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("rock!").size() == 1);
    }

    /**
     * Tests searching by genre when no songs exist for that genre.
     */
    @Test
    public void searchSongsByGenreInvalidGenreTest() {
        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
    }

    /**
     * Tests searching by wrong/nonexistent genre keyword.
     */
    @Test
    public void searchSongsByGenreWrongGenreTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByGenre("Country").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("No song should be found", songService.searchSongsByGenre("Country").isEmpty());
    }

    /**
     * Tests searching by null genre input (returns empty).
     */
    @Test
    public void searchSongsByGenreNullGenreTest() {
        assertTrue("No song should be found", songService.searchSongsByGenre(null).isEmpty());
    }

    /**
     * Tests searching by filename when valid song exists.
     */
    @Test
    public void searchSongsByFilenameValidFilenameTest() {
        Song song = createSong("test.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("test.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("test.mp3"));
    }

    /**
     * Tests searching by two valid filenames.
     */
    @Test
    public void searchSongsByFilenameValidTwoFilenamesTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3") ;

        assertFalse("No song should be found", songService.searchSongByFilename("test.mp3"));
        assertFalse("No song should be found", songService.searchSongByFilename("test2.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("test.mp3"));
        assertFalse("No song should be found", songService.searchSongByFilename("test2.mp3"));
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("One song should be found", songService.searchSongByFilename("test.mp3"));
        assertTrue("One song should be found", songService.searchSongByFilename("test2.mp3"));
    }

    /**
     * Tests searching by filename with capital letters.
     */
    @Test
    public void searchSongsByFilenameValidCapitalTest() {
        Song song = createSong("testTestTest.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("testTestTest.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("testTestTest.mp3"));
    }

    /**
     * Ensures filename search is case-insensitive.
     */
    @Test
    public void searchSongsByFilenameValidSameWordDifferentCaseTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("Test.mp3") ;

        assertFalse("No song should be found", songService.searchSongByFilename("test.mp3"));
        assertFalse("No song should be found", songService.searchSongByFilename("Test.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("test.mp3"));
        assertFalse("No song should be found", songService.searchSongByFilename("Test.mp3"));
        assertTrue("Song two should be added", songService.addSong(song2));
        assertTrue("One song should be found", songService.searchSongByFilename("test.mp3"));
        assertTrue("One song should be found", songService.searchSongByFilename("Test.mp3"));
    }

    /**
     * Tests searching for filenames with spaces.
     */
    @Test
    public void searchSongsByFilenameValidSpaceInFilenameTest() {
        Song song = createSong("Unit Test.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("Unit Test.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("Unit Test.mp3"));
    }

    /**
     * Tests searching by invalid filename when database is empty.
     */
    @Test
    public void searchSongsByFilenameInvalidFilenameTest() {
        assertFalse("No song should be found", songService.searchSongByFilename("Test.mp3"));
    }

    /**
     * Tests searching by wrong filename when different files exist.
     */
    @Test
    public void searchSongsByFilenameWrongFilenameTest() {
        Song song = createSong("test.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("Unit.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("No song should be found", songService.searchSongByFilename("Unit.mp3"));
    }

    /**
     * Tests searching filenames using null input.
     */
    @Test
    public void searchSongsByFilenameNullGenreTest() {
        assertFalse("No song should be found", songService.searchSongByFilename(null));
    }

    /**
     * Tests valid addSong operation adds song successfully.
     */
    @Test
    public void addSongValidTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding two different valid songs.
     */
    @Test
    public void addSongValidTwiceTest() {
        Song song = createSong("test.mp3");
        Song song2 = createSong("test2.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.getAllSongs().size() == 1);
        assertTrue("Song should be added", songService.addSong(song2));
        assertTrue("Two songs should be found", songService.getAllSongs().size() == 2);
    }

    /**
     * Tests adding a song with capitalized filename.
     */
    @Test
    public void addSongValidCapitalTest() {
        Song song = createSong("unitTest.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding a song with capitalized title.
     */
    @Test
    public void addSongValidCapitalTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("Test");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding a song with spaces in the title.
     */
    @Test
    public void addSongValidTitleWithSpaceTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("Unit Test");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding song with an empty title.
     */
    @Test
    public void addSongValidEmptyTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding song with capitalized artist.
     */
    @Test
    public void addSongValidCapitalArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("Spring");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding song with empty artist.
     */
    @Test
    public void addSongValidEmptyArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding song with spaces in artist name.
     */
    @Test
    public void addSongValidArtistWithSpaceTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("Spring Boot");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding duplicate song (should not be added).
     */
    @Test
    public void addSongAlreadyExistingTest() {
        Song song = createSong("unitTest.mp3");
        Song song2 = createSong("unitTest.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
        assertFalse("Song should not be added", songService.addSong(song2));
    }

    /**
     * Tests adding non-MP3 song (should fail).
     */
    @Test
    public void addSongNotMp3Test() {
        Song song = createSong("unitTest.png");

        assertFalse("Song should not be added", songService.addSong(song));
    }

    /**
     * Tests adding song whose filename contains a space.
     */
    @Test
    public void addSongWithSpace() {
        Song song = createSong("Unit Test.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("No song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding song whose title is only whitespace.
     */
    @Test
    public void addSongInvalidEmptyTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("        ");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding song with null title (should not be added).
     */
    @Test
    public void addSongInvalidNullTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle(null);

        assertFalse("Song should not be added", songService.addSong(song));
    }

    /**
     * Tests adding song with whitespace-only artist.
     */
    @Test
    public void addSongInvalidEmptyArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("        ");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    /**
     * Tests adding song with null artist (should not be added).
     */
    @Test
    public void addSongInvalidNullArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist(null);

        assertFalse("Song should not be added", songService.addSong(song));
    }

    /**
     * Tests adding song with null genre (should not be added).
     */
    @Test
    public void addSongInvalidNullGenreTest() {
        Song song = createSong("unitTest.mp3");
        song.setGenre(null);

        assertFalse("Song should not be added", songService.addSong(song));
    }

    /**
     * Helper method to create a test Song object with preset title/artist and assigned genre.
     *
     * @param filename The filename to assign to the new Song
     * @return A new Song instance with the provided filename and default fields
     */
    private Song createSong(String filename) {
        Song song = new Song(filename, "test", "test");
        song.setGenre(genreRepo.findById(genreId1).isPresent() ? genreRepo.findById(genreId1).get() : null);
        return song;

    }
}
