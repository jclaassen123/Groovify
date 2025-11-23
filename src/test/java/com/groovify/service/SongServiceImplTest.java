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
     * GetAllSongs
     */

    // Happy Path

    @Test
    public void getAllSongsOneSongTest() {
        Song song = createSong("test.mp3");

        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should exist", songService.getAllSongs().isEmpty());

    }

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

    // Crappy Path

    @Test
    public void getAllSongsNoSongTest() {
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
    }

    @Test
    public void getAllSongsNoSongTwiceTest() {
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
    }

    /**
     * getSongById
     */

    @Test
    public void getSongByIdOneSongTest() {
        Song song = createSong("test.mp3");

        assertTrue("No songs should exist", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        Long songId = song.getId();
        assertNotNull("Song should exist", songService.getSongById(songId));
    }

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

    // Crappy Path

    @Test
    public void getSongByIdInvalidSongTest() {
        assertNull("No songs should exist", songService.getSongById(1000L));
    }

    @Test
    public void getSongByIdInvalidSongTwiceTest() {
        assertNull("No songs should exist", songService.getSongById(1000L));
        assertNull("No songs should exist", songService.getSongById(1001L));
    }

    @Test
    public void getSongByIdInvalidSongWithValidInDatabaseTest() {
        Song song = createSong("test.mp3");

        assertTrue("Song should be added", songService.addSong(song));
        assertNull("No song should be retrieved", songService.getSongById(1000L));
        assertNull("No songs should be retrieved", songService.getSongById(1001L));
    }

    @Test
    public void getSongByIdNullSongTest() {
        assertNull("No songs should exist", songService.getSongById(null));
    }

    /**
     * searchSongsByTitle
     */

    // Happy Path

    @Test
    public void searchSongsByTitleValidSongTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.searchSongsByTitle("test").isEmpty());
    }

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

    @Test
    public void searchSongsByTitleValidSpaceInTitleTest() {
        Song song = createSong("test.mp3");
        song.setTitle("Test Testing");

        assertTrue("No song should be found", songService.searchSongsByTitle("Test Testing").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("Test Testing").size() == 1);
    }

    @Test
    public void searchSongsByTitleValidSpecialCharacterInTitleTest() {
        Song song = createSong("test.mp3");
        song.setTitle("Test@Groovify.com!!!?!??!");

        assertTrue("No song should be found", songService.searchSongsByTitle("Test@Groovify.com!!!?!??!").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByTitle("Test@Groovify.com!!!?!??!").size() == 1);
    }

    // Crappy Path

    @Test
    public void searchSongsByTitleInvalidTitleTest() {
        assertTrue("No song should be found", songService.searchSongsByTitle("test").isEmpty());
    }

    @Test
    public void searchSongsByTitleWrongTitleTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByTitle("Tsettt").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("No song should be found", songService.searchSongsByTitle("Tsettt").isEmpty());
    }

    @Test
    public void searchSongsByTitleNullTitleTest() {
        assertTrue("No song should be found", songService.searchSongsByTitle(null).isEmpty());
    }

    /**
     * searchSongsByGenre
     */

    // Happy Path

    @Test
    public void searchSongsByGenreValidGenreTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.searchSongsByGenre("Rock").isEmpty());
    }

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

    @Test
    public void searchSongsByGenreValidSpaceInGenreTest() {
        Song song = createSong("test.mp3");
        song.setGenre(genreRepo.findById(genreId3).get());

        assertTrue("No song should be found", songService.searchSongsByGenre("Electronic Rock").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("Electronic Rock").size() == 1);
    }

    @Test
    public void searchSongsByGenreValidSpecialCharacterInGenreTest() {
        Song song = createSong("test.mp3");
        song.setGenre(genreRepo.findById(genreId4).get());

        assertTrue("No song should be found", songService.searchSongsByGenre("rock!").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongsByGenre("rock!").size() == 1);
    }

    // Crappy Path

    @Test
    public void searchSongsByGenreInvalidGenreTest() {
        assertTrue("No song should be found", songService.searchSongsByGenre("Rock").isEmpty());
    }

    @Test
    public void searchSongsByGenreWrongGenreTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.searchSongsByGenre("Country").isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("No song should be found", songService.searchSongsByGenre("Country").isEmpty());
    }

    @Test
    public void searchSongsByGenreNullGenreTest() {
        assertTrue("No song should be found", songService.searchSongsByGenre(null).isEmpty());
    }

    /**
     * searchSongsByFilename
     */

    // Happy Path

    @Test
    public void searchSongsByFilenameValidFilenameTest() {
        Song song = createSong("test.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("test.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("test.mp3"));
    }

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

    @Test
    public void searchSongsByFilenameValidCapitalTest() {
        Song song = createSong("testTestTest.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("testTestTest.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("testTestTest.mp3"));
    }

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

    @Test
    public void searchSongsByFilenameValidSpaceInFilenameTest() {
        Song song = createSong("Unit Test.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("Unit Test.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertTrue("One song should be found", songService.searchSongByFilename("Unit Test.mp3"));
    }

    // Crappy Path

    @Test
    public void searchSongsByFilenameInvalidFilenameTest() {
        assertFalse("No song should be found", songService.searchSongByFilename("Test.mp3"));
    }

    @Test
    public void searchSongsByFilenameWrongFilenameTest() {
        Song song = createSong("test.mp3");

        assertFalse("No song should be found", songService.searchSongByFilename("Unit.mp3"));
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("No song should be found", songService.searchSongByFilename("Unit.mp3"));
    }

    @Test
    public void searchSongsByFilenameNullGenreTest() {
        assertFalse("No song should be found", songService.searchSongByFilename(null));
    }

    /**
     * addSong
     */

    @Test
    public void addSongValidTest() {
        Song song = createSong("test.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

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

    @Test
    public void addSongValidCapitalTest() {
        Song song = createSong("unitTest.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongValidCapitalTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("Test");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongValidTitleWithSpaceTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("Unit Test");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongValidEmptyTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongValidCapitalArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("Spring");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongValidEmptyArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongValidArtistWithSpaceTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("Spring Boot");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    // Crappy Path

    @Test
    public void addSongAlreadyExistingTest() {
        Song song = createSong("unitTest.mp3");
        Song song2 = createSong("unitTest.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
        assertFalse("Song should not be added", songService.addSong(song2));
    }

    @Test
    public void addSongNotMp3Test() {
        Song song = createSong("unitTest.png");

        assertFalse("Song should not be added", songService.addSong(song));
    }

    @Test
    public void addSongWithSpace() {
        Song song = createSong("Unit Test.mp3");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("No song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongInvalidEmptyTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle("        ");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongInvalidNullTitleTest() {
        Song song = createSong("unitTest.mp3");
        song.setTitle(null);

        assertFalse("Song should not be added", songService.addSong(song));
    }

    @Test
    public void addSongInvalidEmptyArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist("        ");

        assertTrue("No song should be found", songService.getAllSongs().isEmpty());
        assertTrue("Song should be added", songService.addSong(song));
        assertFalse("One song should be found", songService.getAllSongs().isEmpty());
    }

    @Test
    public void addSongInvalidNullArtistTest() {
        Song song = createSong("unitTest.mp3");
        song.setArtist(null);

        assertFalse("Song should not be added", songService.addSong(song));
    }

    @Test
    public void addSongInvalidNullGenreTest() {
        Song song = createSong("unitTest.mp3");
        song.setGenre(null);

        assertFalse("Song should not be added", songService.addSong(song));
    }

    /**
     * Create a song for testing purposes
     * @param filename Placeholder file name for Song
     * @return New test song
     */
    private Song createSong(String filename) {
        Song song = new Song(filename, "test", "test");
        song.setGenre(genreRepo.findById(genreId1).isPresent() ? genreRepo.findById(genreId1).get() : null);
        return song;

    }


}
