package com.dcvs.ui;

import com.dcvs.export.PdfExporter;
import com.dcvs.model.Certificate;
import com.dcvs.service.CertificateService;
import com.dcvs.util.AppConstants;
import com.dcvs.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Dark-themed certificate list panel.
 * Module 5 — Sravanika
 */
public class CertificateListPanel extends JPanel {

    private final JFrame             parentFrame;
    private final CertificateService service = new CertificateService();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;

    private static final String[] COLUMNS =
            {"Cert ID", "Student Name", "Stu. ID", "Course", "Issue Date", "Expiry", "Status", "Issued By"};

    public CertificateListPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        buildUI();
        loadCertificates(service.findAll());
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("// CERTIFICATE REGISTRY");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(AppConstants.CYAN);
        header.add(title, BorderLayout.WEST);
        header.add(UIFactory.mutedLabel("all certificates issued by " + AppConstants.ORG_NAME),
                BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        searchField = UIFactory.styledField(24);
        searchField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        searchField.addActionListener(e -> search());
        toolbar.add(searchField);

        JButton searchBtn = UIFactory.primaryButton("🔍 Search");
        searchBtn.addActionListener(e -> search());
        toolbar.add(searchBtn);

        JButton refreshBtn = UIFactory.outlineButton("↺ Refresh");
        refreshBtn.addActionListener(e -> loadCertificates(service.findAll()));
        toolbar.add(refreshBtn);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        JButton revokeBtn = UIFactory.dangerButton("Revoke");
        revokeBtn.addActionListener(e -> revokeSelected());
        toolbar.add(revokeBtn);

        JButton renewBtn = UIFactory.successButton("Renew");
        renewBtn.addActionListener(e -> renewSelected());
        toolbar.add(renewBtn);

        JButton exportBtn = UIFactory.outlineButton("📄 Export PDF");
        exportBtn.addActionListener(e -> exportPdf());
        toolbar.add(exportBtn);

        wrapper.add(toolbar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);

        // Status column renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String status = v != null ? v.toString() : "";
                if (!sel) {
                    switch (status) {
                        case "ACTIVE"  -> { l.setBackground(AppConstants.GREEN_DARK);     l.setForeground(AppConstants.GREEN); }
                        case "REVOKED" -> { l.setBackground(new Color(60, 10, 15));       l.setForeground(AppConstants.DANGER); }
                        case "EXPIRED" -> { l.setBackground(new Color(60, 40, 0));        l.setForeground(AppConstants.WARNING); }
                        default        -> { l.setBackground(AppConstants.BG_CARD);        l.setForeground(AppConstants.TEXT_MUTED); }
                    }
                }
                l.setFont(new Font("Monospaced", Font.BOLD, 11));
                l.setBorder(new EmptyBorder(0, 8, 0, 8));
                return l;
            }
        });

        // Alt row renderer
        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                c.setBackground(sel ? new Color(0, 230, 255, 30)
                        : (row % 2 == 0 ? AppConstants.BG_CARD : AppConstants.BG_ELEVATED));
                c.setForeground(sel ? AppConstants.CYAN : AppConstants.TEXT_PRIMARY);
                ((JLabel) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
        for (int i = 0; i < COLUMNS.length; i++)
            if (i != 6) table.getColumnModel().getColumn(i).setCellRenderer(altR);

        wrapper.add(UIFactory.darkScroll(table), BorderLayout.CENTER);
        return wrapper;
    }

    private void loadCertificates(List<Certificate> certs) {
        tableModel.setRowCount(0);
        for (Certificate c : certs)
            tableModel.addRow(new Object[]{
                    c.getCertId(), c.getRecipientName(), c.getRecipientId(),
                    c.getCourse(), DateUtil.format(c.getIssueDate()),
                    DateUtil.format(c.getExpiryDate()), c.getStatus(), c.getIssuedBy()
            });
    }

    private void search() {
        String kw = searchField.getText().trim();
        loadCertificates(kw.isEmpty() ? service.findAll() : service.search(kw));
    }

    private Certificate getSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a certificate."); return null; }
        String id = (String) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
        return service.findById(id).orElse(null);
    }

    private void revokeSelected() {
        Certificate c = getSelected(); if (c == null) return;
        if (JOptionPane.showConfirmDialog(this,
                "Revoke certificate for " + c.getRecipientName() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    service.revoke(c.getCertId()) ? "✓  Revoked." : "✗  Failed.");
            loadCertificates(service.findAll());
        }
    }

    private void renewSelected() {
        Certificate c = getSelected(); if (c == null) return;
        if (JOptionPane.showConfirmDialog(this,
                "Renew certificate for " + c.getRecipientName() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    service.renew(c.getCertId()) ? "✓  Renewed." : "✗  Failed.");
            loadCertificates(service.findAll());
        }
    }

    private void exportPdf() {
        Certificate c = getSelected(); if (c == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(c.getCertId() + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            new PdfExporter().export(c, chooser.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, "✓  PDF exported.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}