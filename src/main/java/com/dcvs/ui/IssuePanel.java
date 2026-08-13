package com.dcvs.ui;

import com.dcvs.model.Certificate;
import com.dcvs.model.Course;
import com.dcvs.service.CertificateService;
import com.dcvs.service.CourseService;
import com.dcvs.export.PdfExporter;
import com.dcvs.export.QRCodeGenerator;
import com.dcvs.util.AppConstants;
import com.dcvs.util.DateUtil;
import com.dcvs.util.QRCodeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dark-themed certificate issuance form.
 * Module 3 — Afsana
 */
public class IssuePanel extends JPanel {

    private final JFrame             parentFrame;
    private final CertificateService certService   = new CertificateService();
    private final CourseService      courseService = new CourseService();

    private JTextField        nameField;
    private JTextField        recipientIdField;
    private JComboBox<Course> courseCombo;
    private JTextField        issueDateField;
    private JTextField        expiryDateField;
    private JPanel            resultCard;

    public IssuePanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("// ISSUE DIGITAL CERTIFICATE");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(AppConstants.CYAN);
        header.add(title, BorderLayout.WEST);
        header.add(UIFactory.mutedLabel("generate cryptographically signed certificate"), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildFormPanel(), buildResultPanel());
        split.setDividerLocation(440);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(AppConstants.BG_DARK);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel card = UIFactory.glowCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets = new Insets(6, 0, 6, 0);

        JLabel orgBadge = new JLabel("  ● " + AppConstants.ORG_NAME.toUpperCase() + "  ");
        orgBadge.setFont(new Font("Monospaced", Font.BOLD, 10));
        orgBadge.setOpaque(true);
        orgBadge.setBackground(new Color(0, 100, 130, 60));
        orgBadge.setForeground(AppConstants.CYAN);
        orgBadge.setBorder(new EmptyBorder(4, 8, 4, 8));
        card.add(orgBadge, g);
        card.add(UIFactory.darkSeparator(), g);

        card.add(fieldLabel("STUDENT NAME"), g);
        nameField = UIFactory.styledField(24);
        card.add(nameField, g);

        card.add(fieldLabel("STUDENT ID"), g);
        recipientIdField = UIFactory.styledField(24);
        card.add(recipientIdField, g);

