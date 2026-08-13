package com.dcvs.export;

import com.dcvs.model.Certificate;
import com.dcvs.util.AppConstants;
import com.dcvs.util.DateUtil;
import com.dcvs.util.QRCodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Generates a beautiful A4 certificate PDF with:
 * - Organization branding header
 * - Student name, course, cert ID
 * - QR code encoding: certId + studentName + course + orgName + dates
 * - Authorized signature section
 * Module 6 — Lakshmi
 */
public class PdfExporter {

    private static final float W      = PDRectangle.A4.getWidth();
    private static final float H      = PDRectangle.A4.getHeight();
    private static final float MARGIN = 50f;

    public void export(Certificate cert, String filePath) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                drawBackground(cs);
                drawOuterBorder(cs);
                drawInnerBorder(cs);
                drawHeader(cs);
                drawCertificateBody(cs, cert);
                drawDetailsBlock(cs, cert);
                drawSignatureSection(cs);
                drawQrCode(doc, page, cert);
                drawFooter(cs, cert);
            }

            doc.save(filePath);
        }
    }

    // ── Background ────────────────────────────────────────────────────────────

    private void drawBackground(PDPageContentStream cs) throws IOException {
        cs.setNonStrokingColor(new Color(254, 252, 245));
        cs.addRect(0, 0, W, H);
        cs.fill();

        cs.setNonStrokingColor(AppConstants.PRIMARY);
        cs.addRect(0, H - 80, W, 80);
        cs.fill();

        cs.setNonStrokingColor(AppConstants.PRIMARY);
        cs.addRect(0, 0, W, 50);
        cs.fill();

        cs.setNonStrokingColor(new Color(197, 202, 233));
        cs.addRect(MARGIN - 10, 50, 4, H - 130);
        cs.fill();
        cs.addRect(W - MARGIN + 6, 50, 4, H - 130);
        cs.fill();
    }

    private void drawOuterBorder(PDPageContentStream cs) throws IOException {
        cs.setStrokingColor(AppConstants.ACCENT);
        cs.setLineWidth(2f);
        cs.addRect(MARGIN - 15, 55, W - (MARGIN - 15) * 2, H - 115);
        cs.stroke();
    }

    private void drawInnerBorder(PDPageContentStream cs) throws IOException {
        cs.setStrokingColor(new Color(197, 202, 233));
        cs.setLineWidth(0.5f);
        cs.addRect(MARGIN - 5, 65, W - (MARGIN - 5) * 2, H - 135);
        cs.stroke();
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private void drawHeader(PDPageContentStream cs) throws IOException {
        PDFont boldFont  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont plainFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        drawCenteredText(cs, AppConstants.ORG_NAME, boldFont, 16, Color.WHITE, H - 32);
        drawCenteredText(cs, AppConstants.ORG_TAGLINE, plainFont, 9,
                new Color(197, 202, 233), H - 50);

        drawCenteredText(cs, "CERTIFICATE OF COMPLETION", boldFont, 22,
                AppConstants.PRIMARY, H - 110);

        cs.setStrokingColor(new Color(255, 193, 7));
        cs.setLineWidth(2f);
        float lineW = 200f;
        cs.moveTo((W - lineW) / 2, H - 118);
        cs.lineTo((W + lineW) / 2, H - 118);
        cs.stroke();
    }

    // ── Certificate body ──────────────────────────────────────────────────────

    private void drawCertificateBody(PDPageContentStream cs, Certificate cert)
            throws IOException {
        PDFont boldFont   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont plainFont  = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        drawCenteredText(cs, "This is to certify that", italicFont, 13,
                new Color(80, 80, 100), H - 155);

        drawCenteredText(cs, cert.getRecipientName(), boldFont, 28,
                AppConstants.PRIMARY, H - 195);

        float nameW = boldFont.getStringWidth(cert.getRecipientName()) / 1000 * 28;
        float nameX = (W - nameW) / 2;
        cs.setStrokingColor(AppConstants.ACCENT);
        cs.setLineWidth(1.2f);
        cs.moveTo(nameX, H - 200);
        cs.lineTo(nameX + nameW, H - 200);
        cs.stroke();

        drawCenteredText(cs, "( ID: " + cert.getRecipientId() + " )", plainFont, 11,
                new Color(100, 100, 120), H - 220);

        drawCenteredText(cs, "has successfully completed the course", italicFont, 13,
                new Color(80, 80, 100), H - 248);

        drawCenteredText(cs, cert.getCourse(), boldFont, 20,
                AppConstants.ACCENT, H - 280);

        drawCenteredText(cs, "offered by " + cert.getOrgName(), plainFont, 11,
                new Color(80, 80, 100), H - 302);
    }

    // ── Details block ─────────────────────────────────────────────────────────

    private void drawDetailsBlock(PDPageContentStream cs, Certificate cert)
            throws IOException {
        PDFont boldFont  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont plainFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont monoFont  = new PDType1Font(Standard14Fonts.FontName.COURIER);

        cs.setNonStrokingColor(new Color(232, 234, 246));
        cs.addRect(MARGIN + 20, H - 390, W - (MARGIN + 20) * 2, 65);
        cs.fill();

        cs.setStrokingColor(new Color(159, 168, 218));
        cs.setLineWidth(0.5f);
        cs.addRect(MARGIN + 20, H - 390, W - (MARGIN + 20) * 2, 65);
        cs.stroke();

        float colL = MARGIN + 40;
        float colR = W / 2 + 20;
        float y1   = H - 342;
        float y2   = H - 368;

        drawLabelValue(cs, "Certificate ID", cert.getCertId(),   boldFont, monoFont, 9, colL, y1);
        drawLabelValue(cs, "Issue Date",     DateUtil.format(cert.getIssueDate()),  boldFont, plainFont, 11, colL, y2);
        drawLabelValue(cs, "Status",         cert.getStatus(),    boldFont, plainFont, 11, colR, y1);
        drawLabelValue(cs, "Valid Until",    DateUtil.format(cert.getExpiryDate()), boldFont, plainFont, 11, colR, y2);
    }

    // ── Signature section ─────────────────────────────────────────────────────

    private void drawSignatureSection(PDPageContentStream cs) throws IOException {
        PDFont boldFont   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont plainFont  = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        float sigX = MARGIN + 40;
        float sigY = H - 500;

        cs.setStrokingColor(AppConstants.PRIMARY);
        cs.setLineWidth(1.5f);

        for (int i = 0; i < 3; i++) {
            float baseY = sigY + 30 - (i * 8);
            cs.moveTo(sigX, baseY);
            cs.curveTo(sigX + 15, baseY + 8, sigX + 25, baseY - 8, sigX + 40, baseY);
            cs.curveTo(sigX + 55, baseY + 8, sigX + 65, baseY - 8, sigX + 80, baseY);
            cs.curveTo(sigX + 95, baseY + 8, sigX + 105, baseY - 8, sigX + 120, baseY);
            cs.stroke();
        }

        cs.setLineWidth(0.8f);
        cs.setStrokingColor(new Color(100, 100, 130));
        cs.moveTo(sigX, sigY);
        cs.lineTo(sigX + 140, sigY);
        cs.stroke();

        drawText(cs, AppConstants.AUTHORIZED_SIGNATORY, boldFont, 10, AppConstants.PRIMARY, sigX, sigY - 14);
        drawText(cs, AppConstants.ORG_NAME, plainFont, 9, new Color(100, 100, 130), sigX, sigY - 26);

        // Official seal — PDFBox has no addEllipse(); use Bézier approximation instead
        float sealX  = W - MARGIN - 90;
        float sealY  = sigY + 20;
        float sealCX = sealX + 35;  // centre X
        float sealCY = sealY + 35;  // centre Y

        cs.setStrokingColor(AppConstants.ACCENT);
        cs.setLineWidth(2f);
        drawEllipse(cs, sealCX, sealCY, 35, 35);
        cs.stroke();

        cs.setLineWidth(1f);
        drawEllipse(cs, sealCX, sealCY, 30, 30);
        cs.stroke();

        drawCenteredTextAt(cs, "OFFICIAL", boldFont,   8, AppConstants.PRIMARY, sealCX, sealCY + 11);
        drawCenteredTextAt(cs, "SEAL",     boldFont,   8, AppConstants.PRIMARY, sealCX, sealCY);
        drawCenteredTextAt(cs, "DCVS",     italicFont, 7, AppConstants.ACCENT,  sealCX, sealCY - 11);
    }

    /**
     * Draws an ellipse centred at (cx, cy) with horizontal radius rx and vertical
     * radius ry using four cubic Bézier curves.
     * PDFBox's PDPageContentStream has no native addEllipse() method.
     * Call cs.stroke() or cs.fill() after this.
     */
    private void drawEllipse(PDPageContentStream cs,
                              float cx, float cy,
                              float rx, float ry) throws IOException {
        // Bézier approximation constant ≈ 0.5523
        final float k = 0.5523f;
        float kx = k * rx;
        float ky = k * ry;

        cs.moveTo(cx + rx, cy);
        cs.curveTo(cx + rx, cy + ky,  cx + kx, cy + ry,  cx,      cy + ry);
        cs.curveTo(cx - kx, cy + ry,  cx - rx, cy + ky,  cx - rx, cy);
        cs.curveTo(cx - rx, cy - ky,  cx - kx, cy - ry,  cx,      cy - ry);
        cs.curveTo(cx + kx, cy - ry,  cx + rx, cy - ky,  cx + rx, cy);
        cs.closePath();
    }

    // ── QR Code — encodes certId + studentName + course + orgName + dates ─────

    private void drawQrCode(PDDocument doc, PDPage page, Certificate cert) throws IOException {
        try {
            String qrContent = QRCodeUtil.build(cert);

            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new QRCodeWriter()
                    .encode(qrContent, BarcodeFormat.QR_CODE, 120, 120, hints);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                    doc, baos.toByteArray(), "qr");

            try (PDPageContentStream cs = new PDPageContentStream(
                    doc, page, PDPageContentStream.AppendMode.APPEND, true)) {

                float qrX = W / 2 - 40;
                float qrY = H - 630;
                cs.drawImage(pdImage, qrX, qrY, 80, 80);

                PDFont plainFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                PDFont boldFont  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

                drawCenteredText(cs, "Scan to verify", plainFont, 8,
                        new Color(120, 120, 140), qrY - 10);

                float labelX = qrX + 90;
                float labelY = qrY + 68;
                drawText(cs, "Student : " + cert.getRecipientName(), plainFont, 7,
                        new Color(80, 80, 100), labelX, labelY);
                drawText(cs, "Course  : " + cert.getCourse(), plainFont, 7,
                        new Color(80, 80, 100), labelX, labelY - 12);
                drawText(cs, "Org     : " + cert.getOrgName(), plainFont, 7,
                        new Color(80, 80, 100), labelX, labelY - 24);
                drawText(cs, "ID      : " + cert.getCertId().substring(0, 18) + "...",
                        plainFont, 7, new Color(80, 80, 100), labelX, labelY - 36);
            }
        } catch (Exception e) {
            // QR generation failure is non-fatal — PDF still saves
        }
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private void drawFooter(PDPageContentStream cs, Certificate cert) throws IOException {
        PDFont monoFont  = new PDType1Font(Standard14Fonts.FontName.COURIER);
        PDFont plainFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        drawCenteredText(cs, "Certificate ID: " + cert.getCertId(),
                monoFont, 8, new Color(200, 210, 255), 30);
        drawCenteredText(cs,
                "Verify at: " + AppConstants.ORG_WEBSITE + " | " + AppConstants.ORG_NAME,
                plainFont, 7, new Color(180, 190, 230), 18);
    }

    // ── Drawing helpers ───────────────────────────────────────────────────────

    private float drawCenteredText(PDPageContentStream cs, String text,
                                    PDFont font, float size, Color color, float y)
            throws IOException {
        float w = font.getStringWidth(text) / 1000 * size;
        float x = (W - w) / 2;
        cs.beginText();
        cs.setFont(font, size);
        cs.setNonStrokingColor(color);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - size - 4;
    }

    private void drawCenteredTextAt(PDPageContentStream cs, String text,
                                     PDFont font, float size, Color color,
                                     float centerX, float y) throws IOException {
        float w = font.getStringWidth(text) / 1000 * size;
        cs.beginText();
        cs.setFont(font, size);
        cs.setNonStrokingColor(color);
        cs.newLineAtOffset(centerX - w / 2, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawText(PDPageContentStream cs, String text,
                           PDFont font, float size, Color color, float x, float y)
            throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.setNonStrokingColor(color);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawLabelValue(PDPageContentStream cs, String label, String value,
                                  PDFont labelFont, PDFont valueFont, float valueSize,
                                  float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(labelFont, 9);
        cs.setNonStrokingColor(new Color(80, 80, 100));
        cs.newLineAtOffset(x, y);
        cs.showText(label + ":");
        cs.endText();

        cs.beginText();
        cs.setFont(valueFont, valueSize);
        cs.setNonStrokingColor(AppConstants.PRIMARY);
        cs.newLineAtOffset(x, y - 13);
        cs.showText(value != null ? value : "");
        cs.endText();
    }
}