package org.system_false.dats_magic.json;

import com.google.gson.*;

import java.awt.geom.Point2D;
import java.lang.reflect.Type;

public class Point2DAdapter implements JsonDeserializer<Point2D>, JsonSerializer<Point2D> {
    @Override
    public Point2D deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Point2D.Double(jsonElement.getAsJsonObject().get("x").getAsDouble(), jsonElement.getAsJsonObject().get("y").getAsDouble());
    }

    @Override
    public JsonElement serialize(Point2D point2D, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", point2D.getX());
        obj.addProperty("y", point2D.getY());
        return obj;
    }
}
