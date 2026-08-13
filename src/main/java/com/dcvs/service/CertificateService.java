package com.dcvs.service;

import com.dcvs.crypto.Signer;
import com.dcvs.dao.AuditLogDAO;
import com.dcvs.dao.CertificateDAO;
import com.dcvs.model.AuditLog;
import com.dcvs.model.Certificate;
import com.dcvs.util.AppConstants;
import com.dcvs.util.HashUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Orchestrates certificate generation: UUID, hashing, signing, DB save.
 * Module 3 — Afsana
 */
public class CertificateService {

    private static final Logger LOGGER = Logger.getLogger(CertificateService.class.getName());

    private final CertificateDAO certDAO  = new CertificateDAO();
    private final AuditLogDAO    auditDAO = new AuditLogDAO();

    /**
     * Issues a digital certificate when a student completes a course.
     * Automatically sets org name from AppConstants.
     */
    public Certificate issue(String recipientName, String recipientId,
                             int courseId, String courseName,
                             LocalDate issueDate, LocalDate expiryDate) {

        String certId = UUID.randomUUID().toString().toUpperCase();
        String actor  = SessionManager.getInstance().getUsername();
        String org    = AppConstants.ORG_NAME;

        // Build canonical payload for signing
        String payload = buildPayload(certId, recipientName, recipientId,
                courseName, issueDate.toString(), expiryDate.toString(), org);
        String hash      = HashUtil.sha256(payload);
        String signature = Signer.sign(payload);

        if (signature == null) {
            LOGGER.severe("Signing failed — certificate not issued.");
            return null;
        }

        Certificate cert = new Certificate(certId, recipientName, recipientId,
                courseId, courseName, issueDate, expiryDate,
                signature, hash, "ACTIVE", actor, org);

        if (!certDAO.insert(cert)) {
            LOGGER.severe("DB insert failed — certificate not issued.");
            return null;
        }

        auditDAO.insert(new AuditLog(0, "ISSUE_CERT", actor, certId,
                "Issued to " + recipientName + " for " + courseName, LocalDateTime.now()));
        return cert;
    }

    public boolean revoke(String certId) {
        boolean ok = certDAO.updateStatus(certId, "REVOKED");
        if (ok) audit("REVOKE_CERT", certId, "Revoked");
        return ok;
    }

    public boolean renew(String certId) {
        boolean ok = certDAO.updateStatus(certId, "ACTIVE");
        if (ok) audit("RENEW_CERT", certId, "Renewed");
        return ok;
    }

    public Optional<Certificate> findById(String certId) { return certDAO.findById(certId); }
    public List<Certificate>     findAll()               { return certDAO.findAll(); }
    public List<Certificate>     search(String kw)       { return certDAO.search(kw); }

    /** Canonical payload string — must match VerificationService exactly. */
    public static String buildPayload(String certId, String recipientName,
                                       String recipientId, String course,
                                       String issueDate, String expiryDate, String org) {
        return certId + "|" + recipientName + "|" + recipientId + "|"
                + course + "|" + issueDate + "|" + expiryDate + "|" + org;
    }

    private void audit(String action, String target, String details) {
        auditDAO.insert(new AuditLog(0, action,
                SessionManager.getInstance().getUsername(),
                target, details, LocalDateTime.now()));
    }
}
