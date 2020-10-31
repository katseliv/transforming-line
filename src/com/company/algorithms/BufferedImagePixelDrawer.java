package com.company.algorithms;

import com.company.interfaces.PixelDrawer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImagePixelDrawer implements PixelDrawer {

    BufferedImage bufferedImage;

    public BufferedImagePixelDrawer(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void drawPixel(int x, int y, Color color) {
        if (x > 0 && y > 0 && x < bufferedImage.getWidth() && y < bufferedImage.getHeight()) {
            bufferedImage.setRGB(x, y, color.getRGB());

        }
    }
}
