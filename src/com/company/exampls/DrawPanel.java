package com.company.exampls;


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
import java.util.*;
import java.util.List;
import java.util.Timer;

public class DrawPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

    public Timer timer = new Timer("Timer");
    private Color color = Color.BLACK;
    private final List<Line> allLines = new ArrayList<>();
    public final List<Circle> allCircle = new ArrayList<>();
    public final List<Curve> brokenLines = new ArrayList<>();
    private final Line xAxis = new Line(new RealPoint(-1, 0), new RealPoint(1, 0));
    private final Line yAxis = new Line(new RealPoint(0, -1), new RealPoint(0, 1));
    private final ScreenConverter screenConverter = new ScreenConverter(-2, 2, 4, 4, 800, 600);

    public DrawPanel() { //alt + enter - реализуем самостоятельно listener
        this.addMouseMotionListener(this); //на движение мышки
        this.addMouseListener(this); //
        this.addMouseWheelListener(this);
        this.setFocusable(true);
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

        //drawLine(lineDrawer, xAxis);
        //drawLine(lineDrawer, yAxis);

//        for (Line line : allLines) {
//            drawLine(lineDrawer, line);
//            if (currentLine != null) {
//                drawLine(lineDrawer, currentLine);
//            }
//        }

        for (Circle circle : allCircle) {
            drawCircle(ovalDrawer, circle);
            if (currentCircle != null) {
                drawCircle(ovalDrawer, currentCircle);
            }
        }

        RealPoint firstPoint = null;
        for (RealPoint realPoint : realPoints) {
            if (startPoint != null) {
                drawLine(lineDrawer, startPoint, currentPoint);
            }
            if (firstPoint == null) {
                firstPoint = realPoint;
                continue;
            }
            drawLine(lineDrawer, firstPoint, realPoint);
            firstPoint = realPoint;
        }

        for (Curve brokenLine : brokenLines) {
            firstPoint = null;
            for (RealPoint realPoint : brokenLine.getRealPoints()) {
                if (firstPoint == null) {
                    firstPoint = realPoint;
                    continue;
                }
                drawLine(lineDrawer, firstPoint, realPoint);
                firstPoint = realPoint;
            }
        }

        g2d.drawImage(bufferedImage, 0, 0, null);
        g_bufferImage.dispose();
    }

    private void drawLine(LineDrawer lineDrawer, Line line) {
        lineDrawer.drawLine(screenConverter.R2S(line.getP1()), screenConverter.R2S(line.getP2()), Color.BLACK);
    }

    private void drawLine(LineDrawer lineDrawer, RealPoint p1, RealPoint p2) {
        lineDrawer.drawLine(screenConverter.R2S(p1), screenConverter.R2S(p2), color);
    }

    private void drawCircle(OvalDrawer ovalDrawer, Circle circle) {
        ovalDrawer.drawOval(screenConverter.R2S(circle.getCenter()), (int) circle.getRadius(), (int) circle.getRadius(), color);
    }

    public void setCreateBrokenLine(boolean createBrokenLine) {
        this.createBrokenLine = createBrokenLine;
        editBrokenLine = false;
    }

    public void setEditBrokenLine(boolean editBrokenLine) {
        this.editBrokenLine = editBrokenLine;
        createBrokenLine = false;
    }

    public void setColor(String c) {
        switch (c) {
            case "Black":
                color = new Color(0, 0, 0);
                break;
            case "Green":
                color = new Color(30, 165, 3);
                break;
            case "Violet":
                color = new Color(90, 0, 186);
                break;
            case "Turquoise":
                color = new Color(0, 137, 161);
                break;

        }
        repaint();
    }

    public void clearField() {
        realPoints.clear();
        brokenLines.clear();
        allLines.clear();
        allCircle.clear();
        repaint();
    }

    private ScreenPoint lastPosition = null;
    private Line currentLine = null;

    private RealPoint startPoint = null;
    private RealPoint currentPoint = null;
    private Circle currentCircle = null;
    private boolean createBrokenLine = true;

    private int indexOfEditPoint = -1;
    private int indexOfLine = -1;
    private int indexOfCircle = -1;
    private boolean editBrokenLine = false;

    private Curve brokenLine = new Curve();
    private final List<RealPoint> realPoints = new ArrayList<>();

    @Override
    public void mouseDragged(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        RealPoint realPosition = screenConverter.S2R(currentPosition);

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

        if (currentLine != null && currentCircle != null) {
            currentLine.setP2(screenConverter.S2R(currentPosition));
            currentCircle.setCenter(screenConverter.S2R(currentPosition));
            allCircle.add(currentCircle);
        }

        if (editBrokenLine && indexOfLine != -1 && indexOfCircle != -1 && indexOfEditPoint != -1) {
            //System.out.println(indexOfLine + " " + indexOfEditPoint);
            allCircle.get(indexOfCircle).setCenter(new RealPoint(realPosition.getX(), realPosition.getY()));

            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());
        }

        repaint();
    } //движение с зажатой кнопкой мыши

    @Override
    public void mouseMoved(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

        if (currentLine != null && !editBrokenLine) {
            currentLine.setP2(screenConverter.S2R(currentPosition));
        }

        if (currentPoint != null && createBrokenLine) {
            currentCircle = new Circle(screenConverter.S2R(currentPosition));
            currentCircle.setCenter(screenConverter.S2R(currentPosition));
            currentPoint.setX(screenConverter.S2R(currentPosition).getX());
            currentPoint.setY(screenConverter.S2R(currentPosition).getY());
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

//        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && allCircle.size() < 2) {
//            currentCircle = new Circle(screenConverter.S2R(currentPosition), 10);
//            allLines.add(currentLine);
//            allCircle.add(currentCircle);
//            createBrokenLine = true;
//        }

        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && brokenLines.size() < 2 && !editBrokenLine) {
            startPoint = new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY());
            currentPoint = new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY());
            currentCircle = new Circle(screenConverter.S2R(currentPosition));
            realPoints.add(startPoint);
            allCircle.add(currentCircle);
            createBrokenLine = true;
            setFocusable(true);
        }

        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && brokenLines.size() < 2) {
            realPoints.add(currentPoint);
            brokenLine.getRealPoints().addAll(realPoints);
            brokenLines.add(brokenLine);
            allCircle.add(currentCircle);
            brokenLine = new Curve();
            startPoint = null;
            realPoints.clear();
            currentCircle = null;
            createBrokenLine = false;
        }

