package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
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
class PlaylistServiceImplFullTest {
    private static final Playlist fakePlaylist = new Playlist();
    private static final Playlist fakePlaylist2 = new Playlist();
    private static final Song fakeSong = new Song("test.mp3", "Test Song", "Test Artist");
    private static final Song fakeSong2 = new Song("test2.mp3", "Test Song 2", "Test Artist 2");
    private static final Long playlistId = 1000L;
    private static final Long playlist2Id = 1001L;
    private static final Long songId = 2000L;
    private static final Long song2Id = 2001L;
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

    @BeforeEach
    public void beforeTest() {
        assertNotNull("playlistService must be injected", playlistService);
        assertNotNull("playlistRepo must be injected", playlistRepo);
        assertNotNull("songRepo must be injected", songRepo);

        fakePlaylist2.setId(playlist2Id);
        fakePlaylist2.setClientID(clientID);
        fakePlaylist2.setName("FakePlaylist2");

        fakeSong.setId(songId);

        fakeSong2.setId(song2Id);

        // Ensure dummy record is in the DB
        final List<Playlist> playlists = playlistRepo.getPlaylistsById(playlistId);
        if (playlists.isEmpty()) {
            fakePlaylist.setId(playlistId);
            fakePlaylist.setClientID(clientID);
            fakePlaylist.setName("test");
            fakePlaylist.setDescription("test");

            playlistRepo.save(fakePlaylist);
        }

        // Ensure dummy song is in the DB
        if (!songRepo.existsById(songId)) {
            songRepo.save(fakeSong);
        }
    }

    // Get Playlists Happy Tests

