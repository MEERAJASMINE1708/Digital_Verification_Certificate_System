package com.dcvs;

import com.dcvs.dao.DatabaseManager;
import com.dcvs.ui.LoginPanel;
import com.dcvs.util.AppConstants;
import java.awt.*;
import javax.swing.*;

/**
 * DCVS v2 — Entry Point (Dark Theme)
 */
public class Main {

    public static void main(String[] args) {
        applyDarkDefaults();
        DatabaseManager.getInstance().initializeSchema();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DCVS — Secure Certificate Verification System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(980, 620);                        // wide enough for both panels
            frame.setMinimumSize(new Dimension(900, 560));
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.getContentPane().setBackground(AppConstants.BG_DARK);
            frame.setContentPane(new LoginPanel(frame));
            frame.setVisible(true);
        });
    }

    private static void applyDarkDefaults() {
        UIManager.put("Panel.background",            AppConstants.BG_DARK);
        UIManager.put("OptionPane.background",        AppConstants.BG_CARD);
        UIManager.put("OptionPane.messageForeground", AppConstants.TEXT_PRIMARY);
        UIManager.put("Button.background",            AppConstants.BG_ELEVATED);
        UIManager.put("Button.foreground",            AppConstants.TEXT_PRIMARY);
        UIManager.put("Label.foreground",             AppConstants.TEXT_PRIMARY);
        UIManager.put("TextField.background",         AppConstants.BG_INPUT);
        UIManager.put("TextField.foreground",         AppConstants.TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",    AppConstants.CYAN);
        UIManager.put("PasswordField.background",     AppConstants.BG_INPUT);
        UIManager.put("PasswordField.foreground",     AppConstants.TEXT_PRIMARY);
        UIManager.put("TextArea.background",          AppConstants.BG_INPUT);
        UIManager.put("TextArea.foreground",          AppConstants.TEXT_PRIMARY);
        UIManager.put("ComboBox.background",          AppConstants.BG_INPUT);
        UIManager.put("ComboBox.foreground",          AppConstants.TEXT_PRIMARY);
        UIManager.put("ScrollPane.background",        AppConstants.BG_CARD);
        UIManager.put("Viewport.background",          AppConstants.BG_CARD);
        UIManager.put("SplitPane.background",         AppConstants.BG_DARK);
        UIManager.put("SplitPane.dividerSize",        6);
        UIManager.put("CheckBox.background",          AppConstants.BG_CARD);
        UIManager.put("CheckBox.foreground",          AppConstants.TEXT_MUTED);
        UIManager.put("TitledBorder.titleColor",      AppConstants.CYAN);
        UIManager.put("PopupMenu.background",         AppConstants.BG_ELEVATED);
        UIManager.put("MenuItem.background",          AppConstants.BG_ELEVATED);
        UIManager.put("MenuItem.foreground",          AppConstants.TEXT_PRIMARY);
        UIManager.put("List.background",              AppConstants.BG_INPUT);
        UIManager.put("List.foreground",              AppConstants.TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",     new Color(0, 230, 255, 40));
        UIManager.put("List.selectionForeground",     AppConstants.CYAN);
        UIManager.put("FileChooser.background",       AppConstants.BG_CARD);
        UIManager.put("Table.background",             AppConstants.BG_CARD);
        UIManager.put("Table.foreground",             AppConstants.TEXT_PRIMARY);
        UIManager.put("TableHeader.background",       AppConstants.BG_DARKEST);
        UIManager.put("TableHeader.foreground",       AppConstants.CYAN);
    }
}