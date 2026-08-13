package com.dcvs.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * SHA-256 utility methods for password and certificate data hashing.
 * Module 1 — Meera
 */
public final class HashUtil {

    private HashUtil() {}

    /**
     * Computes the SHA-256 hex digest of the given plaintext string.
     *
     * @param input plaintext to hash
     * @return lowercase hex-encoded SHA-256 digest
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Computes SHA-256 of raw bytes and returns the hex digest.
     */
    public static String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Builds a canonical string of certificate fields suitable for hashing.
     * Order must match exactly what Signer and VerificationService use.
     */
    public static String buildCertificatePayload(String certId, String recipientName,
                                                  String recipientId, String course,
                                                  String issueDate, String expiryDate) {
        return certId + "|" + recipientName + "|" + recipientId + "|"
                + course + "|" + issueDate + "|" + expiryDate;
    }
}
