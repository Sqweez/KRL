package com.gosproj.gosproject.Functionals;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GPSHelper {
    Location loc = null;
    double[] latLong;
    Activity activity;
    Context context;
    FusedLocationProviderClient client;
    LocationManager locationManager;
    LocationListener locationListener;

    public GPSHelper(Activity activity, Context context){
        this.context = context;
        this.activity = activity;
    }

    public Location getCoordinates(){
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(activity);
        client.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            public void onSuccess(Location location) {
                if(location!= null){
                    loc = location;
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                }
                else{
                    Toast.makeText(activity, "LOCATION IS NULL", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return loc;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
