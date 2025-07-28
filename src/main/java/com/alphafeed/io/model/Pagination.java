package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

public class Pagination {
    private int total;
    private int limit;
    private int offset;
    
    @SerializedName("has_next")
    private boolean hasNext;
    
    @SerializedName("has_prev")
    private boolean hasPrev;

    public int getTotal() {
        return total;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "total=" + total +
                ", limit=" + limit +
                ", offset=" + offset +
                ", hasNext=" + hasNext +
                ", hasPrev=" + hasPrev +
                '}';
    }
}
