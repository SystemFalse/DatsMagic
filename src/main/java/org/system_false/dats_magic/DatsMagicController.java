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
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatsMagicController implements Initializable {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML private VBox  info;
    @FXML private Label roundNameLabel;
    @FXML private Label roundStartLabel;
    @FXML private Label roundEndLabel;
    @FXML private Label nowLabel;
    @FXML private Label nameLabel;
    @FXML private Label pointsLabel;

    @FXML private Slider mapScale;
    @FXML private CheckBox centerCheck;
    @FXML private ScrollPane mapView;
    @FXML private Canvas map;

    private GameRoundsResponse gameRounds;
    private GamePlay game;

    private int currentRound = -1;

    private void updateUI() {
        MoveResponse resp = game.getLastResponse();
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
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
    }

    public Dimension2D getMinSize() {
        return new Dimension2D(info.getMinWidth() + mapView.getMinWidth() + 20, mapView.getMinHeight() + 64);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        game = new GamePlay(map);
        mapScale.setOnMouseReleased(_ -> {
            game.getDraw().getScaleProperty().set((mapScale.getValue() - mapScale.getMin()) / (mapScale.getMax() - mapScale.getMin()));
        });
//        game.getDraw().getScaleProperty().bind(mapScale.valueProperty().subtract(mapScale.minProperty())
//                .divide(mapScale.maxProperty().subtract(mapScale.minProperty())));
        game.getDraw().getCenterProperty().bind(centerCheck.selectedProperty());
        RequestManager.enqueueRequest(GamePlay.EMPTY_REQUEST, game);
        Request gameRequest = new Request("rounds/magcarp", "GET", false, null);
        scheduler.scheduleAtFixedRate(() -> {
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
                        RequestManager.start(1, TimeUnit.SECONDS);
                        RequestManager.enqueueRequest(GamePlay.EMPTY_REQUEST, game);
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
        RequestManager.start(1, TimeUnit.SECONDS);
    }

    public void autoCenter(ActionEvent event) {
        mapScale.setDisable(centerCheck.isSelected());
    }
}
