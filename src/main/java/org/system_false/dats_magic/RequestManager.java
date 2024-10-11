package org.system_false.dats_magic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RequestManager {
    static final Gson gson;
    static final Logger logger;

    static {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        logger = Logger.getLogger("RequestManager");
    }

    private static URL serverUrl;
    private static String token;

    private static final AtomicReference<Request> requestReference = new AtomicReference<>();
    private static final AtomicReference<Exchanger<Response>> callbackReference = new AtomicReference<>();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;

    private static long rate;
    private static TimeUnit unit;

    private RequestManager() {}

    public static void setServer(String server) throws URISyntaxException, MalformedURLException {
        RequestManager.serverUrl = new URI(server).toURL();
    }

    public static void setToken(String token) {
        RequestManager.token = token;
    }

    public static void start(long rate, TimeUnit unit) {
        if (task != null) {
            task.cancel(false);
        }
        task = scheduler.scheduleAtFixedRate(() -> {
            Request request = requestReference.getAcquire();
            Exchanger<Response> callback = callbackReference.getAcquire();

            if (request != null && callback != null) {
                try {
                    Response response = sendRequest(request);
                    callback.exchange(response);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to send request", e);
                } finally {
                    requestReference.setRelease(null);
                    callbackReference.setRelease(null);
                }
            }
        }, 0, rate, unit);
        RequestManager.rate = rate;
        RequestManager.unit = unit;
    }

    public static void stop() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }

    public static synchronized void enqueueRequest(Request request, Exchanger<Response> callback) {
        requestReference.setRelease(request);
        callbackReference.setRelease(callback);
    }

    public static Response sendRequest(Request request) throws IOException {
        HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
        con.setRequestMethod(request.getRequestMethod());
        con.setRequestProperty("X-Auth-Token", token);
        con.setRequestProperty("Content-Type", "application/json");
        con.setReadTimeout((int) unit.toMillis(rate));
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8))) {
            out.write(request.getBody());
        }
        if (con.getResponseCode() != 200) {
            throw new ErrorCodeException(con.getResponseCode());
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            return new Response(in.lines().collect(Collectors.joining()));
        }
    }
}
