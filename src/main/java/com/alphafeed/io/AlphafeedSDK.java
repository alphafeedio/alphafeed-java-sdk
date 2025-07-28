package com.alphafeed.io;

import com.alphafeed.io.model.NewsSignal;
import com.alphafeed.io.model.SignalsHistoricalDataResponse;
import com.alphafeed.io.util.ISO8601DateAdapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AlphafeedSDK {
    private final String apiKey;
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private AlphafeedWebSocketClient webSocketClient;

    public AlphafeedSDK(String apiKey) {
        this(apiKey, "https://api.alphafeed.io");
    }

    public AlphafeedSDK(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new ISO8601DateAdapter())
                .create();
    }

    public SignalsHistoricalDataResponse getHistoricalData(String dateFrom, String dateTo, String instrument,
                                                            Integer limit, Integer offset, Integer minScore,
                                                            Float minImportance, Float minSentiment) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/historical-data").newBuilder()
                .addQueryParameter("date_from", dateFrom)
                .addQueryParameter("date_to", dateTo);

        if (instrument != null) {
            urlBuilder.addQueryParameter("instrument", instrument);
        }
        
        if (limit != null) {
            urlBuilder.addQueryParameter("limit", String.valueOf(limit));
        }
        
        if (offset != null) {
            urlBuilder.addQueryParameter("offset", String.valueOf(offset));
        }
        
        if (minScore != null) {
            urlBuilder.addQueryParameter("min_score", String.valueOf(minScore));
        }
        
        if (minImportance != null) {
            urlBuilder.addQueryParameter("min_importance", String.valueOf(minImportance));
        }
        
        if (minSentiment != null) {
            urlBuilder.addQueryParameter("min_sentiment", String.valueOf(minSentiment));
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .header("X-API-KEY", apiKey)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, SignalsHistoricalDataResponse.class);
        }
    }

    public void subscribeToRealtime(NewsSignalListener listener, String instrument, 
                                 Integer minScore, Float minImportance, Float minSentiment) {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/realtime/ws").newBuilder()
                .addQueryParameter("api_key", apiKey);

        if (instrument != null) {
            urlBuilder.addQueryParameter("instrument", instrument);
        }
        
        if (minScore != null) {
            urlBuilder.addQueryParameter("min_score", String.valueOf(minScore));
        }
        
        if (minImportance != null) {
            urlBuilder.addQueryParameter("min_importance", String.valueOf(minImportance));
        }
        
        if (minSentiment != null) {
            urlBuilder.addQueryParameter("min_sentiment", String.valueOf(minSentiment));
        }

        String wsUrl = urlBuilder.build().toString().replace("http", "ws");
        webSocketClient = new AlphafeedWebSocketClient(wsUrl, listener);
        webSocketClient.connect();
    }

    public void unsubscribeFromRealtime() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
            webSocketClient = null;
        }
    }
}
