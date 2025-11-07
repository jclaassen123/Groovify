let currentSongId = null;

// Open the modal and store which song is being added
function openAddToPlaylistModal(songId) {
    currentSongId = songId;
    document.getElementById('addToPlaylistModal').style.display = 'flex';
}

// Close the modal
function closeAddToPlaylistModal() {
    document.getElementById('addToPlaylistModal').style.display = 'none';
    currentSongId = null;
}

// Handle adding song to playlist
function addSongToPlaylist(event, playlistElement) {
    event.preventDefault(); // prevent link navigation
    const playlistId = playlistElement.getAttribute('data-playlist-id');

    if (!currentSongId || !playlistId) return;

    // Make a POST request to add the song to the playlist
    fetch(`/playlists/${playlistId}/addSong/${currentSongId}`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            alert('Song added to playlist!');
            closeAddToPlaylistModal();
        } else {
            alert('Failed to add song.');
        }
    }).catch(err => {
        console.error(err);
        alert('Error adding song.');
    });
}