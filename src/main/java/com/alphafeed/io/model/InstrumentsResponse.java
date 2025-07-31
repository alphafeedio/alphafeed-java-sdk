package com.alphafeed.io.model;

import java.util.List;

public class InstrumentsResponse {
    private List<Instrument> data;
    private Integer total;

    public List<Instrument> getData() {
        return data;
    }

    public Integer getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "InstrumentsResponse{" +
                "data=" + data +
                ", total=" + total +
                '}';
    }
}
