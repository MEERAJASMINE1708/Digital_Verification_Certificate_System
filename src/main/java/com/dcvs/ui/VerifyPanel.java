package com.dcvs.ui;

import com.dcvs.model.Certificate;
import com.dcvs.service.VerificationService;
import com.dcvs.service.VerificationService.VerificationResult;
import com.dcvs.util.AppConstants;
import com.dcvs.util.DateUtil;
import com.dcvs.util.QRCodeUtil;
import com.dcvs.util.QRCodeUtil.ParsedQR;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dark blockchain-style certificate verification panel.
 * Shows STATUS / ISSUED / HASH MATCH cards exactly like the reference image.
 * Module 4 — Anjali
 */
public class VerifyPanel extends JPanel {

    private final JFrame              parentFrame;
    private final VerificationService service = new VerificationService();

    private JTextField certIdField;
    private JLabel     qrStudentLabel;
    private JLabel     qrCourseLabel;

    // Result card labels (updated after verification)
    private JLabel statusValueLabel;
    private JLabel issuedValueLabel;
    private JLabel hashValueLabel;

    // Full detail section
    private JPanel detailSection;

    public VerifyPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildMainCard(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        JLabel title = new JLabel("// CERTIFICATE VERIFICATION");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(AppConstants.CYAN);
        h.add(title, BorderLayout.WEST);
        JLabel sub = UIFactory.mutedLabel("cryptographic validation system");
        h.add(sub, BorderLayout.SOUTH);
        return h;
    }

    private JPanel buildMainCard() {
        JPanel card = UIFactory.glowCard();
        card.setLayout(new BorderLayout(0, 16));

        // Input section
        card.add(buildInputSection(), BorderLayout.NORTH);

        // Result section
        detailSection = new JPanel(new BorderLayout(0, 12));
        detailSection.setOpaque(false);
        detailSection.add(buildResultCards(), BorderLayout.NORTH);
        card.add(detailSection, BorderLayout.CENTER);

        return card;
    }

    // ── Input section ─────────────────────────────────────────────────────────

    private JPanel buildInputSection() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets = new Insets(6, 0, 6, 0);
        g.gridx = 0;

        JLabel inputLabel = new JLabel("0x CERT_HASH or Certificate ID");
        inputLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        inputLabel.setForeground(AppConstants.TEXT_MUTED);
        g.gridy = 0; p.add(inputLabel, g);

        certIdField = UIFactory.styledField(40);
        certIdField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        certIdField.setPreferredSize(new java.awt.Dimension(0, 42));
        certIdField.addActionListener(e -> verify());
        g.gridy = 1; p.add(certIdField, g);

        // QR decoded info
        JPanel qrRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        qrRow.setOpaque(false);

        JLabel qs = new JLabel("STUDENT:");
        qs.setFont(new Font("Monospaced", Font.PLAIN, 10));
        qs.setForeground(AppConstants.TEXT_DIM);
        qrStudentLabel = new JLabel("—");
        qrStudentLabel.setFont(new Font("Monospaced", Font.BOLD, 10));
        qrStudentLabel.setForeground(AppConstants.CYAN);

        JLabel qc = new JLabel("COURSE:");
        qc.setFont(new Font("Monospaced", Font.PLAIN, 10));
        qc.setForeground(AppConstants.TEXT_DIM);
        qrCourseLabel = new JLabel("—");
        qrCourseLabel.setFont(new Font("Monospaced", Font.BOLD, 10));
        qrCourseLabel.setForeground(AppConstants.CYAN);

