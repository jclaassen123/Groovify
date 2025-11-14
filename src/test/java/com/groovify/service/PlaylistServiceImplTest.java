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

    // getPlaylistByID Crappy

    @Test
    public void getPlaylistByInvalidIdTest() {
        assertNull("Return false with invalid playlist", playlistService.getPlaylistById(1000L));
    }


    /**
     *  getPlaylistsByClientID
     *  */

    // getPlaylists Happy Path TODO multiple playlists

    @Test
    public void getPlaylistsValidClientTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Should be no playlists before insertion", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        assertFalse("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID).isEmpty());
    }

    // getPlaylists Crappy

    @Test
    public void getPlaylistByInvalidClientTest() {
        assertTrue("Return true with an empty list", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
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

    // getSongs Crappy

    @Test
    public void getSongsInvalidPlaylistTest() {
        assertTrue("getPlaylistByInvalidClient: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
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

    }

    // Crappy Path

    @Test
    public void savePlaylistInvalidTest() {
        Playlist playlist = makeTestPlaylist();
    }

    /**
     *  deletePlaylist
     *  */

    // deletePlaylist Happy Path

    @Test
    public void deletePlaylistSuccessTest() {
        Playlist testPlaylist = new Playlist();
        testPlaylist.setClientID(clientID);
        assertTrue("deletePlaylistSuccessTest: Return true for no errors with insertion", playlistService.savePlaylist(testPlaylist));
        assertTrue("deletePlaylistSuccessTest: ", playlistService.getPlaylistById(testPlaylist.getId()).equals(testPlaylist));
        assertTrue("deletePlaylistSuccessTest: Return true for no errors with deletion", playlistService.deletePlaylist(testPlaylist.getId()));
        assertNull("deletePlaylistSuccessTest: ", playlistService.getPlaylistById(testPlaylist.getId()));
    }


    // deletePlaylist Crappy

//    @Test
//    public void deletePlaylistByInvalidClientTest() {
//        assertTrue("savePlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
//    }

    /**
     * addSongToPlaylist
     * */

    // addSongToPlaylist Happy Path

    @Test
    public void addSongToPlaylistSuccessTest() {
        Playlist playlist = new Playlist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertTrue("No song should be in playlist", playlistService.getSongs(playlist.getId()).isEmpty());
        assertTrue("savePlaylistSuccessTest: Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistID, songID1));
        assertFalse("savePlaylistSuccessTest: Playlist should contain the song", playlistService.getSongs(playlistID).isEmpty());
    }

    // addSongToPlaylist Crappy

    @Test
    public void addSongToPlaylistByInvalidClientTest() {
        assertTrue("savePlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylistsByClientId(clientID + 1).equals(List.of()));
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

    // removeSongFromPlaylist Crappy

    @Test
    public void removeSongFromPlaylistInvalidSongIdTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.savePlaylist(playlist));
        Long playlistID = playlist.getId();
        assertFalse("Return false for no song in playlist with that id", playlistService.removeSongFromPlaylist(playlistID, songID1));
    }

    /**
     * Make a test playlist for unit tests to use
     * @return Basic playlist with
     */
    private Playlist makeTestPlaylist() {
        Playlist playlist = new Playlist();
        playlist.setClientID(clientID);
        playlist.setName("Test Playlist");
        playlist.setDescription("Test Playlist Description");
        return playlist;
    }
}