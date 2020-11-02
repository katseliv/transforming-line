package com.company.mess;

import com.company.*;
import com.company.algorithms.BresenhamDrawer;
import com.company.algorithms.BufferedImagePixelDrawer;
import com.company.interfaces.LineDrawer;
import com.company.interfaces.OvalDrawer;
import com.company.interfaces.PixelDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Redactor extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, ActionListener {

    private final ScreenConverter screenConverter = new ScreenConverter(-2, 2, 4, 4, 800, 600);
    private final List<Line> allLines = new ArrayList<>();
    private final HashMap<Line, List<Circle>> listWithCircle = new HashMap<>();
    private final List<Circle> allCircle = new ArrayList<>();
    private final List<Circle> redactingCircle = new ArrayList<>();
    private final Line xAxis = new Line(new RealPoint(-1, 0), new RealPoint(1, 0));
    private final Line yAxis = new Line(new RealPoint(0, -1), new RealPoint(0, 1));
    private int radius = 5;

    public Redactor() { //alt + enter - реализуем самостоятельно listener
        this.addMouseMotionListener(this); //на движение мышки
        this.addMouseListener(this); //
        this.addMouseWheelListener(this);
        this.addKeyListener(this);
    }

    @Override
    public void paint(Graphics g) {
        screenConverter.setScreenWidth(getWidth());
        screenConverter.setScreenHeight(getHeight());
        Graphics2D g2d = (Graphics2D) g;
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g_bufferImage = bufferedImage.createGraphics();
        g_bufferImage.setColor(Color.WHITE);
        g_bufferImage.fillRect(0, 0, getWidth(), getHeight());
        g_bufferImage.setColor(Color.BLACK);

        PixelDrawer pixelDrawer = new BufferedImagePixelDrawer(bufferedImage);
        LineDrawer lineDrawer = new BresenhamDrawer(pixelDrawer);
        OvalDrawer ovalDrawer = new BresenhamDrawer(pixelDrawer);

        drawLine(lineDrawer, xAxis);
        drawLine(lineDrawer, yAxis);

        for (Line line : allLines) {
            drawLine(lineDrawer, line);
            if (currentLine != null) {
                drawLine(lineDrawer, currentLine);
            }
        }

        for (Map.Entry<Line, List<Circle>> entry : listWithCircle.entrySet()) {
            drawLine(lineDrawer, entry.getKey());
            if (currentLine != null) {
                drawLine(lineDrawer, currentLine);
            }
            for (Circle circle : entry.getValue()) {
                drawCircle(ovalDrawer, circle);
                if (currentCircle != null) {
                    drawCircle(ovalDrawer, currentCircle);
                }
            }
        }

        g2d.drawImage(bufferedImage, 0, 0, null);
        g_bufferImage.dispose();
    }

    private void drawLine(LineDrawer lineDrawer, Line line) {
        lineDrawer.drawLine(screenConverter.R2S(line.getP1()), screenConverter.R2S(line.getP2()), Color.BLACK);
    }

    private void drawCircle(OvalDrawer ovalDrawer, Circle circle) {
        ovalDrawer.drawOval(screenConverter.R2S(circle.getCenter()), (int) circle.getSize(), (int) circle.getSize(), circle.getColor());
    }

    private ScreenPoint lastPosition = null;
    private Line currentLine = null;
    private Circle currentCircle = null;
    private boolean redactor = false;
    private boolean createBrokenLine = true;
    private boolean move = false;
    private List<Circle> circles = new ArrayList<>();

    @Override
    public void mouseDragged(MouseEvent e) { //движение с зажатой кнопкой мыши
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

        if (lastPosition != null) {
            ScreenPoint deltaScreen = new ScreenPoint(
                    currentPosition.getX() - lastPosition.getX(),
                    currentPosition.getY() - lastPosition.getY());
            RealPoint deltaReal = screenConverter.S2R(deltaScreen);
            RealPoint zeroReal = screenConverter.S2R(new ScreenPoint(0, 0));
            RealPoint vector = new RealPoint(deltaReal.getX() - zeroReal.getX(), deltaReal.getY() - zeroReal.getY());
            screenConverter.setCornerX(screenConverter.getCornerX() - vector.getX());
            screenConverter.setCornerY(screenConverter.getCornerY() - vector.getY());
            lastPosition = currentPosition;
        }

        if (currentLine != null) {

            if (redactor && countDistanceBetweenPoints(currentPosition.getX(), currentPosition.getY(), currentLine.getP1().getX(), currentLine.getP1().getY()) < 5) {
                currentLine.setP1(screenConverter.S2R(currentPosition));
                currentLine.setP2(currentLine.getP2());
            } else if (redactor && countDistanceBetweenPoints(currentPosition.getX(), currentPosition.getY(), currentLine.getP2().getX(), currentLine.getP2().getY()) < 5) {
                currentLine.setP2(screenConverter.S2R(currentPosition));
                currentLine.setP1(currentLine.getP1());
            } else {
                currentLine.setP2(screenConverter.S2R(currentPosition));
                System.out.print("drag");
            }

            if (currentCircle != null) {
                currentCircle.setCenter(screenConverter.S2R(currentPosition));

            }

        }

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        if (currentLine != null) {
            currentLine.setP2(screenConverter.S2R(currentPosition));
            System.out.print("move");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

//        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
//            currentCircle = new Circle(screenConverter.S2R(currentPosition), 5);
//            allCircle.add(currentCircle);
//            currentCircle = null;
//        }

        double distance;
        double min = Integer.MAX_VALUE;
        for (Line line : allLines) {
            distance = countDistanceToLine(currentPosition, line);
            if (distance < min) {
                min = distance;
                currentLine = line;
            }
        }

        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && min < 5) {
            redactor = true;
            List<Circle> circles = new ArrayList<>();

            currentCircle = new Circle(currentLine.getP1(), radius);
            currentCircle.setColor(Color.RED);
            circles.add(currentCircle);

            currentCircle = new Circle(currentLine.getP2(), radius);
            currentCircle.setColor(Color.RED);
            circles.add(currentCircle);
            listWithCircle.put(currentLine, circles);
        }

        repaint();
    }

    private double countDistanceToLine(ScreenPoint p, Line line) {
        int x = p.getX();
        int y = p.getY();
        ScreenPoint p1 = screenConverter.R2S(line.getP1());
        ScreenPoint p2 = screenConverter.R2S(line.getP2());

        double numerator = (p1.getY() - p2.getY()) * x
                + (p2.getX() - p1.getX()) * y
                + (p1.getX() * p2.getY() - p2.getX() * p1.getY());

        double denominator = Math.pow(
                Math.pow(p2.getX() - p1.getX(), 2)
                        + Math.pow(p2.getY() - p1.getY(), 2), 0.5);


        return Math.abs(numerator / denominator);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            lastPosition = new ScreenPoint(e.getX(), e.getY());
        } else if (!createBrokenLine && !redactor && e.getButton() == MouseEvent.BUTTON1) {
//            currentCircle = new Circle(screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())), 5);
//            allCircle.add(currentCircle);
            currentLine = new Line(
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())),
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));
        } else if (redactor && e.getButton() == MouseEvent.BUTTON1 && countDistanceForCircle(e.getX(), e.getY()) < 5) {
            RealPoint p1 = currentLine.getP1();
            RealPoint p2 = currentLine.getP2();
            Circle circleEnd;

            RealPoint position = screenConverter.S2R(new ScreenPoint(e.getX(), e.getY()));

//            if (countDistanceBetweenPoints(position.getX(), position.getY(), p1.getX(), p1.getY())
//                    < countDistanceBetweenPoints(position.getX(), position.getY(), p2.getX(), p2.getY())) {
//                listWithCircle.remove(currentLine);
//                currentLine = new Line(screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())), p2);
//                circleEnd = new Circle(p2, radius);
//                circles.add(circleEnd);
//            } else {
//                listWithCircle.remove(currentLine);
//                currentLine = new Line(p1, screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));
//                circleEnd = new Circle(p1, radius);
//                circles.add(circleEnd);
//            }
            listWithCircle.remove(currentLine);
            currentLine = new Line(p1, screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));
            circleEnd = new Circle(p1, radius);
            circles.add(circleEnd);
            currentCircle = new Circle(screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())), radius);
            //currentCircle.setCenter(screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));
        }

        if (createBrokenLine && e.getButton() == MouseEvent.BUTTON1) {

        }
        repaint();
    } //нажимаем

    private double countDistanceForCircle(double x, double y) {
        double distance;
        double min = Integer.MAX_VALUE;
        Circle redactingCircle = null;

//        for (Circle circle : allCircle) {
//            distance = count(x, y, circle.getCenter().getX(), circle.getCenter().getY());
//            if (distance < min) {
//                min = distance;
//                redactingCircle = circle;
//            }
//        }

        for (Circle circle : listWithCircle.get(currentLine)) {
            distance = countDistanceBetweenPoints(x, y, circle.getCenter().getX(), circle.getCenter().getY());
            if (distance < min) {
                min = distance;
                redactingCircle = circle;
            }
        }
        listWithCircle.get(currentLine).remove(redactingCircle);

        currentCircle = redactingCircle;
        return min;
    }

    private double countDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        double xSquare = Math.pow(x2 - x1, 2);
        double ySquare = Math.pow(y2 - y1, 2);
        return Math.pow(xSquare + ySquare, 0.5);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            lastPosition = null;
        } else if (!redactor && e.getButton() == MouseEvent.BUTTON1) {
//            currentCircle = new Circle(screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())), 5);
//            allCircle.add(currentCircle);
            allLines.add(currentLine);
            currentLine = new Line(
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())),
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));
            //currentLine = null;
        } else if (redactor && e.getButton() == MouseEvent.BUTTON1) {
            circles.add(currentCircle);
            listWithCircle.put(currentLine, circles);
            //redactingCircle.add(currentCircle);
            //allLines.add(currentLine);
            currentCircle = null;
            currentLine = null;
            redactor = false;
        }

        repaint();
    } //отпускаем

    @Override
    public void mouseEntered(MouseEvent e) {

    } //вход

    @Override
    public void mouseExited(MouseEvent e) {

    } //выход из элемента управления

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        double scale = 1;
        double coefficient = clicks < 0 ? 1.1 : 0.9;

        for (int i = 0; i < Math.abs(clicks); i++) {
            scale *= coefficient;
        }

        //radius = (int) (radius * scale);
//        for (Circle circle : allCircle){
//            circle.setSize(radius * scale);
//        }

        screenConverter.setRealWidth(screenConverter.getRealWidth() * scale);
        screenConverter.setRealHeight(screenConverter.getRealHeight() * scale);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
