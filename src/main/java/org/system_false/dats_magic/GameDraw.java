package org.system_false.dats_magic;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.system_false.dats_magic.json.*;

import java.util.List;

public class GameDraw {
    private final Canvas map;
    private final SimpleDoubleProperty scale = new SimpleDoubleProperty(1D);
    private final SimpleBooleanProperty center = new SimpleBooleanProperty(false);

    public GameDraw(Canvas map) {
        this.map = map;
    }

    public DoubleProperty getScaleProperty() {
        return scale;
    }

    public BooleanProperty getCenterProperty() {
        return center;
    }

    public void draw(MoveResponse data) {
        boolean center = this.center.get();
        double scale;
        if (!center) {
            scale = this.scale.get();
        } else {
            scale = 1D;
        }
        var g = map.getGraphicsContext2D();
        //очистка карты
        g.clearRect(0, 0, map.getWidth(), map.getHeight());

        //отрисовка границ игрового поля
        Point2D mapSize = data.getMapSize();
        g.setStroke(Color.BLACK);
        g.setLineWidth(5);
        g.moveTo(0, 0);
        g.lineTo(mapSize.getX() * scale, 0);
        g.lineTo(mapSize.getX() * scale, mapSize.getY() * scale);
        g.lineTo(0, mapSize.getY() * scale);
        g.lineTo(0, 0);
        g.stroke();

        //отрисовка аномалий
        List<Anomaly> anomalies = data.getAnomalies();
        for (Anomaly anomaly : anomalies) {
            g.setFill(anomaly.getStrength() >= 0 ? Color.RED : Color.BLUE);
            g.setGlobalAlpha(0.2);
            g.fillOval((anomaly.getX() - anomaly.getEffectiveRadius()) * scale, (anomaly.getY() - anomaly.getEffectiveRadius()) * scale,
                    (2 * anomaly.getEffectiveRadius() + 1) * scale, (2 * anomaly.getEffectiveRadius() + 1) * scale);
            g.setGlobalAlpha(0.6);
            g.fillOval((anomaly.getX() - anomaly.getRadius()) * scale, (anomaly.getY() - anomaly.getRadius()) * scale,
                    (2 * anomaly.getRadius() + 1) * scale, (2 * anomaly.getRadius() + 1) * scale);
        }

        //отрисовка монет
        g.setFill(Color.YELLOW);
        for (Bounty bounty : data.getBounties()) {
            g.fillOval((bounty.getX() - 2) * scale, (bounty.getY() - 2) * scale, 5 * scale, 5 * scale);
        }

        //отрисовка врагов
        int carpetRadius = data.getTransportRadius();
        g.setLineWidth(2);
        g.setStroke(Color.BLUE.brighter());
        List<Enemy> enemies = data.getEnemies();
        for (Enemy enemy : enemies) {
            g.setGlobalAlpha(1D);
            g.setFill(enemy.getKillBounty() >= 5 ? Color.MEDIUMVIOLETRED : Color.RED);
            g.fillOval((enemy.getX() - 2) * scale, (enemy.getY() - 2) * scale, 5 * scale, 5 * scale);
            g.setGlobalAlpha(0.4);
            g.fillOval((enemy.getX() - carpetRadius) * scale, (enemy.getY() - carpetRadius) * scale,
                    (2 * carpetRadius + 1) * scale, (2 * carpetRadius + 1) * scale);
            if (enemy.getShieldLeftMs() > 0) {
                g.setGlobalAlpha(1D);
                g.strokeOval((enemy.getX() - 4) * scale, (enemy.getY() - 4) * scale, 9 * scale, 9 * scale);
            }
        }

        //отрисовка ковров этой команды
        g.setGlobalAlpha(1D);
        double attackSize = (data.getAttackRange() * 2 + 1) * scale;
        for (ResponseTransport transport : data.getTransports()) {
            g.setFill(Color.GREEN);
            g.fillOval((transport.getX() - 2) * scale, (transport.getY() - 2) * scale, 5 * scale, 5 * scale);
            g.setStroke(Color.LIGHTGREEN);
            g.strokeOval((transport.getX() - data.getAttackRange()) * scale, (transport.getY() - data.getAttackRange()) * scale,
                    attackSize, attackSize);
        }
    }
}
