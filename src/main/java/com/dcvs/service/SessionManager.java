package com.dcvs.service;

import com.dcvs.model.User;

/**
 * Holds the currently authenticated user for the duration of the session.
 * Simple in-memory singleton; cleared on logout.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void login(User user)    { this.currentUser = user; }
    public void logout()            { this.currentUser = null; }
    public User getCurrentUser()    { return currentUser; }
    public boolean isLoggedIn()     { return currentUser != null; }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "unknown";
    }
}
