package org.system_false.dats_magic;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.system_false.dats_magic.json.*;

import java.util.List;
import java.util.Objects;

public class GameDraw {
    private final Canvas map;
    private final SimpleDoubleProperty scale = new SimpleDoubleProperty(0.065);
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
        map.setWidth(data.getMapSize().getX() * scale);
        map.setHeight(data.getMapSize().getY() * scale);
        var g = map.getGraphicsContext2D();
        double carpetRadius = 3, carpetSize = 7;
        //очистка карты
        g.clearRect(0, 0, map.getWidth(), map.getHeight());

        //задний фон
        g.setImageSmoothing(false);
        g.setGlobalAlpha(0.5);
        g.drawImage(new Image(Objects.toString(getClass().getResource("Carpet.png"))), 0, 0, map.getWidth(),
                map.getHeight());

        //отрисовка границ игрового поля
        g.setGlobalAlpha(1);
        g.setStroke(Color.BLACK);
        g.setLineWidth(5);
        g.moveTo(0, 0);
        g.lineTo(map.getWidth(), 0);
        g.lineTo(map.getWidth(), map.getHeight());
        g.lineTo(0, map.getHeight());
        g.lineTo(0, 0);
        g.stroke();

        //отрисовка аномалий
        List<Anomaly> anomalies = data.getAnomalies();
        for (Anomaly anomaly : anomalies) {
            g.setFill(anomaly.getStrength() >= 0 ? Color.RED : Color.BLUE);
            g.setGlobalAlpha(0.15);
            g.fillOval((anomaly.getX() - anomaly.getEffectiveRadius()) * scale, (anomaly.getY() - anomaly.getEffectiveRadius()) * scale,
                    (2 * anomaly.getEffectiveRadius() + 1) * scale, (2 * anomaly.getEffectiveRadius() + 1) * scale);
            g.setGlobalAlpha(0.6);
            g.fillOval((anomaly.getX() - anomaly.getRadius()) * scale, (anomaly.getY() - anomaly.getRadius()) * scale,
                    (2 * anomaly.getRadius() + 1) * scale, (2 * anomaly.getRadius() + 1) * scale);
        }

        //отрисовка монет
        g.setFill(Color.GOLD);
        final double bountyRadius = 2, bountySize = 2 * bountyRadius + 1;
        for (Bounty bounty : data.getBounties()) {
            g.fillOval((bounty.getX()) * scale - bountyRadius, (bounty.getY()) * scale - bountyRadius,
                    bountySize, bountySize);
        }

        //отрисовка врагов
        g.setLineWidth(2);
        List<Enemy> enemies = data.getEnemies();
        g.setGlobalAlpha(1D);
        for (Enemy enemy : enemies) {
            g.setFill(enemy.getKillBounty() >= 5 ? Color.MEDIUMVIOLETRED : Color.RED);
            g.fillOval(enemy.getX() * scale - carpetRadius, enemy.getY() * scale - carpetRadius,
                    carpetSize, carpetSize);
            if (enemy.getShieldLeftMs() > 0) {
                g.setStroke(Color.BLUE.brighter());
                g.strokeOval(enemy.getX() * scale - carpetRadius, enemy.getY() * scale - carpetRadius,
                        carpetSize, carpetSize);
            } else {
                g.setStroke(Color.BLACK);
                g.setLineWidth(1);
                g.strokeOval(enemy.getX() * scale - carpetRadius, enemy.getY() * scale - carpetRadius,
                        carpetSize, carpetSize);
            }
        }

        //отрисовка ковров этой команды
        g.setGlobalAlpha(1D);
        double attackSize = (data.getAttackRange() * 2 + 1) * scale;
        for (ResponseTransport transport : data.getTransports()) {
            g.setFill(Color.GREEN);
            g.fillOval(transport.getX() * scale - carpetRadius, transport.getY() * scale - carpetRadius,
                    carpetSize, carpetSize);
            g.setStroke(Color.LIGHTGREEN);
            g.strokeOval((transport.getX() - data.getAttackRange()) * scale, (transport.getY() - data.getAttackRange()) * scale,
                    attackSize, attackSize);
        }
    }
}
