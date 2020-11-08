package com.company;

public class Circle {
    private RealPoint center;
    private double size;

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
}
