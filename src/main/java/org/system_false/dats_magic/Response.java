package org.system_false.dats_magic;

import com.google.gson.JsonElement;

public class Response {
    private JsonElement body;

    public Response(String response) {
        body = RequestManager.gson.toJsonTree(response);
    }

    public JsonElement getBody() {
        return body;
    }
}
