package com.dcvs.service;

import com.dcvs.crypto.Verifier;
import com.dcvs.dao.AuditLogDAO;
import com.dcvs.dao.CertificateDAO;
import com.dcvs.model.AuditLog;
import com.dcvs.model.Certificate;
import com.dcvs.util.AppConstants;
import com.dcvs.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Verifies certificates — checks org ownership, signature, expiry, status.
 * Only certificates issued by AppConstants.ORG_NAME are considered valid.
 * Module 4 — Anjali
 */
public class VerificationService {

    public enum VerificationResult {
        VALID,
        INVALID_SIGNATURE,
        REVOKED,
        EXPIRED,
        NOT_FOUND,
        INVALID_ORG   // cert not issued by this organization
    }

    private final CertificateDAO certDAO  = new CertificateDAO();
    private final AuditLogDAO    auditDAO = new AuditLogDAO();

    public VerificationResult verify(String certId) {
        String actor = SessionManager.getInstance().getUsername();
        Optional<Certificate> opt = certDAO.findById(certId.trim().toUpperCase());

        if (opt.isEmpty()) {
            audit(actor, certId, "NOT_FOUND");
            return VerificationResult.NOT_FOUND;
        }

        Certificate cert = opt.get();

        // ── Organization validation — only our certs are valid ────────────────
        if (!AppConstants.ORG_NAME.equalsIgnoreCase(cert.getOrgName())) {
            audit(actor, certId, "INVALID_ORG");
            return VerificationResult.INVALID_ORG;
        }

        // ── Status checks ─────────────────────────────────────────────────────
        if ("REVOKED".equalsIgnoreCase(cert.getStatus())) {
            audit(actor, certId, "REVOKED");
            return VerificationResult.REVOKED;
        }

        if (DateUtil.isExpired(cert.getExpiryDate())) {
            certDAO.updateStatus(certId, "EXPIRED");
            audit(actor, certId, "EXPIRED");
            return VerificationResult.EXPIRED;
        }

        // ── Cryptographic signature check ─────────────────────────────────────
        String payload = CertificateService.buildPayload(
                cert.getCertId(), cert.getRecipientName(), cert.getRecipientId(),
                cert.getCourse(), cert.getIssueDate().toString(),
                cert.getExpiryDate().toString(), cert.getOrgName());

        boolean valid = Verifier.verify(payload, cert.getSignature());
        audit(actor, certId, valid ? "VALID" : "INVALID_SIGNATURE");

        return valid ? VerificationResult.VALID : VerificationResult.INVALID_SIGNATURE;
    }

    public Optional<Certificate> getCertificate(String certId) {
        return certDAO.findById(certId.trim().toUpperCase());
    }

    private void audit(String actor, String certId, String result) {
        auditDAO.insert(new AuditLog(0, "VERIFY_CERT", actor,
                certId, "Result: " + result, LocalDateTime.now()));
    }
}
