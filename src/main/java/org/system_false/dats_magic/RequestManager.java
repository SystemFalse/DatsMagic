package org.system_false.dats_magic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.geometry.Point2D;
import org.system_false.dats_magic.json.Point2DAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RequestManager {
    static final Gson gson;
    static final Logger logger;

    static {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Point2D.class, new Point2DAdapter())
                .create();
        logger = Logger.getLogger("RequestManager");
        logger.setLevel(Level.INFO);
    }

    private static String serverUrl;
    private static String token;

    private static final AtomicReference<Request> requestReference = new AtomicReference<>();
    private static final AtomicReference<Consumer<Response>> callbackReference = new AtomicReference<>();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;
    private static boolean shouldStop;

    private RequestManager() {}

    public static void setServer(String server) {
        RequestManager.serverUrl = server;
    }

    public static void setToken(String token) {
        RequestManager.token = token;
    }

    public static void start(long rate, TimeUnit unit) {
        if (task != null) {
            task.cancel(true);
        }
        task = scheduler.scheduleAtFixedRate(() -> {
            if (shouldStop) {
                stop0();
            }
            Request request = requestReference.getAcquire();
            Consumer<Response> callback = callbackReference.getAcquire();

            if (request != null && callback != null) {
                requestReference.setRelease(null);
                callbackReference.setRelease(null);
                try {
                    Response response = sendRequest(request);
                    callback.accept(response);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to send request", e);
                    requestReference.setRelease(request);
                    callbackReference.setRelease(callback);
                }
            }
        }, 0, rate, unit);
    }

    public static void stop() {
        if (task != null) {
            shouldStop = true;
        }
    }

    private static void stop0() {
        task.cancel(true);
        task = null;
        shouldStop = false;
    }

    public static synchronized void enqueueRequest(Request request, Consumer<Response> callback) {
        requestReference.setRelease(request);
        callbackReference.setRelease(callback);
    }

    public static Response sendRequest(Request request) throws IOException, URISyntaxException {
        URI uri = new URI(serverUrl + request.getUrl());
        logger.log(Level.FINER, "Sending request to {0} with method {1}", new String[]{uri.toASCIIString(), request.getRequestMethod()});
        HttpURLConnection con = prepareConnection(request, uri);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            return new Response(gson.fromJson(in.lines().collect(Collectors.joining()), JsonObject.class));
        }
    }

    private static HttpURLConnection prepareConnection(Request request, URI uri) throws IOException {
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod(request.getRequestMethod());
        con.setRequestProperty("Content-Type", "application/json");
        if (request.useAuth()) {
            con.setRequestProperty("X-Auth-Token", token);
        }
        if (request.getBody() != null) {
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8))) {
                out.write(request.getBody());
            }
        }
        return con;
    }
}
