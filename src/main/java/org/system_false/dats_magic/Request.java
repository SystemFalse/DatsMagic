package org.system_false.dats_magic;

import com.google.gson.JsonElement;

public class Request {
    private final String url;
    private final String requestMethod;
    private final boolean useAuth;
    private final JsonElement body;

    public Request(String url, String requestMethod, boolean useAuth, JsonElement body) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.useAuth = useAuth;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public boolean useAuth() {
        return useAuth;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getBody() {
        return body != null ? RequestManager.gson.toJson(body) : null;
    }
}
