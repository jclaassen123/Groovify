package com.groovify.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for securely hashing and verifying passwords using SHA-256 with salting.
 * <p>
 * This version stores the salt in a separate database column.
 * </p>
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    /**
     * Generates a random cryptographic salt.
     *
     * @return a Base64-encoded salt string
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password using SHA-256 and the provided salt.
     *
     * @param password the plaintext password
     * @param salt     the Base64-encoded salt
     * @return Base64-encoded hash
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) return null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password by re-hashing with the same salt and comparing hashes.
     *
     * @param password       the plaintext password
     * @param salt           the Base64-encoded salt
     * @param storedPassword the stored hashed password
     * @return true if passwords match; false otherwise
     */
    public static boolean verifyPassword(String password, String salt, String storedPassword) {
        String hash = hashPassword(password, salt);
        return hash != null && hash.equals(storedPassword);
    }
}
