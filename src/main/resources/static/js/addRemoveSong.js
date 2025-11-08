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

function addSongToPlaylist(event, playlistElement) {
    event.preventDefault();
    const playlistId = playlistElement.getAttribute('data-playlist-id');

    if (!currentSongId || !playlistId) return;

    fetch(`/playlists/${playlistId}/addSong/${currentSongId}`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            alert('Song added to playlist!');
            closeAddToPlaylistModal();
        } else if (response.status === 409) {
            alert('This song is already in the playlist.');
        } else {
            alert('Failed to add song.');
        }
    }).catch(err => {
        console.error(err);
        alert('Error adding song.');
    });
}
