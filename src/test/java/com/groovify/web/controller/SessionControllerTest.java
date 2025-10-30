package com.groovify.web.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionControllerTest {

    private final SessionController controller = new SessionController();

    @Test
    void sessionTimedOut_returnsCorrectView() {
        String result = controller.sessionTimedOut();
        assertEquals("sessionTimedOut", result);
    }
}
