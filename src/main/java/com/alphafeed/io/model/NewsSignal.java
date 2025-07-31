package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class NewsSignal {
    private Integer id;

    @SerializedName("signal_score")
    private int signalScore;

    @SerializedName("signal_side")
    private String signalSide;

    @SerializedName("signal_importance_score")
    private float signalImportanceScore;

    @SerializedName("signal_sentiments_score")
    private float signalSentimentsScore;

    @SerializedName("signal_datetime")
    private Date signalDatetime;

    @SerializedName("instrument_name")
    private String instrumentName;

    @SerializedName("instrument_tickers")
    private List<String> instrumentTickers;

    @SerializedName("instrument_id")
    private Integer instrumentId;

    @SerializedName("news_title")
    private String newsTitle;

    @SerializedName("news_datetime")
    private Date newsDatetime;

    @SerializedName("news_site")
    private String newsSite;

    @SerializedName("news_url")
    private String newsUrl;

    public Integer getId() {
        return id;
    }

    public int getSignalScore() {
        return signalScore;
    }

    public String getSignalSide() {
        return signalSide;
    }

    public float getSignalImportanceScore() {
        return signalImportanceScore;
    }

    public float getSignalSentimentsScore() {
        return signalSentimentsScore;
    }

    public Date getSignalDatetime() {
        return signalDatetime;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public List<String> getInstrumentTickers() {
        return instrumentTickers;
    }

    public Integer getInstrumentId() {
        return instrumentId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public Date getNewsDatetime() {
        return newsDatetime;
    }

    public String getNewsSite() {
        return newsSite;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    @Override
    public String toString() {
        return "NewsSignal{" +
                "id=" + id +
                ", signalScore=" + signalScore +
                ", signalSide='" + signalSide + '\'' +
                ", signalImportanceScore=" + signalImportanceScore +
                ", signalSentimentsScore=" + signalSentimentsScore +
                ", signalDatetime=" + signalDatetime +
                ", instrumentName='" + instrumentName + '\'' +
                ", instrumentTickers=" + instrumentTickers +
                ", instrumentId=" + instrumentId +
                ", newsTitle='" + newsTitle + '\'' +
                ", newsDatetime=" + newsDatetime +
                ", newsSite='" + newsSite + '\'' +
                ", newsUrl='" + newsUrl + '\'' +
                '}';
    }
}
