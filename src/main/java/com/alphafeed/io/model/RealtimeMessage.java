package com.alphafeed.io.model;

import com.google.gson.annotations.SerializedName;

public class RealtimeMessage {
    private String type;
    private NewsSignal data;

    public String getType() {
        return type;
    }

    public NewsSignal getData() {
        return data;
    }

    @Override
    public String toString() {
        return "RealtimeMessage{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
