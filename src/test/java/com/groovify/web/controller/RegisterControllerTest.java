package com.groovify.web.controller;

import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.ClientRepo;
import com.groovify.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for RegisterController.
 * Tests are organized into:
 * Good path: Successful registrations and expected behaviors
 * Bad path: User mistakes (validation errors, duplicate usernames)
 * Really bad path: System failures, exceptions, or null states
 */
class RegisterControllerTest {

    private ClientRepo clientRepo;
    private RegisterController controller;

    @BeforeEach
    void setUp() {
        clientRepo = mock(ClientRepo.class);
        controller = new RegisterController(clientRepo);
    }

    // ============================================================
    // === GOOD PATH TESTS ========================================
    // ============================================================

    @Test
    @DisplayName("Should show registration form with empty Client model")
    void testShowRegistrationForm_ReturnsRegisterView() {
        Model model = new ConcurrentModel();

        String view = controller.showRegistrationForm(model);

        assertThat(view).isEqualTo("register");
        assertThat(model.containsAttribute("user")).isTrue();
        assertThat(model.getAttribute("user")).isInstanceOf(Client.class);
    }

    @Test
    @DisplayName("Should register new user successfully and redirect with flash message")
    void testRegisterUser_SuccessfulRegistration() {
        when(clientRepo.findByName("newuser")).thenReturn(Optional.empty());

        Client client = new Client();
        client.setName("newuser");
        client.setPassword("password123");

        BindingResult result = new BeanPropertyBindingResult(client, "user");
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.registerUser(client, result, new ConcurrentModel(), redirectAttributes);

        assertThat(view).isEqualTo("redirect:");
        verify(clientRepo).save(any(Client.class));
        assertThat(redirectAttributes.getFlashAttributes().get("successMessage"))
                .isEqualTo("Registration successful! You can now log in.");
    }

    @Test
    @DisplayName("Should correctly hash password, assign salt, and apply default values")
    void testRegisterUser_PasswordHashedAndDefaultsSet() {
        when(clientRepo.findByName("secureUser")).thenReturn(Optional.empty());

        try (var mocked = mockStatic(PasswordUtil.class)) {
            mocked.when(PasswordUtil::generateSalt).thenReturn("salt123");
            mocked.when(() -> PasswordUtil.hashPassword("mySecret", "salt123")).thenReturn("hashed_pw");

            Client client = new Client();
            client.setName("secureUser");
            client.setPassword("mySecret");

            BindingResult result = new BeanPropertyBindingResult(client, "user");
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

            controller.registerUser(client, result, new ConcurrentModel(), redirectAttributes);

            ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
            verify(clientRepo).save(captor.capture());
            Client saved = captor.getValue();

            assertThat(saved.getPassword()).isEqualTo("hashed_pw");
            assertThat(saved.getPasswordSalt()).isEqualTo("salt123");
            assertThat(saved.getDescription()).isEqualTo("");
            assertThat(saved.getImageFileName()).isEqualTo("Fishing.jpg");
        }
    }

    // ============================================================
    // === BAD PATH TESTS =========================================
    // ============================================================

    @Test
    @DisplayName("Should fail registration when validation errors exist")
    void testRegisterUser_WithValidationErrors() {
        Client client = new Client();
        client.setName("");
        client.setPassword("");

        BindingResult result = new BeanPropertyBindingResult(client, "user");
        result.reject("name", "Name cannot be empty");

        String view = controller.registerUser(client, result, new ConcurrentModel(), new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("register");
        verify(clientRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should reject duplicate usernames and return error message")
    void testRegisterUser_UsernameAlreadyExists() {
        when(clientRepo.findByName("existingUser")).thenReturn(Optional.of(new Client()));

        Client client = new Client();
        client.setName("existingUser");
        client.setPassword("password123");

        Model model = new ConcurrentModel();

        String view = controller.registerUser(client, new BeanPropertyBindingResult(client, "user"), model, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("register");
        assertThat(model.containsAttribute("error")).isTrue();
        verify(clientRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should keep user in model when validation fails")
    void testRegisterUser_ReturnsUserInModelOnValidationError() {
        Client client = new Client();
        client.setName("baduser");
        client.setPassword("");

        BindingResult result = new BeanPropertyBindingResult(client, "user");
        result.rejectValue("password", "empty", "Password cannot be empty");

        Model model = new ConcurrentModel();

        String view = controller.registerUser(client, result, model, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("register");
        assertThat(model.getAttribute("user")).isSameAs(client);
    }

    // ============================================================
    // === REALLY BAD PATH TESTS ==================================
    // ============================================================

    @Test
    @DisplayName("Should handle null password gracefully and not crash")
    void testRegisterUser_NullPassword_ShouldFailGracefully() {
        when(clientRepo.findByName("user123")).thenReturn(Optional.empty());

        Client client = new Client();
        client.setName("user123");
        client.setPassword(null);

        String view = controller.registerUser(client, new BeanPropertyBindingResult(client, "user"),
                new ConcurrentModel(), new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("register");
        verify(clientRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should handle database failure gracefully and return to register view")
    void testRegisterUser_WhenDatabaseFails_ThrowsNoException() {
        when(clientRepo.findByName("failUser")).thenReturn(Optional.empty());
        doThrow(new RuntimeException("DB error")).when(clientRepo).save(any());

        Client client = new Client();
        client.setName("failUser");
        client.setPassword("pw");

        String view = controller.registerUser(client, new BeanPropertyBindingResult(client, "user"),
                new ConcurrentModel(), new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("register");
    }

    @Test
    @DisplayName("Should use unique salts for different users (security test)")
    void testRegisterUser_DifferentUsersHaveDifferentSalts() {
        when(clientRepo.findByName(anyString())).thenReturn(Optional.empty());

        try (var mocked = mockStatic(PasswordUtil.class)) {
            mocked.when(PasswordUtil::generateSalt)
                    .thenReturn("salt1", "salt2");
            mocked.when(() -> PasswordUtil.hashPassword(any(), any()))
                    .thenReturn("hash1", "hash2");

            Client userA = new Client();
            userA.setName("alpha");
            userA.setPassword("pw1");

            Client userB = new Client();
            userB.setName("beta");
            userB.setPassword("pw2");

            controller.registerUser(userA, new BeanPropertyBindingResult(userA, "user"),
                    new ConcurrentModel(), new RedirectAttributesModelMap());
            controller.registerUser(userB, new BeanPropertyBindingResult(userB, "user"),
                    new ConcurrentModel(), new RedirectAttributesModelMap());

            ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
            verify(clientRepo, times(2)).save(captor.capture());

            List<Client> savedUsers = captor.getAllValues();
            assertThat(savedUsers.get(0).getPasswordSalt()).isNotEqualTo(savedUsers.get(1).getPasswordSalt());
        }
    }

    @Test
    @DisplayName("Should still redirect properly even if unexpected model state")
    void testRegisterUser_RedirectsToExpectedPath() {
        when(clientRepo.findByName("newuser")).thenReturn(Optional.empty());

        Client client = new Client();
        client.setName("newuser");
        client.setPassword("password123");

        String view = controller.registerUser(client, new BeanPropertyBindingResult(client, "user"),
                new ConcurrentModel(), new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:");
    }
}
