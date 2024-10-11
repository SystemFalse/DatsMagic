package org.system_false.dats_magic.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class MoveRequest {
    private final List<Transport> transports = new ArrayList<>();

    public void addTransport(Transport transport) {
        transports.add(transport);
    }

    public List<Transport> getTransports() {
        return transports;
    }

    public JsonElement toJson(Gson gson) {
        return gson.toJsonTree(this);
    }
}
