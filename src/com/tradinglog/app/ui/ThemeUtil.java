package com.tradinglog.app.ui;

import javax.swing.*;
import java.awt.*;

public class ThemeUtil {

    private static final Color BG = new Color(34, 34, 34);
    private static final Color FG = new Color(240, 240, 240);
    private static final Color PANEL_BG = new Color(45, 45, 45);
    private static final Color FIELD_BG = new Color(60, 60, 60);

    public static void applyDarkTheme(Container container) {
        if (container == null) return;

        container.setBackground(BG);

        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(PANEL_BG);
                applyDarkTheme((Container) comp);
            } else if (comp instanceof JLabel) {
                comp.setForeground(FG);
            } else if (comp instanceof JButton) {
                comp.setBackground(FIELD_BG);
                comp.setForeground(FG);
                ((JButton) comp).setFocusPainted(false);
            } else if (comp instanceof JTextField) {
                comp.setBackground(FIELD_BG);
                comp.setForeground(FG);
                ((JTextField) comp).setCaretColor(FG);
            } else if (comp instanceof JPasswordField) {
                comp.setBackground(FIELD_BG);
                comp.setForeground(FG);
                ((JPasswordField) comp).setCaretColor(FG);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(FIELD_BG);
                comp.setForeground(FG);
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                table.setBackground(FIELD_BG);
                table.setForeground(FG);
                table.setGridColor(Color.GRAY);
                table.setSelectionBackground(new Color(90, 90, 90));
                table.setSelectionForeground(FG);
                table.getTableHeader().setBackground(PANEL_BG);
                table.getTableHeader().setForeground(FG);
            } else if (comp instanceof Container) {
                applyDarkTheme((Container) comp);
            }
        }
    }
}