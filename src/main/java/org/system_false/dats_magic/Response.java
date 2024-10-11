package org.system_false.dats_magic;

import com.google.gson.JsonElement;

public class Response {
    private final JsonElement body;

    public Response(JsonElement response) {
        body = response;
    }

    public JsonElement getBody() {
        return body;
    }
}
