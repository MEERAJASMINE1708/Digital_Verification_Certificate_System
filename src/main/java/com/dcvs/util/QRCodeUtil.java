package com.dcvs.util;

import com.dcvs.model.Certificate;

/**
 * Central utility for building and parsing the QR code content string.
 *
 * QR Format (pipe-separated):
 *   DCVS|<certId>|<studentName>|<course>|<orgName>|<issueDate>|<expiryDate>
 *
 * All files that generate or scan QR codes use this class
 * so the format stays consistent everywhere.
 */
public final class QRCodeUtil {

    private QRCodeUtil() {}

    /** Separator used between fields inside the QR string. */
    private static final String SEP = "|";

    /** Prefix that identifies a DCVS QR code. */
    public static final String PREFIX = "DCVS";

    /**
     * Builds the full QR content string from a Certificate object.
     *
     * Example output:
     *   DCVS|A1B2-C3D4-...|Diana Sharma|Machine Learning|DCVS Institute of Technology|2026-04-01|2028-04-01
     */
    public static String build(Certificate cert) {
        return PREFIX + SEP
                + cert.getCertId()        + SEP
                + cert.getRecipientName() + SEP
                + cert.getCourse()        + SEP
                + cert.getOrgName()       + SEP
                + cert.getIssueDate()     + SEP
                + cert.getExpiryDate();
    }

    /**
     * Parses a QR string and returns a ParsedQR object.
     * Returns null if the string is not a valid DCVS QR code.
     *
     * Handles both:
     *   - New format: DCVS|certId|studentName|course|orgName|issueDate|expiryDate
     *   - Old format: DCVS|certId|orgName  (backward compatible)
     *   - Plain cert ID (no prefix)
     */
    public static ParsedQR parse(String qrText) {
        if (qrText == null || qrText.isBlank()) return null;

        // Plain cert ID — no pipe separator
        if (!qrText.contains(SEP)) {
            return new ParsedQR(qrText.trim(), null, null, null);
        }

        String[] parts = qrText.split("\\|", -1);

        // Old format: DCVS|certId|orgName  (3 parts)
        if (parts.length == 3 && PREFIX.equals(parts[0])) {
            return new ParsedQR(parts[1], null, null, parts[2]);
        }

        // New format: DCVS|certId|studentName|course|orgName|issueDate|expiryDate (7 parts)
        if (parts.length >= 5 && PREFIX.equals(parts[0])) {
            return new ParsedQR(
                    parts[1],                          // certId
                    parts[2],                          // studentName
                    parts[3],                          // course
                    parts[4]                           // orgName
            );
        }

        // Fallback — treat entire string as cert ID
        return new ParsedQR(qrText.trim(), null, null, null);
    }

    /**
     * Extracts just the Certificate ID from a QR string.
     * This is what the verification module needs.
     */
    public static String extractCertId(String qrText) {
        ParsedQR parsed = parse(qrText);
        return parsed != null ? parsed.certId : qrText.trim();
    }

    // ── Parsed result ─────────────────────────────────────────────────────────

    public static class ParsedQR {
        public final String certId;
        public final String studentName;  // may be null for old QR codes
        public final String course;       // may be null for old QR codes
        public final String orgName;      // may be null for plain cert IDs

        public ParsedQR(String certId, String studentName, String course, String orgName) {
            this.certId      = certId;
            this.studentName = studentName;
            this.course      = course;
            this.orgName     = orgName;
        }

        public boolean hasStudentInfo() {
            return studentName != null && !studentName.isBlank();
        }

        @Override
        public String toString() {
            return "ParsedQR{certId='" + certId + "', student='" + studentName
                    + "', course='" + course + "', org='" + orgName + "'}";
        }
    }
}