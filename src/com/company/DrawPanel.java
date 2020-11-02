package com.company;

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

public class DrawPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

    private final ScreenConverter screenConverter = new ScreenConverter(-2, 2, 4, 4, 800, 600);
    private final List<Line> allLines = new ArrayList<>();
    private final List<Circle> allCircle = new ArrayList<>();
    private final List<BrokenLine> brokenLines = new ArrayList<>();
    private final Line xAxis = new Line(new RealPoint(-1, 0), new RealPoint(1, 0));
    private final Line yAxis = new Line(new RealPoint(0, -1), new RealPoint(0, 1));

    public DrawPanel() { //alt + enter - реализуем самостоятельно listener
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

        for (BrokenLine brokenLine : brokenLines) {
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
        lineDrawer.drawLine(screenConverter.R2S(p1), screenConverter.R2S(p2), Color.BLACK);
    }

    private void drawCircle(OvalDrawer ovalDrawer, Circle circle) {
        ovalDrawer.drawOval(screenConverter.R2S(circle.getCenter()), (int) circle.getSize(), (int) circle.getSize(), circle.getColor());
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
    private RealPoint editPoint = null;
    private boolean editBrokenLine = false;

    private BrokenLine brokenLine = new BrokenLine();
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

        if (currentLine != null) {
            currentLine.setP2(screenConverter.S2R(currentPosition));
            currentCircle.setCenter(screenConverter.S2R(currentPosition));
            allCircle.add(currentCircle);
        }

        if (editBrokenLine) {
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());
        }

        repaint();
    } //движение с зажатой кнопкой мыши

    @Override
    public void mouseMoved(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

        if (currentLine != null) { //&& createBrokenLine
            currentLine.setP2(screenConverter.S2R(currentPosition));
        }

        if (currentPoint != null && createBrokenLine) {
            currentCircle.setCenter(screenConverter.S2R(currentPosition));
            currentPoint.setX(screenConverter.S2R(currentPosition).getX());
            currentPoint.setY(screenConverter.S2R(currentPosition).getY());
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

//        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
//            allLines.add(currentLine);
//            allCircle.add(currentCircle);
//            createBrokenLine = true;
//        }

        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && brokenLines.size() < 2) {
            startPoint = new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY());
            currentPoint = new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY());
            realPoints.add(startPoint);
            allCircle.add(currentCircle);
            createBrokenLine = true;
        }

//        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
//            allLines.add(currentLine);
//            allCircle.add(currentCircle);
//            brokenLines.add(brokenLine);
//            createBrokenLine = false;
//        }

        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && brokenLines.size() < 2) {
            realPoints.add(currentPoint);
            brokenLine.getRealPoints().addAll(realPoints);
            brokenLines.add(brokenLine);
            allCircle.add(currentCircle);

            brokenLine = new BrokenLine();
            startPoint = null;
            realPoints.clear();

            createBrokenLine = false;
        }

        if (e.getClickCount() == 3 && e.getButton() == MouseEvent.BUTTON1) {
            editBrokenLine = true;
        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        RealPoint realPosition = screenConverter.S2R(currentPosition);
        if (e.getButton() == MouseEvent.BUTTON3) {
            lastPosition = new ScreenPoint(e.getX(), e.getY());
        } else if (e.getButton() == MouseEvent.BUTTON1 && !editBrokenLine) {
            currentLine = new Line(
                    screenConverter.S2R(currentPosition),
                    screenConverter.S2R(currentPosition));
            currentCircle = new Circle(screenConverter.S2R(currentPosition), 5);
            allCircle.add(currentCircle);
        }

        if (e.getButton() == MouseEvent.BUTTON1 && editBrokenLine) {
            whatNearestPoint(new ScreenPoint(e.getX(), e.getY()));
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());
        }
        repaint();
    } //нажимаем

    @Override
    public void mouseReleased(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        RealPoint realPosition = screenConverter.S2R(currentPosition);

        if (e.getButton() == MouseEvent.BUTTON3) {
            lastPosition = null;
        } else if (e.getButton() == MouseEvent.BUTTON1 && !editBrokenLine) {
            currentCircle = new Circle(screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())), 5);
            allCircle.add(currentCircle);
            allLines.add(currentLine);
            currentLine = new Line(
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())),
                    screenConverter.S2R(new ScreenPoint(e.getX(), e.getY())));

        }

        if (e.getButton() == MouseEvent.BUTTON1 && editBrokenLine) {
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
        editBrokenLine = e.getKeyCode() == KeyEvent.VK_W;
        System.out.println("I am here");
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public int whatNearestPoint(ScreenPoint screenPoint) {
        int index = -1;
        double min = Integer.MAX_VALUE;

        for (BrokenLine brokenLine : brokenLines) {
            for (RealPoint realPoint : brokenLine.getRealPoints()) {
                double distance = countDistance(realPoint, screenConverter.S2R(screenPoint));
                if (distance < min) {
                    min = distance;
                    indexOfEditPoint = brokenLine.getRealPoints().indexOf(realPoint);
                    indexOfLine = brokenLines.indexOf(brokenLine);
                }
            }
        }

        return index;
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
}
