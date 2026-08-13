package com.dcvs.ui;

import com.dcvs.service.CertificateService;
import com.dcvs.service.CourseService;
import com.dcvs.service.UserService;
import com.dcvs.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dark dashboard home with cyber-style stat cards.
 */
public class DashboardHomePanel extends JPanel {

    private final JFrame parentFrame;

    public DashboardHomePanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);

        add(buildHeader(),     BorderLayout.NORTH);
        add(buildStatCards(),  BorderLayout.CENTER);
        add(buildQuickActions(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);

        JLabel title = new JLabel("// SYSTEM OVERVIEW");
        title.setFont(new Font("Monospaced", Font.BOLD, 20));
        title.setForeground(AppConstants.CYAN);
        h.add(title, BorderLayout.WEST);

        JLabel org = new JLabel(AppConstants.ORG_NAME + "  ●");
        org.setFont(new Font("Monospaced", Font.PLAIN, 11));
        org.setForeground(AppConstants.TEXT_MUTED);
        h.add(org, BorderLayout.EAST);

        return h;
    }

    private JPanel buildStatCards() {
        CertificateService cs = new CertificateService();
        CourseService      cr = new CourseService();
        UserService        us = new UserService();

        long total   = cs.findAll().size();
        long active  = cs.findAll().stream().filter(c -> "ACTIVE".equals(c.getStatus())).count();
        long courses = cr.findActive().size();
        long users   = us.findAll().size();

        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);

        row.add(statCard("TOTAL CERTS",   String.valueOf(total),  "certificates issued",  AppConstants.CYAN));
        row.add(statCard("ACTIVE",        String.valueOf(active), "currently valid",       AppConstants.GREEN));
        row.add(statCard("COURSES",       String.valueOf(courses),"available",             AppConstants.WARNING));
        row.add(statCard("USERS",         String.valueOf(users),  "in system",             new Color(150, 100, 255)));

        return row;
    }

    private JPanel statCard(String label, String value, String sub, Color accent) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppConstants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 1),
                new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.insets = new Insets(2, 0, 2, 0);

        // Top accent line
        JLabel accentBar = new JLabel("─────");
        accentBar.setForeground(accent);
        accentBar.setFont(new Font("Monospaced", Font.PLAIN, 8));
        card.add(accentBar, g);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        valLabel.setForeground(accent);
        card.add(valLabel, g);

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        titleLabel.setForeground(AppConstants.TEXT_PRIMARY);
        card.add(titleLabel, g);

        JLabel subLabel = new JLabel(sub);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLabel.setForeground(AppConstants.TEXT_MUTED);
        card.add(subLabel, g);

        return card;
    }

    private JPanel buildQuickActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        p.setBackground(AppConstants.BG_ELEVATED);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(4, 4, 4, 4)));

        JLabel lbl = new JLabel("  QUICK ACTIONS  //");
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(AppConstants.TEXT_DIM);
        p.add(lbl);

        JButton issueCert     = UIFactory.successButton("⊕  Issue Certificate");
        JButton verifyCert    = UIFactory.primaryButton("◎  Verify Certificate");
        JButton manageCourses = UIFactory.outlineButton("◈  Manage Courses");

        issueCert.addActionListener(e     -> navigateTo(new IssuePanel(parentFrame)));
        verifyCert.addActionListener(e    -> navigateTo(new VerifyPanel(parentFrame)));
        manageCourses.addActionListener(e -> navigateTo(new CourseManagementPanel()));

        p.add(issueCert); p.add(verifyCert); p.add(manageCourses);
        return p;
    }

    private void navigateTo(JPanel panel) {
        Container parent = getParent();
        if (parent != null) {
            parent.removeAll();
            parent.add(panel, BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        }
    }
}