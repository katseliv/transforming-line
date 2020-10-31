package com.company;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private DrawPanel drawPanel;
    private JPanel buttonsPanel;
    private TextField textField;
    private JButton action;

    public MainWindow() throws HeadlessException {
        super("Анимация линии");

        drawPanel = new DrawPanel();
        this.add(drawPanel);
        buttonsPanel = new JPanel();
        this.add(buttonsPanel, BorderLayout.SOUTH);
        textField = new TextField();
        textField.setPreferredSize(new Dimension(100, 25));
        textField.setFocusable(false);
        buttonsPanel.add(textField);
        action = new JButton("Complete");
        buttonsPanel.add(action, BorderLayout.CENTER);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
    }

}
