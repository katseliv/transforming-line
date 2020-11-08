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

                    double coefficient = 0.0001;

                    @Override
                    public void run() {
                        if (realPoints1.size() == realPoints2.size() && textFieldTime.getText() != null) {
                            for (int i = 0; i < realPoints1.size(); i++) {
                                double dx = realPoints2.get(i).getX() - realPoints1.get(i).getX();
                                double dy = realPoints2.get(i).getY() - realPoints1.get(i).getY();

                                realPoints1.get(i).setX(realPoints1.get(i).getX() + dx * coefficient);
                                realPoints1.get(i).setY(realPoints1.get(i).getY() + dy * coefficient);
                            }
                        }
                        coefficient += 0.0001;
                        if (coefficient == 1.0) {
                            drawPanel.timer.cancel();
                        }
                        repaint();
                    }
                }, 0, 100);

            }

        });
        drawPanel.timer.purge();
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
                String conditionName = (String)cb.getSelectedItem();
                switch(Objects.requireNonNull(conditionName)){
                    case "Create":
                        drawPanel.setCreateBrokenLine(true);
                        drawPanel.setEditBrokenLine(false);
                        break;
                    case"Edit":
                        drawPanel.setCreateBrokenLine(false);
                        drawPanel.setEditBrokenLine(true);
                        break;
                }
            }
        });
        buttonsPanel.add(comboBoxConditions);

//        JRadioButton create = new JRadioButton("Create", true);
//        create.setFont(FONT);
//        create.addActionListener(e -> drawPanel.setCreateBrokenLine(true));
//        buttonsPanel.add(create);
//
//        JRadioButton edit = new JRadioButton("Edit", false);
//        edit.setFont(FONT);
//        edit.addActionListener(e -> drawPanel.setEditBrokenLine(true));
//        buttonsPanel.add(edit);
//
//        ButtonGroup buttonGroup = new ButtonGroup();
//        buttonGroup.add(create);
//        buttonGroup.add(edit);

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

//        JRadioButton black = new JRadioButton("Black", true);
//        black.setFont(FONT);
//        black.addActionListener(e -> drawPanel.setColor(0));
//        buttonsPanel.add(black);
//
//        JRadioButton green = new JRadioButton("Green", false);
//        green.setFont(FONT);
//        green.addActionListener(e -> drawPanel.setColor(1));
//        buttonsPanel.add(green);
//
//        JRadioButton violet = new JRadioButton("Violet", false);
//        violet.setFont(FONT);
//        violet.addActionListener(e -> drawPanel.setColor(2));
//        buttonsPanel.add(violet);
//
//        JRadioButton turquoise = new JRadioButton("Turquoise", false);
//        turquoise.setFont(FONT);
//        turquoise.addActionListener(e -> drawPanel.setColor(3));
//        buttonsPanel.add(turquoise);
//
//        ButtonGroup buttonColor = new ButtonGroup();
//        buttonColor.add(green);
//        buttonColor.add(violet);
//        buttonColor.add(turquoise);

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
