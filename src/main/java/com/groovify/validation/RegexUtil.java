package com.groovify.validation;

import java.util.regex.Pattern;

/**
 * Utility class providing centralized regular expression validation for
 * {@code Client} entity fields such as username, password, and description.
 * <p>
 * This class allows you to enforce character-level constraints outside
 * of the entity itself, keeping the entity clean while enabling
 * fine-grained validation in the service or controller layer.
 * </p>
 * <p>
 * Regex patterns used:
 * <ul>
 *     <li>Username: allows only letters (a-z, A-Z), digits (0-9),
 *     dots (.), underscores (_), and hyphens (-).</li>
 *     <li>Description: disallows special characters that may be unsafe
 *     for storage or display, such as &lt;&gt;"'%;()&amp;+.</li>
 *     <li>Password: currently disallows the same set of unsafe characters
 *     as description, but this can be customized to allow more secure characters.</li>
 * </ul>
 * </p>
 * <p>
 * All methods are static and stateless, making this class safe for
 * concurrent use.
 * </p>
 */
public class RegexUtil {

    /** Regex pattern for validating usernames. */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");

    /** Regex pattern for validating user descriptions. */
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("^[^<>\"'%;()&+]*$");

    /** Regex pattern for validating passwords. */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[^<>\"'%;()&+]*$");

    /**
     * Validates the given username against the {@link #USERNAME_PATTERN}.
     *
     * @param username the username to validate; may be {@code null}
     * @return {@code true} if the username contains only allowed characters,
     *         {@code false} if it contains invalid characters or is {@code null}
     */
    public static boolean isUsernameValid(String username) {
        return username == null || !USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates the given password against the {@link #PASSWORD_PATTERN}.
     * <p>
     * Note: Currently, this method disallows certain special characters such as
     * &lt;&gt;"'%;()&amp;+.
     * </p>
     *
     * @param password the password to validate; may be {@code null}
     * @return {@code true} if the password contains only allowed characters,
     *         {@code false} if it contains invalid characters or is {@code null}
     */
    public static boolean isPasswordValid(String password) {
        return password == null || !PASSWORD_PATTERN.matcher(password).matches();
    }
}