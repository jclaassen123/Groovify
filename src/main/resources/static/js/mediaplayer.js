const playButton = document.getElementById("playPause");
const lastSongButton = document.getElementById("lastSong");
const nextSongButton = document.getElementById("nextSong");

var currentSong = null;

const listedSongs = document.querySelectorAll("#songHolder")

listedSongs.forEach(song => {
    song.addEventListener('click', () => {
        const newSong = button.getAttribute("data");
        if (newSong !== currentSong) {
            currentSong = newSong

        }
    })
})