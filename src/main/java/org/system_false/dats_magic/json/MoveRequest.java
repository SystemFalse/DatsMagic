package org.system_false.dats_magic.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class MoveRequest {
    private final List<RequestTransport> requestTransports = new ArrayList<>();

    public void addTransport(RequestTransport requestTransport) {
        requestTransports.add(requestTransport);
    }

    public List<RequestTransport> getTransports() {
        return requestTransports;
    }

    public JsonElement toJson(Gson gson) {
        return gson.toJsonTree(this);
    }
}
