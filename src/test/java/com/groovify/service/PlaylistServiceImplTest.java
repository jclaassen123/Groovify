package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
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

@SpringBootTest
public class PlaylistServiceImplTest {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private SongService songService;
    @Autowired
    private GenreRepo genreRepo;

    private Playlist playlist = new Playlist();
    private Long playlistId;

    private Song song;

//    private Client testClient = new Client("test", "test");
    private Long clientID = 1000L; // TODO remove hardcoded value when register service is fixed
//    private Model tempModel;

    @BeforeEach
    public void beforeEach(){
        if (playlistId == null){
            song = songService.getAllSongs().get(1);
            System.out.println(song.getId());
            playlist.setClientID(clientID);
            playlistService.savePlaylist(playlist);
            playlistId = playlist.getId();
            playlistService.addSongToPlaylist(playlistId, song.getId());
        }

        assertNotNull("playlistService must be injected", playlistService);
        assertNotNull("registerService must be injected", registerService);
        assertNotNull("songService must be injected", songService);
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

    // *** getPlaylists ***

    // getPlaylists Happy Path TODO multiple playlists

    @Test
    public void getPlaylistsValidClientTest() {
        assertTrue("getPlaylistsValidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID).contains(playlist));
    }

    // getPlaylists Crappy

    @Test
    public void getPlaylistByInvalidClientTest() {
        assertTrue("getPlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
    }

    // *** getSongs *** TODO Multiple Songs

    // getSongs Happy Path

    @Test
    @Transactional
    public void getSongsOneSongTest() {
        assertTrue("getSongsOneSongTest: Return true with the song in ", playlistService.getPlaylistById(playlistId).getSongs().contains(song));
    }

    // getSongs Crappy

//    @Test
//    public void getPlaylistByInvalidClientTest() {
//        assertTrue("getPlaylistByInvalidClient: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
//    }

    // *** deletePlaylist *** TODO Multiple Songs

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

    // *** addSongToPlaylist *** TODO Multiple Songs

    // addSongToPlaylist Happy Path

    @Test
    @Transactional
    public void addSongToPlaylistSuccessTest() {
        Song testSong = songService.getAllSongs().get(2);
        assertTrue("savePlaylistSuccessTest: Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistId, testSong.getId()));
        assertTrue("savePlaylistSuccessTest: Playlist should contain the song", playlistService.getSongs(playlistId).contains(testSong));
    }

    // addSongToPlaylist Crappy

//    @Test
//    public void savePlaylistByInvalidClientTest() {
//        assertTrue("savePlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
//    }

    // *** addSongToPlaylist *** TODO Multiple Songs

    // addSongToPlaylist Happy Path

    @Test
    @Transactional
    public void removeSongFromPlaylistSuccessTest() {
        Song testSong = songService.getAllSongs().get(2);
        assertTrue("removeSongFromPlaylistSuccessTest: Return true for no errors with song insertion", playlistService.addSongToPlaylist(playlistId, testSong.getId()));
        assertTrue("removeSongFromPlaylistSuccessTest: Playlist should contain the song", playlistService.getSongs(playlistId).contains(testSong));
        assertTrue("removeSongFromPlaylistSuccessTest: Return true for no errors with song removal", playlistService.removeSongFromPlaylist(playlistId, testSong.getId()));
        assertFalse("removeSongFromPlaylistSuccessTest: Playlist should not contain the song", playlistService.getSongs(playlistId).contains(testSong));
    }

    // addSongToPlaylist Crappy

//    @Test
//    public void savePlaylistByInvalidClientTest() {
//        assertTrue("savePlaylistByInvalidClientTest: Return true with valid playlist", playlistService.getPlaylists(clientID + 1).equals(List.of()));
//    }



}