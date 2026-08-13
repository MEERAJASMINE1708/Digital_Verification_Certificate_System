package com.dcvs.ui;

import com.dcvs.model.User;
import com.dcvs.service.SessionManager;
import com.dcvs.service.UserService;
import com.dcvs.util.AppConstants;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dark-themed login panel — DCVS branding.
 * Module 1 — Meera
 */
public class LoginPanel extends JPanel {

    private final JFrame      parentFrame;
    private final UserService userService = new UserService();

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;

    private Timer  animTimer;
    private int    dotCount = 0;
    private JLabel dotLabel;

    public LoginPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        buildUI();
        startAnimation();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppConstants.BG_DARK);
        add(buildLeftPanel(),  BorderLayout.WEST);
        add(buildRightPanel(), BorderLayout.CENTER);
    }

    // ── Left branding panel ───────────────────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppConstants.BG_DARKEST);
        p.setPreferredSize(new Dimension(360, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppConstants.CYAN_DARK));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx  = 0;
        g.gridy  = GridBagConstraints.RELATIVE;
        g.insets = new Insets(8, 16, 8, 16);
        g.anchor = GridBagConstraints.CENTER;
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Status badge
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(4, 12, 4, 12)));
        JLabel badgeDot  = new JLabel("●");
        badgeDot.setForeground(AppConstants.GREEN);
        badgeDot.setFont(new Font("SansSerif", Font.PLAIN, 10));
        JLabel badgeText = new JLabel("SYSTEM ONLINE");
        badgeText.setForeground(AppConstants.TEXT_MUTED);
        badgeText.setFont(new Font("Monospaced", Font.PLAIN, 10));
        badge.add(badgeDot); badge.add(badgeText);
        p.add(badge, g);

        // Project acronym — large
        JLabel acronym = new JLabel("DCVS");
        acronym.setFont(new Font("SansSerif", Font.BOLD, 52));
        acronym.setForeground(AppConstants.CYAN);
        acronym.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(acronym, g);

        // Full project name
        JLabel fullName = new JLabel("<html><center>Digital Certificate<br>Verification System</center></html>");
        fullName.setFont(new Font("SansSerif", Font.BOLD, 16));
        fullName.setForeground(new Color(180, 220, 255));
        fullName.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(fullName, g);

        // Separator
        p.add(UIFactory.darkSeparator(), g);

        
        // Animated dots
        dotLabel = new JLabel("● ● ●");
        dotLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        dotLabel.setForeground(AppConstants.CYAN_DARK);
        dotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(dotLabel, g);

         

        return p;
    }

    private JPanel miniStatCard(String label, String value) {
        JPanel c = new JPanel(new GridLayout(2, 1));
        c.setBackground(AppConstants.BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(8, 4, 8, 4)));
        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("Monospaced", Font.BOLD, 11));
        l.setForeground(AppConstants.CYAN);
        c.add(l);
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("Monospaced", Font.PLAIN, 9));
        v.setForeground(AppConstants.TEXT_MUTED);
        c.add(v);
        return c;
    }

    // ── Right login form ──────────────────────────────────────────────────────

    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(AppConstants.BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppConstants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(32, 32, 32, 32)));
        card.setMaximumSize(new Dimension(360, 9999));
        card.setPreferredSize(new Dimension(340, 480));

        // Header
        JLabel titleLabel = new JLabel("SYSTEM ACCESS");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 17));
        titleLabel.setForeground(AppConstants.CYAN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));

        JLabel subLabel = new JLabel("Enter credentials to authenticate");
        subLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        subLabel.setForeground(AppConstants.TEXT_MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subLabel);
        card.add(Box.createVerticalStrut(14));

        card.add(stretchSeparator());
        card.add(Box.createVerticalStrut(14));

        // Username
        card.add(fieldLabel("USERNAME"));
        card.add(Box.createVerticalStrut(4));
        usernameField = UIFactory.styledField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));

        // Password
        card.add(fieldLabel("PASSWORD"));
        card.add(Box.createVerticalStrut(4));
        passwordField = UIFactory.styledPassword(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.addActionListener(e -> attemptLogin());
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(AppConstants.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(0, 80, 40));
        loginBtn.setForeground(AppConstants.GREEN);
        loginBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.GREEN, 1),
                new EmptyBorder(12, 18, 12, 18)));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(0, 100, 50));
                loginBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(new Color(0, 80, 40));
                loginBtn.setForeground(AppConstants.GREEN);
            }
        });
        loginBtn.addActionListener(e -> attemptLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(14));

        card.add(stretchSeparator());
        card.add(Box.createVerticalStrut(14));


        
        outer.add(card, new GridBagConstraints());
        return outer;
    }

    private JPanel resultCard(String label, String value, Color valueColor) {
        JPanel c = new JPanel(new GridLayout(2, 1));
        c.setBackground(AppConstants.BG_ELEVATED);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(10, 8, 10, 8)));
        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("Monospaced", Font.PLAIN, 9));
        l.setForeground(AppConstants.TEXT_MUTED);
        c.add(l);
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 13));
        v.setForeground(valueColor);
        c.add(v);
        return c;
    }

    // ── Auth logic ────────────────────────────────────────────────────────────

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠  Username and password are required.");
            return;
        }
        statusLabel.setText("// authenticating...");
        statusLabel.setForeground(AppConstants.CYAN);

        Optional<User> result = userService.authenticate(username, password);
        if (result.isEmpty()) {
            statusLabel.setText("✗  ACCESS DENIED — invalid credentials.");
            statusLabel.setForeground(AppConstants.DANGER);
            passwordField.setText("");
            return;
        }

        User user = result.get();
        SessionManager.getInstance().login(user);
        statusLabel.setText("✓  ACCESS GRANTED");
        statusLabel.setForeground(AppConstants.GREEN);
        openDashboard(user);
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            JPanel dashboard = switch (user.getRole()) {
                case ADMIN    -> new AdminDashboard(parentFrame);
                case ISSUER   -> new IssuePanel(parentFrame);
                case VERIFIER -> new VerifyPanel(parentFrame);
            };
            parentFrame.setTitle("DCVS — " + user.getRole() + " | " + user.getUsername());
            parentFrame.setSize(1200, 760);
            parentFrame.setResizable(true);
            parentFrame.setLocationRelativeTo(null);
            parentFrame.setContentPane(dashboard);
            parentFrame.revalidate();
            parentFrame.repaint();
        });
    }

    private void startAnimation() {
        animTimer = new Timer(600, e -> {
            dotCount = (dotCount + 1) % 4;
            String dots = "● ".repeat(dotCount) + "○ ".repeat(3 - dotCount);
            if (dotLabel != null) dotLabel.setText(dots.trim());
        });
        animTimer.start();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.BOLD, 10));
        l.setForeground(AppConstants.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /** A full-width separator that works inside a BoxLayout panel. */
    private JSeparator stretchSeparator() {
        JSeparator sep = UIFactory.darkSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }
}