package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public abstract class Signal {
    protected Integer id;

    @SerializedName("signal_score")
    protected int signalScore;

    @SerializedName("signal_side")
    protected String signalSide;

    @SerializedName("signal_strength_score")
    protected Float signalStrengthScore;

    @SerializedName("signal_datetime")
    protected Date signalDatetime;

    @SerializedName("instrument_name")
    protected String instrumentName;

    @SerializedName("instrument_tickers")
    protected List<String> instrumentTickers;

    @SerializedName("instrument_id")
    protected Integer instrumentId;

    @SerializedName("comment")
    protected String comment;

    @SerializedName("reason_code")
    protected String reasonCode;

    public Integer getId() {
        return id;
    }

    public int getSignalScore() {
        return signalScore;
    }

    public String getSignalSide() {
        return signalSide;
    }

    public Float getSignalStrengthScore() {
        return signalStrengthScore;
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

    public String getComment() {
        return comment;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    protected String baseToString() {
        return "id=" + id +
                ", signalScore=" + signalScore +
                ", signalSide='" + signalSide + '\'' +
                ", signalStrengthScore=" + signalStrengthScore +
                ", signalDatetime=" + signalDatetime +
                ", instrumentName='" + instrumentName + '\'' +
                ", instrumentTickers=" + instrumentTickers +
                ", instrumentId=" + instrumentId +
                ", comment='" + comment + '\'' +
                ", reasonCode='" + reasonCode + '\'';
    }
}
