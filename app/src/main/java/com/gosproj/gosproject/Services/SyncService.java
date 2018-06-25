package com.gosproj.gosproject.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SyncService extends IntentService
{
    public SyncService(String name)
    {
        super(name);
    }

    public void onCreate() {
        super.onCreate();
        Log.d("SyncService", "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        boolean isNetworkConnected = extras.getBoolean("isNetworkConnected");

        if (isNetworkConnected)
        {

        }
        else
        {

        }

        Log.d("SyncService", "Result" + isNetworkConnected);
    }
}
