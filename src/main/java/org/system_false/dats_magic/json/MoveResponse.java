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
    private List<Transport> transports;
    private List<Enemy> wantedList;

    private MoveResponse() {}

    public MoveResponse(List<Anomaly> anomalies, int attackCooldownMs, int attackDamage, double attackExplosionRadius,
                        double attackRange, List<Bounty> bounties, List<Enemy> enemies, Point2D mapSize, double maxAccel,
                        double maxSpeed, String name, int points, int reviveTimeoutSec, int shieldCooldownMs,
                        int shieldTimeMs, int transportRadius, List<Transport> transports, List<Enemy> wantedList) {
        this.anomalies = anomalies;
        this.attackCooldownMs = attackCooldownMs;
        this.attackDamage = attackDamage;
        this.attackExplosionRadius = attackExplosionRadius;
        this.attackRange = attackRange;
        this.bounties = bounties;
        this.enemies = enemies;
        this.mapSize = mapSize;
        this.maxAccel = maxAccel;
        this.maxSpeed = maxSpeed;
        this.name = name;
        this.points = points;
        this.reviveTimeoutSec = reviveTimeoutSec;
        this.shieldCooldownMs = shieldCooldownMs;
        this.shieldTimeMs = shieldTimeMs;
        this.transportRadius = transportRadius;
        this.transports = transports;
        this.wantedList = wantedList;
    }

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

    public List<Transport> getTransports() {
        return transports;
    }

    public List<Enemy> getWantedList() {
        return wantedList;
    }
}
