package org.system_false.dats_magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.system_false.dats_magic.json.GameRoundsResponse;
import org.system_false.dats_magic.json.MoveResponse;
import org.system_false.dats_magic.json.Round;

import java.net.URL;
import java.text.DateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatsMagicController implements Initializable {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML Label roundStartLabel;
    @FXML Label roundEndLabel;
    @FXML Label nameLabel;
    @FXML Label pointsLabel;
    @FXML Label attackDamageLabel;
    @FXML Label attackCooldownLabel;
    @FXML Label attackExplosionRadiusLabel;
    @FXML Label attackRangeLabel;
    @FXML Label maxAccelLabel;
    @FXML Label maxSpeedLabel;
    @FXML Label reviveTimeoutSecLabel;
    @FXML Label shieldCooldownMsLabel;
    @FXML Label shieldTimeMsLabel;
    @FXML Label transportRadiusLabel;
    @FXML Label wantedListLabel;

    private GameRoundsResponse gameRounds;
    private GameLoop game;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        game = new GameLoop();
        RequestManager.enqueueRequest(GameLoop.EMPTY_REQUEST, game);
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
                System.out.println(root.get("error").getAsString());
                return;
            }
            gameRounds = RequestManager.gson.fromJson(root, GameRoundsResponse.class);
            Platform.runLater(this::updateUI);
            Platform.runLater(() -> {
                if (stage != null) {
                    Date now = new Date();
                    var rounds = gameRounds.getRounds();
                    Round current = null;
                    for (Round round : rounds) {
                        if (round.getStartAt().after(now) && now.before(round.getEndAt())) {
                            current = round;
                            break;
                        }
                    }
                    if (current != null) {
                        stage.setTitle("DatsMagic - Игра \"%s\", Раунд \"%s\"".formatted(gameRounds.getGameName(),
                                current.getName()));
                    }
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
        RequestManager.start(1, TimeUnit.SECONDS);
    }
}
