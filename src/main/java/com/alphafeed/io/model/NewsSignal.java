package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NewsSignal extends Signal {
    @SerializedName("news_title")
    private String newsTitle;

    @SerializedName("news_datetime")
    private Date newsDatetime;

    @SerializedName("news_site")
    private String newsSite;

    @SerializedName("news_url")
    private String newsUrl;

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
        return "NewsSignal{" + baseToString() +
                ", newsTitle='" + newsTitle + '\'' +
                ", newsDatetime=" + newsDatetime +
                ", newsSite='" + newsSite + '\'' +
                ", newsUrl='" + newsUrl + '\'' +
                '}';
    }
}
