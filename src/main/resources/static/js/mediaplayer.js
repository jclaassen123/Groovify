const playButton = document.getElementById("playPause");
const lastSongButton = document.getElementById("lastSong");
const nextSongButton = document.getElementById("nextSong");
const progressBar = document.getElementById("progress");
const volumeBar = document.getElementById("volume");
const audioPlayer = document.getElementById("player");

let currentSong = null;
const songFolderPath = "/songs/";

const listedSongs = document.querySelectorAll("#songHolder .song-card");

listedSongs.forEach(song => {
    song.addEventListener('click', () => {
        const newSong = song.getAttribute("data-filename");
        if (newSong !== currentSong) {
            currentSong = newSong;
            audioPlayer.src = songFolderPath + encodeURIComponent(currentSong); // encode filename to support more browsers
            playSong();
            playButton.disabled = false;

        } else {
            audioPlayer.currentTime = 0;
        }
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
});

audioPlayer.addEventListener('timeupdate', () => {
    if (audioPlayer.duration) {
        progressBar.value = audioPlayer.currentTime;
    }
});

audioPlayer.addEventListener('ended', () => {
    playButton.disabled = true;
    playButton.textContent = "Play";
});

progressBar.addEventListener('input', () => {
    if (audioPlayer.duration) {
        audioPlayer.currentTime = progressBar.value;
    }
});

volumeBar.addEventListener('input', () => {
   audioPlayer.volume = volumeBar.value;
});

function playSong() {
    audioPlayer.play();
    playButton.textContent = "Pause";
}

function pauseSong() {
    audioPlayer.pause();
    playButton.textContent = "Play";
}
