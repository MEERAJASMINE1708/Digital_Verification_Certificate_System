package com.dcvs.crypto;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates RSA-SHA256 digital signatures over certificate payload bytes.
 * Module 1 — Meera
 */
public class Signer {

    private static final Logger LOGGER = Logger.getLogger(Signer.class.getName());
    private static final String ALGORITHM = "SHA256withRSA";

    private Signer() {}

    /**
     * Signs the given payload string with the system private key.
     *
     * @param payload canonical certificate string (built by HashUtil)
     * @return Base64-encoded RSA signature, or null on failure
     */
    public static String sign(String payload) {
        try {
            PrivateKey privateKey = KeyManager.getPrivateKey();
            Signature sig = Signature.getInstance(ALGORITHM);
            sig.initSign(privateKey);
            sig.update(payload.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = sig.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Signing failed", e);
            return null;
        }
    }
}
