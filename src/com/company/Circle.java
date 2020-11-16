package com.company;

public class Circle {
    private RealPoint center;

    public Circle(RealPoint center) {
        this.center = center;
    }

    public RealPoint getCenter() {
        return center;
    }

    public void setCenter(RealPoint center) {
        this.center = center;
    }

    public double getRadius() {
        return 10;
    }
}
