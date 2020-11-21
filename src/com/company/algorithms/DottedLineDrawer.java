package com.company.algorithms;

import java.awt.*;

import com.company.ScreenPoint;
import com.company.interfaces.LineDrawer;

public class DottedLineDrawer implements LineDrawer {

    private final Graphics g;

    public DottedLineDrawer(Graphics g) {
        this.g = g;
    }

    @Override
    public void drawLine(ScreenPoint p1, ScreenPoint p2, Color color) {
        Graphics2D g2d = (Graphics2D) g.create();

        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();

        //set the stroke of the copy, not the original
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(x1, y1, x2, y2);

        //gets rid of the copy
        g2d.dispose();
    }
}
