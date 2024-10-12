package org.system_false.dats_magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.system_false.dats_magic.json.GameRoundsResponse;
import org.system_false.dats_magic.json.MoveResponse;
import org.system_false.dats_magic.json.Round;

import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatsMagicController implements Initializable {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> task;

    @FXML private VBox  info;
    @FXML private ComboBox<String> serverUrl;
    @FXML private Label roundNameLabel;
    @FXML private Label roundStartLabel;
    @FXML private Label roundEndLabel;
    @FXML private Label nowLabel;
    @FXML private Label nameLabel;
    @FXML private Label pointsLabel;
    @FXML private Label carpetsInGameLabel;

    @FXML private Slider mapScale;
    @FXML private TextField scaleText;
    @FXML private ScrollPane mapView;
    @FXML private Canvas map;

    private GameRoundsResponse gameRounds;
    private GamePlay game;

    private int currentRound = -1;

    private void updateUI() {
        MoveResponse resp = game.getLastResponse();
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        if (this.currentRound != -1) {
            Round currentRound = gameRounds.getRounds().get(this.currentRound);
            roundNameLabel.setText(currentRound.getName());
            roundStartLabel.setText(dateFormat.format(currentRound.getStartAt()));
            roundEndLabel.setText(dateFormat.format(currentRound.getEndAt()));
        }
        nowLabel.setText(dateFormat.format(gameRounds.getNow()));
        if (resp == null) {
            return;
        }
        nameLabel.setText(resp.getName());
        pointsLabel.setText(String.valueOf(resp.getPoints()));
        carpetsInGameLabel.setText(String.valueOf(resp.getTransports().stream().filter(t -> t.getHealth() > 0).count()));
    }

    public Dimension2D getMinSize() {
        return new Dimension2D(info.getMinWidth() + mapView.getMinWidth() + 20, mapView.getMinHeight() + 64);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverUrl.getItems().addAll("https://games-test.datsteam.dev/", "https://games.datsteam.dev/");
        game = new GamePlay(map);
        mapScale.setOnMouseReleased(_ -> {
            double scale = (mapScale.getValue() - mapScale.getMin()) / (mapScale.getMax() - mapScale.getMin());
            game.getDraw().getScaleProperty().set(scale);
            scaleText.setText(String.valueOf(scale));
        });
        scaleText.setOnAction(_ -> {
            try {
                double scale = Double.parseDouble(scaleText.getText());
                mapScale.setValue(scale * (mapScale.getMax() - mapScale.getMin()) + mapScale.getMin());
            } catch (NumberFormatException e) {
                scaleText.setText(String.valueOf(game.getDraw().getScaleProperty().get()));
            }
        });
        scaleText.setText("0.065");
    }

    @FXML
    public void serverChoose(ActionEvent event) {
        RequestManager.stop();
        if (task != null) {
            task.cancel(true);
        }
        RequestManager.setServer(serverUrl.getValue());
        RequestManager.enqueueRequest(GamePlay.EMPTY_REQUEST, game);
        Request gameRequest = new Request("rounds/magcarp", "GET", false, null);
        task = scheduler.scheduleAtFixedRate(() -> {
            Response response;
            try {
                response = RequestManager.sendRequest(gameRequest);
            } catch (Exception e) {
                RequestManager.logger.log(Level.WARNING, "Failed to send request", e);
                RequestManager.enqueueRequest(GamePlay.EMPTY_REQUEST, game);
                return;
            }
            JsonElement body = response.getBody();
            JsonObject root = body.getAsJsonObject();
            if (root.has("error")) {
                RequestManager.logger.log(Level.WARNING, "Error {0}: {1}",
                        new String[]{root.get("errCode").getAsString(), root.get("error").getAsString()});
                RequestManager.enqueueRequest(GamePlay.EMPTY_REQUEST, game);
                return;
            }
            gameRounds = RequestManager.gson.fromJson(root, GameRoundsResponse.class);
            final int roundCount = gameRounds.getRounds().size();
            boolean roundSet = false;
            for (int i = roundCount - 1; i >= 0; i--) {
                Round round = gameRounds.getRounds().get(i);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(gameRounds.getNow());
                calendar.add(Calendar.HOUR, -1);
                Date now = calendar.getTime();
                if (round.getStartAt().after(now) && now.before(round.getEndAt())) {
                    if (currentRound != i) {
                        RequestManager.enqueueRequest(GamePlay.EMPTY_REQUEST, game);
                        RequestManager.start(500, TimeUnit.MILLISECONDS);
                    }
                    currentRound = i;
                    roundSet = true;
                    break;
                }
            }
            if (!roundSet) {
                RequestManager.stop();
            }
            Platform.runLater(this::updateUI);
        }, 0, 1, TimeUnit.SECONDS);
        RequestManager.start(500, TimeUnit.MILLISECONDS);
    }
}
