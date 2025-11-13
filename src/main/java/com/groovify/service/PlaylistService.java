package com.groovify.service;

import com.groovify.jpa.model.Playlist;
import com.groovify.jpa.model.Song;
import java.util.List;

/**
 * Service interface for managing playlists and their associated songs.
 *
 * <p>This interface defines business logic operations for retrieving,
 * creating, updating, and deleting playlists, as well as for managing
 * song associations within a playlist.</p>
 *
 * <p>Implementations of this service typically interact with repository
 * classes (such as {@code PlaylistRepo} and {@code SongRepo}) to perform
 * database operations, and may include validation or transactional logic.</p>
 *
 * @author Nevin Fullerton
 * @version 1.0
 * @see com.groovify.jpa.model.Playlist
 * @see com.groovify.jpa.model.Song
 */
public interface PlaylistService {

    /**
     * Retrieves all playlists associated with a specific client.
     *
     * @param id the ID of the client
     * @return a list of {@link Playlist} objects owned by the client
     */
    List<Playlist> getPlaylists(Long id);

    /**
     * Retrieves a single playlist by its unique identifier.
     *
     * @param id the ID of the playlist
     * @return the {@link Playlist} with the specified ID, or {@code null} if not found
     */
    Playlist getPlaylistById(Long id);

    /**
     * Retrieves all songs associated with a specific playlist.
     *
     * @param id the ID of the playlist
     * @return a list of {@link Song} objects contained in the playlist
     */
    List<Song> getSongs(Long id);

    /**
     * Saves or updates a playlist in the database.
     *
     * @param playlist the {@link Playlist} object to be saved
     * @return Whether insertion was successful
     */
    boolean savePlaylist(Playlist playlist);

    /**
     * Deletes a playlist by its unique identifier.
     *
     * @param id the ID of the playlist to delete
     * @return Whether deletion was successful
     */
    boolean deletePlaylist(Long id);

    /**
     * Adds a song to a specific playlist.
     *
     * @param playlistId the ID of the playlist to which the song will be added
     * @param songId the ID of the song to add
     * @return Whether song addition was successful
     */
    boolean addSongToPlaylist(Long playlistId, Long songId);

    /**
     * Removes a song from a specific playlist.
     *
     * @param playlistId the ID of the playlist from which the song will be removed
     * @param songId the ID of the song to remove
     * @return Whether song removal was successful
     */
    boolean removeSongFromPlaylist(Long playlistId, Long songId);
}
