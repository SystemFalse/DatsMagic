package org.system_false.dats_magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import org.system_false.dats_magic.json.MoveRequest;
import org.system_false.dats_magic.json.MoveResponse;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GamePlay implements Consumer<Response> {
    public static final Request EMPTY_REQUEST = new Request("play/magcarp/player/move", "POST",
            true, new MoveRequest().toJson(RequestManager.gson));

    private static final Logger logger;

    static {
        logger = Logger.getLogger(GamePlay.class.getName());
        logger.setLevel(Level.INFO);
    }

    private MoveResponse lastResponse;
    private final GameDraw draw;

    public GamePlay(Canvas map) {
        draw = new GameDraw(map);
    }

    public GameDraw getDraw() {
        return draw;
    }

    public MoveResponse getLastResponse() {
        return lastResponse;
    }

    @Override
    public void accept(Response response) {
        JsonElement body = response.getBody();
        JsonObject root = body.getAsJsonObject();
        if (root.has("error")) {
            logger.log(Level.WARNING, "Error {0}: {1}", new String[]{root.get("errCode").getAsString(),
                    root.get("error").getAsString()});
            RequestManager.enqueueRequest(EMPTY_REQUEST, this);
            return;
        }
        lastResponse = RequestManager.gson.fromJson(root, MoveResponse.class);
        //отрисовка всего
        Platform.runLater(() -> draw.draw(lastResponse));

        MoveRequest request = new MoveRequest();

        RequestManager.enqueueRequest(new Request("play/magcarp/player/move", "POST", true,
                request.toJson(RequestManager.gson)), this);
    }
}
