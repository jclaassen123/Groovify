/**
 * navbar.js
 *
 * Highlights the active navbar link based on the current URL.
 * Updates the active class when a navbar link is clicked.
 */

document.addEventListener('DOMContentLoaded', () => {
    // Select all navbar links
    const links = document.querySelectorAll('.navbar li a');

    links.forEach(link => {
        // Highlight the active page based on the current URL
        // Matches the link href to the current path
        if (window.location.pathname === link.getAttribute('href')) {
            link.classList.add('active');
        }

        // Update active class immediately when a link is clicked
        link.addEventListener('click', () => {
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
        });
    });
});
