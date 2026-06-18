package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class EventSignal extends Signal {
    @SerializedName("event_name")
    private String eventName;

    @SerializedName("event_datetime")
    private Date eventDatetime;

    @SerializedName("currency_code")
    private String currencyCode;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("impact")
    private String impact;

    @SerializedName("previous_value")
    private String previousValue;

    @SerializedName("forecast_value")
    private String forecastValue;

    @SerializedName("category")
    private String category;

    @SerializedName("event_site")
    private String eventSite;

    public String getEventName() {
        return eventName;
    }

    public Date getEventDatetime() {
        return eventDatetime;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getImpact() {
        return impact;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getForecastValue() {
        return forecastValue;
    }

    public String getCategory() {
        return category;
    }

    public String getEventSite() {
        return eventSite;
    }

    @Override
    public String toString() {
        return "EventSignal{" + baseToString() +
                ", eventName='" + eventName + '\'' +
                ", eventDatetime=" + eventDatetime +
                ", currencyCode='" + currencyCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", impact='" + impact + '\'' +
                ", previousValue='" + previousValue + '\'' +
                ", forecastValue='" + forecastValue + '\'' +
                ", category='" + category + '\'' +
                ", eventSite='" + eventSite + '\'' +
                '}';
    }
}
