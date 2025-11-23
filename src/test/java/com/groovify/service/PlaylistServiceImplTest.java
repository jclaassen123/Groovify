package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Genre;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

@Transactional
@SpringBootTest
public class PlaylistServiceImplTest {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private SongService songService;

    private Long clientID;
    private Long genreID;
    private Long songID1;
    private Long songID2;

    /**
     * Sets up test data before each test execution.
     * Creates a test client, three test songs, and stores their IDs for use in tests.
     */
    @BeforeEach
    public void beforeEach(){
        Client testClient = new Client("Test", "Testing");

        genreID = genreRepo.save(new Genre("Test Genre")).getId();

        registerService.saveUser(testClient);
        clientID = testClient.getId();
        assertNotNull("Client ID not null", clientID);
        assertTrue("Must be valid", clientID >= 0);
        Song song1 = new Song("test.mp3", "Test Song", "Test Artist");
        song1.setGenre(genreRepo.findById(genreID).get());
        songService.addSong(song1);
        songID1 = song1.getId();
        Song song2 = new Song("test2.mp3", "Test Song", "Test Artist");
        song2.setGenre(genreRepo.findById(genreID).get());
        songService.addSong(song2);
        songID2 = song2.getId();
        Song song3 = new Song("test3.mp3", "Test Song", "Test Artist");
        song3.setGenre(genreRepo.findById(genreID).get());
        songService.addSong(song3);
    }

    /**
     * getPlaylistById
     */

    // Happy Path

