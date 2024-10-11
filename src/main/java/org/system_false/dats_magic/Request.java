package org.system_false.dats_magic;

import com.google.gson.JsonElement;

import java.net.URL;

public class Request {
    private final URL url;
    private final String requestMethod;
    private final String authToken;
    private JsonElement body;

    public Request(URL url, String requestMethod, String authToken) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.authToken = authToken;
    }

    public URL getUrl() {
        return url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setBody(JsonElement body) {
        this.body = body;
    }

    public String getBody() {
        return body != null ? RequestManager.gson.toJson(body) : null;
    }
}
