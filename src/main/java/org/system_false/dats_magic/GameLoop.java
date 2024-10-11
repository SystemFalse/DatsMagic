package org.system_false.dats_magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.system_false.dats_magic.json.MoveResponse;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLoop implements Consumer<Response> {
    private static final Logger logger;

    static {
        logger = Logger.getLogger(GameLoop.class.getName());
        logger.setLevel(Level.INFO);
    }

    private MoveResponse lastResponse;

    @Override
    public void accept(Response response) {
        JsonElement body = response.getBody();
        JsonObject root = body.getAsJsonObject();
        if (root.has("error")) {

        }
    }
}
