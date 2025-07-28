package com.alphafeed.io;

import com.alphafeed.io.model.NewsSignal;
import com.alphafeed.io.model.RealtimeMessage;
import com.google.gson.Gson;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alphafeed.io.util.ISO8601DateAdapter;

public class AlphafeedWebSocketClient extends WebSocketListener {
    private final OkHttpClient client;
    private final String url;
    private final NewsSignalListener listener;
    private final Gson gson;
    private WebSocket webSocket;
    private boolean isConnected = false;
    private final ScheduledExecutorService pingScheduler = Executors.newSingleThreadScheduledExecutor();

    public AlphafeedWebSocketClient(String url, NewsSignalListener listener) {
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // Disable timeouts for WebSockets
                .build();
        this.url = url;
        this.listener = listener;
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new ISO8601DateAdapter())
                .create();
    }

    public void connect() {
        Request request = new Request.Builder()
                .url(url)
                .build();
        webSocket = client.newWebSocket(request, this);
        
        // Schedule ping messages every 30 seconds
        pingScheduler.scheduleAtFixedRate(() -> {
            if (isConnected && webSocket != null) {
                JSONObject pingMessage = new JSONObject();
                pingMessage.put("type", "ping");
                webSocket.send(pingMessage.toString());
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Closed by client");
        }
        pingScheduler.shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        isConnected = true;
        listener.onConnectionStateChange(true);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            RealtimeMessage message = gson.fromJson(text, RealtimeMessage.class);
            
            if (message.getType() != null && message.getType().equals("news_signal") && message.getData() != null) {
                listener.onSignal(message.getData());
            }
        } catch (Exception e) {
            listener.onError(e);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        isConnected = false;
        listener.onConnectionStateChange(false);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        isConnected = false;
        listener.onError(t);
        listener.onConnectionStateChange(false);
    }
}
