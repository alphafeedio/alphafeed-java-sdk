package com.alphafeed.io;

import com.alphafeed.io.model.NewsSignal;

public interface NewsSignalListener {
    void onSignal(NewsSignal signal);
    void onError(Throwable t);
    void onConnectionStateChange(boolean connected);
}
