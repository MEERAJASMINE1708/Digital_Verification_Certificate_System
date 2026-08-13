package com.dcvs.export;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Generates ZXing QR codes encoding the certificate ID.
 * Module 6 — Lakshmi
 */
public class QRCodeGenerator {

    private static final Logger LOGGER = Logger.getLogger(QRCodeGenerator.class.getName());
    private static final int QR_SIZE = 200; // pixels

    private QRCodeGenerator() {}

    /**
     * Generates a QR code image for the given certificate ID.
     *
     * @param certId  the certificate ID to encode
     * @return BufferedImage of the QR code, or null on failure
     */
    public static BufferedImage generate(String certId) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(certId, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate QR code", e);
            return null;
        }
    }

    /**
     * Returns the QR code as a PNG byte array (for embedding in PDFs, etc.).
     */
    public static byte[] generatePngBytes(String certId) {
        BufferedImage img = generate(certId);
        if (img == null) return new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "PNG", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to encode QR PNG bytes", e);
            return new byte[0];
        }
    }

    /**
     * Generates a QR code and saves it as a PNG file.
     *
     * @param content   the text/data to encode in the QR code
     * @param filePath  absolute path where the PNG should be saved
     * @param size      width and height in pixels
     * @return true if the file was written successfully, false otherwise
     */
    public static boolean generateToFile(String content, String filePath, int size) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            Path output = new File(filePath).toPath();
            MatrixToImageWriter.writeToPath(matrix, "PNG", output);
            return true;
        } catch (WriterException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write QR code to file: " + filePath, e);
            return false;
        }
    }
}