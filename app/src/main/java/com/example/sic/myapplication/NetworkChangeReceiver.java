package com.example.sic.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sic.myapplication.Activity.MainActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {
    String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (checkInternet(context)) {
            MainActivity.getNews();
        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }

}