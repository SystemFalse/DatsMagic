package org.system_false.dats_magic.json;

import javafx.geometry.Point2D;

public class Anomaly {
    private double effectiveRadius;
    private String id;
    private double radius;
    private double strength;
    private Point2D velocity;
    private int x;
    private int y;

    private Anomaly() {}

    public Anomaly(double effectiveRadius, String id, double radius, double strength, Point2D velocity, int x, int y) {
        this.effectiveRadius = effectiveRadius;
        this.id = id;
        this.radius = radius;
        this.strength = strength;
        this.velocity = velocity;
        this.x = x;
        this.y = y;
    }

    public double getEffectiveRadius() {
        return effectiveRadius;
    }

    public String getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public double getStrength() {
        return strength;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
