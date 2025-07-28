package com.alphafeed.io.model;

import java.util.List;

public class SignalsHistoricalDataResponse {
    private List<NewsSignal> data;
    private Pagination pagination;

    public List<NewsSignal> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
    
    @Override
    public String toString() {
        return "SignalsHistoricalDataResponse{" +
                "data=" + data +
                ", pagination=" + pagination +
                '}';
    }
}
