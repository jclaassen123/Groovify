package com.groovify.service;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.PlaylistRepo;
import com.groovify.jpa.repo.SongRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
class PlaylistServiceImplTest {
    private static Playlist fakePlaylist = new Playlist();
    private static Playlist fakePlaylist2 = new Playlist();
    private static Song fakeSong = new Song("test.mp3", "Test Song", "Test Artist");
    private static Song fakeSong2 = new Song("test2.mp3", "Test Song 2", "Test Artist 2");
    private static final Long clientID = 10000L;
    private static final Long nullClientID = 100001L;
    private static final Long nullPlaylistID = 100002L;
    private static final Long nullSongID = 200001L;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private PlaylistRepo playlistRepo;

    @Autowired
    private SongRepo songRepo;

    @Autowired
    private ClientRepo clientRepo;

    @BeforeEach
    void setupEntities() {
        fakePlaylist = new Playlist();
        fakePlaylist.setClientID(clientID);
        fakePlaylist.setName("test");
        fakePlaylist.setDescription("test");

        fakePlaylist2 = new Playlist();
        fakePlaylist2.setClientID(clientID);
        fakePlaylist2.setName("FakePlaylist2");
        fakePlaylist2.setDescription("testing");

        fakeSong = new Song("test.mp3", "Test Song", "Test Artist");
        fakeSong2 = new Song("test2.mp3", "Test Song 2", "Test Artist 2");
    }

