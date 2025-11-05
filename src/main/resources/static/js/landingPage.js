/**
 * landingPage.js
 *
 * Handles the behavior of the landing page for Groovify.
 * Specifically, it allows a guest user to bypass login and go to the home page.
 */

document.addEventListener('DOMContentLoaded', () => {
    // Get the "Guest" button by ID
    const guestBtn = document.getElementById('guestBtn');

    // If the button exists, attach a click handler
    if (guestBtn) {
        guestBtn.addEventListener('click', () => {
            // Redirect the user to the home page
            window.location.href = '/home';
        });
    }
});
