package org.system_false.dats_magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import org.system_false.dats_magic.json.GameRoundsResponse;
import org.system_false.dats_magic.json.MoveResponse;
import org.system_false.dats_magic.json.Round;

import java.net.URL;
import java.text.DateFormat;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatsMagicController implements Initializable {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML private VBox  info;
    @FXML private Label roundStartLabel;
    @FXML private Label roundEndLabel;
    @FXML private Label nameLabel;
    @FXML private Label pointsLabel;
    @FXML private Label attackDamageLabel;
    @FXML private Label attackCooldownLabel;
    @FXML private Label attackExplosionRadiusLabel;
    @FXML private Label attackRangeLabel;
    @FXML private Label maxAccelLabel;
    @FXML private Label maxSpeedLabel;
    @FXML private Label reviveTimeoutSecLabel;
    @FXML private Label shieldCooldownMsLabel;
    @FXML private Label shieldTimeMsLabel;
    @FXML private Label transportRadiusLabel;
    @FXML private Label wantedListLabel;

    @FXML private Slider mapScale;
    @FXML private CheckBox centerCheck;
    @FXML private ScrollPane mapView;
    @FXML private Canvas map;

    private GameRoundsResponse gameRounds;
    private GamePlay game;

    private void updateUI() {
        MoveResponse resp = game.getLastResponse();
        Round currentRound = gameRounds.getCurrentRound();
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/Kaliningrad")));
        roundStartLabel.setText(dateFormat.format(currentRound.getStartAt()));
        roundEndLabel.setText(dateFormat.format(currentRound.getEndAt()));
        if (resp == null) {
            return;
        }
        nameLabel.setText(resp.getName());
        pointsLabel.setText(String.valueOf(resp.getPoints()));
        attackDamageLabel.setText(String.valueOf(resp.getAttackDamage()));
        attackCooldownLabel.setText(String.valueOf(resp.getAttackCooldownMs()));
        attackExplosionRadiusLabel.setText(String.valueOf(resp.getAttackExplosionRadius()));
        attackRangeLabel.setText(String.valueOf(resp.getAttackRange()));
        maxAccelLabel.setText(String.valueOf(resp.getMaxAccel()));
        maxSpeedLabel.setText(String.valueOf(resp.getMaxSpeed()));
        reviveTimeoutSecLabel.setText(String.valueOf(resp.getReviveTimeoutSec()));
        shieldCooldownMsLabel.setText(String.valueOf(resp.getShieldCooldownMs()));
        shieldTimeMsLabel.setText(String.valueOf(resp.getShieldTimeMs()));
        transportRadiusLabel.setText(String.valueOf(resp.getTransportRadius()));
        wantedListLabel.setText(String.valueOf(resp.getWantedList().size()));
    }

    public Dimension2D getMinSize() {
        return new Dimension2D(info.getMinWidth() + mapView.getMinWidth() + 20, mapView.getMinHeight() + 64);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        game = new GamePlay(map);
        game.getDraw().getScaleProperty().bind(mapScale.valueProperty().subtract(mapScale.minProperty())
                .divide(mapScale.maxProperty().subtract(mapScale.minProperty())));
        game.getDraw().getCenterProperty().bind(centerCheck.selectedProperty());
        RequestManager.enqueueRequest(GamePlay.EMPTY_REQUEST, game);
        Request gameRequest = new Request("rounds/magcarp", "GET", false, null);
        scheduler.scheduleAtFixedRate(() -> {
            Response response;
            try {
                response = RequestManager.sendRequest(gameRequest);
            } catch (Exception e) {
                RequestManager.logger.log(Level.WARNING, "Failed to send request", e);
                return;
            }
            JsonElement body = response.getBody();
            JsonObject root = body.getAsJsonObject();
            if (root.has("error")) {
                RequestManager.logger.log(Level.WARNING, "Error {0}: {1}",
                        new String[]{root.get("errCode").getAsString(), root.get("error").getAsString()});
                return;
            }
            gameRounds = RequestManager.gson.fromJson(root, GameRoundsResponse.class);
            Platform.runLater(this::updateUI);
        }, 0, 1, TimeUnit.SECONDS);
        RequestManager.start(1, TimeUnit.SECONDS);
    }

    public void autoCenter(ActionEvent event) {
        mapScale.setDisable(centerCheck.isSelected());
    }
}
