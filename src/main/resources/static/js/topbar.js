/**
 * topbar.js
 *
 * Handles profile dropdown toggle and logout functionality
 * in the top navigation bar.
 */

document.addEventListener("DOMContentLoaded", () => {
    const profileCircle = document.getElementById("profileCircle");
    const profileDropdown = document.getElementById("profileDropdown");
    const logoutTopbarBtn = document.getElementById("logoutTopbarBtn");

    // --- Show dropdown when profile circle is clicked ---
    profileCircle.addEventListener("click", (e) => {
        e.stopPropagation(); // Prevent event from bubbling to document
        profileDropdown.style.display = "flex";
        profileCircle.style.display = "none";
    });

    // --- Logout button click handler ---
    logoutTopbarBtn.addEventListener("click", () => {
        window.location.href = "/logout";
    });

    // --- Close dropdown if click occurs outside of it ---
    document.addEventListener("click", (e) => {
        if (!profileDropdown.contains(e.target)) {
            profileDropdown.style.display = "none";
            profileCircle.style.display = "inline-block";
        }
    });
});
