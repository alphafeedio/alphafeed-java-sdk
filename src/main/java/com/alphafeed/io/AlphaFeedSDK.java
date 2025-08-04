package com.alphafeed.io;

import com.alphafeed.io.model.InstrumentsResponse;
import com.alphafeed.io.model.SignalsHistoricalDataResponse;
import com.alphafeed.io.util.GsonFactory;
import com.alphafeed.io.util.HttpUtils;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AlphaFeedSDK {
    private final String apiKey;
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private AlphafeedWebSocketClient webSocketClient;

    public AlphaFeedSDK(String apiKey) {
        this(apiKey, "https://api.alphafeed.io/v1");
    }

    public AlphaFeedSDK(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.httpClient = HttpUtils.createHttpClient();
        this.gson = GsonFactory.getGson();
    }

    public SignalsHistoricalDataResponse getHistoricalData(Date dateFrom, Date dateTo, List<String> instrumentNames,
                                                           List<Integer> instrumentIds, Integer limit, Integer offset, Integer minScore,
                                                           Float minImportance, Float minSentiment) throws IOException {
        String dateFromIso = dateFrom.toInstant().toString();
        String dateToIso = dateTo.toInstant().toString();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/historical-data").newBuilder()
                .addQueryParameter("date_from", dateFromIso)
                .addQueryParameter("date_to", dateToIso);

        HttpUtils.addInstrumentParameters(urlBuilder, instrumentNames, instrumentIds);
        HttpUtils.addPaginationParameters(urlBuilder, limit, offset);
        HttpUtils.addFilterParameters(urlBuilder, minScore, minImportance, minSentiment);

        Request request = HttpUtils.createAuthenticatedRequestBuilder(urlBuilder.build().toString(), apiKey)
                .get()
                .build();

        return executeRequest(request, SignalsHistoricalDataResponse.class);
    }

    public InstrumentsResponse getInstruments() throws IOException {
        return getInstruments(null, null);
    }

    public InstrumentsResponse getInstruments(List<String> instrumentTypes, String keywords) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/instruments").newBuilder();

        if (instrumentTypes != null && !instrumentTypes.isEmpty()) {
            urlBuilder.addQueryParameter("instrument_types", String.join(",", instrumentTypes));
        }

        if (keywords != null && !keywords.trim().isEmpty()) {
            urlBuilder.addQueryParameter("keywords", keywords);
        }

        Request request = HttpUtils.createAuthenticatedRequestBuilder(urlBuilder.build().toString(), apiKey)
                .get()
                .build();

        return executeRequest(request, InstrumentsResponse.class);
    }

    public void subscribeToRealtime(NewsSignalListener listener, List<String> instrumentNames, List<Integer> instrumentIds,
                                    Integer minScore, Float minImportance, Float minSentiment) {
        subscribeToRealtime(listener, instrumentNames, instrumentIds, minScore, minImportance, minSentiment, 3, 5);
    }

    public void subscribeToRealtime(NewsSignalListener listener, List<String> instrumentNames, List<Integer> instrumentIds,
                                    Integer minScore, Float minImportance, Float minSentiment,
                                    int maxReconnectAttempts, int reconnectIntervalSeconds) {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/realtime/ws").newBuilder()
                .addQueryParameter("api_key", apiKey);

        HttpUtils.addInstrumentParameters(urlBuilder, instrumentNames, instrumentIds);
        HttpUtils.addFilterParameters(urlBuilder, minScore, minImportance, minSentiment);

        String wsUrl = urlBuilder.build().toString().replace("http", "ws");

        webSocketClient = new AlphafeedWebSocketClient(wsUrl, listener, maxReconnectAttempts, reconnectIntervalSeconds);
        webSocketClient.connect();
    }

    public void unsubscribeFromRealtime() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
            webSocketClient = null;
        }
    }

    private <T> T executeRequest(Request request, Class<T> responseType) throws IOException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseType);
        }
    }
}
