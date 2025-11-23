package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.jpa.repo.PlaylistRepo;
import com.groovify.jpa.repo.SongRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final SongService songService;
    private final Logger log = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    /**
     * Constructs a new {@code PlaylistServiceImpl} with the required repositories.
     *
     * @param playlistRepo the repository for managing {@link Playlist} entities
     * @param songService the repository for managing {@link Song} entities
     */
    public PlaylistServiceImpl(PlaylistRepo playlistRepo, SongService songService) {
        this.playlistRepo = playlistRepo;
        this.songService = songService;
    }

    /**
     * Retrieves all playlists associated with a given client.
     *
     * @param clientID the ID of the client
     * @return a list of {@link Playlist} objects belonging to the specified client
     */
    @Override
    public List<Playlist> getPlaylistsByClientId(Long clientID) {
        log.info("Getting playlists for User {}", clientID);
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
        log.info("Getting songs for Playlist {}", playlistId);

        if (playlistId == null) {
            log.error("Playlistid is null");
            return List.of();
        }

        Playlist playlist = playlistRepo.findById(playlistId).orElse(null);

        if (playlist == null) {
            log.warn("Playlist not found");
            return List.of();
        }

        return playlist.getSongs();
    }

    /**
     * Saves a playlist in the database.
     *
     * @param playlist the {@link Playlist} entity to save
     * @return {@code true} if the playlist was successfully saved, {@code false} otherwise
     */
    @Override
    public boolean savePlaylist(Playlist playlist) {
        log.info("Saving playlist titled {}", playlist.getName());
        try {
            if (playlist.getClientID() == null) {
                log.error("Playlist titled {} has null client id", playlist.getName());
                return false;
            }

            if (playlist.getName() == null) {
                log.error("Playlist titled {} has null name", playlist.getName());
                return false;
            }

            if (playlist.getDescription() == null) {
                log.error("Playlist titled {} has null description", playlist.getName());
                return false;
            }

            playlistRepo.save(playlist);
            log.info("Playlist {} saved", playlist.getId());
            return true;
        } catch (Exception e) {
            log.error("Error while saving playlist {}", playlist.getId(), e);
            return false;
        }
    }

    /**
     * Deletes a playlist by its unique identifier.
     *
     * @param id the ID of the playlist to delete
     * @return {@code true} if the playlist was successfully deleted, {@code false} if not found or deletion failed
     */
    @Override
    public boolean deletePlaylist(Long id) {
        log.info("Deleting playlist {}", id);
        try {
            if (!playlistRepo.existsById(id)) {
                log.warn("Playlist {} not found", id);
                return false;
            }
            playlistRepo.deleteById(id);
            log.info("Playlist {} deleted", id);
            return true;
        } catch (Exception e) {
            log.error("Error while deleting playlist {}", id, e);
            return false;
        }
    }

    /**
     * Retrieves a playlist by its unique identifier.
     *
     * @param id the ID of the playlist
     * @return the {@link Playlist} entity if found, or {@code null} if not found
     */
    @Override
    public Playlist getPlaylistById(Long id) {
        log.info("Getting playlist {}", id);

        if (id == null) {
            log.error("Playlist id is null");
            return null;
        }

        return playlistRepo.findById(id).orElse(null);
    }

    /**
     * Adds a song to a playlist if it is not already present.
     * <p>This operation is transactional to ensure consistency
     * when modifying the playlist-song relationship.</p>
     *
     * @param playlistId the ID of the playlist to update
     * @param songId the ID of the song to add
     * @return {@code true} if the song was successfully added, {@code false} if not found or already exists
     */
    @Transactional
    @Override
    public boolean addSongToPlaylist(Long playlistId, Long songId) {
        log.info("Adding song {} to playlist {}", songId, playlistId);
        try {
            Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
            Song song = songService.getSongById(songId);

            if (playlist == null || song == null) {
                log.error("Playlist {} not found or Song {} not found", playlistId, songId);
                return false;
            }

            if (playlist.getSongs().contains(song)) {
                log.error("Song {} is already in playlist {}", songId, playlistId);
                return false; // Song already in playlist
            }

            playlist.getSongs().add(song);
            playlistRepo.save(playlist);
            log.info("Song {} added to playlist {}", songId, playlistId);
            return true;
        } catch (Exception e) {
            log.error("Error while adding song {}", songId, e);
            return false;
        }
    }

    /**
     * Removes a song from a playlist if it exists in that playlist.
     *
     * @param playlistId the ID of the playlist to modify
     * @param songId the ID of the song to remove
     * @return {@code true} if the song was successfully removed, {@code false} if not found or removal failed
     */
    @Override
    public boolean removeSongFromPlaylist(Long playlistId, Long songId) {
        log.info("Removing song {} from playlist {}", songId, playlistId);
        try {
            Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
            Song song = songService.getSongById(songId);

            if (playlist == null || song == null || playlist.getSongs() == null) {
                log.error("Playlist {} not found or Song {} not found", playlistId, songId);
                return false;
            }

            boolean removed = playlist.getSongs().remove(song);
            if (removed) {
                playlistRepo.save(playlist);
                log.info("Song {} removed from playlist {}", songId, playlistId);
            }
            return removed;
        } catch (Exception e) {
            log.error("Error while removing song {} from playlist {}", songId, playlistId, e);
            return false;
        }
    }
}