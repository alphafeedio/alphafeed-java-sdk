package com.alphafeed.io;

import com.alphafeed.io.model.NewsSignal;
import com.alphafeed.io.model.RealtimeMessage;
import com.alphafeed.io.util.GsonFactory;
import com.alphafeed.io.util.HttpUtils;
import com.google.gson.Gson;
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
import java.util.concurrent.atomic.AtomicInteger;

import com.alphafeed.io.util.ISO8601DateAdapter;

public class AlphafeedWebSocketClient extends WebSocketListener {
    private final OkHttpClient client;
    private final String url;
    private final NewsSignalListener listener;
    private final Gson gson;
    private WebSocket webSocket;
    private boolean isConnected = false;
    private final ScheduledExecutorService pingScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
    
    private final int maxReconnectAttempts;
    private final int reconnectIntervalSeconds;
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private boolean intentionalDisconnect = false;

    public AlphafeedWebSocketClient(String url, NewsSignalListener listener) {
        this(url, listener, 3, 5);
    }

    public AlphafeedWebSocketClient(String url, NewsSignalListener listener, int maxReconnectAttempts, int reconnectIntervalSeconds) {
        this.client = HttpUtils.createWebSocketHttpClient();
        this.url = url;
        this.listener = listener;
        this.gson = GsonFactory.getGson();
        this.maxReconnectAttempts = maxReconnectAttempts;
        this.reconnectIntervalSeconds = reconnectIntervalSeconds;
    }

    public void connect() {
        intentionalDisconnect = false;
        reconnectAttempts.set(0);
        connectWebSocket();
        
        // Schedule ping messages every 30 seconds
        pingScheduler.scheduleAtFixedRate(() -> {
            if (isConnected && webSocket != null) {
                JSONObject pingMessage = new JSONObject();
                pingMessage.put("type", "ping");
                webSocket.send(pingMessage.toString());
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void connectWebSocket() {
        Request request = new Request.Builder()
                .url(url)
                .build();
        webSocket = client.newWebSocket(request, this);
    }

    public void disconnect() {
        intentionalDisconnect = true;
        if (webSocket != null) {
            webSocket.close(1000, "Closed by client");
        }
        pingScheduler.shutdown();
        reconnectScheduler.shutdown();
    }

    private void attemptReconnect() {
        if (intentionalDisconnect) {
            return;
        }

        int attempts = reconnectAttempts.incrementAndGet();
        if (attempts <= maxReconnectAttempts) {
            listener.onConnectionStateChange(false, "Connection lost. Reconnect attempt " + attempts + 
                                           " of " + maxReconnectAttempts + " in " + reconnectIntervalSeconds + " seconds");

            
            reconnectScheduler.schedule(this::connectWebSocket, reconnectIntervalSeconds, TimeUnit.SECONDS);
        } else {
            listener.onConnectionStateChange(false, "Failed to reconnect after " + maxReconnectAttempts + " attempts");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        isConnected = true;
        reconnectAttempts.set(0);
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
        
        if (!intentionalDisconnect) {
            attemptReconnect();
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        isConnected = false;
        listener.onError(t);
        listener.onConnectionStateChange(false);
        
        if (!intentionalDisconnect) {
            attemptReconnect();
        }
    }
}
