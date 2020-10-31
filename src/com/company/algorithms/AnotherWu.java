package com.company.algorithms;

import com.company.ScreenPoint;
import com.company.interfaces.LineDrawer;
import com.company.interfaces.PixelDrawer;

import java.awt.*;

import static java.lang.Math.*;
import static java.lang.Math.floor;

public class AnotherWu implements LineDrawer {

    PixelDrawer pixelDrawer;

    public AnotherWu(PixelDrawer pixelDrawer) {
        this.pixelDrawer = pixelDrawer;
    }

    @Override
    public void drawLine(ScreenPoint p1, ScreenPoint p2, Color color) {
        drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), color);
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(abs(dx), abs(dy)); //наклон рассчитывается по большей проекции

        float r = color.getRed() / 255f, g = color.getGreen() / 255f, b = color.getBlue() / 255f;

        float xInc = dx / (float) steps; // на сколько за один шаг должен увеличиться параметр каждой
        float yInc = dy / (float) steps;

        float x = x1;
        float y = y1;
        double gradient;
        for (int i = 0; i <= steps; i++) {
            if (Math.abs(dx) > Math.abs(dy)) {
                gradient = (y - floor(y));
            } else {
                gradient = (x - floor(x));
            }

            Color firstPixel = new Color(r, g, b, (float) gradient);
            Color secondPixel = new Color(r, g, b, (float) (1F - gradient));

            pixelDrawer.drawPixel((int) ceil(x), (int) ceil(y), firstPixel); //округление дроби в большую сторону
            pixelDrawer.drawPixel((int) floor(x), (int) floor(y), secondPixel); // округление дроби в меньшую сторону

            x += xInc;
            y += yInc;
        }
    }
}
