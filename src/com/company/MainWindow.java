package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private DrawPanel drawPanel;
    private JPanel buttonsPanel;
    private TextField textField;
    private JButton action;
    private JButton delete;

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
        delete = new JButton("Delete");
        buttonsPanel.add(delete, BorderLayout.CENTER);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.clearField();
                System.out.println("OK");
            }
        });
    }

}
