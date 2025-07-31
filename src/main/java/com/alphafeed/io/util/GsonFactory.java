package com.alphafeed.io.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class GsonFactory {
    
    private static Gson instance;
    
    private GsonFactory() {
        // Utility class, no instantiation
    }
    
    public static Gson getGson() {
        if (instance == null) {
            instance = createGson();
        }
        return instance;
    }
    
    private static Gson createGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new ISO8601DateAdapter())
                .create();
    }
}
