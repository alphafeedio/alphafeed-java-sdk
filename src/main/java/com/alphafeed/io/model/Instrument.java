package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Instrument {
    private Integer id;
    private String name;
    private List<String> tickers;
    
    @SerializedName("instrument_categories")
    private List<String> instrumentCategories;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTickers() {
        return tickers;
    }

    public List<String> getInstrumentCategories() {
        return instrumentCategories;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tickers=" + tickers +
                ", instrumentCategories=" + instrumentCategories +
                '}';
    }
}
