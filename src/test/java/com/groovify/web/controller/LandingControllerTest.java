package com.groovify.web.controller;

import com.groovify.service.LoginService;
import com.groovify.web.form.LoginForm;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LandingControllerTest {

    private final LoginService loginService = mock(LoginService.class);
    private final LandingController controller = new LandingController(loginService);

    @Test
    void testSuccessfulLogin() {
        LoginForm form = new LoginForm();
        form.setUsername("testuser");
        form.setPassword("password");

        BindingResult result = mock(BindingResult.class);
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes attrs = mock(RedirectAttributes.class);

        when(result.hasErrors()).thenReturn(false);
        when(loginService.validateUser("testuser", "password")).thenReturn(true);

        String view = controller.loginPost(form, result, session, attrs);

        assertEquals("redirect:/home", view);
        verify(session).setAttribute("username", "testuser");
    }

    @Test
    void testInvalidLoginCredentials() {
        LoginForm form = new LoginForm();
        form.setUsername("wronguser");
        form.setPassword("badpassword");

        BindingResult result = mock(BindingResult.class);
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes attrs = mock(RedirectAttributes.class);

        when(result.hasErrors()).thenReturn(false);
        when(loginService.validateUser("wronguser", "badpassword")).thenReturn(false);

        String view = controller.loginPost(form, result, session, attrs);

        assertEquals("landingPage", view);
        verify(session, never()).setAttribute(anyString(), any());
    }
}
