package org.system_false.dats_magic.json;

import javafx.geometry.Point2D;

public class Enemy {
    private int health;
    private int killBounty;
    private int shieldLeftMs;
    private String status;
    private Point2D velocity;
    private int x;
    private int y;

    private Enemy() {}

    public Enemy(int health, int killBounty, int shieldLeftMs, String status, Point2D velocity, int x, int y) {
        this.health = health;
        this.killBounty = killBounty;
        this.shieldLeftMs = shieldLeftMs;
        this.status = status;
        this.velocity = velocity;
        this.x = x;
        this.y = y;
    }

    public int getHealth() {
        return health;
    }

    public int getKillBounty() {
        return killBounty;
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
