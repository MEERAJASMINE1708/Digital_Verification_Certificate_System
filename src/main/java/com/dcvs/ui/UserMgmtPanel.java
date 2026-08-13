package com.dcvs.ui;

import com.dcvs.model.User;
import com.dcvs.service.UserService;
import com.dcvs.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dark-themed user management panel.
 * Module 5 — Sravanika
 */
public class UserMgmtPanel extends JPanel {

    private final UserService         userService = new UserService();
    private JTable                    table;
    private DefaultTableModel         tableModel;
    private JTextField                usernameField;
    private JPasswordField            passwordField;
    private JComboBox<User.Role>      roleCombo;

    private static final String[] COLUMNS = {"ID", "Username", "Role", "Active"};

    public UserMgmtPanel() {
        buildUI();
        loadUsers();
    }

    private void buildUI() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("// USER MANAGEMENT");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(AppConstants.CYAN);
        header.add(title, BorderLayout.WEST);
        header.add(UIFactory.mutedLabel("manage system users — admins, issuers, verifiers"),
                BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablePanel(), buildFormPanel());
        split.setDividerLocation(520);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(AppConstants.BG_DARK);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        JButton refreshBtn = UIFactory.outlineButton("↺ Refresh");
        refreshBtn.addActionListener(e -> loadUsers());
        toolbar.add(refreshBtn);
        p.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIFactory.styleTable(table);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                c.setBackground(sel ? new Color(0, 230, 255, 30)
                        : (row % 2 == 0 ? AppConstants.BG_CARD : AppConstants.BG_ELEVATED));
                c.setForeground(sel ? AppConstants.CYAN : AppConstants.TEXT_PRIMARY);
                ((JLabel) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });
        p.add(UIFactory.darkScroll(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel card = UIFactory.glowCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.insets = new Insets(6, 0, 6, 0);

        JLabel title = new JLabel("USER DETAILS");
        title.setFont(new Font("Monospaced", Font.BOLD, 13));
        title.setForeground(AppConstants.CYAN);
        card.add(title, g);
        card.add(UIFactory.darkSeparator(), g);

        card.add(fieldLabel("USERNAME *"), g);
        usernameField = UIFactory.styledField(18);
        card.add(usernameField, g);

        card.add(fieldLabel("PASSWORD"), g);
        passwordField = UIFactory.styledPassword(18);
        card.add(passwordField, g);

        card.add(fieldLabel("ROLE *"), g);
        roleCombo = new JComboBox<>(User.Role.values());
        roleCombo.setBackground(AppConstants.BG_INPUT);
        roleCombo.setForeground(AppConstants.TEXT_PRIMARY);
        roleCombo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        card.add(roleCombo, g);

        card.add(Box.createVerticalStrut(8), g);

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 6, 6));
        btnGrid.setOpaque(false);
        JButton createBtn = UIFactory.successButton("Create");
        createBtn.addActionListener(e -> createUser());
        JButton updateBtn = UIFactory.primaryButton("Update");
        updateBtn.addActionListener(e -> updateUser());
        JButton deleteBtn = UIFactory.dangerButton("Delete");
        deleteBtn.addActionListener(e -> deleteUser());
        JButton clearBtn  = UIFactory.outlineButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        btnGrid.add(createBtn); btnGrid.add(updateBtn);
        btnGrid.add(deleteBtn); btnGrid.add(clearBtn);
        card.add(btnGrid, g);

        return card;
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        for (User u : userService.findAll())
            tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole(), u.isActive()});
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int mr = table.convertRowIndexToModel(row);
        usernameField.setText((String) tableModel.getValueAt(mr, 1));
        roleCombo.setSelectedItem(tableModel.getValueAt(mr, 2));
        passwordField.setText("");
    }

    private void createUser() {
        String uname = usernameField.getText().trim();
        String pw    = new String(passwordField.getPassword());
        if (uname.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password required.");
            return;
        }
        boolean ok = userService.createUser(uname, pw, (User.Role) roleCombo.getSelectedItem());
        JOptionPane.showMessageDialog(this, ok ? "✓  User created." : "✗  Username exists.");
        if (ok) { clearForm(); loadUsers(); }
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        int userId = (int) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
        userService.findAll().stream().filter(u -> u.getUserId() == userId).findFirst()
                .ifPresent(u -> {
                    u.setRole((User.Role) roleCombo.getSelectedItem());
                    String pw = new String(passwordField.getPassword());
                    boolean ok = userService.updateUser(u, pw.isBlank() ? null : pw);
                    JOptionPane.showMessageDialog(this, ok ? "✓  Updated." : "✗  Failed.");
                    if (ok) { clearForm(); loadUsers(); }
                });
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        int userId = (int) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
        String uname = (String) tableModel.getValueAt(table.convertRowIndexToModel(row), 1);
        if (JOptionPane.showConfirmDialog(this, "Delete '" + uname + "'?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            boolean ok = userService.deleteUser(userId);
            JOptionPane.showMessageDialog(this, ok ? "✓  Deleted."
                    : "✗  Cannot delete — last admin or self-delete blocked.");
            if (ok) { clearForm(); loadUsers(); }
        }
    }

    private void clearForm() {
        usernameField.setText(""); passwordField.setText("");
        roleCombo.setSelectedIndex(0); table.clearSelection();
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, 10));
        l.setForeground(AppConstants.TEXT_MUTED);
        return l;
    }
}