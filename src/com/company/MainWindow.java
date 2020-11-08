package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

public class MainWindow extends JFrame {
    private DrawPanel drawPanel;
    private JPanel buttonsPanel;
    private final TextField textFieldTime = new TextField();
    private final TextField textFieldSpeed = new TextField();
    private final JButton action = new JButton("Complete");
    private final JButton delete = new JButton("Clear");
    private static final Font FONT = new Font(Font.SERIF, Font.ITALIC, 16);

    public MainWindow() throws HeadlessException {
        super("Animation line");

        panel();

        this.add(drawPanel);
        this.add(buttonsPanel, BorderLayout.NORTH);
        this.addKeyListener(drawPanel);
        this.setFocusable(true);
        //textField.setFocusable(false);
    }

    private void panel() {
        drawPanel = new DrawPanel();
        buttonsPanel = new JPanel();

        Label time = new Label("Time ");
        time.setFont(FONT);
        textFieldTime.setPreferredSize(new Dimension(100, 25));
        Label speed = new Label("Speed ");
        speed.setFont(FONT);
        textFieldSpeed.setPreferredSize(new Dimension(100, 25));
        buttonsPanel.add(time);
        buttonsPanel.add(textFieldTime);
        buttonsPanel.add(speed);
        buttonsPanel.add(textFieldSpeed);

        //double time = Double.parseDouble(textFieldTime.getText()) / 100;
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.timer.schedule(new TimerTask() {
                    final List<BrokenLine> brokenLines = drawPanel.brokenLines;

                    final BrokenLine brokenLine1 = brokenLines.get(0);
                    final BrokenLine brokenLine2 = brokenLines.get(1);

                    final List<RealPoint> realPoints1 = brokenLine1.getRealPoints();
                    final List<RealPoint> realPoints2 = brokenLine2.getRealPoints();

                    final List<Circle> circles1 = brokenLine1.getCircles();
                    //final List<Circle> circles2 = brokenLine2.getCircles();

                    double coefficient = 0.0001;

                    @Override
                    public void run() {
                        if (realPoints1.size() == realPoints2.size() && textFieldTime.getText() != null) {
                            for (int i = 0; i < realPoints1.size(); i++) {
                                double dx = realPoints2.get(i).getX() - realPoints1.get(i).getX();
                                double dy = realPoints2.get(i).getY() - realPoints1.get(i).getY();

                                realPoints1.get(i).setX(realPoints1.get(i).getX() + dx * coefficient);
                                realPoints1.get(i).setY(realPoints1.get(i).getY() + dy * coefficient);
                                circles1.get(i).setCenter(new RealPoint(realPoints1.get(i).getX() + dx * coefficient, realPoints1.get(i).getY() + dy * coefficient));
                            }
                        }
                        coefficient += 0.0001;
                        if (coefficient == 1.0) {
                            for (int i = 0; i < realPoints1.size(); i++) {
                                realPoints1.get(i).setX(realPoints2.get(i).getX());
                                realPoints1.get(i).setY(realPoints2.get(i).getY());
                                circles1.get(i).setCenter(new RealPoint(realPoints2.get(i).getX(), realPoints2.get(i).getY()));
                            }
                            drawPanel.timer.cancel();
                            drawPanel.timer.purge();
                        }
                        repaint();
                    }
                }, 0, 100);

            }

        });

        action.setFont(FONT);
        buttonsPanel.add(action);

        Label labelConditions = new Label("Choose action:");
        labelConditions.setFont(FONT);
        buttonsPanel.add(labelConditions);

        String[] conditions = {
                "Create",
                "Edit"
        };

        JComboBox<String> comboBoxConditions = new JComboBox<>(conditions);
        //comboBox.setEditable(true);
        comboBoxConditions.setFont(FONT);
        comboBoxConditions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> cb = (JComboBox<String>) e.getSource();
                String conditionName = (String) cb.getSelectedItem();
                switch (Objects.requireNonNull(conditionName)) {
                    case "Create":
                        drawPanel.setCreateBrokenLine(true);
                        break;
                    case "Edit":
                        //drawPanel.timer.cancel();
                        drawPanel.setEditBrokenLine(true);
                        break;
                }
            }
        });
        buttonsPanel.add(comboBoxConditions);

        Label labelColors = new Label("Choose color:");
        labelColors.setFont(FONT);
        buttonsPanel.add(labelColors);

        String[] colors = {
                "Black",
                "Green",
                "Violet",
                "Turquoise"
        };
        JComboBox<String> comboBoxColors = new JComboBox<>(colors);
        //comboBox.setEditable(true);
        comboBoxColors.setFont(FONT);
        comboBoxColors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> cb = (JComboBox<String>) e.getSource();
                String colorName = (String) cb.getSelectedItem();
                drawPanel.setColor(colorName);
            }
        });
        buttonsPanel.add(comboBoxColors);

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawPanel.clearField();
            }
        });
        delete.setFont(FONT);
        buttonsPanel.add(delete);

        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
    }

}
