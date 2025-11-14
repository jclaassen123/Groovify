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
    private ClientRepo clientRepo; // TODO Remove when register service is fixed

    @Autowired
    private RegisterService registerService;

    @Autowired
    private SongService songService;

//    private Playlist playlist = new Playlist();
//    private Long playlistId;

//    private Song song;

    private Long clientID;

    @BeforeEach
    public void beforeEach(){
        Client testClient = new Client("Test", "Testing");
        clientRepo.save(testClient);
        clientID = testClient.getId();
        assertNotNull("Client ID not null", clientID);
        assertTrue("Must be valid", clientID >= 0);
        songService.addSong(new Song("test.mp3", "Test Song", "Test Artist"));
        songService.addSong(new Song("test2.mp3", "Test Song 2", "Test Artist 2"));
        songService.addSong(new Song("test3.mp3", "Test Song 3", "Test Artist 3"));
    }

    /**
     * savePlayLists
     * */

    @Test
    public void savePlaylistValidTest() {
        Playlist playlist = makeTestPlaylist();

        assertTrue("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());
        assertTrue("Valid playlist", playlistService.savePlaylist(playlist));
        assertFalse("Playlist should be persisted", playlistService.getPlaylistsByClientId(clientID).isEmpty());

    }


    // *** getPlaylistByID ***

    // getPlaylistByID Happy Path

    @Test
    public void getPlaylistByValidIdTest() {
        assertTrue("getPlaylistByValidIdTest: Return true with valid playlist", playlistService.getPlaylistById(playlistId).equals(playlist));
    }

    // getPlaylistByID Crappy

    @Test
    public void getPlaylistByInvalidIdTest() {
        assertNull("getPlaylistByInvalidIdTest: Return true with valid playlist", playlistService.getPlaylistById(playlistId + 1));
    }

//
//    // *** getPlaylists ***
//
//    // getPlaylists Happy Path TODO multiple playlists
//
//    @Test
//    public void getPlaylistsValidClientTest() {
//        assertTrue("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID).contains(playlist));
//    }
//
//    // getPlaylists Crappy
//
//    @Test
//    public void getPlaylistByInvalidClientTest() {
//        assertTrue("getPlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
//    }
//
//    // *** getSongs *** TODO Multiple Songs
//
//    // getSongs Happy Path
//
//    @Test
//    public void getSongsOneSongTest() {
//        assertTrue("getSongsOneSongTest: Return true with the song in ", playlistService.getPlaylistById(playlistId).getSongs().contains(song));
//    }
//
//    // getSongs Crappy
//
////    @Test
////    public void getPlaylistByInvalidClientTest() {
////        assertTrue("getPlaylistByInvalidClient: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
////    }
//
//    // *** deletePlaylist *** TODO Multiple Songs
//
//    // deletePlaylist Happy Path
//
//    @Test
//    public void deletePlaylistSuccessTest() {
//        Playlist testPlaylist = new Playlist();
//        testPlaylist.setClientID(clientID);
//        assertTrue("deletePlaylistSuccessTest: Return true for no errors with insertion", playlistService.savePlaylist(testPlaylist));
//        assertTrue("deletePlaylistSuccessTest: ", playlistService.getPlaylistById(testPlaylist.getId()).equals(testPlaylist));
//        assertTrue("deletePlaylistSuccessTest: Return true for no errors with deletion", playlistService.deletePlaylist(testPlaylist.getId()));
//        assertNull("deletePlaylistSuccessTest: ", playlistService.getPlaylistById(testPlaylist.getId()));
//    }
//
//    // deletePlaylist Crappy
//
////    @Test
////    public void deletePlaylistByInvalidClientTest() {
////        assertTrue("savePlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
////    }
//
//    // *** addSongToPlaylist *** TODO Multiple Songs
//
//    // addSongToPlaylist Happy Path
//
//    @Test
//    public void addSongToPlaylistSuccessTest() {
//        Song testSong = songService.getAllSongs().get(2);
//        assertTrue("savePlaylistSuccessTest: Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistId, testSong.getId()));
//        assertTrue("savePlaylistSuccessTest: Playlist should contain the song", playlistService.getSongs(playlistId).contains(testSong));
//    }
//
//    // addSongToPlaylist Crappy
//
////    @Test
////    public void savePlaylistByInvalidClientTest() {
////        assertTrue("savePlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
////    }
//
//    // *** addSongToPlaylist *** TODO Multiple Songs
//
//    // addSongToPlaylist Happy Path
//
//    @Test
//    public void removeSongFromPlaylistSuccessTest() {
//        Song testSong = songService.getAllSongs().get(2);
//        assertTrue("removeSongFromPlaylistSuccessTest: Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistId, testSong.getId()));
//        assertTrue("removeSongFromPlaylistSuccessTest: Playlist should contain the song", playlistService.getSongs(playlistId).contains(testSong));
//        assertTrue("removeSongFromPlaylistSuccessTest: Return true for no errors with song removal", playlistService.removeSongFromPlaylist(playlistId, testSong.getId()));
//        assertFalse("removeSongFromPlaylistSuccessTest: Playlist should not contain the song", playlistService.getSongs(playlistId).contains(testSong));
//    }
//
//    // addSongToPlaylist Crappy
//
////    @Test
////    public void savePlaylistByInvalidClientTest() {
////        assertTrue("savePlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
////    }
//
//



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