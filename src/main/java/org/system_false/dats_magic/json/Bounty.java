package org.system_false.dats_magic.json;

public class Bounty {
    private int points;
    private int radius;
    private int x;
    private int y;

    private Bounty() {}

    public Bounty(int points, int radius, int x, int y) {
        this.points = points;
        this.radius = radius;
        this.x = x;
        this.y = y;
    }

    public int getPoints() {
        return points;
    }

    public int getRadius() {
        return radius;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
