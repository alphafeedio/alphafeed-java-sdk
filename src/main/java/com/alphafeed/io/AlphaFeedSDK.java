package com.alphafeed.io;

import com.alphafeed.io.model.EventSignal;
import com.alphafeed.io.model.InstrumentsResponse;
import com.alphafeed.io.model.NewsSignal;
import com.alphafeed.io.model.SignalsHistoricalDataResponse;
import com.alphafeed.io.util.GsonFactory;
import com.alphafeed.io.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
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

    public SignalsHistoricalDataResponse<NewsSignal> getNewsHistoricalData(Date dateFrom, Date dateTo, List<String> instrumentNames,
                                                                            List<Integer> instrumentIds, Integer limit, Integer offset, Integer minScore,
                                                                            Float minStrength, List<String> reasonCodes) throws IOException {
        HttpUrl.Builder urlBuilder = buildHistoricalDataUrl("/news-historical-data", dateFrom, dateTo, instrumentNames,
                instrumentIds, limit, offset, minScore, minStrength, reasonCodes);

        Request request = HttpUtils.createAuthenticatedRequestBuilder(urlBuilder.build().toString(), apiKey)
                .get()
                .build();

        Type responseType = TypeToken.getParameterized(SignalsHistoricalDataResponse.class, NewsSignal.class).getType();
        return executeRequest(request, responseType);
    }

    public SignalsHistoricalDataResponse<EventSignal> getEventsHistoricalData(Date dateFrom, Date dateTo, List<String> instrumentNames,
                                                                               List<Integer> instrumentIds, Integer limit, Integer offset, Integer minScore,
                                                                               Float minStrength, List<String> reasonCodes,
                                                                               List<String> eventSignalTypes, List<String> impact,
                                                                               List<String> currencyCodes) throws IOException {
        HttpUrl.Builder urlBuilder = buildHistoricalDataUrl("/events-historical-data", dateFrom, dateTo, instrumentNames,
                instrumentIds, limit, offset, minScore, minStrength, reasonCodes);
        HttpUtils.addEventFilterParameters(urlBuilder, eventSignalTypes, impact, currencyCodes);

        Request request = HttpUtils.createAuthenticatedRequestBuilder(urlBuilder.build().toString(), apiKey)
                .get()
                .build();

        Type responseType = TypeToken.getParameterized(SignalsHistoricalDataResponse.class, EventSignal.class).getType();
        return executeRequest(request, responseType);
    }

    private HttpUrl.Builder buildHistoricalDataUrl(String path, Date dateFrom, Date dateTo, List<String> instrumentNames,
                                                   List<Integer> instrumentIds, Integer limit, Integer offset, Integer minScore,
                                                   Float minStrength, List<String> reasonCodes) {
        String dateFromIso = dateFrom.toInstant().toString();
        String dateToIso = dateTo.toInstant().toString();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + path).newBuilder()
                .addQueryParameter("date_from", dateFromIso)
                .addQueryParameter("date_to", dateToIso);

        HttpUtils.addInstrumentParameters(urlBuilder, instrumentNames, instrumentIds);
        HttpUtils.addPaginationParameters(urlBuilder, limit, offset);
        HttpUtils.addFilterParameters(urlBuilder, minScore, minStrength, reasonCodes);

        return urlBuilder;
    }

    public InstrumentsResponse getInstruments() throws IOException {
        return getInstruments(null, null);
    }

    public InstrumentsResponse getInstruments(List<String> instrumentCategories, String keywords) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/instruments").newBuilder();

        if (instrumentCategories != null && !instrumentCategories.isEmpty()) {
            urlBuilder.addQueryParameter("instrument_categories", String.join(",", instrumentCategories));
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
                                    Integer minScore, Float minStrength, List<String> reasonCodes) {
        subscribeToRealtime(listener, instrumentNames, instrumentIds, minScore, minStrength, reasonCodes, 3, 5);
    }

    public void subscribeToRealtime(NewsSignalListener listener, List<String> instrumentNames, List<Integer> instrumentIds,
                                    Integer minScore, Float minStrength, List<String> reasonCodes,
                                    int maxReconnectAttempts, int reconnectIntervalSeconds) {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/realtime/ws").newBuilder()
                .addQueryParameter("api_key", apiKey);

        HttpUtils.addInstrumentParameters(urlBuilder, instrumentNames, instrumentIds);
        HttpUtils.addFilterParameters(urlBuilder, minScore, minStrength, reasonCodes);

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
        return executeRequest(request, (Type) responseType);
    }

    private <T> T executeRequest(Request request, Type responseType) throws IOException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseType);
        }
    }
}
