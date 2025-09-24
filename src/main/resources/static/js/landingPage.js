// landingPage.js
document.addEventListener('DOMContentLoaded', () => {
    const guestBtn = document.getElementById('guestBtn');
    if (guestBtn) {
        guestBtn.addEventListener('click', () => {
            window.location.href = '/home';
        });
    }
});