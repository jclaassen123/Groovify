document.addEventListener("DOMContentLoaded", () => {
    const profileCircle = document.getElementById("profileCircle");
    const profileDropdown = document.getElementById("profileDropdown");
    const logoutTopbarBtn = document.getElementById("logoutTopbarBtn");

    // Show dropdown and hide circle
    profileCircle.addEventListener("click", (e) => {
        e.stopPropagation();
        profileDropdown.style.display = "flex";
        profileCircle.style.display = "none";
    });

    // Logout button
    logoutTopbarBtn.addEventListener("click", () => {
        window.location.href = "/logout";
    });

    // Close dropdown if click outside
    document.addEventListener("click", (e) => {
        if (!profileDropdown.contains(e.target)) {
            profileDropdown.style.display = "none";
            profileCircle.style.display = "inline-block";
        }
    });
});