//        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON3) {
//            editBrokenLine = true;
//        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        RealPoint realPosition = screenConverter.S2R(currentPosition);

        if (e.getButton() == MouseEvent.BUTTON3 && !editBrokenLine) {
            lastPosition = new ScreenPoint(e.getX(), e.getY());
        } else if (e.getButton() == MouseEvent.BUTTON1 && !editBrokenLine) {
            currentLine = new Line(
                    screenConverter.S2R(currentPosition),
                    screenConverter.S2R(currentPosition));

            //currentCircle = new Circle(screenConverter.S2R(currentPosition), 5);
            //allCircle.add(currentCircle);

        }

        if (e.getButton() == MouseEvent.BUTTON3 && editBrokenLine) {
            //currentCircle = new Circle(screenConverter.S2R(currentPosition), 10);
            //allCircle.add(currentCircle);

            whatNearestPoint(new ScreenPoint(e.getX(), e.getY()));
            allCircle.get(indexOfCircle).setCenter(new RealPoint(realPosition.getX(), realPosition.getY()));
            //allCircle.get(indexOfCircle).getCenter().setX(realPosition.getX());
            //allCircle.get(indexOfCircle).getCenter().setY(realPosition.getY());

            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());
        }

        repaint();
    } //нажимаем

    public void whatNearestPoint(ScreenPoint screenPoint) {
        double min = Integer.MAX_VALUE;

        for (Curve brokenLine : brokenLines) {
            for (RealPoint realPoint : brokenLine.getRealPoints()) {
                double distance = countDistance(realPoint, screenConverter.S2R(screenPoint));
                if (distance < min) {
                    min = distance;
                    indexOfEditPoint = brokenLine.getRealPoints().indexOf(realPoint);
                    indexOfLine = brokenLines.indexOf(brokenLine);
                    indexOfCircle = allCircle.indexOf(allCircle.get(indexOfLine));
                }
            }
        }
    }

    private double countDistance(RealPoint p1, RealPoint p2) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double xSquare = Math.pow(x2 - x1, 2);
        double ySquare = Math.pow(y2 - y1, 2);
        return Math.pow(xSquare + ySquare, 0.5);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        RealPoint realPosition = screenConverter.S2R(currentPosition);

        if (e.getButton() == MouseEvent.BUTTON3 && !editBrokenLine && !createBrokenLine) {
            lastPosition = null;
        } else if (e.getButton() == MouseEvent.BUTTON1 && !editBrokenLine && !createBrokenLine) {
            currentCircle = new Circle(screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));
            allCircle.add(currentCircle);
            allLines.add(currentLine);
            currentLine = new Line(
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())),
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));

        }

        if (e.getButton() == MouseEvent.BUTTON3 && editBrokenLine) {
            allCircle.get(indexOfCircle).setCenter(new RealPoint(realPosition.getX(), realPosition.getY()));

            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());
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

        screenConverter.setRealWidth(screenConverter.getRealWidth() * scale);
        screenConverter.setRealHeight(screenConverter.getRealHeight() * scale);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            System.out.println("press");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}

