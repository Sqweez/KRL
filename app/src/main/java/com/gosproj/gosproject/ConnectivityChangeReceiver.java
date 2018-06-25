package com.gosproj.gosproject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.gosproj.gosproject.Services.SyncService;

public class ConnectivityChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("MyReceiver", "onReceive");

        ComponentName comp = new ComponentName(context.getPackageName(),
                SyncService.class.getName());
        intent.putExtra("isNetworkConnected", isConnected(context));
        context.startService(intent.setComponent(comp));
    }

    public  boolean isConnected(Context context)
    {
        Log.d("RECIVE","IsConnect");

        ConnectivityManager connectivityManager = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
