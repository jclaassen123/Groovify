const playButton = document.getElementById("playPause");
const lastSongButton = document.getElementById("lastSong");
const nextSongButton = document.getElementById("nextSong");
const progressBar = document.getElementById("progress");
const currentTimeDisplay = document.getElementById("currentTime");
const durationDisplay = document.getElementById("duration");
const volumeBar = document.getElementById("volume");
const audioPlayer = document.getElementById("player");



let currentSong = null;
const songFolderPath = "/songs/";

const listedSongs = document.querySelectorAll("#songHolder .song-card");

listedSongs.forEach(song => {
    listedSongs.forEach(song => {
        song.addEventListener('click', () => {
            const filename = song.getAttribute("data-filename");
            const genre = song.getAttribute("data-genre");

            if (filename !== currentSong) {
                currentSong = filename;
                const songPath = `/songs/${encodeURIComponent(genre)}/${encodeURIComponent(filename)}`;
                audioPlayer.src = songPath;
                playSong();
                playButton.disabled = false;
            } else {
                audioPlayer.currentTime = 0;
            }
        });
    });
});

playButton.addEventListener('click', () => {
    if (audioPlayer.paused) {
        playSong();
    } else {
        pauseSong();
    }
});

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
});

progressBar.addEventListener('input', () => {
    if (audioPlayer.duration) {
        audioPlayer.currentTime = progressBar.value;
        currentTimeDisplay.textContent = formatTime(audioPlayer.currentTime);
    }
});

volumeBar.addEventListener('input', () => {
   audioPlayer.volume = volumeBar.value;
});

function playSong() {
    audioPlayer.play();
    playButton.textContent = "⏸";
}

function pauseSong() {
    audioPlayer.pause();
    playButton.textContent = "▶";
}

function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${minutes}:${secs < 10 ? "0" : ""}${secs}`;
}

const toggleButton = document.getElementById("toggleMediaplayer");
const mediaPlayer = document.querySelector(".mediaplayer");

toggleButton.addEventListener('click', () => {
    mediaPlayer.classList.toggle('folded');

    if (mediaPlayer.classList.contains('folded')) {
        toggleButton.textContent = "▲"; // folded → arrow points up
    } else {
        toggleButton.textContent = "▼"; // expanded → arrow points down
    }
});