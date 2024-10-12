package org.system_false.dats_magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import org.system_false.dats_magic.json.*;

import java.util.Comparator;
import java.util.Optional;
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

        for (ResponseTransport carpet : lastResponse.getTransports()) {
            int x = carpet.getX(), y = carpet.getY();
            Point2D acceleration = new Point2D(0, 0), attack = null;
            boolean activateShield = false;

            Optional<Bounty> nearestBounty = lastResponse.getBounties().parallelStream()
                    .min(Comparator.comparingDouble(b -> distanceBetween(x, y, b.getX(), b.getY())));
            if (nearestBounty.isPresent()) {
                double distance = distanceBetween(x, y, nearestBounty.get().getX(), nearestBounty.get().getY());
                double k = lastResponse.getMaxAccel() / distance;
                acceleration = new Point2D(k * (nearestBounty.get().getX() - x) + carpet.getAnomalyAcceleration().getX(),
                        k * (nearestBounty.get().getY() - y) + carpet.getAnomalyAcceleration().getY());
            }

            request.addTransport(new RequestTransport(acceleration, activateShield, attack, carpet.getId()));
        }

        RequestManager.enqueueRequest(new Request("play/magcarp/player/move", "POST", true,
                request.toJson(RequestManager.gson)), this);
    }

    private static double distanceBetween(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
