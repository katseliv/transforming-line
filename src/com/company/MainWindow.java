package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.TimerTask;

public class MainWindow extends JFrame {
    private DrawPanel drawPanel;
    private JPanel buttonsPanel;
    private TextField textField;
    private JButton action;
    private JButton create;
    private JButton edit;
    private JButton delete;

    public MainWindow() throws HeadlessException {
        super("Анимация линии");

        drawPanel = new DrawPanel();
        this.add(drawPanel);
        buttonsPanel = new JPanel();
        this.add(buttonsPanel, BorderLayout.SOUTH);
        textField = new TextField();
        textField.setPreferredSize(new Dimension(100, 25));
        this.addKeyListener(drawPanel);
        this.setFocusable(true);
        //textField.setFocusable(false);
        buttonsPanel.add(textField);
        action = new JButton("Complete");
        //drawPanel.countSteps(Integer.parseInt(textField.getText()));
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.timer.schedule(new TimerTask() {
                    final List<BrokenLine> brokenLines = drawPanel.brokenLines;

                    final BrokenLine brokenLine1 = brokenLines.get(0);
                    final BrokenLine brokenLine2 = brokenLines.get(1);

                    final List<RealPoint> realPoints1 = brokenLine1.getRealPoints();
                    final List<RealPoint> realPoints2 = brokenLine2.getRealPoints();

                    double coefficient = 0.1;

                    @Override
                    public void run() {
                        if (realPoints1.size() == realPoints2.size()) {
                            for (int i = 0; i < realPoints1.size(); i++) {
                                double dx = realPoints2.get(i).getX() - realPoints1.get(i).getX();
                                double dy = realPoints2.get(i).getY() - realPoints1.get(i).getY();

                                realPoints1.get(i).setX(realPoints1.get(i).getX() + dx * coefficient);
                                realPoints1.get(i).setY(realPoints1.get(i).getY() + dy * coefficient);
                            }
                        }
                        coefficient += 0.1;
                        if (coefficient == 1.0){
                            drawPanel.timer.cancel();
                        }
                        repaint();
                    }
                }, 0, 100);

            }

        });

        drawPanel.timer.purge();

        buttonsPanel.add(action, BorderLayout.CENTER);
        create = new JButton("Create");
        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCreateBrokenLine(true);
            }
        });
        buttonsPanel.add(create, BorderLayout.CENTER);
        edit = new JButton("Edit");
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setEditBrokenLine(true);
            }
        });
        buttonsPanel.add(edit, BorderLayout.CENTER);
        delete = new JButton("Clear");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.clearField();
            }
        });
        buttonsPanel.add(delete, BorderLayout.CENTER);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

    }

}
