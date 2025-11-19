package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

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
    private SongService songService;

    private Long clientID;
    private Long songID1;
    private Long songID2;
    private Long songID3;

    @BeforeEach
    public void beforeEach(){
        Client testClient = new Client("Test", "Testing");
        registerService.saveUser(testClient);
        clientID = testClient.getId();
        assertNotNull("Client ID not null", clientID);
        assertTrue("Must be valid", clientID >= 0);
        Song song1 = new Song("test.mp3", "Test Song", "Test Artist");
        songService.addSong(song1);
        songID1 = song1.getId();
        Song song2 = new Song("test.mp3", "Test Song", "Test Artist");
        songService.addSong(song2);
        songID2 = song2.getId();
        Song song3 = new Song("test.mp3", "Test Song", "Test Artist");
        songService.addSong(song3);
        songID3 = song3.getId();
    }

    /**
     * getPlaylistByID
     * */

    // getPlaylistByID Happy Path

    @Test
    public void getPlaylistByValidIdTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Valid playlist", playlistService.getPlaylistById(playlistID).equals(playlist));
    }

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

    // getPlaylistByID Crappy

    @Test
    public void getPlaylistByInvalidIdTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(1000L));
    }

    @Test
    public void getPlaylistByInvalidIdTwiceTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(1000L));
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(1001L));
    }

    @Test
    public void getPlaylistByNullIdTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(null));
    }

    /**
     *  getPlaylistsByClientID
     *  */

    // getPlaylists Happy Path

    @Test
    public void getPlaylistsByClientIdOnceTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Should be no playlists before insertion", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertFalse("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    @Test
    public void getPlaylistsByClientIdTwiceTest() {
        Playlist playlist = makeTestPlaylist();
        Playlist playlist2 = makeTestPlaylist();

        assertTrue("Should be no playlists before insertion", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertTrue("Playlist 2 should be persisted", playlistService.savePlaylist(playlist2));
        assertTrue("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID).size() == 2);
    }

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

    // getPlaylists Crappy

    @Test
    public void getPlaylistByClientIdInvalidTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
    }

    @Test
    public void getPlaylistByClientIdInvalidTwiceTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
    }

    @Test
    public void getPlaylistByClientIdNullTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(null).equals(List.of()));
    }

    /**
     *  getSongs
     **/

    // getSongs Happy Path

    @Test
    public void getSongsOneSongTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();

        assertTrue("Return true with no song in the playlist", playlistService.getSongs(playlistID).isEmpty());
        assertTrue("Playlist song should be added", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("Return true with the song in the playlist", playlistService.getSongs(playlistID).isEmpty());
    }

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

    // getSongs Crappy

    @Test
    public void getSongsInvalidPlaylistTest() {
        assertTrue("Return true with empty list", playlistService.getSongs(1000L).isEmpty());
    }

    @Test
    public void getSongsInvalidPlaylistTwiceTest() {
        assertTrue("Return true with empty list", playlistService.getSongs(1000L).isEmpty());
        assertTrue("Return true with empty list", playlistService.getSongs(1001L).isEmpty());
    }

    @Test
    public void getSongsNullPlaylistTest() {
        assertTrue("Return true with empty list", playlistService.getSongs(null).isEmpty());
    }

    /**
     * savePlayLists
     * */

    // Happy Path

    @Test
    public void savePlaylistValidTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());

    }

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
        assertFalse("There should be two playlists", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertFalse("There should be two playlists", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
    }

    @Test
    public void savePlaylistNoNameWithDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    @Test
    public void savePlaylistWithNameNoDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setDescription("");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    @Test
    public void savePlaylistNoNameNoDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("");
        playlist.setDescription("");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    @Test
    public void savePlaylistSpecialCharactersTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName("Test!!!@#$%^&*()");
        playlist.setDescription("De#$%*)$_5io)_%*#)%#*-=#[];';].");

        assertTrue("There should be no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist should be saved", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    // Crappy Path

    @Test
    public void savePlaylistInvalidClientIdTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setClientID(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    @Test
    public void savePlaylistNullNameTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    @Test
    public void savePlaylistNullDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    @Test
    public void savePlaylistNullNameAndDescriptionTest() {
        Playlist playlist = makeTestPlaylist();
        playlist.setName(null);
        playlist.setDescription(null);

        assertFalse("Playlist should not be saved", playlistService.savePlaylist(playlist));
    }

    /**
     *  deletePlaylist
     *  */

    // deletePlaylist Happy Path

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
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID1));
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Client should have playlist", playlistService.getPlaylistsByClientId(clientID2).size() == 1);
        assertTrue("Return true for no errors with deletion", playlistService.deletePlaylist(playlistID2));
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Client should have no playlist", playlistService.getPlaylistsByClientId(clientID2).isEmpty());
    }

    // deletePlaylist Crappy

    @Test
    public void deletePlaylistByInvalidIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(1000L));
    }

    @Test
    public void deletePlaylistByInvalidTwiceIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(1000L));
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(1001L));
    }

    @Test
    public void deletePlaylistByNullIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(null));
    }

    @Test
    public void deletePlaylistByNegativeIDTest() {
        assertFalse("Should fail with invalid playlist id", playlistService.deletePlaylist(-100L));
    }

    /**
     * addSongToPlaylist
     * */

    // addSongToPlaylist Happy Path

    @Test
    public void addSongToPlaylistSuccessTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlist.getId()).isEmpty());
        assertTrue("savePlaylistSuccessTest: Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("savePlaylistSuccessTest: Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

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

    // addSongToPlaylist Crappy

    @Test
    public void addSongToPlaylistByInvalidSongTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertFalse("Song should fail to save",  playlistService.addSongToPlaylist(playlistID, 1000L));
        assertTrue("Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

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

    @Test
    public void addSongToPlaylistInvalidPlaylistTest() {
        assertFalse("Should fail to save playlist", playlistService.addSongToPlaylist(null, songID1));
    }

    @Test
    public void addSongToPlaylistInvalidPlaylistLongTest() {
        assertFalse("Should fail to save playlist", playlistService.addSongToPlaylist(1000L, songID1));
    }

    @Test
    public void addSongToPlaylistInvalidPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(1000L, 1000L));
    }

    @Test
    public void addSongToPlaylistNullPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(null, 1000L));
    }

    @Test
    public void addSongToPlaylistInvalidPlaylistIDAndNullSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(1000L, null));
    }

    @Test
    public void addSongToPlaylistNullPlaylistIDAndNullSongIDTest() {
        assertFalse("Failed to add song to playlist", playlistService.addSongToPlaylist(null, null));
    }

    /**
     *  removeSongFromPlaylist
     *  */

    // removeSongFromPlaylist Happy Path

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

    // removeSongFromPlaylist Crappy

    @Test
    public void removeSongFromPlaylistPreventDuplicatesTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("Song should be in playlist", playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Playlist should contain the song", playlistService.getSongs(playlistID).size() == 1);
        assertFalse("Song should fail to save",  playlistService.addSongToPlaylist(playlistID, songID1));
        assertTrue("Playlist should contain only one song", playlistService.getSongs(playlistID).size() == 1);
    }

    @Test
    public void removeSongFromPlaylistInvalidSongIdTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertFalse("Fail to remove invalid song", playlistService.removeSongFromPlaylist(playlistID, 1000L));
    }

    @Test
    public void removeSongFromPlaylistInvalidPlaylistIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(1000L, songID1));
    }

    @Test
    public void removeSongFromPlaylistInvalidPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(1000L, 1000L));
    }

    @Test
    public void removeSongFromPlaylistNullPlaylistIDAndInvalidSongIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(null, 1000L));
    }

    @Test
    public void removeSongFromPlaylistInvalidPlaylistIDAndNullSongIDTest() {
        assertFalse("Fail to remove from playlist", playlistService.removeSongFromPlaylist(1000L, null));
    }

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