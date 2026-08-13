package com.dcvs.dao;

import com.dcvs.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CRUD operations for the users table — MySQL version.
 * No date columns — identical logic to SQLite version.
 * Module 2 — Meghana
 */
public class UserDAO {

    private static final Logger LOGGER =
            Logger.getLogger(UserDAO.class.getName());

    private final DatabaseManager db = DatabaseManager.getInstance();

    // ── Insert ────────────────────────────────────────────────────────────────

    public boolean insert(User user) {
        String sql =
                "INSERT INTO users (username, hashed_password, role, active) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getRole().name());
            ps.setInt(4,    user.isActive() ? 1 : 0);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to insert user: " + user.getUsername(), e);
            return false;
        }
    }

    // ── Find by username ──────────────────────────────────────────────────────

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find user: " + username, e);
        }
        return Optional.empty();
    }

    // ── Find all ──────────────────────────────────────────────────────────────

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve all users", e);
        }
        return list;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean update(User user) {
        String sql =
                "UPDATE users SET hashed_password = ?, role = ?, active = ? " +
                "WHERE user_id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getHashedPassword());
            ps.setString(2, user.getRole().name());
            ps.setInt(3,    user.isActive() ? 1 : 0);
            ps.setInt(4,    user.getUserId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update user ID: " + user.getUserId(), e);
            return false;
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete user ID: " + userId, e);
            return false;
        }
    }

    // ── Row mapper ────────────────────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setHashedPassword(rs.getString("hashed_password"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setActive(rs.getInt("active") == 1);
        return u;
    }
}
