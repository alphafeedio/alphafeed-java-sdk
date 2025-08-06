package com.alphafeed.io;

import com.alphafeed.io.model.NewsSignal;

public interface NewsSignalListener {
    void onSignal(NewsSignal signal);
    void onError(Throwable t);
    default void onConnectionStateChange(boolean connected) {
        onConnectionStateChange(connected, "");
    }
    void onConnectionStateChange(boolean connected, String message);
}
