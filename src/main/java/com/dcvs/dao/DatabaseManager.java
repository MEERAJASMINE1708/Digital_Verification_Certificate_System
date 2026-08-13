package com.dcvs.dao;

import com.dcvs.util.HashUtil;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MySQL connection manager — Singleton.
 * Reads credentials from environment variables with fallback defaults.
 */
public class DatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    private static final String DB_HOST = System.getenv().getOrDefault("DCVS_DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DCVS_DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DCVS_DB_NAME", "dcvs");
    private static final String DB_USER = System.getenv().getOrDefault("DCVS_DB_USER", "root");
    private static final String DB_PASS = System.getenv().getOrDefault("DCVS_DB_PASS", "Smeera@1708");

    private static final String DB_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC&characterEncoding=UTF-8&autoReconnect=true";

    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("MySQL JDBC Driver loaded.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found — check pom.xml", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public void initializeSchema() {
        try (Connection conn = getConnection()) {
            LOGGER.info("MySQL OK → host=" + DB_HOST + " db=" + DB_NAME + " user=" + DB_USER);
            // Seed default admin
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM users WHERE username='admin'");
            rs.next();
            if (rs.getInt(1) == 0) {
                conn.createStatement().executeUpdate(
                    "INSERT INTO users (username,hashed_password,role,active) VALUES " +
                    "('admin','" + HashUtil.sha256("admin123") + "','ADMIN',TRUE)");
                LOGGER.info("Default admin seeded.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "MySQL connection failed!", e);
            throw new RuntimeException("Cannot connect to MySQL. Check credentials.", e);
        }
    }
}
