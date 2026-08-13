package com.dcvs.model;

/**
 * POJO representing a system user (Admin / Issuer / Verifier).
 * Module 2 — Meghana
 */
public class User {

    public enum Role { ADMIN, ISSUER, VERIFIER }

    private int userId;
    private String username;
    private String hashedPassword;  // SHA-256 hashed
    private Role role;
    private boolean active;

    public User() {}

    public User(int userId, String username, String hashedPassword, Role role, boolean active) {
        this.userId = userId;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.active = active;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getUserId()              { return userId; }
    public String getUsername()         { return username; }
    public String getHashedPassword()   { return hashedPassword; }
    public Role getRole()               { return role; }
    public boolean isActive()           { return active; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setUserId(int userId)                   { this.userId = userId; }
    public void setUsername(String username)            { this.username = username; }
    public void setHashedPassword(String hp)            { this.hashedPassword = hp; }
    public void setRole(Role role)                      { this.role = role; }
    public void setActive(boolean active)               { this.active = active; }

    @Override
    public String toString() {
        return "User{username='" + username + "', role=" + role + '}';
    }
}
