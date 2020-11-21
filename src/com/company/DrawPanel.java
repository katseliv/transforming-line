package com.company;

import com.company.algorithms.BresenhamDrawer;
import com.company.algorithms.BufferedImagePixelDrawer;
import com.company.algorithms.DottedLineDrawer;
import com.company.figures.Circle;
import com.company.figures.Curve;
import com.company.figures.Line;
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

    private Color color = Color.BLACK;
    private Curve animateCurve = new Curve();
    private final List<Curve> bezierCurves = new ArrayList<>();
    private final List<Curve> brokenLines = new ArrayList<>();
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
        LineDrawer dottedLineDrawer = new DottedLineDrawer(g_bufferImage);
        LineDrawer bresenhamLineDrawer = new BresenhamDrawer(pixelDrawer);
        OvalDrawer ovalDrawer = new BresenhamDrawer(pixelDrawer);

        g.setColor(Color.black);

        RealPoint firstPoint = null;
        for (RealPoint point : realPoints) {
            Circle circle = new Circle(point);
            drawCircle(ovalDrawer, circle);
            if (startPoint != null) {
                drawLine(dottedLineDrawer, startPoint, currentPoint);
            }
            if (currentCircle != null) {
                drawCircle(ovalDrawer, currentCircle);
            }
            if (firstPoint == null) {
                firstPoint = point;
                continue;
            }
            drawLine(dottedLineDrawer, firstPoint, point);
            firstPoint = point;
        }

        for (Curve brokenLine : brokenLines) {
            firstPoint = null;
            for (int i = 0; i < brokenLine.getRealPoints().size(); i++) {
                Circle circle = new Circle(brokenLine.getRealPoints().get(i));
                drawCircle(ovalDrawer, circle);
                if (firstPoint == null) {
                    firstPoint = brokenLine.getRealPoints().get(i);
                    continue;
                }
                drawLine(dottedLineDrawer, firstPoint, brokenLine.getRealPoints().get(i));
                firstPoint = brokenLine.getRealPoints().get(i);
            }
        }

        for (Curve curve : bezierCurves) {
            if (bezierCurve != null) {
                createBezierCurve(bezierCurve.getRealPoints(), bresenhamLineDrawer);
            }
            createBezierCurve(curve.getRealPoints(), bresenhamLineDrawer);
        }


        if (animateCurve != null) {
            drawCurve(animateCurve, dottedLineDrawer, ovalDrawer);
            createBezierCurve(animateCurve.getRealPoints(), bresenhamLineDrawer);
        }

        g2d.drawImage(bufferedImage, 0, 0, null);
        g_bufferImage.dispose();
    }

    private void drawLine(LineDrawer lineDrawer, RealPoint p1, RealPoint p2) {
        lineDrawer.drawLine(screenConverter.R2S(p1), screenConverter.R2S(p2), color);
    }

    private void drawCurve(Curve curve, LineDrawer lineDrawer, OvalDrawer ovalDrawer) {
        RealPoint firstPoint = null;
        for (int i = 0; i < curve.getRealPoints().size(); i++) {
            Circle circle = new Circle(curve.getRealPoints().get(i));
            drawCircle(ovalDrawer, circle);
            if (firstPoint == null) {
                firstPoint = curve.getRealPoints().get(i);
                continue;
            }
            drawLine(lineDrawer, firstPoint, curve.getRealPoints().get(i));
            firstPoint = curve.getRealPoints().get(i);
        }
    }

    private void drawCircle(OvalDrawer ovalDrawer, Circle circle) {
        ovalDrawer.drawOval(screenConverter.R2S(circle.getCenter()), (int) circle.getRadius(), (int) circle.getRadius(), color);
    }

    public void createBezierCurve(List<RealPoint> sourcePoints, LineDrawer lineDrawer) {
        // ф-ия расчитывает финальный набор точек,
        // по которым будет строится кривая, а затем рисует ее
        List<RealPoint> finalPoints = new ArrayList<>();

        for (double t = 0; t <= 1; t += 0.01)
            finalPoints.add(calculateBezierFunction(sourcePoints, t));
        drawBezierCurve(finalPoints, lineDrawer);
    }

    private RealPoint calculateBezierFunction(List<RealPoint> realPoints, double t) {
        // ф-ия расчитывает очередную точку на кривой,
        // исходя из входного набора управляющих точек
        double x = 0;
        double y = 0;

        int n = realPoints.size() - 1;
        for (int i = 0; i <= n; i++) {
            double pow = Math.pow(1 - t, n - i);
            x += factorial(n) / (factorial(i) * factorial(n - i)) * realPoints.get(i).getX() * Math.pow(t, i) * pow;
            y += factorial(n) / (factorial(i) * factorial(n - i)) * realPoints.get(i).getY() * Math.pow(t, i) * pow;
        }

        return new RealPoint(x, y);
    }

    private double factorial(double arg) {
        if (arg < 0) throw new RuntimeException("Negative argument");
        if (arg == 0) return 1;

        double result = 1;
        for (int i = 1; i <= arg; i++)
            result *= i;
        return result;
    }

    private void drawBezierCurve(List<RealPoint> points, LineDrawer lineDrawer) {
        for (int i = 1; i < points.size(); i++) {
            double x1 = points.get(i - 1).getX();
            double y1 = points.get(i - 1).getY();
            double x2 = points.get(i).getX();
            double y2 = points.get(i).getY();
            drawLine(lineDrawer, new RealPoint(x1, y1), new RealPoint(x2, y2));
        }
    }

    public List<Curve> getBezierCurves() {
        return bezierCurves;
    }

    public List<Curve> getBrokenLines() {
        return brokenLines;
    }

    public void setAnimateCurve(Curve animateCurve) {
        this.animateCurve = animateCurve;
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
        animateCurve = null;
        bezierCurves.clear();
        realPoints.clear();
        brokenLines.clear();
        repaint();
    }

    private ScreenPoint lastPosition = null;

    private RealPoint startPoint = null;
    private RealPoint currentPoint = null;
    private Circle currentCircle = null;
    private boolean createBrokenLine = true;

    private int indexOfEditPoint = -1;
    private int indexOfLine = -1;
    private int indexOfCurve = -1;
    private boolean editBrokenLine = false;

    private Curve brokenLine = null;
    private Curve bezierCurve = new Curve();
    private final List<RealPoint> realPoints = new ArrayList<>();
    private final List<RealPoint> bezierPoints = new ArrayList<>();

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

        if (editBrokenLine && indexOfLine != -1 && indexOfEditPoint != -1) {
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());
        }

        repaint();
    } //движение с зажатой кнопкой мыши

    @Override
    public void mouseMoved(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

        if (currentPoint != null && startPoint != null && createBrokenLine) {
            if (currentCircle == null) {
                currentCircle = new Circle(screenConverter.S2R(currentPosition));
            } else {
                currentCircle.setCenter(screenConverter.S2R(currentPosition));
            }

            currentPoint.setX(screenConverter.S2R(currentPosition).getX());
            currentPoint.setY(screenConverter.S2R(currentPosition).getY());
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());

        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && brokenLines.size() < 2 && !editBrokenLine) {
            bezierPoints.add(new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY()));

            brokenLine = new Curve();
            startPoint = new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY());
            currentPoint = new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY());

            realPoints.add(startPoint);
            createBrokenLine = true;
        }

        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3 && brokenLines.size() < 2 && createBrokenLine && !editBrokenLine) {
            realPoints.add(currentPoint);
            brokenLine.getRealPoints().addAll(realPoints);
            brokenLines.add(brokenLine);

            bezierPoints.add(new RealPoint(screenConverter.S2R(currentPosition).getX(), screenConverter.S2R(currentPosition).getY()));
            bezierCurve.getRealPoints().addAll(bezierPoints);
            bezierCurves.add(bezierCurve);

            realPoints.clear();
            bezierPoints.clear();
            startPoint = null;
            brokenLine = null;
            bezierCurve = new Curve();
            //bezierCurve = null;
            currentCircle = null;
            createBrokenLine = false;
        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        RealPoint realPosition = screenConverter.S2R(currentPosition);

        if (e.getButton() == MouseEvent.BUTTON3 && createBrokenLine) {
            lastPosition = new ScreenPoint(e.getX(), e.getY());
        } else if (e.getButton() == MouseEvent.BUTTON3 && editBrokenLine) {

            indexOfLine = whatNearestPoint(brokenLines, currentPosition);

            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            brokenLines.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());

            bezierCurves.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setX(realPosition.getX());
            bezierCurves.get(indexOfLine).getRealPoints().get(indexOfEditPoint).setY(realPosition.getY());
        }

        repaint();
    } //нажимаем

    public int whatNearestPoint(List<Curve> curves, ScreenPoint screenPoint) {
        double min = Integer.MAX_VALUE;
        int index = -1;

        for (Curve curve : curves) {
            for (int i = 0; i < curve.getRealPoints().size(); i++) {
                double distance = countDistance(curve.getRealPoints().get(i), screenConverter.S2R(screenPoint));
                if (distance < min) {
                    min = distance;
                    index = curves.indexOf(curve);
                    indexOfEditPoint = curve.getRealPoints().indexOf(curve.getRealPoints().get(i));
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

    @Override
    public void mouseReleased(MouseEvent e) {
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        RealPoint realPosition = screenConverter.S2R(currentPosition);

        if (e.getButton() == MouseEvent.BUTTON3 && createBrokenLine) {
            lastPosition = null;
        } else if (e.getButton() == MouseEvent.BUTTON3 && editBrokenLine) {
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
