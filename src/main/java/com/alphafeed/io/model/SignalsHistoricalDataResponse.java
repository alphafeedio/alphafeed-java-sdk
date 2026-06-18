package com.alphafeed.io.model;

import java.util.List;

public class SignalsHistoricalDataResponse<T extends Signal> {
    private List<T> data;
    private Pagination pagination;

    public List<T> getData() {
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
