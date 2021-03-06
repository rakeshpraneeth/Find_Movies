package com.krp.findmovies.utilities;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// This class is used to perform the operations on the separate thread.
public final class FindMoviesExecutors {

    private static final Object LOCK = new Object();

    private static FindMoviesExecutors instance;

    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private FindMoviesExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static FindMoviesExecutors getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new FindMoviesExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(4),
                        new MainThreadExecutor());
            }
        }
        return instance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
