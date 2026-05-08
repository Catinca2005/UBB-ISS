package org.example.weddingplanner.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class handling platform security operations.
 * Includes methods for password hashing to ensure NFR-1 compliance.
 */
public class SecurityUtils {

    /**
     * Hashes a plaintext password using the SHA-256 cryptographic algorithm.
     *
     * @param plainTextPassword The raw password submitted via the signup form.
     * @return A Base64 encoded string representing the hashed password.
     * @throws RuntimeException if the SHA-256 algorithm is not found by the JVM.
     */
    public static String hashPassword(String plainTextPassword) {
        try {
            // Initialize the SHA-256 hashing algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform the hashing on the string's bytes
            byte[] hash = digest.digest(plainTextPassword.getBytes());

            // Encode the resulting bytes into a readable Base64 string
            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Critical security error: Hashing algorithm not found.", e);
        }
    }
}