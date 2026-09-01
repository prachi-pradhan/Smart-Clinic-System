package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UITheme {

    public static final Color BG = new Color(245, 248, 252);
    public static final Color CARD = Color.WHITE;

    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font LABEL = new Font("SansSerif", Font.BOLD, 14);
    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 14);

    public static void styleTitle(JLabel label) {
        label.setFont(TITLE);
        label.setForeground(new Color(30, 35, 45));
    }

    public static void styleLabel(JLabel label) {
        label.setFont(LABEL);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(NORMAL);
        field.setPreferredSize(new Dimension(170, 30));
    }

    public static void styleButton(JButton button) {
        button.setFont(LABEL);
        button.setPreferredSize(new Dimension(170, 32));
    }

    public static void styleDangerButton(JButton button) {
        button.setFont(LABEL);
        button.setPreferredSize(new Dimension(190, 32));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(NORMAL);
        table.getTableHeader().setFont(LABEL);
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }
}