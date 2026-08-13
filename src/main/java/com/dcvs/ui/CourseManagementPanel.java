package com.dcvs.ui;

import com.dcvs.model.Course;
import com.dcvs.service.CourseService;
import com.dcvs.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dark-themed Course Management panel.
 */
public class CourseManagementPanel extends JPanel {

    private final CourseService service = new CourseService();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        nameField;
    private JComboBox<String> categoryCombo;
    private JTextArea         descArea;
    private JTextField        durationField;
    private JCheckBox         activeCheck;

    private static final String[] COLUMNS   = {"ID", "Course Name", "Category", "Duration", "Active"};
    private static final String[] CATEGORIES = {
            "AI & Data", "Web Dev", "Programming", "Infrastructure", "Security", "Design", "Business", "Other"
    };

    public CourseManagementPanel() {
        buildUI();
        loadCourses();
    }

    private void buildUI() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("// COURSE MANAGEMENT");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(AppConstants.CYAN);
        header.add(title, BorderLayout.WEST);
        header.add(UIFactory.mutedLabel("manage skill-based courses offered by the organization"),
                BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablePanel(), buildFormPanel());
        split.setDividerLocation(600);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(AppConstants.BG_DARK);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        JButton refreshBtn = UIFactory.outlineButton("↺ Refresh");
        refreshBtn.addActionListener(e -> loadCourses());
        toolbar.add(refreshBtn);
        p.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 4 ? Boolean.class : String.class;
            }
        };
        table = new JTable(tableModel);
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
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

        JScrollPane scroll = UIFactory.darkScroll(table);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel card = UIFactory.glowCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets = new Insets(5, 0, 5, 0);

        JLabel formTitle = new JLabel("COURSE DETAILS");
        formTitle.setFont(new Font("Monospaced", Font.BOLD, 13));
        formTitle.setForeground(AppConstants.CYAN);
        card.add(formTitle, g);
        card.add(UIFactory.darkSeparator(), g);

        card.add(fieldLabel("COURSE NAME *"), g);
        nameField = UIFactory.styledField(20);
        card.add(nameField, g);

        card.add(fieldLabel("CATEGORY *"), g);
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setBackground(AppConstants.BG_INPUT);
        categoryCombo.setForeground(AppConstants.TEXT_PRIMARY);
        categoryCombo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        card.add(categoryCombo, g);

        card.add(fieldLabel("DURATION"), g);
        durationField = UIFactory.styledField(20);
        card.add(durationField, g);

        card.add(fieldLabel("DESCRIPTION"), g);
        descArea = new JTextArea(3, 20);
        descArea.setBackground(AppConstants.BG_INPUT);
        descArea.setForeground(AppConstants.TEXT_PRIMARY);
        descArea.setCaretColor(AppConstants.CYAN);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1),
                new EmptyBorder(6, 8, 6, 8)));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1));
        descScroll.getViewport().setBackground(AppConstants.BG_INPUT);
        card.add(descScroll, g);

        activeCheck = new JCheckBox("ACTIVE (visible in issue form)");
        activeCheck.setFont(new Font("Monospaced", Font.PLAIN, 11));
        activeCheck.setForeground(AppConstants.TEXT_MUTED);
        activeCheck.setBackground(AppConstants.BG_CARD);
        activeCheck.setSelected(true);
        card.add(activeCheck, g);
        card.add(Box.createVerticalStrut(8), g);

        JPanel btnRow = new JPanel(new GridLayout(2, 2, 6, 6));
        btnRow.setOpaque(false);
        JButton addBtn    = UIFactory.successButton("Add Course");
        addBtn.addActionListener(e -> addCourse());
        JButton updateBtn = UIFactory.primaryButton("Update");
        updateBtn.addActionListener(e -> updateCourse());
        JButton deleteBtn = UIFactory.dangerButton("Delete");
        deleteBtn.addActionListener(e -> deleteCourse());
        JButton clearBtn  = UIFactory.outlineButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        btnRow.add(addBtn); btnRow.add(updateBtn);
        btnRow.add(deleteBtn); btnRow.add(clearBtn);
        card.add(btnRow, g);

        return card;
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        for (Course c : service.findAll())
            tableModel.addRow(new Object[]{
                    c.getCourseId(), c.getCourseName(), c.getCategory(), c.getDuration(), c.isActive()
            });
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        service.findById(id).ifPresent(c -> {
            nameField.setText(c.getCourseName());
            categoryCombo.setSelectedItem(c.getCategory());
            durationField.setText(c.getDuration());
            descArea.setText(c.getDescription());
            activeCheck.setSelected(c.isActive());
        });
    }

    private void addCourse() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Course name required."); return; }
        boolean ok = service.addCourse(name, (String) categoryCombo.getSelectedItem(),
                descArea.getText().trim(), durationField.getText().trim());
        JOptionPane.showMessageDialog(this, ok ? "✓  Course added." : "✗  Failed.");
        if (ok) { clearForm(); loadCourses(); }
    }

    private void updateCourse() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
        service.findById(id).ifPresent(c -> {
            c.setCourseName(nameField.getText().trim());
            c.setCategory((String) categoryCombo.getSelectedItem());
            c.setDuration(durationField.getText().trim());
            c.setDescription(descArea.getText().trim());
            c.setActive(activeCheck.isSelected());
            boolean ok = service.updateCourse(c);
            JOptionPane.showMessageDialog(this, ok ? "✓  Updated." : "✗  Failed.");
            if (ok) { clearForm(); loadCourses(); }
        });
    }

    private void deleteCourse() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
        String name = (String) tableModel.getValueAt(table.convertRowIndexToModel(row), 1);
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + name + "\"?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            boolean ok = service.deleteCourse(id);
            JOptionPane.showMessageDialog(this, ok ? "✓  Deleted." : "✗  Failed.");
            if (ok) { clearForm(); loadCourses(); }
        }
    }

    private void clearForm() {
        nameField.setText(""); durationField.setText(""); descArea.setText("");
        categoryCombo.setSelectedIndex(0); activeCheck.setSelected(true);
        table.clearSelection();
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, 10));
        l.setForeground(AppConstants.TEXT_MUTED);
        return l;
    }
}