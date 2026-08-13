package com.dcvs.model;

import java.time.LocalDate;

/**
 * POJO representing a digital certificate.
 */
public class Certificate {

    private String    certId;
    private String    recipientName;
    private String    recipientId;
    private int       courseId;
    private String    course;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String    signature;
    private String    certHash;
    private String    status;      // ACTIVE | REVOKED | EXPIRED
    private String    issuedBy;
    private String    orgName;     // organization that issued the cert

    public Certificate() {}

    public Certificate(String certId, String recipientName, String recipientId,
                       int courseId, String course, LocalDate issueDate, LocalDate expiryDate,
                       String signature, String certHash, String status,
                       String issuedBy, String orgName) {
        this.certId        = certId;
        this.recipientName = recipientName;
        this.recipientId   = recipientId;
        this.courseId      = courseId;
        this.course        = course;
        this.issueDate     = issueDate;
        this.expiryDate    = expiryDate;
        this.signature     = signature;
        this.certHash      = certHash;
        this.status        = status;
        this.issuedBy      = issuedBy;
        this.orgName       = orgName;
    }

    public String    getCertId()        { return certId; }
    public String    getRecipientName() { return recipientName; }
    public String    getRecipientId()   { return recipientId; }
    public int       getCourseId()      { return courseId; }
    public String    getCourse()        { return course; }
    public LocalDate getIssueDate()     { return issueDate; }
    public LocalDate getExpiryDate()    { return expiryDate; }
    public String    getSignature()     { return signature; }
    public String    getCertHash()      { return certHash; }
    public String    getStatus()        { return status; }
    public String    getIssuedBy()      { return issuedBy; }
    public String    getOrgName()       { return orgName; }

    public void setCertId(String v)        { certId = v; }
    public void setRecipientName(String v) { recipientName = v; }
    public void setRecipientId(String v)   { recipientId = v; }
    public void setCourseId(int v)         { courseId = v; }
    public void setCourse(String v)        { course = v; }
    public void setIssueDate(LocalDate v)  { issueDate = v; }
    public void setExpiryDate(LocalDate v) { expiryDate = v; }
    public void setSignature(String v)     { signature = v; }
    public void setCertHash(String v)      { certHash = v; }
    public void setStatus(String v)        { status = v; }
    public void setIssuedBy(String v)      { issuedBy = v; }
    public void setOrgName(String v)       { orgName = v; }
}
