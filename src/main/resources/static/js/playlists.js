// This is a single-line comment
    // Get modal and button elements
    const modal = document.getElementById("playlistModal");
    const btn = document.getElementById("createPlaylistBtn");
    const closeBtn = modal.querySelector(".close");

    // Open modal when button is clicked
    btn.onclick = () => modal.style.display = "block";

    // Close modal when clicking the 'x'
    closeBtn.onclick = () => modal.style.display = "none";

    // Close modal if clicking outside the modal content
    window.onclick = (event) => {
        if (event.target === modal) {
        modal.style.display = "none";
        }
    }
