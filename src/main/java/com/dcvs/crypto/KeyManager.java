package com.dcvs.crypto;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RSA-2048 key pair generation and persistence.
 * Keys are stored as PEM-style Base64 files in the working directory.
 * Module 1 — Meera
 */
public class KeyManager {

    private static final Logger LOGGER = Logger.getLogger(KeyManager.class.getName());
    private static final String PRIVATE_KEY_FILE = "dcvs_private.key";
    private static final String PUBLIC_KEY_FILE  = "dcvs_public.key";
    private static final int KEY_SIZE = 2048;

    private static KeyPair cachedKeyPair;

    private KeyManager() {}

    /**
     * Returns the system key pair. Generates a new pair on first call and
     * persists the keys to disk.  Subsequent calls load from disk.
     */
    public static synchronized KeyPair getOrCreateKeyPair() {
        if (cachedKeyPair != null) return cachedKeyPair;

        File privFile = new File(PRIVATE_KEY_FILE);
        File pubFile  = new File(PUBLIC_KEY_FILE);

        if (privFile.exists() && pubFile.exists()) {
            try {
                cachedKeyPair = loadKeyPair(privFile, pubFile);
                LOGGER.info("RSA key pair loaded from disk.");
                return cachedKeyPair;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load key pair; regenerating.", e);
            }
        }

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(KEY_SIZE, new SecureRandom());
            cachedKeyPair = kpg.generateKeyPair();
            saveKeyPair(cachedKeyPair, privFile, pubFile);
            LOGGER.info("New RSA-2048 key pair generated and saved.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
        return cachedKeyPair;
    }

    public static PrivateKey getPrivateKey() {
        return getOrCreateKeyPair().getPrivate();
    }

    public static PublicKey getPublicKey() {
        return getOrCreateKeyPair().getPublic();
    }

    // ── Persistence helpers ───────────────────────────────────────────────────

    private static void saveKeyPair(KeyPair kp, File privFile, File pubFile) throws IOException {
        try (FileWriter fw = new FileWriter(privFile)) {
            fw.write(Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded()));
        }
        try (FileWriter fw = new FileWriter(pubFile)) {
            fw.write(Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
        }
    }

    private static KeyPair loadKeyPair(File privFile, File pubFile)
            throws IOException, GeneralSecurityException {

        String privB64 = readFile(privFile);
        String pubB64  = readFile(pubFile);

        byte[] privBytes = Base64.getDecoder().decode(privB64.trim());
        byte[] pubBytes  = Base64.getDecoder().decode(pubB64.trim());

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privBytes));
        PublicKey  publicKey  = kf.generatePublic(new X509EncodedKeySpec(pubBytes));

        return new KeyPair(publicKey, privateKey);
    }

    private static String readFile(File f) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}
