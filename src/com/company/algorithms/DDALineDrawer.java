package com.company.algorithms;

import com.company.ScreenPoint;
import com.company.interfaces.LineDrawer;
import com.company.interfaces.PixelDrawer;
import java.awt.*;

public class DDALineDrawer implements LineDrawer {
    private final PixelDrawer pixelDrawer;

    public DDALineDrawer(PixelDrawer pixelDrawer) {
        this.pixelDrawer = pixelDrawer;
    }

    @Override
    public void drawLine(ScreenPoint p1, ScreenPoint p2, Color color) {
        int x1 = p1.getX();
        int x2 = p2.getX();
        int y1 = p1.getY();
        int y2 = p2.getY();

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
                pixelDrawer.drawPixel(j, (int) i, color);
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
                pixelDrawer.drawPixel((int) j, (int) i, color);
            }
        }

    }

}
