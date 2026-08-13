package com.dcvs.dao;

import com.dcvs.model.Certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CRUD operations for the certificates table — MySQL v2.
 * Fixed: explicit java.sql.Date to avoid ambiguity with java.util.Date.
 * Fixed: course_id uses setNull when 0 to avoid FK constraint failure.
 */
public class CertificateDAO {

    private static final Logger LOGGER = Logger.getLogger(CertificateDAO.class.getName());
    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean insert(Certificate c) {
        String sql = "INSERT INTO certificates " +
                "(cert_id, recipient_name, recipient_id, course_id, course, " +
                "issue_date, expiry_date, signature, cert_hash, status, issued_by, org_name) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getCertId());
            ps.setString(2, c.getRecipientName());
            ps.setString(3, c.getRecipientId());

            // Use NULL when courseId is 0 to avoid FK constraint violation
            if (c.getCourseId() > 0) {
                ps.setInt(4, c.getCourseId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, c.getCourse());
            ps.setDate(6, java.sql.Date.valueOf(c.getIssueDate()));   // explicit java.sql.Date
            ps.setDate(7, java.sql.Date.valueOf(c.getExpiryDate()));  // explicit java.sql.Date
            ps.setString(8,  c.getSignature());
            ps.setString(9,  c.getCertHash());
            ps.setString(10, c.getStatus());
            ps.setString(11, c.getIssuedBy());
            ps.setString(12, c.getOrgName());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Insert certificate failed: " + e.getMessage(), e);
            return false;
        }
    }

    public Optional<Certificate> findById(String certId) {
        String sql = "SELECT * FROM certificates WHERE cert_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, certId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Find certificate failed", e);
        }
        return Optional.empty();
    }

    public List<Certificate> findAll() {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT * FROM certificates ORDER BY issue_date DESC";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "FindAll certificates failed", e);
        }
        return list;
    }

    public List<Certificate> search(String kw) {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT * FROM certificates " +
                "WHERE cert_id LIKE ? OR recipient_name LIKE ? OR course LIKE ? " +
                "ORDER BY issue_date DESC";
        String p = "%" + kw + "%";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Search certificates failed", e);
        }
        return list;
    }

    public boolean updateStatus(String certId, String status) {
        String sql = "UPDATE certificates SET status = ? WHERE cert_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, certId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "UpdateStatus failed", e);
            return false;
        }
    }

    private Certificate mapRow(ResultSet rs) throws SQLException {
        Certificate c = new Certificate();
        c.setCertId(rs.getString("cert_id"));
        c.setRecipientName(rs.getString("recipient_name"));
        c.setRecipientId(rs.getString("recipient_id"));
        c.setCourseId(rs.getInt("course_id"));
        c.setCourse(rs.getString("course"));
        c.setIssueDate(rs.getDate("issue_date").toLocalDate());
        c.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        c.setSignature(rs.getString("signature"));
        c.setCertHash(rs.getString("cert_hash"));
        c.setStatus(rs.getString("status"));
        c.setIssuedBy(rs.getString("issued_by"));
        c.setOrgName(rs.getString("org_name"));
        return c;
    }
}