        qrRow.add(qs); qrRow.add(qrStudentLabel);
        qrRow.add(qc); qrRow.add(qrCourseLabel);
        g.gridy = 2; p.add(qrRow, g);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);

        JButton verifyBtn = new JButton("[ RUN VERIFICATION ]");
        verifyBtn.setBackground(new Color(0, 80, 40));
        verifyBtn.setForeground(AppConstants.GREEN);
        verifyBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        verifyBtn.setFocusPainted(false);
        verifyBtn.setOpaque(true);
        verifyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        verifyBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.GREEN, 1),
                new EmptyBorder(10, 20, 10, 20)));
        verifyBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                verifyBtn.setBackground(new Color(0, 255, 136, 40));
                verifyBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                verifyBtn.setBackground(new Color(0, 80, 40));
                verifyBtn.setForeground(AppConstants.GREEN);
            }
        });
        verifyBtn.addActionListener(e -> verify());

        JButton qrBtn = UIFactory.outlineButton("📷 Scan QR");
        qrBtn.addActionListener(e -> scanQr());

        JButton clearBtn = UIFactory.outlineButton("Clear");
        clearBtn.addActionListener(e -> {
            certIdField.setText("");
            qrStudentLabel.setText("—");
            qrCourseLabel.setText("—");
            resetResultCards();
        });

        btnRow.add(verifyBtn); btnRow.add(qrBtn); btnRow.add(clearBtn);
        g.gridy = 3; p.add(btnRow, g);

        return p;
    }

    // ── Result cards (STATUS / ISSUED / HASH MATCH) ───────────────────────────

    private JPanel buildResultCards() {
        JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
        row.setOpaque(false);

        // STATUS card
        JPanel statusCard = resultCard("STATUS");
        statusValueLabel = (JLabel) ((JPanel) statusCard.getComponent(0)).getComponent(1);
        row.add(statusCard);

        // ISSUED card
        JPanel issuedCard = resultCard("ISSUED");
        issuedValueLabel = (JLabel) ((JPanel) issuedCard.getComponent(0)).getComponent(1);
        row.add(issuedCard);

        // HASH MATCH card
        JPanel hashCard = resultCard("HASH MATCH");
        hashValueLabel = (JLabel) ((JPanel) hashCard.getComponent(0)).getComponent(1);
        row.add(hashCard);

        return row;
    }

    private JPanel resultCard(String label) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppConstants.BG_ELEVATED);
        outer.setBorder(BorderFactory.createLineBorder(AppConstants.BORDER_SOLID, 1));

        JPanel inner = new JPanel(new GridLayout(2, 1));
        inner.setBackground(AppConstants.BG_ELEVATED);
        inner.setBorder(new EmptyBorder(14, 12, 14, 12));

        JLabel labelL = new JLabel(label, SwingConstants.LEFT);
        labelL.setFont(new Font("Monospaced", Font.PLAIN, 10));
        labelL.setForeground(AppConstants.TEXT_MUTED);

        JLabel valueL = new JLabel("—", SwingConstants.LEFT);
        valueL.setFont(new Font("SansSerif", Font.BOLD, 16));
        valueL.setForeground(AppConstants.TEXT_MUTED);

        inner.add(labelL);
        inner.add(valueL);
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    private void resetResultCards() {
        statusValueLabel.setText("—"); statusValueLabel.setForeground(AppConstants.TEXT_MUTED);
        issuedValueLabel.setText("—"); issuedValueLabel.setForeground(AppConstants.TEXT_MUTED);
        hashValueLabel.setText("—");   hashValueLabel.setForeground(AppConstants.TEXT_MUTED);
        // Remove detail table
        Component[] comps = detailSection.getComponents();
        if (comps.length > 1) detailSection.remove(comps[1]);
        detailSection.revalidate();
        detailSection.repaint();
    }

    // ── Verification logic ────────────────────────────────────────────────────

    private void verify() {
        String id = certIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Certificate ID.");
            return;
        }
        statusValueLabel.setText("...");
        statusValueLabel.setForeground(AppConstants.CYAN);

        VerificationResult result = service.verify(id);
        Optional<Certificate> certOpt = service.getCertificate(id);

        updateResultCards(result, certOpt.orElse(null));
    }

    private void updateResultCards(VerificationResult result, Certificate cert) {
        boolean isValid = result == VerificationResult.VALID;

        // STATUS card
        if (isValid) {
            statusValueLabel.setText("✓  VALID");
            statusValueLabel.setForeground(AppConstants.GREEN);
        } else {
            statusValueLabel.setText("✗  " + result.name().replace('_', ' '));
            statusValueLabel.setForeground(AppConstants.DANGER);
        }

        // ISSUED card
        if (cert != null) {
            issuedValueLabel.setText(cert.getIssueDate().toString());
            issuedValueLabel.setForeground(AppConstants.TEXT_PRIMARY);
        } else {
            issuedValueLabel.setText("N/A");
            issuedValueLabel.setForeground(AppConstants.TEXT_MUTED);
        }

        // HASH MATCH card
        hashValueLabel.setText(isValid ? "100%" : "FAILED");
        hashValueLabel.setForeground(isValid ? AppConstants.GREEN : AppConstants.DANGER);

        // Detail table
        Component[] comps = detailSection.getComponents();
        if (comps.length > 1) detailSection.remove(comps[1]);

        if (cert != null) {
            detailSection.add(buildDetailTable(cert, isValid), BorderLayout.CENTER);
        }

        detailSection.revalidate();
        detailSection.repaint();
    }

    private JPanel buildDetailTable(Certificate cert, boolean isValid) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppConstants.BG_ELEVATED);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        isValid ? AppConstants.GREEN_DARK : new Color(60, 10, 15), 1),
                new EmptyBorder(14, 16, 14, 16)));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 6);

        // Header
        JLabel headerLabel = new JLabel(isValid
                ? "✓  CERTIFICATE VERIFIED — AUTHENTIC"
                : "✗  CERTIFICATE INVALID");
        headerLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        headerLabel.setForeground(isValid ? AppConstants.GREEN : AppConstants.DANGER);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 4;
        card.add(headerLabel, g);
        g.gridwidth = 1;

        // Separator
        g.gridy = 1; g.gridwidth = 4;
        card.add(UIFactory.darkSeparator(), g);
        g.gridwidth = 1;

        // Fields
        String[][] rows = {
            {"CERT ID",    cert.getCertId()},
            {"STUDENT",    cert.getRecipientName()},
            {"STU ID",     cert.getRecipientId()},
            {"COURSE",     cert.getCourse()},
            {"ORG",        cert.getOrgName()},
            {"ISSUED",     DateUtil.format(cert.getIssueDate())},
            {"EXPIRES",    DateUtil.format(cert.getExpiryDate())},
            {"ISSUED BY",  cert.getIssuedBy()},
            {"STATUS",     cert.getStatus()},
        };

        for (int i = 0; i < rows.length; i++) {
            int col = (i % 2) * 2;
            int row = (i / 2) + 2;

            JLabel kl = new JLabel(rows[i][0]);
            kl.setFont(new Font("Monospaced", Font.PLAIN, 10));
            kl.setForeground(AppConstants.TEXT_MUTED);
            g.gridx = col; g.gridy = row; g.weightx = 0.2;
            card.add(kl, g);

            // Highlight student and course
            boolean highlight = rows[i][0].equals("STUDENT") || rows[i][0].equals("COURSE");
            JLabel vl = new JLabel(rows[i][1]);
            vl.setFont(new Font("Monospaced", highlight ? Font.BOLD : Font.PLAIN, 11));
            vl.setForeground(highlight ? AppConstants.CYAN : AppConstants.TEXT_PRIMARY);
            g.gridx = col + 1; g.weightx = 0.3;
            card.add(vl, g);
        }

        return card;
    }

    // ── QR scan ───────────────────────────────────────────────────────────────

    private void scanQr() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select QR Code Image");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "png", "jpg", "jpeg", "bmp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            BufferedImage img = ImageIO.read(chooser.getSelectedFile());
            LuminanceSource src = new BufferedImageLuminanceSource(img);
            BinaryBitmap bmp   = new BinaryBitmap(new HybridBinarizer(src));
            Result qr = new MultiFormatReader().decode(bmp);
            ParsedQR parsed = QRCodeUtil.parse(qr.getText());
            if (parsed != null) {
                certIdField.setText(parsed.certId);
                qrStudentLabel.setText(parsed.hasStudentInfo() ? parsed.studentName : "(not in QR)");
                qrCourseLabel.setText(parsed.course != null ? parsed.course : "(not in QR)");
                verify();
            }
        } catch (NotFoundException e) {
            JOptionPane.showMessageDialog(this, "No QR code found in image.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}