package com.dcvs.service;

import com.dcvs.dao.AuditLogDAO;
import com.dcvs.dao.UserDAO;
import com.dcvs.model.AuditLog;
import com.dcvs.model.User;
import com.dcvs.util.HashUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Business logic for user creation, role assignment, and deletion.
 * Module 5 — Sravanika
 */
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private final UserDAO     userDAO  = new UserDAO();
    private final AuditLogDAO auditDAO = new AuditLogDAO();

    /** Authenticates a user. Returns the User if credentials match, empty otherwise. */
    public Optional<User> authenticate(String username, String plainPassword) {
        Optional<User> opt = userDAO.findByUsername(username);
        if (opt.isEmpty()) return Optional.empty();
        User user = opt.get();
        if (!user.isActive()) return Optional.empty();
        String hashed = HashUtil.sha256(plainPassword);
        if (hashed.equals(user.getHashedPassword())) {
            String actor = SessionManager.getInstance().isLoggedIn()
                    ? SessionManager.getInstance().getUsername() : username;
            auditDAO.insert(new AuditLog(0, "LOGIN", actor, null,
                    "Successful login as " + user.getRole(), LocalDateTime.now()));
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /** Creates a new user account. Returns false if username already exists. */
    public boolean createUser(String username, String plainPassword, User.Role role) {
        if (userDAO.findByUsername(username).isPresent()) return false;
        User user = new User(0, username, HashUtil.sha256(plainPassword), role, true);
        boolean ok = userDAO.insert(user);
        if (ok) {
            String actor = SessionManager.getInstance().getUsername();
            auditDAO.insert(new AuditLog(0, "CREATE_USER", actor, username,
                    "Created user with role " + role, LocalDateTime.now()));
        }
        return ok;
    }

    /**
     * Updates a user's password and/or role (called from UserMgmtPanel).
     *
     * @param user             the User object with updated role/active fields already set
     * @param newPlainPassword new plain-text password, or null/blank to keep existing
     */
    public boolean updateUser(User user, String newPlainPassword) {
        if (newPlainPassword != null && !newPlainPassword.isBlank()) {
            user.setHashedPassword(HashUtil.sha256(newPlainPassword));
        }
        boolean ok = userDAO.update(user);
        if (ok) {
            String actor = SessionManager.getInstance().getUsername();
            auditDAO.insert(new AuditLog(0, "UPDATE_USER", actor, user.getUsername(),
                    "Updated role/status", LocalDateTime.now()));
        }
        return ok;
    }

    /**
     * Updates a user's password and/or role by user ID.
     * Kept for internal/service-layer use.
     */
    public boolean updateUser(int userId, String newPlainPassword, User.Role role, boolean active) {
        Optional<User> opt = userDAO.findAll().stream()
                .filter(u -> u.getUserId() == userId).findFirst();
        if (opt.isEmpty()) return false;
        User user = opt.get();
        user.setRole(role);
        user.setActive(active);
        return updateUser(user, newPlainPassword);
    }

    /** Deletes a user by ID. Prevents deletion of the last admin. */
    public boolean deleteUser(int userId) {
        List<User> admins = userDAO.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN && u.isActive()).toList();
        Optional<User> target = userDAO.findAll().stream()
                .filter(u -> u.getUserId() == userId).findFirst();
        if (target.isEmpty()) return false;
        if (target.get().getRole() == User.Role.ADMIN && admins.size() <= 1) {
            return false; // Protect the last admin
        }
        boolean ok = userDAO.delete(userId);
        if (ok) {
            String actor = SessionManager.getInstance().getUsername();
            auditDAO.insert(new AuditLog(0, "DELETE_USER", actor,
                    target.get().getUsername(), "User deleted", LocalDateTime.now()));
        }
        return ok;
    }

    /** Returns all users. Alias for getAllUsers() — used by UI panels. */
    public List<User> findAll() {
        return userDAO.findAll();
    }

    /** Returns all users. */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}