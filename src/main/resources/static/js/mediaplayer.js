/**
 * mediaplayer.js
 *
 * Handles the functionality of the media player on the Groovify site.
 * Supports play/pause, next/previous song, playlist selection, progress tracking,
 * volume control, and folding/unfolding the media player UI.
 */

// -----------------------
// DOM elements
// -----------------------
const playButton = document.getElementById("playPause");
const lastSongButton = document.getElementById("lastSong");
const nextSongButton = document.getElementById("nextSong");
const progressBar = document.getElementById("progress");
const currentTimeDisplay = document.getElementById("currentTime");
const durationDisplay = document.getElementById("duration");
const volumeBar = document.getElementById("volume");
const audioPlayer = document.getElementById("player");
const songTitleDisplay = document.getElementById("songTitle");
const toggleButton = document.getElementById("toggleMediaplayer");
const mediaPlayer = document.querySelector(".mediaplayer");

// Playlist state
let currentSong = null;
let currentIndex = -1;
const listedSongs = document.querySelectorAll("#songHolder .song-card");

// -----------------------
// Helper functions
// -----------------------

/**
 * Formats seconds into mm:ss format.
 * @param {number} seconds
 * @returns {string} formatted time string
 */
function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${minutes}:${secs < 10 ? "0" : ""}${secs}`;
}

/**
 * Play the currently loaded audio.
 */
function playSong() {
    audioPlayer.play();
    playButton.textContent = "⏸";
}

/**
 * Pause the currently loaded audio.
 */
function pauseSong() {
    audioPlayer.pause();
    playButton.textContent = "▶";
}

/**
 * Load and play a song from a playlist card.
 * @param {HTMLElement} songElement the song card element
 * @param {number} index the index of the song in the playlist
 */
function playSongFromCard(songElement, index) {
    const filename = songElement.getAttribute("data-filename");
    const genre = songElement.getAttribute("data-genre");
    const title = songElement.getAttribute("data-title");

    if (filename !== currentSong) {
        currentIndex = index;
        currentSong = filename;
        audioPlayer.src = `/songs/${encodeURIComponent(genre)}/${encodeURIComponent(filename)}`;
        songTitleDisplay.textContent = title;

        playSong();
        playButton.disabled = false;
        lastSongButton.disabled = false;
        nextSongButton.disabled = false;
    } else {
        audioPlayer.currentTime = 0;
    }
}

// -----------------------
// Event listeners
// -----------------------

// Click a song in the playlist
listedSongs.forEach((song, index) => {
    song.addEventListener('click', () => {
        playSongFromCard(song, index);
    });
});

// Play/pause button
playButton.addEventListener('click', () => {
    if (audioPlayer.paused) {
        playSong();
    } else {
        pauseSong();
    }
});

// Previous / Next buttons
lastSongButton.addEventListener('click', () => {
    if (currentIndex > 0) {
        playSongFromCard(listedSongs[currentIndex - 1], currentIndex - 1);
    } else {
        // Wrap to last song
        playSongFromCard(listedSongs[listedSongs.length - 1], listedSongs.length - 1);
    }
});

nextSongButton.addEventListener('click', () => {
    if (currentIndex < listedSongs.length - 1) {
        playSongFromCard(listedSongs[currentIndex + 1], currentIndex + 1);
    } else {
        // Wrap to first song
        playSongFromCard(listedSongs[0], 0);
    }
});

// Audio events
audioPlayer.addEventListener('loadedmetadata', () => {
    progressBar.max = audioPlayer.duration;
    durationDisplay.textContent = formatTime(audioPlayer.duration);
});

audioPlayer.addEventListener('timeupdate', () => {
    if (audioPlayer.duration) {
        progressBar.value = audioPlayer.currentTime;
        currentTimeDisplay.textContent = formatTime(audioPlayer.currentTime);
    }
});

audioPlayer.addEventListener('ended', () => {
    playButton.disabled = true;
    playButton.textContent = "▶";
    songTitleDisplay.textContent = "No song playing";
});

// Progress bar input
progressBar.addEventListener('input', () => {
    if (audioPlayer.duration) {
        audioPlayer.currentTime = progressBar.value;
        currentTimeDisplay.textContent = formatTime(audioPlayer.currentTime);
    }
});

// Volume control
volumeBar.addEventListener('input', () => {
    audioPlayer.volume = volumeBar.value;
});

// Fold/unfold media player
toggleButton.addEventListener('click', () => {
    mediaPlayer.classList.toggle('folded');
    toggleButton.textContent = mediaPlayer.classList.contains('folded') ? "▲" : "▼";
});