    @Test
    public void getPlaylistsOneSuccess() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);
        assertEquals("getPlaylistsOneSuccess: Should get the one playlist of the client", comparePlaylist, playlistService.getPlaylists(clientID));
    }

    @Test
    public void getPlaylistsTwoSuccess() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);
        comparePlaylist.add(fakePlaylist2);

        playlistRepo.save(fakePlaylist2);

        assertEquals("getPlaylistsTwoSuccess: Should get the two playlists of the client", comparePlaylist, playlistService.getPlaylists(clientID));

        playlistRepo.delete(fakePlaylist2);
    }

    @Test
    public void getPlaylistsTwice() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);

        assertEquals("getPlaylistsTwice: Should work twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
        assertEquals("getPlaylistsTwice: Should work twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
    }

    @Test
    public void getPlaylistsCheckAddCheck() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);

        assertEquals("getPlaylistsCheckAddCheck: Should get the one playlist of the client before insertion", comparePlaylist, playlistService.getPlaylists(clientID));
        playlistRepo.save(fakePlaylist2);
        comparePlaylist.add(fakePlaylist2);
        assertEquals("getPlaylistsCheckAddCheck: Should get the two playlists of the client after insertion", comparePlaylist, playlistService.getPlaylists(clientID));

        playlistRepo.delete(fakePlaylist2);
    }

    @Test
    public void getPlaylistsRemoveCheck() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);
        comparePlaylist.add(fakePlaylist2);

        playlistRepo.save(fakePlaylist2);

        assertEquals("getPlaylistsRemoveCheck: Should get the two playlists of the client before removal", comparePlaylist, playlistService.getPlaylists(clientID));

        playlistRepo.delete(fakePlaylist2);
        comparePlaylist.remove(fakePlaylist2);

        assertEquals("getPlaylistsRemoveCheck: Should get the one playlist of the client after removal", comparePlaylist, playlistService.getPlaylists(clientID));
    }

    // Get Playlists Crappy Tests

    @Test
    public void getPlaylistsOneFailure() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();

        assertEquals("getPlaylistsOneFailure: Should get a null list from the null client", comparePlaylist, playlistService.getPlaylists(nullClientID));
    }

    @Test
    public void getPlaylistsNoPlaylists() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();

        playlistRepo.delete(fakePlaylist);

        assertEquals("getPlaylistsNoPlaylists: Should get no playlists of the client", comparePlaylist, playlistService.getPlaylists(clientID));
    }

    @Test
    public void getPlaylistsNoPlaylistsTwice() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();

        playlistRepo.delete(fakePlaylist);

        assertEquals("getPlaylistsNoPlaylistsTwice: Should get empty list twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
        assertEquals("getPlaylistsNoPlaylistsTwice: Should get empty list twice in a row", comparePlaylist, playlistService.getPlaylists(clientID));
    }

    // Get Playlist by ID Happy

    @Test
    public void getPlaylistByIdSuccess() {
        assertEquals("getPlaylistByIdSuccess: Should return the correct playlist", fakePlaylist, playlistService.getPlaylistById(playlistId));
    }

    @Test
    public void getPlaylistByIdTwice() {
        assertEquals("getPlaylistByIdTwice: Should work twice in a row", fakePlaylist, playlistService.getPlaylistById(playlistId));
        assertEquals("getPlaylistByIdTwice: Should work twice in a row", fakePlaylist, playlistService.getPlaylistById(playlistId));
    }

    @Test
    public void getPlaylistByIdAfterSave() {
        playlistRepo.save(fakePlaylist2);
        assertEquals("getPlaylistByIdAfterSave: Should return the newly saved playlist", fakePlaylist2, playlistService.getPlaylistById(playlist2Id));
        playlistRepo.delete(fakePlaylist2);
    }

    // Get Playlist by ID Crappy

    @Test
    public void getPlaylistByIdNotFound() {
        assertNull("getPlaylistByIdNotFound: Should return null for non-existent playlist", playlistService.getPlaylistById(nullPlaylistID));
    }

    @Test
    public void getPlaylistByIdAfterDelete() {
        playlistRepo.save(fakePlaylist2);
        assertEquals("getPlaylistByIdAfterDelete: Should return playlist before delete", fakePlaylist2, playlistService.getPlaylistById(playlist2Id));
        playlistRepo.delete(fakePlaylist2);
        assertNull("getPlaylistByIdAfterDelete: Should return null after delete", playlistService.getPlaylistById(playlist2Id));
    }

    // Get Songs Happy

    @Test
    public void getSongsEmpty() {
        List<Song> compareSongs = new LinkedList<Song>();
        assertEquals("getSongsEmpty: Should return empty list for playlist with no songs", compareSongs, playlistService.getSongs(playlistId));
    }

    @Test
    public void getSongsOneSuccess() {
        List<Song> compareSongs = new LinkedList<Song>();
        compareSongs.add(fakeSong);

        playlistService.addSongToPlaylist(playlistId, songId);
        assertEquals("getSongsOneSuccess: Should return one song", compareSongs, playlistService.getSongs(playlistId));

        playlistService.removeSongFromPlaylist(playlistId, songId);
    }

    @Test
    public void getSongsTwoSuccess() {
        List<Song> compareSongs = new LinkedList<Song>();
        compareSongs.add(fakeSong);
        compareSongs.add(fakeSong2);

        songRepo.save(fakeSong2);
        playlistService.addSongToPlaylist(playlistId, songId);
        playlistService.addSongToPlaylist(playlistId, song2Id);

        assertEquals("getSongsTwoSuccess: Should return two songs", compareSongs, playlistService.getSongs(playlistId));

        playlistService.removeSongFromPlaylist(playlistId, songId);
        playlistService.removeSongFromPlaylist(playlistId, song2Id);
        songRepo.delete(fakeSong2);
    }

    @Test
    public void getSongsCheckAddCheck() {
        List<Song> compareSongs = new LinkedList<Song>();

        assertEquals("getSongsCheckAddCheck: Should return empty before add", compareSongs, playlistService.getSongs(playlistId));

        playlistService.addSongToPlaylist(playlistId, songId);
        compareSongs.add(fakeSong);

        assertEquals("getSongsCheckAddCheck: Should return one song after add", compareSongs, playlistService.getSongs(playlistId));

        playlistService.removeSongFromPlaylist(playlistId, songId);
    }

    // Get Songs Crappy

    @Test
    public void getSongsNullPlaylist() {
        List<Song> compareSongs = new LinkedList<Song>();
        assertEquals("getSongsNullPlaylist: Should return empty list for non-existent playlist", compareSongs, playlistService.getSongs(nullPlaylistID));
    }

    @Test
    public void getSongsAfterRemoval() {
        List<Song> compareSongs = new LinkedList<Song>();

        playlistService.addSongToPlaylist(playlistId, songId);
        playlistService.removeSongFromPlaylist(playlistId, songId);

        assertEquals("getSongsAfterRemoval: Should return empty list after removing song", compareSongs, playlistService.getSongs(playlistId));
    }

    // Save Playlist Happy

    @Test
    public void savePlaylistSuccess() {
        assertTrue("SavePlaylistSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
        assertEquals("SavePlaylistSuccess: Playlist should be saved to client", fakePlaylist2, playlistService.getPlaylistById(playlist2Id));
        playlistRepo.delete(fakePlaylist2);
    }

    @Test
    public void savePlaylistTwoSuccess() {
        playlistRepo.delete(fakePlaylist);

        assertTrue("savePlaylistTwoSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist));
        assertEquals("savePlaylistTwoSuccess: Playlist should be saved to client", fakePlaylist, playlistService.getPlaylistById(playlistId));
        assertTrue("savePlaylistTwoSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
        assertEquals("savePlaylistTwoSuccess: Playlist should be saved to client", fakePlaylist2, playlistService.getPlaylistById(playlist2Id));

        playlistRepo.delete(fakePlaylist2);
    }

    @Test
    public void savePlaylistBeforeRemovalFalseAfterTrue() {
        assertNull("savePlaylistBeforeRemovalFalseAfterTrue: Client should not have playlist", playlistService.getPlaylistById(playlist2Id));
        assertTrue("savePlaylistBeforeRemovalFalseAfterTrue: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
        assertEquals("savePlaylistBeforeRemovalFalseAfterTrue: Playlist should be saved to client", fakePlaylist2, playlistService.getPlaylistById(playlist2Id));

        playlistRepo.delete(fakePlaylist2);
    }

    // Save Playlist Crappy

    @Test
    public void savePlaylistDuplicate() {
        playlistRepo.delete(fakePlaylist);

        assertTrue("SavePlaylistDuplicate: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist));
        assertFalse("SavePlaylistDuplicate: Should return false for duplicate", playlistService.savePlaylist(fakePlaylist));
    }

    // Delete Playlist Happy

    @Test
    public void deletePlaylistSuccess() {
        playlistRepo.save(fakePlaylist2);
        assertTrue("deletePlaylistSuccess: Should return true for successful deletion", playlistService.deletePlaylist(playlist2Id));
        assertNull("deletePlaylistSuccess: Playlist should no longer exist", playlistService.getPlaylistById(playlist2Id));
    }

    @Test
    public void deletePlaylistCheckDeleteCheck() {
        playlistRepo.save(fakePlaylist2);
        assertNotNull("deletePlaylistCheckDeleteCheck: Playlist should exist before delete", playlistService.getPlaylistById(playlist2Id));
        assertTrue("deletePlaylistCheckDeleteCheck: Should return true for successful deletion", playlistService.deletePlaylist(playlist2Id));
        assertNull("deletePlaylistCheckDeleteCheck: Playlist should not exist after delete", playlistService.getPlaylistById(playlist2Id));
    }

    // Delete Playlist Crappy

    @Test
    public void deletePlaylistNotFound() {
        assertFalse("deletePlaylistNotFound: Should return false for non-existent playlist", playlistService.deletePlaylist(nullPlaylistID));
    }

    @Test
    public void deletePlaylistTwice() {
        playlistRepo.save(fakePlaylist2);
        assertTrue("deletePlaylistTwice: Should return true for first deletion", playlistService.deletePlaylist(playlist2Id));
        assertFalse("deletePlaylistTwice: Should return false for second deletion", playlistService.deletePlaylist(playlist2Id));
    }

    // Add Song to Playlist Happy

    @Test
    public void addSongToPlaylistSuccess() {
        assertTrue("addSongToPlaylistSuccess: Should return true for successful addition", playlistService.addSongToPlaylist(playlistId, songId));
        List<Song> songs = playlistService.getSongs(playlistId);
        assertTrue("addSongToPlaylistSuccess: Song should be in playlist", songs.contains(fakeSong));

        playlistService.removeSongFromPlaylist(playlistId, songId);
    }

    @Test
    public void addSongToPlaylistTwo() {
        songRepo.save(fakeSong2);

        assertTrue("addSongToPlaylistTwo: Should add first song", playlistService.addSongToPlaylist(playlistId, songId));
        assertTrue("addSongToPlaylistTwo: Should add second song", playlistService.addSongToPlaylist(playlistId, song2Id));

        List<Song> songs = playlistService.getSongs(playlistId);
        assertTrue("addSongToPlaylistTwo: First song should be in playlist", songs.contains(fakeSong));
        assertTrue("addSongToPlaylistTwo: Second song should be in playlist", songs.contains(fakeSong2));

        playlistService.removeSongFromPlaylist(playlistId, songId);
        playlistService.removeSongFromPlaylist(playlistId, song2Id);
        songRepo.delete(fakeSong2);
    }

    @Test
    public void addSongToPlaylistCheckAddCheck() {
        List<Song> songs = playlistService.getSongs(playlistId);
        assertFalse("addSongToPlaylistCheckAddCheck: Song should not be in playlist before add", songs.contains(fakeSong));

        playlistService.addSongToPlaylist(playlistId, songId);
        songs = playlistService.getSongs(playlistId);
        assertTrue("addSongToPlaylistCheckAddCheck: Song should be in playlist after add", songs.contains(fakeSong));

        playlistService.removeSongFromPlaylist(playlistId, songId);
    }

    // Add Song to Playlist Crappy

    @Test
    public void addSongToPlaylistDuplicate() {
        assertTrue("addSongToPlaylistDuplicate: Should add song first time", playlistService.addSongToPlaylist(playlistId, songId));
        assertFalse("addSongToPlaylistDuplicate: Should return false for duplicate", playlistService.addSongToPlaylist(playlistId, songId));

        playlistService.removeSongFromPlaylist(playlistId, songId);
    }

    @Test
    public void addSongToPlaylistNullPlaylist() {
        assertFalse("addSongToPlaylistNullPlaylist: Should return false for non-existent playlist", playlistService.addSongToPlaylist(nullPlaylistID, songId));
    }

    @Test
    public void addSongToPlaylistNullSong() {
        assertFalse("addSongToPlaylistNullSong: Should return false for non-existent song", playlistService.addSongToPlaylist(playlistId, nullSongID));
    }

    // Remove Song from Playlist Happy

    @Test
    public void removeSongFromPlaylistSuccess() {
        playlistService.addSongToPlaylist(playlistId, songId);
        assertTrue("removeSongFromPlaylistSuccess: Should return true for successful removal", playlistService.removeSongFromPlaylist(playlistId, songId));
        List<Song> songs = playlistService.getSongs(playlistId);
        assertFalse("removeSongFromPlaylistSuccess: Song should not be in playlist after removal", songs.contains(fakeSong));
    }

    @Test
    public void removeSongFromPlaylistCheckRemoveCheck() {
        playlistService.addSongToPlaylist(playlistId, songId);
        List<Song> songs = playlistService.getSongs(playlistId);
        assertTrue("removeSongFromPlaylistCheckRemoveCheck: Song should be in playlist before removal", songs.contains(fakeSong));

        playlistService.removeSongFromPlaylist(playlistId, songId);
        songs = playlistService.getSongs(playlistId);
        assertFalse("removeSongFromPlaylistCheckRemoveCheck: Song should not be in playlist after removal", songs.contains(fakeSong));
    }

    @Test
    public void removeSongFromPlaylistMultiple() {
        songRepo.save(fakeSong2);
        playlistService.addSongToPlaylist(playlistId, songId);
        playlistService.addSongToPlaylist(playlistId, song2Id);

        assertTrue("removeSongFromPlaylistMultiple: Should remove first song", playlistService.removeSongFromPlaylist(playlistId, songId));
        List<Song> songs = playlistService.getSongs(playlistId);
        assertFalse("removeSongFromPlaylistMultiple: First song should not be in playlist", songs.contains(fakeSong));
        assertTrue("removeSongFromPlaylistMultiple: Second song should still be in playlist", songs.contains(fakeSong2));

        playlistService.removeSongFromPlaylist(playlistId, song2Id);
        songRepo.delete(fakeSong2);
    }

    // Remove Song from Playlist Crappy

    @Test
    public void removeSongFromPlaylistNotInPlaylist() {
        assertFalse("removeSongFromPlaylistNotInPlaylist: Should return false when song not in playlist", playlistService.removeSongFromPlaylist(playlistId, songId));
    }

    @Test
    public void removeSongFromPlaylistTwice() {
        playlistService.addSongToPlaylist(playlistId, songId);
        assertTrue("removeSongFromPlaylistTwice: Should return true for first removal", playlistService.removeSongFromPlaylist(playlistId, songId));
        assertFalse("removeSongFromPlaylistTwice: Should return false for second removal", playlistService.removeSongFromPlaylist(playlistId, songId));
    }

    @Test
    public void removeSongFromPlaylistNullPlaylist() {
        assertFalse("removeSongFromPlaylistNullPlaylist: Should return false for non-existent playlist", playlistService.removeSongFromPlaylist(nullPlaylistID, songId));
    }

    @Test
    public void removeSongFromPlaylistNullSong() {
        assertFalse("removeSongFromPlaylistNullSong: Should return false for non-existent song", playlistService.removeSongFromPlaylist(playlistId, nullSongID));
    }
}