package org.system_false.dats_magic.json;

import javafx.geometry.Point2D;

public class ResponseTransport {
    private Point2D anomalyAcceleration;
    private int attackCooldownMs;
    private int deathCount;
    private int health;
    private String id;
    private Point2D selfAcceleration;
    private int shieldCooldownMs;
    private int shieldLeftMs;
    private String status;
    private Point2D velocity;
    private int x;
    private int y;

    private ResponseTransport() {}

    public Point2D getAnomalyAcceleration() {
        return anomalyAcceleration;
    }

    public int getAttackCooldownMs() {
        return attackCooldownMs;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public int getHealth() {
        return health;
    }

    public String getId() {
        return id;
    }

    public Point2D getSelfAcceleration() {
        return selfAcceleration;
    }

    public int getShieldCooldownMs() {
        return shieldCooldownMs;
    }

    public int getShieldLeftMs() {
        return shieldLeftMs;
    }

    public String getStatus() {
        return status;
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
