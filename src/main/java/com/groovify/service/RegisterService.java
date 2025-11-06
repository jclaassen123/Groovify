package com.groovify.service;

import com.groovify.jpa.model.Client;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Service interface for handling user registration logic.
 * <p>
 * Encapsulates validation, password hashing, default value management,
 * and persistence logic for registering new users.
 */
public interface RegisterService {

    /**
     * Main entry point for user registration.
     *
     * @param user               the submitted Client object
     * @param result             binding/validation results
     * @param model              model for returning data to the view
     * @param redirectAttributes redirect attributes for flash messages
     * @return the view name to render or redirect to
     */
    String registerUser(Client user,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes);

    /**
     * Validates binding results from @Valid annotation.
     *
     * @param user   the Client being registered
     * @param result the validation result
     * @param model  model to attach errors to
     * @return true if valid, false otherwise
     */
    boolean validateInput(Client user, BindingResult result, Model model);

    /**
     * Checks if the username already exists.
     *
     * @param user  the Client being registered
     * @param model model to attach errors to
     * @return true if available, false otherwise
     */
    boolean checkUsernameAvailability(Client user, Model model);

    /**
     * Validates that the user’s password is not null or blank.
     *
     * @param user  the Client being registered
     * @param model model to attach errors to
     * @return true if valid, false otherwise
     */
    boolean validatePassword(Client user, Model model);

    /**
     * Generates a salt, hashes the user’s password, and sets them on the entity.
     *
     * @param user  the Client being registered
     * @param model model to attach errors to
     * @return true if hashing succeeded, false otherwise
     */
    boolean hashAndSetPassword(Client user, Model model);

    /**
     * Sets default values for optional user fields if not provided.
     *
     * @param user the Client being registered
     */
    void setDefaultValues(Client user);

    /**
     * Persists the new user to the database.
     *
     * @param user  the Client being registered
     * @param model model to attach errors to
     * @return true if save succeeded, false otherwise
     */
    boolean saveUser(Client user, Model model);

    /**
     * Adds a success message and logs completion.
     *
     * @param redirectAttributes redirect attributes for flash message
     * @param username           the username that was registered
     */
    void addSuccessRedirect(RedirectAttributes redirectAttributes, String username);
}
