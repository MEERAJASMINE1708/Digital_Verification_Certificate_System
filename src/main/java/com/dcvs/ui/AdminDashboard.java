package com.dcvs.ui;

import com.dcvs.service.SessionManager;
import com.dcvs.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Dark admin dashboard with sidebar navigation.
 * Module 5 — Sravanika
 */
public class AdminDashboard extends JPanel {

    private final JFrame parentFrame;
    private JPanel       contentArea;
    private JButton      activeNavBtn;

    public AdminDashboard(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppConstants.BG_DARK);

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(AppConstants.BG_DARK);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(contentArea, BorderLayout.CENTER);

        showPanel(new DashboardHomePanel(parentFrame));
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppConstants.BG_DARKEST);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppConstants.CYAN_DARK),
                new EmptyBorder(10, 20, 10, 20)));
        bar.setPreferredSize(new Dimension(0, 52));

        // Left: system label
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(AppConstants.GREEN);
        dot.setFont(new Font("SansSerif", Font.PLAIN, 10));
        left.add(dot);

        JLabel title = new JLabel("DCVS  //  Secure Certificate Verification System");
        title.setFont(new Font("Monospaced", Font.BOLD, 13));
        title.setForeground(AppConstants.CYAN);
        left.add(title);
        bar.add(left, BorderLayout.WEST);

        // Right: user + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel userLabel = new JLabel("[ "
                + SessionManager.getInstance().getUsername().toUpperCase()
                + " : ADMIN ]");
        userLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        userLabel.setForeground(AppConstants.TEXT_MUTED);
        right.add(userLabel);

        JButton logoutBtn = UIFactory.outlineButton("[ EXIT ]");
        logoutBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        logoutBtn.addActionListener(e -> logout());
        right.add(logoutBtn);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppConstants.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppConstants.BORDER_SOLID));

        // Nav label
        JLabel navLabel = new JLabel("  NAVIGATION");
        navLabel.setFont(new Font("Monospaced", Font.PLAIN, 9));
        navLabel.setForeground(AppConstants.TEXT_DIM);
        navLabel.setBorder(new EmptyBorder(16, 16, 8, 0));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navLabel);

        addNavButton(sidebar, "⬡  Dashboard",   () -> showPanel(new DashboardHomePanel(parentFrame)));
        addNavButton(sidebar, "◈  Courses",      () -> showPanel(new CourseManagementPanel()));
        addNavButton(sidebar, "◉  Certificates", () -> showPanel(new CertificateListPanel(parentFrame)));
        addNavButton(sidebar, "⊕  Issue Cert",   () -> showPanel(new IssuePanel(parentFrame)));
        addNavButton(sidebar, "◎  Verify Cert",  () -> showPanel(new VerifyPanel(parentFrame)));
        addNavButton(sidebar, "⊞  Users",         () -> showPanel(new UserMgmtPanel()));
        addNavButton(sidebar, "≡  Audit Log",     () -> showPanel(new AuditLogPanel()));

        sidebar.add(Box.createVerticalGlue());

        // Footer
        JLabel footer = new JLabel("  v" + AppConstants.SYSTEM_VERSION + "  |  RSA-2048");
        footer.setFont(new Font("Monospaced", Font.PLAIN, 9));
        footer.setForeground(AppConstants.TEXT_DIM);
        footer.setBorder(new EmptyBorder(8, 16, 12, 0));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 12));
        btn.setForeground(AppConstants.TEXT_MUTED);
        btn.setBackground(AppConstants.BG_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(11, 20, 11, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeNavBtn) {
                    btn.setBackground(AppConstants.BG_ELEVATED);
                    btn.setForeground(AppConstants.CYAN);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeNavBtn) {
                    btn.setBackground(AppConstants.BG_SIDEBAR);
                    btn.setForeground(AppConstants.TEXT_MUTED);
                }
            }
        });

        btn.addActionListener(e -> {
            if (activeNavBtn != null) {
                activeNavBtn.setBackground(AppConstants.BG_SIDEBAR);
                activeNavBtn.setForeground(AppConstants.TEXT_MUTED);
                activeNavBtn.setBorderPainted(false);
            }
            activeNavBtn = btn;
            btn.setBackground(new Color(0, 230, 255, 20));
            btn.setForeground(AppConstants.CYAN);
            btn.setBorderPainted(true);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, AppConstants.CYAN),
                    new EmptyBorder(11, 17, 11, 20)));
            action.run();
        });

        sidebar.add(btn);
    }

    public void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void logout() {
        SessionManager.getInstance().logout();
        parentFrame.setSize(900, 600);
        parentFrame.setResizable(false);
        parentFrame.setLocationRelativeTo(null);
        parentFrame.setContentPane(new LoginPanel(parentFrame));
        parentFrame.setTitle("DCVS — Secure Certificate Verification System");
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}