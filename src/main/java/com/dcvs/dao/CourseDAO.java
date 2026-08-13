package com.dcvs.dao;

import com.dcvs.model.Course;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CRUD operations for the courses table.
 */
public class CourseDAO {

    private static final Logger LOGGER = Logger.getLogger(CourseDAO.class.getName());
    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean insert(Course c) {
        String sql = "INSERT INTO courses (course_name,category,description,duration,active,created_at) VALUES (?,?,?,?,?,?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getDuration());
            ps.setBoolean(5, c.isActive());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Insert course failed", e); return false; }
    }

    public boolean update(Course c) {
        String sql = "UPDATE courses SET course_name=?,category=?,description=?,duration=?,active=? WHERE course_id=?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getDuration());
            ps.setBoolean(5, c.isActive());
            ps.setInt(6, c.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Update course failed", e); return false; }
    }

    public boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Delete course failed", e); return false; }
    }

    public Optional<Course> findById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id=?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Find course failed", e); }
        return Optional.empty();
    }

    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY category, course_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "FindAll courses failed", e); }
        return list;
    }

    public List<Course> findActive() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE active=TRUE ORDER BY category, course_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "FindActive courses failed", e); }
        return list;
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseName(rs.getString("course_name"));
        c.setCategory(rs.getString("category"));
        c.setDescription(rs.getString("description"));
        c.setDuration(rs.getString("duration"));
        c.setActive(rs.getBoolean("active"));
        Timestamp ts = rs.getTimestamp("created_at");
        c.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return c;
    }
}
