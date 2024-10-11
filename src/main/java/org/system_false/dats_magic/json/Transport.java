package org.system_false.dats_magic.json;

import javafx.geometry.Point2D;

public class Transport {
    private Point2D acceleration;
    private boolean activateShield;
    private Point2D attack;
    private String id;

    private Transport() {}

    public Transport(Point2D acceleration, boolean activateShield, Point2D attack, String id) {
        this.acceleration = acceleration;
        this.activateShield = activateShield;
        this.attack = attack;
        this.id = id;
    }

    public Point2D getAcceleration() {
        return acceleration;
    }

    public boolean isActivateShield() {
        return activateShield;
    }

    public Point2D getAttack() {
        return attack;
    }

    public String getId() {
        return id;
    }
}
