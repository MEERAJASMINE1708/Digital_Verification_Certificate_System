package com.dcvs.ui;

import com.dcvs.util.AppConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dark Charcoal + Electric Cyan + Neon Green UI factory.
 */
public final class UIFactory {

    private UIFactory() {}

    // ── Buttons ───────────────────────────────────────────────────────────────

    /** Cyan outline button — primary action. */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(AppConstants.BG_CARD);
        btn.setForeground(AppConstants.CYAN);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.CYAN, 1),
                new EmptyBorder(8, 18, 8, 18)));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0, 230, 255, 30));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(AppConstants.BG_CARD);
                btn.setForeground(AppConstants.CYAN);
            }
        });
        return btn;
    }

    /** Green filled button — success/generate actions. */
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(AppConstants.GREEN_DARK);
        btn.setForeground(AppConstants.GREEN);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.GREEN, 1),
                new EmptyBorder(8, 18, 8, 18)));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0, 255, 136, 40));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(AppConstants.GREEN_DARK);
                btn.setForeground(AppConstants.GREEN);
            }
        });
        return btn;
    }

    /** Red danger button. */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(60, 10, 15));
        btn.setForeground(AppConstants.DANGER);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.DANGER, 1),
                new EmptyBorder(8, 18, 8, 18)));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 60, 80, 30));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 10, 15));
                btn.setForeground(AppConstants.DANGER);
            }
        });
        return btn;
    }

    /** Dim outline button — secondary actions. */
    public static JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(AppConstants.BG_ELEVATED);
        btn.setForeground(AppConstants.TEXT_MUTED);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(7, 14, 7, 14)));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(AppConstants.CYAN);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppConstants.CYAN, 1),
                        new EmptyBorder(7, 14, 7, 14)));
            }
            public void mouseExited(MouseEvent e) {
                btn.setForeground(AppConstants.TEXT_MUTED);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                        new EmptyBorder(7, 14, 7, 14)));
            }
        });
        return btn;
    }

    // ── Input fields ──────────────────────────────────────────────────────────

    public static JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(AppConstants.BG_INPUT);
        f.setForeground(AppConstants.TEXT_PRIMARY);
        f.setCaretColor(AppConstants.CYAN);
        f.setFont(new Font("Monospaced", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(8, 12, 8, 12)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppConstants.CYAN, 1),
                        new EmptyBorder(8, 12, 8, 12)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                        new EmptyBorder(8, 12, 8, 12)));
            }
        });
        return f;
    }

    public static JPasswordField styledPassword(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setBackground(AppConstants.BG_INPUT);
        f.setForeground(AppConstants.TEXT_PRIMARY);
        f.setCaretColor(AppConstants.CYAN);
        f.setFont(new Font("Monospaced", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(8, 12, 8, 12)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppConstants.CYAN, 1),
                        new EmptyBorder(8, 12, 8, 12)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                        new EmptyBorder(8, 12, 8, 12)));
            }
        });
        return f;
    }

    // ── Cards & panels ────────────────────────────────────────────────────────

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(AppConstants.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JPanel glowCard() {
        JPanel p = new JPanel();
        p.setBackground(AppConstants.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.CYAN_DARK, 1),
                new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    // ── Labels ────────────────────────────────────────────────────────────────

    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setForeground(AppConstants.CYAN);
        return l;
    }

    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(AppConstants.TEXT_MUTED);
        return l;
    }

    public static JLabel monoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, 12));
        l.setForeground(AppConstants.TEXT_PRIMARY);
        return l;
    }

    public static JLabel statusBadge(String status) {
        JLabel l = new JLabel("  " + status + "  ");
        l.setFont(new Font("Monospaced", Font.BOLD, 11));
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(3, 8, 3, 8));
        switch (status.toUpperCase()) {
            case "ACTIVE"  -> { l.setBackground(new Color(0, 50, 30));  l.setForeground(AppConstants.GREEN); }
            case "REVOKED" -> { l.setBackground(new Color(60, 10, 15)); l.setForeground(AppConstants.DANGER); }
            case "EXPIRED" -> { l.setBackground(new Color(60, 40, 0));  l.setForeground(AppConstants.WARNING); }
            default        -> { l.setBackground(AppConstants.BG_ELEVATED); l.setForeground(AppConstants.TEXT_MUTED); }
        }
        return l;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setBackground(AppConstants.BG_CARD);
        table.setForeground(AppConstants.TEXT_PRIMARY);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(0, 230, 255, 40));
        table.setSelectionForeground(AppConstants.CYAN);
        table.getTableHeader().setBackground(AppConstants.BG_DARKEST);
        table.getTableHeader().setForeground(AppConstants.CYAN);
        table.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 12));
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppConstants.CYAN_DARK));
        table.setAutoCreateRowSorter(true);
        table.setGridColor(AppConstants.BORDER_SOLID);
    }

    // ── Scroll pane ───────────────────────────────────────────────────────────

    public static JScrollPane darkScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(AppConstants.BG_CARD);
        scroll.setBackground(AppConstants.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1));
        scroll.getVerticalScrollBar().setBackground(AppConstants.BG_ELEVATED);
        scroll.getHorizontalScrollBar().setBackground(AppConstants.BG_ELEVATED);
        return scroll;
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    public static JButton logoutButton(JFrame frame) {
        JButton btn = outlineButton("[ EXIT ]");
        btn.setFont(new Font("Monospaced", Font.BOLD, 11));
        btn.addActionListener(e -> {
            com.dcvs.service.SessionManager.getInstance().logout();
            frame.setSize(900, 600);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new LoginPanel(frame));
            frame.setTitle("DCVS — Secure Certificate Verification System");
            frame.revalidate();
            frame.repaint();
        });
        return btn;
    }

    // ── Separator ─────────────────────────────────────────────────────────────

    public static JSeparator darkSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppConstants.BORDER_SOLID);
        sep.setBackground(AppConstants.BG_DARK);
        return sep;
    }
}