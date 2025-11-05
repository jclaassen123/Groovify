/**
 * session-timeout.js
 *
 * Automatically redirects the user to the session timeout page
 * after a specified period of inactivity.
 */

(function() {
    // Session timeout duration in milliseconds
    // 3600000 ms = 1 hour
    const SESSION_TIMEOUT = 3600000;

    // Redirect user to timeout page after the session expires
    setTimeout(() => {
        window.location.href = "/sessionTimedOut";
    }, SESSION_TIMEOUT);
})();
