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
    private static final Long playlistId = 1000L;
    private static final Long playlist2Id = 1001L;
    private static final Long clientID = 10000L;
    private static final Long nullClientID = 100001L;

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

        fakePlaylist2.setId(playlist2Id);
        fakePlaylist2.setClientID(clientID);
        fakePlaylist2.setName("FakePlaylist2");



        // Ensure dummy record is in the DB
        final List<Playlist> playlists = playlistRepo.getPlaylistsById(playlistId);
        if (playlists.isEmpty()) {
            fakePlaylist.setId(playlistId);
            fakePlaylist.setClientID(clientID);
            fakePlaylist.setName("test");
            fakePlaylist.setDescription("test");

            playlistRepo.save(fakePlaylist);
        }
    }

    // Get Playlists Happy Tests

    @Test
    public void getPlaylistsOneSuccess() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);
        assertEquals("getPlaylistsOneSuccess: Should get the one playlist of the client", comparePlaylist, playlistService.getPlaylistById(clientID));
    }

    @Test
    public void getPlaylistsTwoSuccess() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);
        comparePlaylist.add(fakePlaylist2);

        playlistRepo.save(fakePlaylist2);

        assertEquals("getPlaylistsTwoSuccess: Should get the two playlists of the client", comparePlaylist, playlistService.getPlaylistById(clientID));

        playlistRepo.delete(fakePlaylist2);
    }

    @Test
    public void getPlaylistsTwice() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);

        assertEquals("getPlaylistsTwice: Should work twice in a row", comparePlaylist, playlistService.getPlaylistById(clientID));
        assertEquals("getPlaylistsTwice: Should work twice in a row", comparePlaylist, playlistService.getPlaylistById(clientID));
    }

    @Test
    public void getPlaylistsCheckAddCheck() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);


        assertEquals("getPlaylistsCheckAddCheck: Should get the one playlist of the client before insertion", comparePlaylist, playlistService.getPlaylistById(clientID));
        playlistRepo.save(fakePlaylist2);
        comparePlaylist.add(fakePlaylist2);
        assertEquals("getPlaylistsCheckAddCheck: Should get the two playlists of the client after insertion", comparePlaylist, playlistService.getPlaylistById(clientID));

        playlistRepo.delete(fakePlaylist2);
    }

    @Test
    public void getPlaylistsRemoveCheck() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();
        comparePlaylist.add(fakePlaylist);
        comparePlaylist.add(fakePlaylist2);

        playlistRepo.save(fakePlaylist2);

        assertEquals("getPlaylistsRemoveCheck: Should get the two playlists of the client before removal", comparePlaylist, playlistService.getPlaylistById(clientID));

        playlistRepo.delete(fakePlaylist2);
        comparePlaylist.remove(fakePlaylist2);

        assertEquals("getPlaylistsRemoveCheck: Should get the one playlist of the client after removal", comparePlaylist, playlistService.getPlaylistById(clientID));
    }

    // Get Playlists Crappy Tests

    @Test
    public void getPlaylistsOneFailure() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();

        assertEquals("getPlaylistsOneFailure: Should get a null list from the null client", comparePlaylist, playlistService.getPlaylistById(nullClientID));
    }

    @Test
    public void getPlaylistsNoPlaylists() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();

        playlistRepo.delete(fakePlaylist);

        assertEquals("getPlaylistsNoPlaylists: Should get no playlists of the client", comparePlaylist, playlistService.getPlaylistById(clientID));

    }

    @Test
    public void getPlaylistsNoPlaylistsTwice() {
        List<Playlist> comparePlaylist = new LinkedList<Playlist>();

        playlistRepo.delete(fakePlaylist);

        assertEquals("getPlaylistsNoPlaylistsTwice: Should get empty list twice in a row", comparePlaylist, playlistService.getPlaylistById(clientID));
        assertEquals("getPlaylistsNoPlaylistsTwice: Should get empty list twice in a row", comparePlaylist, playlistService.getPlaylistById(clientID));
    }

    // Get Playlist by ID Happy

    // Get Playlist by ID Crappy

    // Get Songs Happy

    // Get Songs Crappy

    // Save Playlist Happy

    @Test
    public void savePlaylistSuccess() {
        assertTrue("SavePlaylistSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
        assertTrue("SavePlaylistSuccess: Playlist should be saved to client", fakePlaylist2.equals(playlistService.getPlaylistById(playlist2Id)));
    }

    @Test
    public void savePlaylistTwoSuccess() {
        playlistRepo.delete(fakePlaylist);

        assertTrue("savePlaylistTwoSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist));
        assertTrue("savePlaylistTwoSuccess: Playlist should be saved to client", fakePlaylist.equals(playlistService.getPlaylistById(playlistId)));
        assertTrue("savePlaylistTwoSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
        assertTrue("savePlaylistTwoSuccess: Playlist should be saved to client", fakePlaylist2.equals(playlistService.getPlaylistById(playlist2Id)));
    }

    @Test
    public void savePlaylistBeforeRemovalFalseAfterTrue() {
        assertFalse("savePlaylistTwoSuccess: Client should not have playlist", fakePlaylist2.equals(playlistService.getPlaylistById(playlist2Id)));
        assertTrue("savePlaylistTwoSuccess: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist2));
        assertTrue("savePlaylistTwoSuccess: Playlist should be saved to client", fakePlaylist2.equals(playlistService.getPlaylistById(playlist2Id)));
    }

    // Save Playlist Crappy

    @Test
    public void savePlaylistDuplicate() {
        playlistRepo.delete(fakePlaylist);

        assertTrue("SavePlaylistDuplicate: Should return true for successful insertion", playlistService.savePlaylist(fakePlaylist));
        assertFalse("SavePlaylistDuplicate: Should return false for duplicate", playlistService.savePlaylist(fakePlaylist));
    }

}