package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.PlaylistRepo;
import com.groovify.jpa.repo.SongRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link PlaylistService} interface for managing playlists
 * and their associated songs.
 *
 * <p>This service provides business logic for playlist operations such as retrieving,
 * creating, updating, and deleting playlists, as well as adding or removing songs
 * from playlists.</p>
 *
 * <p>The class leverages Spring Data JPA repositories ({@link PlaylistRepo} and {@link SongRepo})
 * to perform database operations and is annotated with {@link Service} to mark it
 * as a Spring-managed service component.</p>
 *
 * <p>Transactional methods are used where necessary to ensure database consistency
 * when modifying playlists and song relationships.</p>
 *
 * @author Nevin Fullerton
 * @version 1.0
 * @see PlaylistService
 * @see PlaylistRepo
 * @see SongRepo
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepo playlistRepo;
    private final SongRepo songRepo;

    /**
     * Constructs a new {@code PlaylistServiceImpl} with the required repositories.
     *
     * @param playlistRepo the repository for managing {@link Playlist} entities
     * @param songRepo the repository for managing {@link Song} entities
     */
    public PlaylistServiceImpl(PlaylistRepo playlistRepo, SongRepo songRepo) {
        this.playlistRepo = playlistRepo;
        this.songRepo = songRepo;
    }

    /**
     * Retrieves all playlists associated with a given client.
     *
     * @param clientID the ID of the client
     * @return a list of {@link Playlist} objects belonging to the specified client
     */
    @Override
    public List<Playlist> getPlaylists(Long clientID) {
        return playlistRepo.findByClientID(clientID);
    }

    /**
     * Retrieves all songs contained within a specific playlist.
     *
     * @param playlistId the ID of the playlist
     * @return a list of {@link Song} objects in the specified playlist, or an empty list if not found
     */
    @Override
    public List<Song> getSongs(Long playlistId) {
        Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
        if (playlist == null) return List.of();
        return playlist.getSongs();
    }

    /**
     * Saves or updates a playlist in the database.
     *
     * @param playlist the {@link Playlist} entity to save
     */
    @Override
    public void savePlaylist(Playlist playlist) {
        playlistRepo.save(playlist);
    }

    /**
     * Deletes a playlist by its unique identifier.
     *
     * @param id the ID of the playlist to delete
     */
    @Override
    public void deletePlaylist(Long id) {
        playlistRepo.deleteById(id);
    }

    /**
     * Retrieves a playlist by its unique identifier.
     *
     * @param id the ID of the playlist
     * @return the {@link Playlist} entity if found, or {@code null} if not found
     */
    @Override
    public Playlist getPlaylistById(Long id) {
        return playlistRepo.findById(id).orElse(null);
    }

    /**
     * Adds a song to a playlist if it is not already present.
     * <p>This operation is transactional to ensure consistency
     * when modifying the playlist-song relationship.</p>
     *
     * @param playlistId the ID of the playlist to update
     * @param songId the ID of the song to add
     * @throws RuntimeException if either the playlist or song cannot be found
     */
    @Transactional
    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found: " + playlistId));
        Song song = songRepo.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found: " + songId));

        if (playlist.getSongs() == null) {
            playlist.setSongs(new ArrayList<>());
        }

        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
            playlistRepo.save(playlist);
        }
    }

    /**
     * Removes a song from a playlist if it exists in that playlist.
     *
     * @param playlistId the ID of the playlist to modify
     * @param songId the ID of the song to remove
     */
    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
        Song song = songRepo.findById(songId).orElse(null);

        if (playlist != null && song != null && playlist.getSongs() != null) {
            playlist.getSongs().remove(song);
            playlistRepo.save(playlist);
        }
    }
}
