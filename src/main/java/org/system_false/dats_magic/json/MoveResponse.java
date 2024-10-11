package org.system_false.dats_magic.json;

import javafx.geometry.Point2D;

import java.util.List;

public class MoveResponse {
    private List<Anomaly> anomalies;
    private int attackCooldownMs;
    private int attackDamage;
    private double attackExplosionRadius;
    private double attackRange;
    private List<Bounty> bounties;
    private List<Enemy> enemies;
    private Point2D mapSize;
    private double maxAccel;
    private double maxSpeed;
    private String name;
    private int points;
    private int reviveTimeoutSec;
    private int shieldCooldownMs;
    private int shieldTimeMs;
    private int transportRadius;
    private List<ResponseTransport> transports;
    private List<Enemy> wantedList;

    private MoveResponse() {}

    public List<Anomaly> getAnomalies() {
        return anomalies;
    }

    public int getAttackCooldownMs() {
        return attackCooldownMs;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public double getAttackExplosionRadius() {
        return attackExplosionRadius;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public List<Bounty> getBounties() {
        return bounties;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Point2D getMapSize() {
        return mapSize;
    }

    public double getMaxAccel() {
        return maxAccel;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getReviveTimeoutSec() {
        return reviveTimeoutSec;
    }

    public int getShieldCooldownMs() {
        return shieldCooldownMs;
    }

    public int getShieldTimeMs() {
        return shieldTimeMs;
    }

    public int getTransportRadius() {
        return transportRadius;
    }

    public List<ResponseTransport> getTransports() {
        return transports;
    }

    public List<Enemy> getWantedList() {
        return wantedList;
    }
}
