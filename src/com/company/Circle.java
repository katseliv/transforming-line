package com.company;

import java.awt.*;

public class Circle {
    private RealPoint center;
    private double size;
    private Color color = Color.GREEN;

    public Circle(RealPoint center, double size) {
        this.center = center;
        this.size = size;
    }

    public RealPoint getCenter() {
        return center;
    }

    public void setCenter(RealPoint center) {
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
