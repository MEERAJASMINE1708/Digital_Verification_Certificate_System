package com.dcvs.dao;

import com.dcvs.model.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CRUD operations for the audit_logs table — MySQL version.
 * Uses java.sql.Timestamp for DATETIME columns (MySQL native type).
 * Module 2 — Meghana
 */
public class AuditLogDAO {

    private static final Logger LOGGER =
            Logger.getLogger(AuditLogDAO.class.getName());

    private final DatabaseManager db = DatabaseManager.getInstance();

    // ── Insert ────────────────────────────────────────────────────────────────

    public boolean insert(AuditLog log) {
        String sql =
                "INSERT INTO audit_logs (action, actor, target_id, details, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getAction());
            ps.setString(2, log.getActor());
            ps.setString(3, log.getTargetId());
            ps.setString(4, log.getDetails());
            ps.setTimestamp(5, Timestamp.valueOf(log.getTimestamp())); // MySQL DATETIME

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to insert audit log: " + log.getAction(), e);
            return false;
        }
    }

    // ── Find all ──────────────────────────────────────────────────────────────

    public List<AuditLog> findAll() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY timestamp DESC";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve all audit logs", e);
        }
        return list;
    }

    // ── Find by date range ────────────────────────────────────────────────────

    public List<AuditLog> findByDateRange(LocalDateTime from, LocalDateTime to) {
        List<AuditLog> list = new ArrayList<>();
        String sql =
                "SELECT * FROM audit_logs " +
                "WHERE timestamp BETWEEN ? AND ? " +
                "ORDER BY timestamp DESC";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(from)); // MySQL DATETIME
            ps.setTimestamp(2, Timestamp.valueOf(to));   // MySQL DATETIME
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve audit logs by date range", e);
        }
        return list;
    }

    // ── Row mapper ────────────────────────────────────────────────────────────

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog al = new AuditLog();
        al.setLogId(rs.getInt("log_id"));
        al.setAction(rs.getString("action"));
        al.setActor(rs.getString("actor"));
        al.setTargetId(rs.getString("target_id"));
        al.setDetails(rs.getString("details"));
        al.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime()); // Timestamp → LocalDateTime
        return al;
    }
}
