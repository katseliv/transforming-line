package com.company.algorithms;

import com.company.interfaces.PixelDrawer;

import java.awt.*;

public class GraphicsPixelDrawer implements PixelDrawer {
    private final Graphics g;

    public GraphicsPixelDrawer(Graphics g) {
        this.g = g;
    }

    @Override
    public void drawPixel(int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x, y, 1, 1);
    }
}