    /**
     * Tests retrieval of a playlist using a valid ID.
     * Verifies that a saved playlist can be successfully retrieved by its ID.
     */
    @Test
    public void getPlaylistByValidIdTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Valid playlist", playlistService.getPlaylistById(playlistID).equals(playlist));
    }

    /**
     * Tests retrieval of two different playlists using valid IDs.
     * Verifies that multiple playlists can be saved and retrieved independently.
     */
    @Test
    public void getPlaylistByValidIdTwiceTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("Playlist 2 should be persisted", playlistService.savePlaylist(playlist2));
        Long playlistID = playlist.getId();
        Long playlistID2 = playlist2.getId();
        assertTrue("Valid playlist", playlistService.getPlaylistById(playlistID).equals(playlist));
        assertTrue("Valid playlist 2", playlistService.getPlaylistById(playlistID2).equals(playlist2));
    }

    // Crappy Path

    /**
     * Tests retrieval of a playlist using an invalid ID.
     * Verifies that null is returned when the playlist ID does not exist.
     */
    @Test
    public void getPlaylistByInvalidIdTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(1000L));
    }

    /**
     * Tests retrieval of playlists using two different invalid IDs.
     * Verifies that null is returned for multiple non-existent playlist IDs.
     */
    @Test
    public void getPlaylistByInvalidIdTwiceTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(1000L));
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(1001L));
    }

    /**
     * Tests retrieval of a playlist using a null ID.
     * Verifies that null is returned when the playlist ID is null.
     */
    @Test
    public void getPlaylistByNullIdTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(null));
    }

    /**
     * Tests retrieval of a playlist using a negative ID.
     * Verifies that null is returned when the playlist ID is negative.
     */
    @Test
    public void getPlaylistByNegativeIdTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(-1000L));
    }

    /**
     * getPlaylistByClientId
     */

    // Happy

    /**
     * Tests retrieval of playlists for a valid client ID with one playlist.
     * Verifies that an empty list is returned before saving, and the playlist appears after saving.
     */
    @Test
    public void getPlaylistsByClientIdOnceTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Should be no playlists before insertion", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertFalse("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    /**
     * Tests retrieval of playlists for a valid client ID with two playlists.
     * Verifies that both playlists are returned for the client.
     */
    @Test
    public void getPlaylistsByClientIdTwiceTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Should be no playlists before insertion", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("Playlist 2 should be persisted", playlistService.savePlaylist(playlist2));
        assertTrue("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID).size() == 2);
    }

    /**
     * Tests retrieval of playlists for two different client IDs.
     * Verifies that playlists are correctly associated with their respective clients.
     */
    @Test
    public void getPlaylistsByTwoClientIdTest() {
        Playlist playlist = makeTestPlaylist();

        Client client2 = new Client("Test2", "Testing2");
        registerService.saveUser(client2);
        Long clientID2 = client2.getId();

        Playlist playlist2 = makeTestPlaylist();
        playlist2.setClientID(clientID2);

        assertTrue("Should be no playlists before insertion", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Should be no playlists before insertion", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID).size() == 1);
        assertTrue("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
        assertTrue("Playlist 2 should be persisted", playlistService.savePlaylist(playlist2));
        assertFalse("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
    }

    //Crappy Path

    /**
     * Tests retrieval of playlists using an invalid client ID.
     * Verifies that an empty list is returned when the client ID does not exist.
     */
    @Test
    public void getPlaylistByClientIdInvalidTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
    }

    /**
     * Tests retrieval of playlists using a negative client ID.
     * Verifies that an empty list is returned when the client ID is negative.
     */
    @Test
    public void getPlaylistByClientIdNegativeTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(-1000L).equals(List.of()));
    }

    /**
     * Tests retrieval of playlists using the same invalid client ID twice.
     * Verifies that an empty list is consistently returned for non-existent client IDs.
     */
    @Test
    public void getPlaylistByClientIdInvalidTwiceTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
    }

    /**
     * Tests retrieval of playlists using a null client ID.
     * Verifies that an empty list is returned when the client ID is null.
     */
    @Test
    public void getPlaylistByClientIdNullTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(null).equals(List.of()));
    }

    /**
     * getSongs
     */

    // Happy Path

    /**
     * Tests retrieval of songs from a playlist containing one song.
     * Verifies that the song list is empty before adding, and contains the song after adding.
     */
    @Test
    public void getSongsOneSongTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();

        assertTrue("Return true with no song in the playlist", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("Playlist song should be added", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Return true with the song in the playlist", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests retrieval of songs from a playlist containing two songs.
     * Verifies that songs are correctly added and the count is accurate.
     */
    @Test
    public void getSongsTwoSongTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();

        assertTrue("Return true with no song in the playlist", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("Playlist song should be added", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Return false with a song in the playlist", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("Playlist song 2 should be added", playlistService.addSongToPlaylist(playlistID, songID2));
        assertTrue("Should have two songs", playlistService.getSongs(playlistID).size() == 2);
    }

    /**
     * Tests retrieval of songs from two different playlists.
     * Verifies that songs are correctly associated with their respective playlists.
     */
    @Test
    public void getSongsTwoPlaylistTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist2));
        Long playlistID = playlist.getId();
        Long playlistID2 = playlist2.getId();
        assertTrue("Playlist should have song", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID).size() == 1);
        assertTrue("Should have no songs", playlistService.getSongs(playlistID2).isEmpty());
        assertTrue("Playlist 2 should have song", playlistService.addSongToPlaylist(playlistID2, songID1));
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID).size() == 1);
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID2).size() == 1);
    }

    /**
     * Tests retrieval of songs from two playlists, each containing two songs.
     * Verifies that songs can be added independently to multiple playlists.
     */
    @Test
    public void getSongsTwoPlaylistTwoSongsTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist2));
        Long playlistID = playlist.getId();
        Long playlistID2 = playlist2.getId();
        assertTrue("Playlist should have song", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID).size() == 1);
        assertTrue("Should have no songs", playlistService.getSongs(playlistID2).isEmpty());
        assertTrue("Playlist 2 should have song", playlistService.addSongToPlaylist(playlistID2, songID1));
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID).size() == 1);
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID2).size() == 1);
        assertTrue("Add song to playlist 1", playlistService.addSongToPlaylist(playlistID, songID2));
        assertTrue("Should have 2 songs", playlistService.getSongs(playlistID).size() == 2);
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID2).size() == 1);
        assertTrue("Add song to playlist 1", playlistService.addSongToPlaylist(playlistID2, songID2));
        assertTrue("Should have 2 songs", playlistService.getSongs(playlistID).size() == 2);
        assertTrue("Should have 1 songs", playlistService.getSongs(playlistID2).size() == 2);
    }

    // Crappy Test

    /**
     * Tests retrieval of songs from an invalid playlist ID.
     * Verifies that an empty list is returned when the playlist does not exist.
     */
    @Test
    public void getSongsInvalidPlaylistTest() {
        assertTrue("Return true with empty list", playlistService.getSongs(1000L).isEmpty());
    }

    /**
     * Tests retrieval of songs from two different invalid playlist IDs.
     * Verifies that an empty list is consistently returned for non-existent playlists.
     */
    @Test
    public void getSongsInvalidPlaylistTwiceTest() {
        assertTrue("Return true with empty list", playlistService.getSongs(1000L).isEmpty());
        assertTrue("Return true with empty list", playlistService.getSongs(1001L).isEmpty());
    }

    /**
     * Tests retrieval of songs from a null playlist ID.
     * Verifies that an empty list is returned when the playlist ID is null.
     */
    @Test
    public void getSongsNullPlaylistTest() {
        assertTrue("Return true with empty list", playlistService.getSongs(null).isEmpty());
    }

    /**
     * Tests retrieval of songs from a negative playlist ID.
     * Verifies that an empty list is returned when the playlist ID is negative.
     */
    @Test
    public void getSongsNegativePlaylistTest() {
        assertTrue("Return true with empty list", playlistService.getSongs(-1000L).isEmpty());
    }

    /**
     * savePlaylist
     */

    // Happy Path

    /**
     * Tests saving a valid playlist.
     * Verifies that a playlist with valid data is successfully persisted.
     */
    @Test
    public void savePlaylistValidTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    /**
     * Tests saving two playlists for the same client.
     * Verifies that multiple playlists can be saved for a single client.
     */
    @Test
    public void savePlaylistTwiceTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertFalse("There should be a playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Playlist 2 should be persisted", playlistService.savePlaylist(playlist2));
        assertTrue("There should be two playlists", playlistService.getPlaylistsByClientId(clientID).size() == 2);
    }

    /**
     * Tests saving playlists for two different clients.
     * Verifies that playlists are correctly associated with their respective clients.
     */
    @Test
    public void savePlaylistTwoClientsTest() {
        Playlist playlist = makeTestPlaylist();

        Client client2 = new Client("Test Client 2", "Test Password 2");
        registerService.saveUser(client2);
        Long clientID2 = client2.getId();

        Playlist playlist2 = makeTestPlaylist();
        playlist2.setClientID(clientID2);

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertFalse("There should be a playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
        assertTrue("Playlist 2 should be persisted", playlistService.savePlaylist(playlist2));
        assertFalse("There should be a playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertFalse("There should be a playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
    }

    /**
     * Tests saving multiple playlists for two different clients.
     * Verifies that each client can have multiple playlists and they remain properly separated.
     */
    @Test
    public void savePlaylistTwoClientsTwiceTest() {
        Playlist playlist = makeTestPlaylist();

        Client client2 = new Client("Test Client 2", "Test Password 2");
        registerService.saveUser(client2);
        Long clientID2 = client2.getId();

        Playlist playlist2 = makeTestPlaylist();
        playlist2.setClientID(clientID2);

        Playlist playlist3 = makeTestPlaylist();
        playlist3.setClientID(clientID);

        Playlist playlist4 = makeTestPlaylist();
        playlist4.setClientID(clientID2);

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertFalse("There should be a playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
        assertTrue("Playlist 2 should be persisted", playlistService.savePlaylist(playlist2));
        assertTrue("There should be a playlists", playlistService.getPlaylistsByClientId(clientID).size() == 1);
        assertTrue("There should be a playlists", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Playlist 3 should be persisted", playlistService.savePlaylist(playlist3));
        assertTrue("There should be two playlists", playlistService.getPlaylistsByClientId(clientID).size() == 2);
        assertTrue("There should be a playlist", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Playlist 3 should be persisted", playlistService.savePlaylist(playlist4));
        assertTrue("There should be two playlists", playlistService.getPlaylistsByClientId(clientID).size() == 2);
        assertTrue("There should be two playlists", playlistService.getPlaylistsByClientId(clientID2).size() == 2);
    }

    // Crappy Path

    /**
     * Tests saving a playlist with an empty name but with a description.
     * Verifies that playlists can be saved without a name if description is provided.
     */
    @Test
    public void savePlaylistNoNameWithDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    /**
     * Tests saving a playlist with a name but with an empty description.
     * Verifies that playlists can be saved without a description if name is provided.
     */
    @Test
    public void savePlaylistWithNameNoDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setDescription("");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    /**
     * Tests saving a playlist with both empty name and description.
     * Verifies that playlists can be saved with both fields empty.
     */
    @Test
    public void savePlaylistNoNameNoDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("");
        playlist.setDescription("");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    /**
     * Tests saving a playlist with special characters in name and description.
     * Verifies that special characters are properly handled during persistence.
     */
    @Test
    public void savePlaylistSpecialCharactersTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("Test!!!@#$%^&*()");
        playlist.setDescription("De#$%*)$_5io)_%*#)%#*-=#[];';].");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    /**
     * Tests saving a playlist with an invalid (null) client ID.
     * Verifies that playlists cannot be saved without a valid client association.
     */
    @Test
    public void savePlaylistInvalidClientIdTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setClientID(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    /**
     * Tests saving a playlist with a null name.
     * Verifies that playlists with null names are rejected during save.
     */
    @Test
    public void savePlaylistNullNameTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    /**
     * Tests saving a playlist with a null description.
     * Verifies that playlists with null descriptions are rejected during save.
     */
    @Test
    public void savePlaylistNullDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    /**
     * Tests saving a playlist with both null name and description.
     * Verifies that playlists with both fields null are rejected during save.
     */
    @Test
    public void savePlaylistNullNameAndDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName(null);
        playlist.setDescription(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    /**
     * Tests saving a playlist with a name containing only whitespace.
     * Verifies that whitespace-only names are accepted during save.
     */
    @Test
    public void savePlaylistWhiteSpaceNameTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("                       ");

        assertTrue("Playlist should be be saved", playlistService.savePlaylist(playlist));
    }

    /**
     * Tests saving a playlist with a description containing only whitespace.
     * Verifies that whitespace-only descriptions are accepted during save.
     */
    @Test
    public void savePlaylistWhiteSpaceDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setDescription("                       ");

        assertTrue("Playlist should be be saved", playlistService.savePlaylist(playlist));
    }

    /**
     * Tests saving a playlist with both name and description containing only whitespace.
     * Verifies that playlists with whitespace-only fields are accepted during save.
     */
    @Test
    public void savePlaylistWhiteSpaceNameAndDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("                                 ");
        playlist.setDescription("                            ");

        assertTrue("Playlist should be be saved", playlistService.savePlaylist(playlist));
    }

    /**
     * deletePlaylist
     */

    // Happy Path

    /**
     * Tests successful deletion of a playlist.
     * Verifies that a playlist can be deleted and is no longer retrievable afterward.
     */
    @Test
    public void deletePlaylistSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        playlist.setClientID(clientID);
        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Playlist should exist", playlistService.getPlaylistById(playlistID).equals(playlist));
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID));
        assertNull("deletePlaylistSuccessTest: ", playlistService.getPlaylistById(playlistID));
    }

    /**
     * Tests successful deletion of a playlist that contains songs.
     * Verifies that playlists with songs can be deleted without issues.
     */
    @Test
    public void deletePlaylistWithSongsSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        playlist.setClientID(clientID);
        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Song should be added to playlist", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Playlist contains song", playlistService.getSongs(playlistID).size() == 1);
        assertTrue("Playlist should exist", playlistService.getPlaylistById(playlistID).equals(playlist));
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID));
        assertNull("deletePlaylistSuccessTest: ", playlistService.getPlaylistById(playlistID));
    }

    /**
     * Tests successful deletion of two playlists.
     * Verifies that multiple playlists can be deleted independently.
     */
    @Test
    public void deletePlaylistTwiceSuccessTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist));
        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist2));
        Long playlistID = playlist.getId();
        Long playlistID2 = playlist2.getId();
        assertTrue("Playlist should exist", playlistService.getPlaylistById(playlistID).equals(playlist));
        assertTrue("Playlist 2 should exist", playlistService.getPlaylistById(playlistID2).equals(playlist2));
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID));
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID2));
        assertNull("Playlist should not exist ", playlistService.getPlaylistById(playlistID));
        assertNull("Playlist should not exist ", playlistService.getPlaylistById(playlistID2));
    }

    /**
     * Tests deletion of playlists belonging to two different clients.
     * Verifies that deleting one client's playlist does not affect another client's playlist.
     */
    @Test
    public void deletePlaylistTwoClientsTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        Client client2 = new Client("Test 2", "Test 2");
        registerService.saveUser(client2);
        Long clientID2 = client2.getId();

        playlist2.setClientID(clientID2);

        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist));
        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist2));
        Long playlistID1 = playlist.getId();
        Long playlistID2 = playlist2.getId();
        assertTrue("Client should have playlist", playlistService.getPlaylistsByClientId(clientID).size() == 1);
        assertTrue("Client should have playlist", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID1));
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Client should have playlist", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID2));
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
    }

    /**
     * Tests deletion of multiple playlists belonging to two different clients.
     * Verifies that each client's playlists can be deleted independently and counts are maintained correctly.
     */
    @Test
    public void deletePlaylistTwoClientsTwiceTest() {
        Playlist playlist = makeTestPlaylist();

        Client client2 = new Client("Test 2", "Test 2");
        registerService.saveUser(client2);
        Long clientID2 = client2.getId();

        Playlist playlist2 = makeTestPlaylist();
        playlist2.setClientID(clientID2);

        Playlist playlist3 = makeTestPlaylist();
        playlist3.setClientID(clientID);

        Playlist playlist4 = makeTestPlaylist();
        playlist4.setClientID(clientID2);

        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist));
        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist2));
        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist3));
        assertTrue("Return true for no errors with insertion", playlistService.savePlaylist(playlist4));
        Long playlistID1 = playlist.getId();
        Long playlistID2 = playlist2.getId();
        Long playlistID3 = playlist3.getId();
        Long playlistID4 = playlist4.getId();
        assertTrue("Client should have two playlists", playlistService.getPlaylistsByClientId(clientID).size() == 2);
        assertTrue("Client 2 should have two playlists", playlistService.getPlaylistsByClientId(clientID2).size() == 2);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID1));
        assertTrue("Client should have one playlist", playlistService.getPlaylistsByClientId(clientID).size() == 1);
        assertTrue("Client 2 should have two playlists", playlistService.getPlaylistsByClientId(clientID2).size() == 2);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID2));
        assertTrue("Client should have one playlist", playlistService.getPlaylistsByClientId(clientID).size() == 1);
        assertTrue("Client 2 should have one playlist", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID3));
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Client 2 should have one playlist", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID4));
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Client 2 should have no playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
    }

    // Crappy Path

    /**
     * Tests deletion of a playlist using an invalid ID.
     * Verifies that deletion fails when the playlist ID does not exist.
     */
    @Test
    public void deletePlaylistByInvalidIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(1000L));
    }

    /**
     * Tests deletion of playlists using two different invalid IDs.
     * Verifies that deletion consistently fails for non-existent playlist IDs.
     */
    @Test
    public void deletePlaylistByInvalidTwiceIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(1000L));
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(1001L));
    }

    /**
     * Tests deletion of a playlist using a null ID.
     * Verifies that deletion fails when the playlist ID is null.
     */
    @Test
    public void deletePlaylistByNullIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(null));
    }

    /**
     * Tests deletion of a playlist using a negative ID.
     * Verifies that deletion fails when the playlist ID is negative.
     */
    @Test
    public void deletePlaylistByNegativeIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(-100L));
    }

    /**
     * addSongToPlaylist
     */

    // Happy Path

    /**
     * Tests successful addition of a song to a playlist.
     * Verifies that a song can be added to an empty playlist.
     */
    @Test
    public void addSongToPlaylistSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlist.getId()).isEmpty());
        assertTrue("savePlaylistSuccessTest: Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("savePlaylistSuccessTest: Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests successful addition of two songs to a playlist.
     * Verifies that multiple songs can be added to the same playlist.
     */
    @Test
    public void addSongToPlaylistTwiceSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlist.getId()).isEmpty());
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID2));
        assertTrue("Playlist should have two songs", playlistService.getSongs(playlistID).size() == 2);
    }

    /**
     * Tests addition of songs to two different playlists.
     * Verifies that songs can be added to multiple playlists independently.
     */
    @Test
    public void addSongToTwoPlaylistsSuccessTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist2));
        Long playlistID = playlist.getId();
        Long playlistID2 = playlist2.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("No song should be in playlist", playlistService.getSongs(playlistID2).isEmpty());
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("No song should be in playlist", playlistService.getSongs(playlistID2).isEmpty());
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID2, songID2));
        assertFalse("Playlist should have two songs", playlistService.getSongs(playlistID).isEmpty());
        assertFalse("No song should be in playlist", playlistService.getSongs(playlistID2).isEmpty());
    }

    // Crappy Path

    /**
     * Tests addition of a song with an empty title to a playlist.
     * Verifies that songs without titles can be added to playlists.
     */
    @Test
    public void addSongToPlaylistNoTitleSuccessTest() {
        Playlist playlist = makeTestPlaylist();
        songService.getSongById(songID1).setTitle("");

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlist.getId()).isEmpty());
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests addition of a song with a whitespace-only title to a playlist.
     * Verifies that songs with whitespace-only titles can be added to playlists.
     */
    @Test
    public void addSongToPlaylistWhiteSpaceTitleSuccessTest() {
        Playlist playlist = makeTestPlaylist();
        songService.getSongById(songID1).setTitle("                    ");

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlist.getId()).isEmpty());
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests addition of a song with an empty artist field to a playlist.
     * Verifies that songs without artist information can be added to playlists.
     */
    @Test
    public void addSongToPlaylistNoArtistSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        Song song = new Song("Testing.mp3", "Test", "");
        song.setGenre(genreRepo.findById(genreID).get());
        assertTrue("Song saved", songService.addSong(song));

        Long songID4 = song.getId();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlist.getId()).isEmpty());
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID4));
        assertFalse("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests addition of a song with an invalid ID to a playlist.
     * Verifies that adding a non-existent song fails and playlist remains empty.
     */
    @Test
    public void addSongToPlaylistByInvalidSongTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertFalse("Song should fail to save",  playlistService.addSongToPlaylist(playlistID, 1000L));
        assertTrue("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests addition of a song with a null ID to a playlist.
     * Verifies that adding a null song ID fails and playlist remains empty.
     */
    @Test
    public void addSongToPlaylistByNullSongTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertFalse("Song should fail to save",  playlistService.addSongToPlaylist(playlistID, null));
        assertTrue("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests prevention of duplicate songs in a playlist.
     * Verifies that adding the same song twice to a playlist is prevented.
     */
    @Test
    public void addSongToPlaylistPreventDuplicatesTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Song should be in playlist", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Playlist should contain the song", playlistService.getSongs(playlistID).size() == 1);
        assertFalse("Song should fail to save",  playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Playlist should contain only one song", playlistService.getSongs(playlistID).size() == 1);
    }

    /**
     * Tests addition of a song to a null playlist ID.
     * Verifies that adding a song to a null playlist fails.
     */
    @Test
    public void addSongToPlaylistNullPlaylistTest() {
        assertFalse("Should fail to save playlist", playlistService.addSongToPlaylist(null, songID1));
    }

    /**
     * Tests addition of a song to an invalid playlist ID.
     * Verifies that adding a song to a non-existent playlist fails.
     */
    @Test
    public void addSongToPlaylistInvalidPlaylistTest() {
        assertFalse("Should fail to save playlist", playlistService.addSongToPlaylist(1000L, songID1));
    }

    /**
     * Tests addition of a song with both invalid playlist ID and invalid song ID.
     * Verifies that the operation fails when both parameters are invalid.
     */
    @Test
    public void addSongToPlaylistInvalidPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(1000L, 1000L));
    }

    /**
     * Tests addition of a song with null playlist ID and invalid song ID.
     * Verifies that the operation fails when playlist ID is null and song ID is invalid.
     */
    @Test
    public void addSongToPlaylistNullPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(null, 1000L));
    }

    /**
     * Tests addition of a song with invalid playlist ID and null song ID.
     * Verifies that the operation fails when playlist ID is invalid and song ID is null.
     */
    @Test
    public void addSongToPlaylistInvalidPlaylistIDAndNullSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(1000L, null));
    }

    /**
     * Tests addition of a song with both null playlist ID and null song ID.
     * Verifies that the operation fails when both parameters are null.
     */
    @Test
    public void addSongToPlaylistNullPlaylistIDAndNullSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(null, null));
    }

    /**
     * removeSongFromPlaylist
     */

    // Happy Path

    /**
     * Tests successful removal of a song from a playlist.
     * Verifies that a song can be removed from a playlist and the playlist becomes empty.
     */
    @Test
    public void removeSongFromPlaylistSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Song should be added into playlist", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("Return true for no errors with song removal", playlistService.removeSongFromPlaylist(playlistID, songID1));
        assertTrue("Playlist should not contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests successful removal of two songs from a playlist.
     * Verifies that multiple songs can be removed independently from a playlist.
     */
    @Test
    public void removeSongFromPlaylistTwiceSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Song should be added into playlist", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Song should be added into playlist", playlistService.addSongToPlaylist(playlistID, songID2));
        assertFalse("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("Return true for no errors with song removal", playlistService.removeSongFromPlaylist(playlistID, songID1));
        assertTrue("Playlist should contain only 1 song", playlistService.getSongs(playlistID).size() == 1);
        assertTrue("Return true for no errors with song removal", playlistService.removeSongFromPlaylist(playlistID, songID2));
        assertTrue("Playlist should contain no songs", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests removal of songs from two different playlists.
     * Verifies that songs can be removed from multiple playlists independently.
     */
    @Test
    public void removeSongFromTwoPlaylistsSuccessTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist2));
        Long playlistID = playlist.getId();
        Long playlistID2 = playlist2.getId();
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID2, songID2));
        assertFalse("Song should be in playlist", playlistService.getSongs(playlistID).isEmpty());
        assertFalse("Song should be in playlist", playlistService.getSongs(playlistID2).isEmpty());
        assertTrue("Return true for no errors with song deletion", playlistService.removeSongFromPlaylist(playlistID, songID1));
        assertTrue("No song should be in playlist", playlistService.getSongs(playlistID).isEmpty());
        assertFalse("Song should be in playlist", playlistService.getSongs(playlistID2).isEmpty());
        assertTrue("Return true for no errors with song deletion", playlistService.removeSongFromPlaylist(playlistID2, songID2));
        assertTrue("No song should be in playlist", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("No song should be in playlist", playlistService.getSongs(playlistID2).isEmpty());
    }

    // Crappy Path

    /**
     * Tests removal of the same song twice from a playlist.
     * Verifies that removing an already-removed song fails on the second attempt.
     */
    @Test
    public void removeSongFromPlaylistTwiceTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Song should be in playlist", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Playlist should contain the song", playlistService.getSongs(playlistID).size() == 1);

        assertTrue("Song should be removed",  playlistService.removeSongFromPlaylist(playlistID, songID1));
        assertFalse("Song already was already removed",  playlistService.removeSongFromPlaylist(playlistID, songID1));
        assertTrue("Playlist should contain only one song", playlistService.getSongs(playlistID).isEmpty());
    }

    /**
     * Tests removal of a song with an invalid song ID from a playlist.
     * Verifies that removing a non-existent song fails.
     */
    @Test
    public void removeSongFromPlaylistInvalidSongIdTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertFalse("Fail to remove invalid song", playlistService.removeSongFromPlaylist(playlistID, 1000L));
    }

    /**
     * Tests removal of a song from an invalid playlist ID.
     * Verifies that removing a song from a non-existent playlist fails.
     */
    @Test
    public void removeSongFromPlaylistInvalidPlaylistIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(1000L, songID1));
    }

    /**
     * Tests removal with both invalid playlist ID and invalid song ID.
     * Verifies that the operation fails when both parameters are invalid.
     */
    @Test
    public void removeSongFromPlaylistInvalidPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(1000L, 1000L));
    }

    /**
     * Tests removal with null playlist ID and invalid song ID.
     * Verifies that the operation fails when playlist ID is null and song ID is invalid.
     */
    @Test
    public void removeSongFromPlaylistNullPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(null, 1000L));
    }

    /**
     * Tests removal with invalid playlist ID and null song ID.
     * Verifies that the operation fails when playlist ID is invalid and song ID is null.
     */
    @Test
    public void removeSongFromPlaylistInvalidPlaylistIDAndNullSongIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(1000L, null));
    }

    /**
     * Tests removal with both null playlist ID and null song ID.
     * Verifies that the operation fails when both parameters are null.
     */
    @Test
    public void removeSongFromPlaylistNullPlaylistIDAndNullSongIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(null, null));
    }

    /**
     * Make a test playlist for unit tests to use
     * @return Basic playlist with placeholder properties
     */
    private Playlist makeTestPlaylist() {
        Playlist playlist = new Playlist();
        playlist.setClientID(clientID);
        playlist.setName("Test Playlist");
        playlist.setDescription("Test Playlist Description");
        return playlist;
    }
}