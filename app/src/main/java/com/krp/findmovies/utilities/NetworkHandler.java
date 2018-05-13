package com.krp.findmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;

public final class NetworkHandler {

    private NetworkHandler() {
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
