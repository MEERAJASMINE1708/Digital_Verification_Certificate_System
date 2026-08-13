package com.dcvs.model;

import java.time.LocalDateTime;

/**
 * POJO representing an audit log entry.
 * Module 2 — Meghana
 */
public class AuditLog {

    private int logId;
    private String action;       // e.g. "ISSUE_CERT", "REVOKE_CERT", "LOGIN", "VERIFY"
    private String actor;        // username who performed the action
    private String targetId;     // certId or userId affected (nullable)
    private String details;      // free-text description
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(int logId, String action, String actor,
                    String targetId, String details, LocalDateTime timestamp) {
        this.logId = logId;
        this.action = action;
        this.actor = actor;
        this.targetId = targetId;
        this.details = details;
        this.timestamp = timestamp;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getLogId()               { return logId; }
    public String getAction()           { return action; }
    public String getActor()            { return actor; }
    public String getTargetId()         { return targetId; }
    public String getDetails()          { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setLogId(int logId)               { this.logId = logId; }
    public void setAction(String action)          { this.action = action; }
    public void setActor(String actor)            { this.actor = actor; }
    public void setTargetId(String targetId)      { this.targetId = targetId; }
    public void setDetails(String details)        { this.details = details; }
    public void setTimestamp(LocalDateTime ts)    { this.timestamp = ts; }

    @Override
    public String toString() {
        return "AuditLog{action='" + action + "', actor='" + actor +
               "', timestamp=" + timestamp + '}';
    }
}
