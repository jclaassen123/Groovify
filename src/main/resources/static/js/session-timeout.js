// session-timeout.js
(function() {
    // Set timeout in milliseconds (e.g. 5000 = 5 seconds for testing)
    const SESSION_TIMEOUT = 120000;

    setTimeout(function() {
        window.location.href = "/sessionTimedOut";
    }, SESSION_TIMEOUT);
})();
