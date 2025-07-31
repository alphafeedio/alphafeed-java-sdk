package com.alphafeed.io.util;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    
    private HttpUtils() {
        // Utility class, no instantiation
    }
    
    public static OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    public static OkHttpClient createWebSocketHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // Disable timeouts for WebSockets
                .build();
    }
    
    public static Request.Builder createAuthenticatedRequestBuilder(String url, String apiKey) {
        return new Request.Builder()
                .url(url)
                .header("X-API-KEY", apiKey);
    }
    
    public static void addInstrumentParameters(HttpUrl.Builder urlBuilder, 
                                               List<String> instrumentNames, 
                                               List<Integer> instrumentIds) {
        if (instrumentNames != null && !instrumentNames.isEmpty()) {
            urlBuilder.addQueryParameter("instrument_names", String.join(",", instrumentNames));
        }

        if (instrumentIds != null && !instrumentIds.isEmpty()) {
            urlBuilder.addQueryParameter("instrument_ids", instrumentIds.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + "," + b)
                    .orElse(""));
        }
    }
    
    public static void addFilterParameters(HttpUrl.Builder urlBuilder, 
                                          Integer minScore, 
                                          Float minImportance, 
                                          Float minSentiment) {
        if (minScore != null) {
            urlBuilder.addQueryParameter("min_score", String.valueOf(minScore));
        }

        if (minImportance != null) {
            urlBuilder.addQueryParameter("min_importance", String.valueOf(minImportance));
        }

        if (minSentiment != null) {
            urlBuilder.addQueryParameter("min_sentiment", String.valueOf(minSentiment));
        }
    }
    
    public static void addPaginationParameters(HttpUrl.Builder urlBuilder, Integer limit, Integer offset) {
        if (limit != null) {
            urlBuilder.addQueryParameter("limit", String.valueOf(limit));
        }

        if (offset != null) {
            urlBuilder.addQueryParameter("offset", String.valueOf(offset));
        }
    }
}
