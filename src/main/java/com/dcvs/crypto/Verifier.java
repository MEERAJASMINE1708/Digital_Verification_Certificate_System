package com.dcvs.crypto;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates RSA-SHA256 signatures using the stored public key.
 * Module 1 — Meera
 */
public class Verifier {

    private static final Logger LOGGER = Logger.getLogger(Verifier.class.getName());
    private static final String ALGORITHM = "SHA256withRSA";

    private Verifier() {}

    /**
     * Verifies that {@code base64Signature} was produced by signing {@code payload}
     * with the private key that corresponds to the system public key.
     *
     * @param payload          canonical certificate payload string
     * @param base64Signature  Base64-encoded signature from the database
     * @return true if signature is valid
     */
    public static boolean verify(String payload, String base64Signature) {
        try {
            PublicKey publicKey = KeyManager.getPublicKey();
            Signature sig = Signature.getInstance(ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(payload.getBytes(StandardCharsets.UTF_8));
            byte[] sigBytes = Base64.getDecoder().decode(base64Signature);
            return sig.verify(sigBytes);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Signature verification error", e);
            return false;
        }
    }
}
