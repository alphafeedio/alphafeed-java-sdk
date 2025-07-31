package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Instrument {
    private Integer id;
    private String name;
    private List<String> tickers;
    
    @SerializedName("instrument_type")
    private String instrumentType;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTickers() {
        return tickers;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tickers=" + tickers +
                ", instrumentType='" + instrumentType + '\'' +
                '}';
    }
}
