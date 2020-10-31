package com.company.algorithms;

import com.company.ScreenPoint;
import com.company.interfaces.LineDrawer;
import com.company.interfaces.OvalDrawer;
import com.company.interfaces.PixelDrawer;

import java.awt.*;

public class BresenhamDrawer implements LineDrawer, OvalDrawer {
    PixelDrawer pixelDrawer;

    public BresenhamDrawer(PixelDrawer pixelDrawer) {
        this.pixelDrawer = pixelDrawer;
    }

    @Override
    public void drawLine(ScreenPoint p1, ScreenPoint p2, Color color) {
        int x = p1.getX();
        int y = p1.getY();
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        dx = sign(dx);
        dy = sign(dy);

        if (absDx >= absDy) {
            int error = 2 * absDy - absDx;

            for (int i = 0; i < absDx; i++) {
                if (error >= 0) {
                    y += dy;
                    error += 2 * (absDy - absDx);
                } else {
                    error += 2 * absDy;
                }
                x += dx;
                pixelDrawer.drawPixel(x, y, color);
            }
        } else {
            int error = 2 * absDx - absDy;

            for (int i = 0; i < absDy; i++) {
                if (error >= 0) {
                    x += dx;
                    error += 2 * (absDx - absDy);
                } else {
                    error += 2 * absDx;
                }
                y += dy;
                pixelDrawer.drawPixel(x, y, color);
            }
        }
    }

    private int sign(int i) {
        return (i > 0) ? 1 : -1;
    }

    @Override
    public void drawOval(ScreenPoint center, int width, int height, Color color) {
        drawEllipse(center.getX(), center.getY(), width, height, color);
    }

    void drawEllipse(int x, int y, int a, int b, Color color) {
        int componentX = 0; // Компонента x
        int componentY = b; // Компонента y
        int sqrA = a * a; // a^2, a - большая полуось
        int sqrB = b * b; // b^2, b - малая полуось
        int delta = 4 * sqrB * ((componentX + 1) * (componentX + 1)) + sqrA * ((2 * componentY - 1) * (2 * componentY - 1)) - 4 * sqrA * sqrB; // Функция координат точки (x+1, y-1/2)

        while (sqrA * (2 * componentY - 1) > 2 * sqrB * (componentX + 1)) { // Первая часть дуги
            pixel(x, y, componentX, componentY, color);
            componentX++;
            if (delta < 0) { // Переход по горизонтали
                delta += 4 * sqrB * (2 * componentX + 3);
            } else { // Переход по диагонали
                delta = delta - 8 * sqrA * (componentY - 1) + 4 * sqrB * (2 * componentX + 3);
                componentY--;
            }
        }

        delta = sqrB * ((2 * componentX + 1) * (2 * componentX + 1)) + 4 * sqrA * ((componentY + 1) * (componentY + 1)) - 4 * sqrA * sqrB; // Функция координат точки (x+1/2, y-1)
        while (componentY + 1 != 0) { // Вторая часть дуги, если не выполняется условие первого цикла, значит выполняется a^2(2y - 1) <= 2b^2(x + 1)
            pixel(x, y, componentX, componentY, color);
            componentY--;
            if (delta < 0) { // Переход по вертикали
                delta += 4 * sqrA * (2 * componentY + 3);
            } else { // Переход по диагонали
                delta = delta - 8 * sqrB * (componentX + 1) + 4 * sqrA * (2 * componentY + 3);
                componentX++;
            }
        }
    }

    void pixel(int x1, int y1, int x2, int y2, Color color) { // Рисование пикселя для первого квадранта, и, симметрично, для остальных
        pixelDrawer.drawPixel(x1 + x2, y1 + y2, color);
        pixelDrawer.drawPixel(x1 + x2, y1 - y2, color);
        pixelDrawer.drawPixel(x1 - x2, y1 - y2, color);
        pixelDrawer.drawPixel(x1 - x2, y1 + y2, color);
    }

}
