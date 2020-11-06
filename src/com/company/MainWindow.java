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
    private JButton edit;
    private JButton delete;
    private int i = 0;
    private int j = 0;

    public MainWindow() throws HeadlessException {
        super("Анимация линии");

        drawPanel = new DrawPanel();
        this.add(drawPanel);
        buttonsPanel = new JPanel();
        this.add(buttonsPanel, BorderLayout.SOUTH);
        textField = new TextField();
        textField.setPreferredSize(new Dimension(100, 25));
        //textField.setFocusable(false);
        buttonsPanel.add(textField);
        action = new JButton("Complete");
        //drawPanel.countSteps(Integer.parseInt(textField.getText()));
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //drawPanel.function(20);
                        //drawPanel.functionForCircle(20);

                        List<CircleInt> allCircle = drawPanel.allCircle;

                        CircleInt circle1 = allCircle.get(0);
                        CircleInt circle2 = allCircle.get(1);

                        ScreenPoint screenPoint = circle1.getCenter();
                        ScreenPoint screenPoint1 = circle2.getCenter();

                        int x1 = screenPoint.getX();
                        int y1 = screenPoint.getY();
                        int x2 = screenPoint1.getX();
                        int y2 = screenPoint1.getY();

                        double dx = x2 - x1;
                        double dy = y2 - y1;

                        if (Math.abs(dx) > Math.abs(dy)) { //горизонтальная
                            double k = dy / dx;
                            if (x1 > x2) {
                                int temp = x1;
                                x1 = x2;
                                x2 = temp;
                                temp = y1;
                                y1 = y2;
                                y2 = temp;
                            }
                            j++;
                            double i = k * (j - x1) + y1;
                            circle1.setCenter(new ScreenPoint(j, (int) i));
                            if (circle1.getCenter().getX() == circle2.getCenter().getX() && circle1.getCenter().getY() == circle2.getCenter().getY()) {
                                drawPanel.timer.cancel();
                            }
                        } else { //вертикальная
                            double k = dx / dy;
                            if (y1 > y2) {
                                int temp = x1;
                                x1 = x2;
                                x2 = temp;
                                temp = y1;
                                y1 = y2;
                                y2 = temp;
                            }

                            i++;
                            double j = k * (i - y1) + x1;
                            circle1.setCenter(new ScreenPoint((int) j, i));
                            if (circle1.getCenter().getX() == circle2.getCenter().getX() && circle1.getCenter().getY() == circle2.getCenter().getY()) {
                                drawPanel.timer.cancel();
                            }
                        }

                        repaint();
                    }
                }, 0, 10);

            }
        });


        buttonsPanel.add(action, BorderLayout.CENTER);
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

    private void function() {
        List<CircleInt> allCircle = drawPanel.allCircle;

        CircleInt circle1 = allCircle.get(0);
        CircleInt circle2 = allCircle.get(1);

        ScreenPoint screenPoint = circle1.getCenter();
        ScreenPoint screenPoint1 = circle2.getCenter();

        move(screenPoint.getX(), screenPoint.getY(), screenPoint1.getX(), screenPoint1.getY());
    }

    private void move(int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (Math.abs(dx) > Math.abs(dy)) { //горизонтальная
            double k = dy / dx;
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }

            for (int j = x1; j <= x2; j++) {
                double i = k * (j - x1) + y1; //y = k(x - x1) + y1
                //pixelDrawer.drawPixel(j, (int) i, color);
            }
        } else { //вертикальная
            double k = dx / dy;
            if (y1 > y2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            for (int i = y1; i <= y2; i++) {
                double j = k * (i - y1) + x1; //x = (1 / k) * (y - y1) + x1
                //pixelDrawer.drawPixel((int) j, (int) i, color);
            }
        }
    }

}
