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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PlaylistServiceImplFullTest {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private PlaylistRepo playlistRepo;

    @Autowired
    private SongRepo songRepo;

    private Playlist playlist;
    private Song song1;
    private Song song2;

    @BeforeEach
    void setUp() {
        playlist = new Playlist();
        playlist.setName("Test Playlist");
        playlist.setDescription("Desc");
        playlist.setClientID(1L);
        playlistRepo.save(playlist);

        song1 = new Song("song1.mp3", "Song 1", "Artist1");
        songRepo.save(song1);

        song2 = new Song("song2.mp3", "Song 2", "Artist2");
        songRepo.save(song2);
    }

    // ================== ADD SONG ==================
    @Test
    void happyAddSong() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(1, songs.size());
        assertEquals("Song 1", songs.get(0).getTitle());
    }

    @Test
    void happyAddTwoSongs() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.addSongToPlaylist(playlist.getId(), song2.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(2, songs.size());
    }

    @Test
    void crappyAddSongPlaylistNotFound() {
        Exception ex = assertThrows(RuntimeException.class,
                () -> playlistService.addSongToPlaylist(999L, song1.getId()));
        assertTrue(ex.getMessage().contains("Playlist not found"));
    }

    @Test
    void crappyAddSongSongNotFound() {
        Exception ex = assertThrows(RuntimeException.class,
                () -> playlistService.addSongToPlaylist(playlist.getId(), 999L));
        assertTrue(ex.getMessage().contains("Song not found"));
    }

    @Test
    void crazyAddSameSongTwice() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(1, songs.size());
    }

    @Test
    void crazyAddSongWithNullSongList() {
        playlist.setSongs(null);
        playlistRepo.save(playlist);
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(1, songs.size());
    }

    @Test
    void crazyAddSongExtremeIds() {
        Song extreme = new Song();
        extreme.setTitle("Extreme");
        extreme.setFilename("extreme.mp3");
        songRepo.save(extreme);
        playlistService.addSongToPlaylist(playlist.getId(), extreme.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertTrue(songs.stream().anyMatch(s -> s.getTitle().equals("Extreme")));
    }

    // ================== REMOVE SONG ==================
    @Test
    void happyRemoveSong() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.removeSongFromPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertTrue(songs.isEmpty());
    }

    @Test
    void happyRemoveOneOfTwoSongs() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.addSongToPlaylist(playlist.getId(), song2.getId());
        playlistService.removeSongFromPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(1, songs.size());
        assertEquals("Song 2", songs.get(0).getTitle());
    }

    @Test
    void crappyRemoveNonExistentPlaylist() {
        playlistService.removeSongFromPlaylist(999L, song1.getId());
    }

    @Test
    void crappyRemoveNonExistentSong() {
        playlistService.removeSongFromPlaylist(playlist.getId(), 999L);
    }

    @Test
    void crazyRemoveSongEmptyPlaylist() {
        playlistService.removeSongFromPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertTrue(songs.isEmpty());
    }

    @Test
    void crazyRemoveNullSongList() {
        playlist.setSongs(null);
        playlistRepo.save(playlist);
        playlistService.removeSongFromPlaylist(playlist.getId(), song1.getId());
        assertNull(playlistService.getPlaylistById(playlist.getId()).getSongs());
    }

    @Test
    void crazyRemoveSameSongTwice() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.removeSongFromPlaylist(playlist.getId(), song1.getId());
        playlistService.removeSongFromPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertTrue(songs.isEmpty());
    }

    // ================== GET PLAYLISTS ==================
    @Test
    void happyGetPlaylists() {
        List<Playlist> playlists = playlistService.getPlaylists(1L);
        assertEquals(1, playlists.size());
    }

    @Test
    void happyGetMultiplePlaylists() {
        Playlist extra = new Playlist();
        extra.setName("Extra");
        extra.setClientID(1L);
        playlistRepo.save(extra);
        List<Playlist> playlists = playlistService.getPlaylists(1L);
        assertEquals(2, playlists.size());
    }

    @Test
    void crappyGetNonExistentClient() {
        List<Playlist> playlists = playlistService.getPlaylists(999L);
        assertTrue(playlists.isEmpty());
    }

    @Test
    void crazyGetPlaylistsNullClientId() {
        List<Playlist> playlists = playlistService.getPlaylists(null);
        assertTrue(playlists.isEmpty());
    }

    @Test
    void crazyGetPlaylistsNoPlaylists() {
        playlistRepo.deleteAll();
        List<Playlist> playlists = playlistService.getPlaylists(1L);
        assertTrue(playlists.isEmpty());
    }

    @Test
    void crazyGetPlaylistsLargeClientId() {
        Playlist p = new Playlist();
        p.setClientID(Long.MAX_VALUE);
        p.setName("Big Client");
        playlistRepo.save(p);
        List<Playlist> playlists = playlistService.getPlaylists(Long.MAX_VALUE);
        assertEquals(1, playlists.size());
    }

    @Test
    void crazyGetPlaylistsMixedClientIds() {
        Playlist p2 = new Playlist();
        p2.setClientID(2L);
        p2.setName("Other Client");
        playlistRepo.save(p2);
        List<Playlist> playlists = playlistService.getPlaylists(1L);
        assertEquals(1, playlists.size());
    }

    // ================== GET SONGS ==================
    @Test
    void happyGetSongs() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(1, songs.size());
    }

    @Test
    void happyGetMultipleSongs() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.addSongToPlaylist(playlist.getId(), song2.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(2, songs.size());
    }

    @Test
    void crappyGetSongsPlaylistNotFound() {
        List<Song> songs = playlistService.getSongs(999L);
        assertTrue(songs.isEmpty());
    }

    @Test
    void crazyGetSongsNullSongList() {
        playlist.setSongs(null);
        playlistRepo.save(playlist);
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertTrue(songs.isEmpty());
    }

    @Test
    void crazyGetSongsEmptyPlaylist() {
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertTrue(songs.isEmpty());
    }

    @Test
    void crazyGetSongsAfterAddRemove() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.removeSongFromPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertTrue(songs.isEmpty());
    }

    @Test
    void crazyGetSongsAddSameSongTwice() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        List<Song> songs = playlistService.getSongs(playlist.getId());
        assertEquals(1, songs.size());
    }

    // ================== SAVE PLAYLIST ==================
    @Test
    void happySavePlaylist() {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setName("Saved");
        newPlaylist.setClientID(1L);
        playlistService.savePlaylist(newPlaylist);
        assertNotNull(newPlaylist.getId());
    }

    @Test
    void happySavePlaylistUpdate() {
        playlist.setName("Updated");
        playlistService.savePlaylist(playlist);
        Playlist updated = playlistService.getPlaylistById(playlist.getId());
        assertEquals("Updated", updated.getName());
    }

    @Test
    void crappySavePlaylistNullName() {
        Playlist p = new Playlist();
        p.setClientID(1L);
        playlistService.savePlaylist(p);
        assertNotNull(p.getId());
    }

    @Test
    void crazySavePlaylistNoClientId() {
        Playlist p = new Playlist();
        p.setName("NoClient");
        playlistService.savePlaylist(p);
        assertNotNull(p.getId());
    }

    @Test
    void crazySavePlaylistEmptyDescription() {
        Playlist p = new Playlist();
        p.setName("EmptyDesc");
        p.setClientID(1L);
        p.setDescription("");
        playlistService.savePlaylist(p);
        assertNotNull(p.getId());
    }

    @Test
    void crazySavePlaylistDuplicateName() {
        Playlist dup = new Playlist();
        dup.setName(playlist.getName());
        dup.setClientID(1L);
        playlistService.savePlaylist(dup);
        assertNotNull(dup.getId());
    }

    @Test
    void crazySavePlaylistAllNulls() {
        Playlist p = new Playlist();
        playlistService.savePlaylist(p);
        assertNotNull(p.getId());
    }

    // ================== DELETE PLAYLIST ==================
    @Test
    void happyDeletePlaylist() {
        Long id = playlist.getId();
        playlistService.deletePlaylist(id);
        assertNull(playlistService.getPlaylistById(id));
    }

    @Test
    void happyDeleteThenAddAgain() {
        Long id = playlist.getId();
        playlistService.deletePlaylist(id);
        Playlist newP = new Playlist();
        newP.setName("New");
        newP.setClientID(1L);
        playlistService.savePlaylist(newP);
        assertNotNull(newP.getId());
    }

    @Test
    void crappyDeleteNonExistentPlaylist() {
        playlistService.deletePlaylist(999L);
    }

    @Test
    void crazyDeleteTwice() {
        Long id = playlist.getId();
        playlistService.deletePlaylist(id);
        playlistService.deletePlaylist(id);
    }

    @Test
    void crazyDeletePlaylistWithSongs() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        playlistService.deletePlaylist(playlist.getId());
        assertNull(playlistService.getPlaylistById(playlist.getId()));
    }

    @Test
    void crazyDeletePlaylistNullId() {
        playlistService.deletePlaylist(null);
    }

    @Test
    void crazyDeletePlaylistManyTimes() {
        for (int i = 0; i < 10; i++) {
            Playlist p = new Playlist();
            p.setName("Loop" + i);
            p.setClientID(1L);
            playlistService.savePlaylist(p);
            playlistService.deletePlaylist(p.getId());
            assertNull(playlistService.getPlaylistById(p.getId()));
        }
    }

    // ================== GET PLAYLIST BY ID ==================
    @Test
    void happyGetPlaylistById() {
        Playlist found = playlistService.getPlaylistById(playlist.getId());
        assertNotNull(found);
    }

    @Test
    void happyGetPlaylistAfterUpdate() {
        playlist.setName("Updated Name");
        playlistService.savePlaylist(playlist);
        Playlist found = playlistService.getPlaylistById(playlist.getId());
        assertEquals("Updated Name", found.getName());
    }

    @Test
    void crappyGetPlaylistNonExistentId() {
        Playlist found = playlistService.getPlaylistById(999L);
        assertNull(found);
    }

    @Test
    void crazyGetPlaylistNullId() {
        Playlist found = playlistService.getPlaylistById(null);
        assertNull(found);
    }

    @Test
    void crazyGetPlaylistAfterDelete() {
        Long id = playlist.getId();
        playlistService.deletePlaylist(id);
        Playlist found = playlistService.getPlaylistById(id);
        assertNull(found);
    }

    @Test
    void crazyGetPlaylistMultiplePlaylists() {
        Playlist extra = new Playlist();
        extra.setName("Extra");
        extra.setClientID(1L);
        playlistService.savePlaylist(extra);
        Playlist found = playlistService.getPlaylistById(extra.getId());
        assertEquals("Extra", found.getName());
    }

    @Test
    void crazyGetPlaylistWithSongs() {
        playlistService.addSongToPlaylist(playlist.getId(), song1.getId());
        Playlist found = playlistService.getPlaylistById(playlist.getId());
        assertNotNull(found.getSongs());
        assertEquals(1, found.getSongs().size());
    }
}
