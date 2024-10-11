package org.system_false.dats_magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.system_false.dats_magic.json.GameRoundsResponse;
import org.system_false.dats_magic.json.MoveRequest;
import org.system_false.dats_magic.json.Round;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatsMagicApplication extends Application {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static GameRoundsResponse gameRounds;
    private static Stage stage;

    private final GameLoop game;

    public DatsMagicApplication() {
        this.game = new GameLoop();
    }

    @Override
    public void init() throws Exception {
        RequestManager.enqueueRequest(new Request("play/magcarp/player/move", "POST", true,
                new MoveRequest().toJson(RequestManager.gson)), game);
    }

    @Override
    public void start(Stage stage) {
        DatsMagicApplication.stage = stage;
        stage.setTitle("DatsMagic");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setScene(new Scene(new AnchorPane()));
        stage.show();
    }

    public static void main(String[] args) {
        RequestManager.setToken(args[0]);
        RequestManager.setServer(args[1]);
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
                        stage.setTitle("DatsMagic - game %s, round %s".formatted(gameRounds.getGameName(),
                                current.getName()));
                    }
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
        RequestManager.start(1, TimeUnit.SECONDS);
        launch();
    }
}