    // Get Playlists Happy Tests
//
//    @Test
//    public void getPlaylistsOneSuccess() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//        comparePlaylist.add(fakePlaylist);
//        playlistRepo.save(fakePlaylist);
//        assertEquals("getPlaylistsOneSuccess: Should get the one playlist of the client", comparePlaylist, playlistService.getPlaylists(clientID));
//    }
//
//    @Test
//    public void getPlaylistsTwoSuccess() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//        comparePlaylist.add(fakePlaylist);
//        comparePlaylist.add(fakePlaylist2);
//
//        playlistRepo.save(fakePlaylist);
//        playlistRepo.save(fakePlaylist2);
//
//        assertEquals("getPlaylistsTwoSuccess: Should get the two playlists of the client", comparePlaylist, playlistService.getPlaylists(clientID));
//
//        playlistRepo.delete(fakePlaylist2);
//    }
//
//    @Test
//    public void getPlaylistsTwice() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//        comparePlaylist.add(fakePlaylist);
//
//        playlistRepo.save(fakePlaylist);
//
//        assertEquals("getPlaylistsTwice: Should work twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
//        assertEquals("getPlaylistsTwice: Should work twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
//    }
//
//    @Test
//    public void getPlaylistsCheckAddCheck() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//        comparePlaylist.add(fakePlaylist);
//
//        playlistRepo.save(fakePlaylist);
//        assertEquals("getPlaylistsCheckAddCheck: Should get the one playlist of the client before insertion", comparePlaylist, playlistService.getPlaylists(clientID));
//        playlistRepo.save(fakePlaylist2);
//        comparePlaylist.add(fakePlaylist2);
//        assertEquals("getPlaylistsCheckAddCheck: Should get the two playlists of the client after insertion", comparePlaylist, playlistService.getPlaylists(clientID));
//
//        playlistRepo.delete(fakePlaylist2);
//    }
//
//    @Test
//    public void getPlaylistsRemoveCheck() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//        comparePlaylist.add(fakePlaylist);
//        comparePlaylist.add(fakePlaylist2);
//
//        playlistRepo.save(fakePlaylist);
//        playlistRepo.save(fakePlaylist2);
//
//        assertEquals("getPlaylistsRemoveCheck: Should get the two playlists of the client before removal", comparePlaylist, playlistService.getPlaylists(clientID));
//
//        playlistRepo.delete(fakePlaylist2);
//        comparePlaylist.remove(fakePlaylist2);
//
//        assertEquals("getPlaylistsRemoveCheck: Should get the one playlist of the client after removal", comparePlaylist, playlistService.getPlaylists(clientID));
//    }
//
//    // Get Playlists Crappy Tests
//
//    @Test
//    public void getPlaylistsOneFailure() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//
//        assertEquals("getPlaylistsOneFailure: Should get a null list from the null client", comparePlaylist, playlistService.getPlaylists(nullClientID));
//    }
//
//    @Test
//    public void getPlaylistsNoPlaylists() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//
//        playlistRepo.delete(fakePlaylist);
//
//        assertEquals("getPlaylistsNoPlaylists: Should get no playlists of the client", comparePlaylist, playlistService.getPlaylists(clientID));
//    }
//
//    @Test
//    public void getPlaylistsNoPlaylistsTwice() {
//        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
//
//        playlistRepo.delete(fakePlaylist);
//
//        assertEquals("getPlaylistsNoPlaylistsTwice: Should get empty list twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
//        assertEquals("getPlaylistsNoPlaylistsTwice: Should get empty list twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
//    }
//
//    // Get Playlist by ID Happy
//
//    @Test
//    public void getPlaylistByIdSuccess() {
//        playlistRepo.save(fakePlaylist);
//        assertEquals("getPlaylistByIdSuccess: Should return the correct playlist", fakePlaylist, playlistService.getPlaylistById(fakePlaylist.getId()));
//    }
//
//    @Test
//    public void getPlaylistByIdTwice() {
//        playlistRepo.save(fakePlaylist);
//        assertEquals("getPlaylistByIdTwice: Should work twice in a row", fakePlaylist, playlistService.getPlaylistById(fakePlaylist.getId()));
//        assertEquals("getPlaylistByIdTwice: Should work twice in a row", fakePlaylist, playlistService.getPlaylistById(fakePlaylist.getId()));
//    }
//
//    @Test
//    public void getPlaylistByIdAfterSave() {
//        playlistRepo.save(fakePlaylist2);
//        assertEquals("getPlaylistByIdAfterSave: Should return the newly saved playlist", fakePlaylist2, playlistService.getPlaylistById(fakePlaylist2.getId()));
//        playlistRepo.delete(fakePlaylist2);
//    }
//
//    // Get Playlist by ID Crappy
//
//    @Test
//    public void getPlaylistByIdNotFound() {
//        assertNull("getPlaylistByIdNotFound: Should return null for non-existent playlist", playlistService.getPlaylistById(nullPlaylistID));
//    }
//
//    @Test
//    public void getPlaylistByIdAfterDelete() {
//        playlistRepo.save(fakePlaylist2);
//        assertEquals("getPlaylistByIdAfterDelete: Should return playlist before delete", fakePlaylist2, playlistService.getPlaylistById(fakePlaylist2.getId()));
//        playlistRepo.delete(fakePlaylist2);
//        assertNull("getPlaylistByIdAfterDelete: Should return null after delete", playlistService.getPlaylistById(fakePlaylist2.getId()));
//    }
//
//    // Get Songs Happy
//
//    @Test
//    @Transactional
//    public void getSongsEmpty() {
//        List<Song> compareSongs = new LinkedList<Song>();
//        playlistRepo.save(fakePlaylist2);
//        assertEquals("getSongsEmpty: Should return empty list for playlist with no songs", compareSongs, playlistService.getSongs(fakePlaylist2.getId()));
//    }
//
////    @Test
////    @Transactional
////    public void getSongsOneSuccess() {
////        List<Song> compareSongs = new LinkedList<Song>();
////        compareSongs.add(fakeSong);
////        playlistRepo.save(fakePlaylist);
////
////        songRepo.save(fakeSong);
////
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        assertEquals("getSongsOneSuccess: Should return one song", compareSongs, playlistService.getSongs(fakePlaylist.getId()));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////    }
//
////    @Test
////    @Transactional
////    public void getSongsTwoSuccess() {
////        List<Song> compareSongs = new LinkedList<Song>();
////        compareSongs.add(fakeSong);
////        compareSongs.add(fakeSong2);
////
////        playlistRepo.save(fakePlaylist);
////
////        songRepo.save(fakeSong);
////        songRepo.save(fakeSong2);
////
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong2.getId());
////
////        assertEquals("getSongsTwoSuccess: Should return two songs", compareSongs, playlistService.getSongs(fakePlaylist.getId()));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong2.getId());
////        songRepo.delete(fakeSong2);
////    }
//
////    @Test
////    @Transactional
////    public void getSongsCheckAddCheck() {
////        List<Song> compareSongs = new LinkedList<Song>();
////
////        playlistRepo.save(fakePlaylist);
////
////        assertEquals("getSongsCheckAddCheck: Should return empty before add", compareSongs, playlistService.getSongs(fakePlaylist.getId()));
////
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        compareSongs.add(fakeSong);
////
////        assertEquals("getSongsCheckAddCheck: Should return one song after add", compareSongs, playlistService.getSongs(fakePlaylist.getId()));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////    }
//
//    // Get Songs Crappy
//
//    @Test
//    public void getSongsNullPlaylist() {
//        List<Song> compareSongs = new LinkedList<Song>();
//        assertEquals("getSongsNullPlaylist: Should return empty list for non-existent playlist", compareSongs, playlistService.getSongs(nullPlaylistID));
//    }
//
////    @Test
////    public void getSongsAfterRemoval() {
////        List<Song> compareSongs = new LinkedList<Song>();
////        playlistRepo.save(fakePlaylist);
////        songRepo.save(fakeSong);
////
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////
////        assertEquals("getSongsAfterRemoval: Should return empty list after removing song", compareSongs, playlistService.getSongs(fakePlaylist.getId()));
////    }
//
//    // Save Playlist Happy
//
//    @Test
//    public void savePlaylistSuccess() {
//        assertTrue("SavePlaylistSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
//        assertEquals("SavePlaylistSuccess: Playlist should be saved to client", fakePlaylist2, playlistService.getPlaylistById(fakePlaylist2.getId()));
//        playlistRepo.delete(fakePlaylist2);
//    }
//
//    @Test
//    public void savePlaylistTwoSuccess() {
//
//        assertTrue("savePlaylistTwoSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist));
//        assertEquals("savePlaylistTwoSuccess: Playlist should be saved to client", fakePlaylist, playlistService.getPlaylistById(fakePlaylist.getId()));
//        assertTrue("savePlaylistTwoSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
//        assertEquals("savePlaylistTwoSuccess: Playlist should be saved to client", fakePlaylist2, playlistService.getPlaylistById(fakePlaylist2.getId()));
//
//        playlistRepo.delete(fakePlaylist2);
//    }
//
//    @Test
//    public void savePlaylistBeforeRemovalFalseAfterTrue() {
//        assertTrue("savePlaylistBeforeRemovalFalseAfterTrue: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
//        assertEquals("savePlaylistBeforeRemovalFalseAfterTrue: Playlist should be saved to client", fakePlaylist2, playlistService.getPlaylistById(fakePlaylist2.getId()));
//
//        playlistRepo.delete(fakePlaylist2);
//    }
//
//    // Save Playlist Crappy
//
////    @Test
////    public void savePlaylistDuplicate() {
////        assertTrue("SavePlaylistDuplicate: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist));
////        assertFalse("SavePlaylistDuplicate: Should return false for duplicate", playlistService.savePlaylist(fakePlaylist));
////    }
//
//    // Delete Playlist Happy
//
//    @Test
//    public void deletePlaylistSuccess() {
//        playlistRepo.save(fakePlaylist2);
//        assertTrue("deletePlaylistSuccess: Should return true for successful deletion", playlistService.deletePlaylist(fakePlaylist2.getId()));
//        assertNull("deletePlaylistSuccess: Playlist should no longer exist", playlistService.getPlaylistById(fakePlaylist2.getId()));
//    }
//
//    @Test
//    public void deletePlaylistCheckDeleteCheck() {
//        playlistRepo.save(fakePlaylist);
//        playlistRepo.save(fakePlaylist2);
//        assertNotNull("deletePlaylistCheckDeleteCheck: Playlist should exist before delete", playlistService.getPlaylistById(fakePlaylist2.getId()));
//        assertTrue("deletePlaylistCheckDeleteCheck: Should return true for successful deletion", playlistService.deletePlaylist(fakePlaylist2.getId()));
//        assertNull("deletePlaylistCheckDeleteCheck: Playlist should not exist after delete", playlistService.getPlaylistById(fakePlaylist2.getId()));
//    }
//
//    // Delete Playlist Crappy
//
//    @Test
//    public void deletePlaylistNotFound() {
//        assertFalse("deletePlaylistNotFound: Should return false for non-existent playlist", playlistService.deletePlaylist(nullPlaylistID));
//    }
//
//    @Test
//    public void deletePlaylistTwice() {
//        playlistRepo.save(fakePlaylist2);
//        assertTrue("deletePlaylistTwice: Should return true for first deletion", playlistService.deletePlaylist(fakePlaylist2.getId()));
//        assertFalse("deletePlaylistTwice: Should return false for second deletion", playlistService.deletePlaylist(fakePlaylist2.getId()));
//    }
//
//    // Add Song to Playlist Happy
//
////    @Test
////    @Transactional
////    public void addSongToPlaylistSuccess() {
////        playlistRepo.save(fakePlaylist);
////        songRepo.save(fakeSong);
////        assertTrue("addSongToPlaylistSuccess: Should return true for successful addition", playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId()));
////        List<Song> songs = playlistService.getSongs(fakePlaylist.getId());
////        assertTrue("addSongToPlaylistSuccess: Song should be in playlist", songs.contains(fakeSong));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////    }
//
////    @Test
////    @Transactional
////    public void addSongToPlaylistTwo() {
////        playlistRepo.save(fakePlaylist);
////        songRepo.save(fakeSong);
////        songRepo.save(fakeSong2);
////
////        assertTrue("addSongToPlaylistTwo: Should add first song", playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId()));
////        assertTrue("addSongToPlaylistTwo: Should add second song", playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong2.getId()));
////
////        List<Song> songs = playlistService.getSongs(fakePlaylist.getId());
////        assertTrue("addSongToPlaylistTwo: First song should be in playlist", songs.contains(fakeSong));
////        assertTrue("addSongToPlaylistTwo: Second song should be in playlist", songs.contains(fakeSong2));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong2.getId());
////        songRepo.delete(fakeSong2);
////    }
//
////    @Test
////    @Transactional
////    public void addSongToPlaylistCheckAddCheck() {
////        playlistRepo.save(fakePlaylist);
////        List<Song> songs = playlistService.getSongs(fakePlaylist.getId());
////        assertFalse("addSongToPlaylistCheckAddCheck: Song should not be in playlist before add", songs.contains(fakeSong));
////
////        songRepo.save(fakeSong);
////
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        songs = playlistService.getSongs(fakePlaylist.getId());
////        assertTrue("addSongToPlaylistCheckAddCheck: Song should be in playlist after add", songs.contains(fakeSong));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////    }
//
//    // Add Song to Playlist Crappy
//
//    @Test
//    public void addSongToPlaylistDuplicate() {
//        playlistRepo.save(fakePlaylist);
//        songRepo.save(fakeSong);
//        assertTrue("addSongToPlaylistDuplicate: Should add song first time", playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId()));
//        assertFalse("addSongToPlaylistDuplicate: Should return false for duplicate", playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId()));
//
//        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
//    }
//
//    @Test
//    public void addSongToPlaylistNullPlaylist() {
//        songRepo.save(fakeSong);
//        assertFalse("addSongToPlaylistNullPlaylist: Should return false for non-existent playlist", playlistService.addSongToPlaylist(nullPlaylistID, fakeSong.getId()));
//    }
//
//    @Test
//    public void addSongToPlaylistNullSong() {
//        playlistRepo.save(fakePlaylist);
//        assertFalse("addSongToPlaylistNullSong: Should return false for non-existent song", playlistService.addSongToPlaylist(fakePlaylist.getId(), nullSongID));
//    }
//
//    // Remove Song from Playlist Happy
//
////    @Test
////    @Transactional
////    public void removeSongFromPlaylistSuccess() {
////        playlistRepo.save(fakePlaylist);
////        songRepo.save(fakeSong);
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        assertTrue("removeSongFromPlaylistSuccess: Should return true for successful removal", playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId()));
////        List<Song> songs = playlistService.getSongs(fakePlaylist.getId());
////        assertFalse("removeSongFromPlaylistSuccess: Song should not be in playlist after removal", songs.contains(fakeSong));
////    }
//
////    @Test
////    @Transactional
////    public void removeSongFromPlaylistCheckRemoveCheck() {
////        playlistRepo.save(fakePlaylist);
////        songRepo.save(fakeSong);
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        List<Song> songs = playlistService.getSongs(fakePlaylist.getId());
////        assertTrue("removeSongFromPlaylistCheckRemoveCheck: Song should be in playlist before removal", songs.contains(fakeSong));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        songs = playlistService.getSongs(fakePlaylist.getId());
////        assertFalse("removeSongFromPlaylistCheckRemoveCheck: Song should not be in playlist after removal", songs.contains(fakeSong));
////    }
//
////    @Test
////    @Transactional
////    public void removeSongFromPlaylistMultiple() {
////        playlistRepo.save(fakePlaylist);
////        songRepo.save(fakeSong);
////        songRepo.save(fakeSong2);
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong2.getId());
////
////        assertTrue("removeSongFromPlaylistMultiple: Should remove first song", playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId()));
////        List<Song> songs = playlistService.getSongs(fakePlaylist.getId());
////        assertFalse("removeSongFromPlaylistMultiple: First song should not be in playlist", songs.contains(fakeSong));
////        assertTrue("removeSongFromPlaylistMultiple: Second song should still be in playlist", songs.contains(fakeSong2));
////
////        playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong2.getId());
////        songRepo.delete(fakeSong2);
////    }
//
//    // Remove Song from Playlist Crappy
//
//    @Test
//    public void removeSongFromPlaylistNotInPlaylist() {
//        assertFalse("removeSongFromPlaylistNotInPlaylist: Should return false when song not in playlist", playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId()));
//    }
//
////    @Test
////    @Transactional
////    public void removeSongFromPlaylistTwice() {
////        playlistRepo.save(fakePlaylist);
////        songRepo.save(fakeSong);
////        playlistService.addSongToPlaylist(fakePlaylist.getId(), fakeSong.getId());
////        assertTrue("removeSongFromPlaylistTwice: Should return true for first removal", playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId()));
////        assertFalse("removeSongFromPlaylistTwice: Should return false for second removal", playlistService.removeSongFromPlaylist(fakePlaylist.getId(), fakeSong.getId()));
////    }
//
//    @Test
//    public void removeSongFromPlaylistNullPlaylist() {
//        assertFalse("removeSongFromPlaylistNullPlaylist: Should return false for non-existent playlist", playlistService.removeSongFromPlaylist(nullPlaylistID, fakeSong.getId()));
//    }
//
//    @Test
//    public void removeSongFromPlaylistNullSong() {
//        assertFalse("removeSongFromPlaylistNullSong: Should return false for non-existent song", playlistService.removeSongFromPlaylist(fakePlaylist.getId(), nullSongID));
//    }
}