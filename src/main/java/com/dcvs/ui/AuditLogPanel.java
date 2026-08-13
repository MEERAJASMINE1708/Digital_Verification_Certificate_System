package com.dcvs.ui;

import com.dcvs.dao.AuditLogDAO;
import com.dcvs.model.AuditLog;
import com.dcvs.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dark-themed audit log panel.
 * Module 5 — Sravanika
 */
public class AuditLogPanel extends JPanel {

    private final AuditLogDAO    dao = new AuditLogDAO();
    private JTable               table;
    private DefaultTableModel    tableModel;
    private JTextField           fromField;
    private JTextField           toField;

    private static final String[] COLUMNS =
            {"Log ID", "Action", "Actor", "Target", "Details", "Timestamp"};

    public AuditLogPanel() {
        buildUI();
        loadAll();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("// AUDIT LOG");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(AppConstants.CYAN);
        header.add(title, BorderLayout.WEST);
        header.add(UIFactory.mutedLabel("track all system actions and events"), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JLabel fromLbl = new JLabel("FROM:");
        fromLbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        fromLbl.setForeground(AppConstants.TEXT_MUTED);
        fromField = UIFactory.styledField(11);
        fromField.setText(LocalDate.now().minusDays(30).toString());

        JLabel toLbl = new JLabel("TO:");
        toLbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        toLbl.setForeground(AppConstants.TEXT_MUTED);
        toField = UIFactory.styledField(11);
        toField.setText(LocalDate.now().toString());

        JButton filterBtn  = UIFactory.primaryButton("Filter");
        filterBtn.addActionListener(e -> filterByDate());
        JButton allBtn     = UIFactory.outlineButton("Show All");
        allBtn.addActionListener(e -> loadAll());
        JButton exportBtn  = UIFactory.outlineButton("📥 Export CSV");
        exportBtn.addActionListener(e -> exportCsv());

        toolbar.add(fromLbl); toolbar.add(fromField);
        toolbar.add(toLbl);   toolbar.add(toField);
        toolbar.add(filterBtn); toolbar.add(allBtn);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(exportBtn);
        add(toolbar, BorderLayout.CENTER);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);

        // Action column color
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String action = v != null ? v.toString() : "";
                if (!sel) {
                    l.setBackground(row % 2 == 0 ? AppConstants.BG_CARD : AppConstants.BG_ELEVATED);
                    if (action.contains("ISSUE"))  l.setForeground(AppConstants.GREEN);
                    else if (action.contains("REVOKE") || action.contains("DELETE"))
                                                   l.setForeground(AppConstants.DANGER);
                    else if (action.contains("VERIFY")) l.setForeground(AppConstants.CYAN);
                    else                           l.setForeground(AppConstants.TEXT_MUTED);
                }
                l.setFont(new Font("Monospaced", Font.BOLD, 11));
                l.setBorder(new EmptyBorder(0, 8, 0, 8));
                return l;
            }
        });

        // Default row renderer
        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                c.setBackground(sel ? new Color(0, 230, 255, 30)
                        : (row % 2 == 0 ? AppConstants.BG_CARD : AppConstants.BG_ELEVATED));
                c.setForeground(sel ? AppConstants.CYAN : AppConstants.TEXT_PRIMARY);
                ((JLabel) c).setFont(new Font("Monospaced", Font.PLAIN, 11));
                ((JLabel) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
        for (int i = 0; i < COLUMNS.length; i++)
            if (i != 1) table.getColumnModel().getColumn(i).setCellRenderer(altR);

        JScrollPane scroll = UIFactory.darkScroll(table);
        add(scroll, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(0, 600));
    }

    private void loadLogs(List<AuditLog> logs) {
        tableModel.setRowCount(0);
        for (AuditLog al : logs)
            tableModel.addRow(new Object[]{
                    al.getLogId(), al.getAction(), al.getActor(),
                    al.getTargetId(), al.getDetails(), al.getTimestamp()
            });
    }

    private void loadAll() { loadLogs(dao.findAll()); }

    private void filterByDate() {
        try {
            LocalDateTime from = LocalDate.parse(fromField.getText().trim()).atStartOfDay();
            LocalDateTime to   = LocalDate.parse(toField.getText().trim()).atTime(LocalTime.MAX);
            loadLogs(dao.findByDateRange(from, to));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.");
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("audit_log.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            pw.println("Log ID,Action,Actor,Target,Details,Timestamp");
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    if (c > 0) sb.append(",");
                    Object v = tableModel.getValueAt(r, c);
                    sb.append(v == null ? "" : v.toString().replace(",", ";"));
                }
                pw.println(sb);
            }
            JOptionPane.showMessageDialog(this, "✓  CSV exported: " + chooser.getSelectedFile().getName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}