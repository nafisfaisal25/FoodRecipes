package com.example.foodrecipes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {
    private static AppExecutors instance;
    private final ScheduledExecutorService mNetworkIO = Executors.newScheduledThreadPool(3);

    public static AppExecutors getInstance() {
        if (instance == null) {
            instance =new AppExecutors();
        }
        return instance;
    }

    public ScheduledExecutorService getNetworkIO() {
        return mNetworkIO;
    }
}
