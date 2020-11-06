package com.company;

import java.awt.*;

public class CircleInt {
    private ScreenPoint center;
    private double size;
    private Color color = Color.GREEN;

    public CircleInt(ScreenPoint center, double size) {
        this.center = center;
        this.size = size;
    }

    public ScreenPoint getCenter() {
        return center;
    }

    public void setCenter(ScreenPoint center) {
        this.center = center;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
