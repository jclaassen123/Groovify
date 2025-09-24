document.addEventListener('DOMContentLoaded', () => {
    // Select all navbar links
    const links = document.querySelectorAll('.navbar li a');

    links.forEach(link => {
        // Highlight the active page based on the URL
        // This works if your href matches part of the current path
        if (window.location.pathname === link.getAttribute('href')) {
            link.classList.add('active');
        }

        // Optional: click behavior to update active class immediately
        link.addEventListener('click', () => {
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
        });
    });
});