        card.add(fieldLabel("COURSE"), g);
        List<Course> courses = courseService.findActive();
        courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        courseCombo.setBackground(AppConstants.BG_INPUT);
        courseCombo.setForeground(AppConstants.TEXT_PRIMARY);
        courseCombo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        courseCombo.setBorder(BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1));
        courseCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? new Color(0, 230, 255, 30) : AppConstants.BG_INPUT);
                setForeground(sel ? AppConstants.CYAN : AppConstants.TEXT_PRIMARY);
                setFont(new Font("Monospaced", Font.PLAIN, 12));
                if (value instanceof Course c)
                    setText(c.getCourseName() + "  [" + c.getCategory() + "]");
                return this;
            }
        });
        card.add(courseCombo, g);

        card.add(fieldLabel("ISSUE DATE  (yyyy-MM-dd)"), g);
        issueDateField = UIFactory.styledField(24);
        issueDateField.setText(LocalDate.now().toString());
        card.add(issueDateField, g);

        card.add(fieldLabel("EXPIRY DATE (yyyy-MM-dd)"), g);
        expiryDateField = UIFactory.styledField(24);
        expiryDateField.setText(DateUtil.defaultExpiry(LocalDate.now()).toString());
        card.add(expiryDateField, g);

        card.add(fieldLabel("ISSUING ORGANIZATION"), g);
        JTextField orgField = UIFactory.styledField(24);
        orgField.setText(AppConstants.ORG_NAME);
        orgField.setEditable(false);
        orgField.setForeground(AppConstants.TEXT_MUTED);
        card.add(orgField, g);

        card.add(Box.createVerticalStrut(8), g);

        JButton issueBtn = UIFactory.successButton("[ GENERATE CERTIFICATE ]");
        issueBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        issueBtn.setPreferredSize(new Dimension(0, 42));
        issueBtn.addActionListener(e -> issueCertificate());
        card.add(issueBtn, g);

        JButton clearBtn = UIFactory.outlineButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());
        card.add(clearBtn, g);

        return card;
    }

    private JPanel buildResultPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 12, 0, 0));

        resultCard = UIFactory.glowCard();
        resultCard.setLayout(new BorderLayout());

        JLabel placeholder = new JLabel("// awaiting input...", SwingConstants.CENTER);
        placeholder.setFont(new Font("Monospaced", Font.PLAIN, 13));
        placeholder.setForeground(AppConstants.TEXT_DIM);
        resultCard.add(placeholder, BorderLayout.CENTER);

        wrapper.add(resultCard, BorderLayout.CENTER);
        return wrapper;
    }

    private void issueCertificate() {
        String name  = nameField.getText().trim();
        String recId = recipientIdField.getText().trim();
        Course course = (Course) courseCombo.getSelectedItem();

        if (name.isEmpty() || recId.isEmpty() || course == null) {
            JOptionPane.showMessageDialog(this, "Student Name, Student ID, and Course are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate issueDate, expiryDate;
        try {
            issueDate  = LocalDate.parse(issueDateField.getText().trim());
            expiryDate = LocalDate.parse(expiryDateField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!expiryDate.isAfter(issueDate)) {
            JOptionPane.showMessageDialog(this, "Expiry date must be after issue date.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Certificate cert = certService.issue(name, recId, course.getCourseId(),
                course.getCourseName(), issueDate, expiryDate);

        if (cert == null) {
            JOptionPane.showMessageDialog(this, "Certificate generation failed. Check logs.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        showCertResult(cert);
        clearForm();
    }

    private void showCertResult(Certificate cert) {
        resultCard.removeAll();
        resultCard.setLayout(new BorderLayout(0, 10));

        // Success header
        JPanel successBanner = new JPanel(new BorderLayout());
        successBanner.setBackground(AppConstants.GREEN_DARK);
        successBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppConstants.GREEN),
                new EmptyBorder(10, 14, 10, 14)));
        JLabel sl = new JLabel("✓  CERTIFICATE GENERATED SUCCESSFULLY");
        sl.setFont(new Font("Monospaced", Font.BOLD, 12));
        sl.setForeground(AppConstants.GREEN);
        successBanner.add(sl);
        resultCard.add(successBanner, BorderLayout.NORTH);

        // Fields
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.setBackground(AppConstants.BG_CARD);
        fields.setBorder(new EmptyBorder(14, 14, 14, 14));

        addRow(fields, "CERT ID",      cert.getCertId());
        addRow(fields, "STUDENT",      cert.getRecipientName());
        addRow(fields, "STU ID",       cert.getRecipientId());
        addRow(fields, "COURSE",       cert.getCourse());
        addRow(fields, "ORG",          cert.getOrgName());
        addRow(fields, "ISSUE DATE",   DateUtil.format(cert.getIssueDate()));
        addRow(fields, "EXPIRY",       DateUtil.format(cert.getExpiryDate()));
        addRow(fields, "STATUS",       cert.getStatus());
        addRow(fields, "QR ENCODES",   "Name+Course+ID+Org+Dates");
        resultCard.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btns.setBackground(AppConstants.BG_CARD);
        JButton pdfBtn = UIFactory.primaryButton("📄 Export PDF");
        pdfBtn.addActionListener(e -> exportPdf(cert));
        JButton qrBtn = UIFactory.outlineButton("📷 Save QR");
        qrBtn.addActionListener(e -> saveQr(cert));
        btns.add(pdfBtn); btns.add(qrBtn);
        resultCard.add(btns, BorderLayout.SOUTH);

        resultCard.revalidate();
        resultCard.repaint();
    }

    private void addRow(JPanel p, String key, String value) {
        JLabel k = new JLabel(key);
        k.setFont(new Font("Monospaced", Font.PLAIN, 10));
        k.setForeground(AppConstants.TEXT_MUTED);
        JLabel v = new JLabel(value != null ? value : "");
        v.setFont(new Font("Monospaced", Font.BOLD, 10));
        v.setForeground(AppConstants.CYAN);
        p.add(k); p.add(v);
    }

    private void exportPdf(Certificate cert) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(cert.getCertId() + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            new PdfExporter().export(cert, chooser.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, "✓  PDF saved: " + chooser.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "PDF failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveQr(Certificate cert) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(cert.getCertId() + "_qr.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        boolean ok = QRCodeGenerator.generateToFile(QRCodeUtil.build(cert),
                chooser.getSelectedFile().getAbsolutePath(), 300);
        JOptionPane.showMessageDialog(this, ok ? "✓  QR saved." : "QR generation failed.");
    }

    private void clearForm() {
        nameField.setText(""); recipientIdField.setText("");
        issueDateField.setText(LocalDate.now().toString());
        expiryDateField.setText(DateUtil.defaultExpiry(LocalDate.now()).toString());
        if (courseCombo.getItemCount() > 0) courseCombo.setSelectedIndex(0);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.BOLD, 10));
        l.setForeground(AppConstants.TEXT_MUTED);
        return l;
    }
}