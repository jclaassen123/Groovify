package com.groovify.jpa.repo;

import com.groovify.jpa.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing {@link Playlist} entities.
 *
 * <p>This interface provides CRUD operations and query methods
 * for interacting with the {@code Playlist} table in the database.
 * It extends {@link JpaRepository}, which supplies standard JPA
 * persistence functionality such as saving, deleting, and finding entities.</p>
 *
 * <p>Custom query methods can be defined by following Spring Data JPA’s
 * method naming conventions. For example, {@link #findByClientID(Long)}
 * retrieves all playlists belonging to a specific client.</p>
 *
 * @author Nevin Fullerton
 * @version 1.0
 * @see Playlist
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface PlaylistRepo extends JpaRepository<Playlist, Long> {

    /**
     * Finds all playlists that belong to a specific client.
     *
     * @param clientID the ID of the client whose playlists should be retrieved
     * @return a list of {@link Playlist} objects associated with the given client ID
     */
    List<Playlist> findByClientID(Long clientID);
}
