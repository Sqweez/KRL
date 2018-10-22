package com.gosproj.gosproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.location.LocationListener;
import com.gosproj.gosproject.Adapters.RVMainAdapter;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.NavigationDrawer;
import com.gosproj.gosproject.Services.SyncService;
import com.gosproj.gosproject.Structures.MainCategory;
import com.gosproj.gosproject.Structures.SecondaryCategory;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    Activity activity;
    Context context;
    Resources resources;
    Toolbar toolbar;
    FloatingActionButton loadScan;
    RecyclerView recyclerView;
    RVMainAdapter rvMainAdapter;
    SharedPreferences pref = null;
    ArrayList<MainCategory> mainCategories = new ArrayList<MainCategory>();

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        context = this;
        resources = getResources();
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            }
        }).check();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
        pref = getSharedPreferences("com.gosproj.gosproject", MODE_PRIVATE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));

        new NavigationDrawer(context, activity, toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        FloatingActionButton loadActs = (FloatingActionButton) findViewById(R.id.loadActs);
        loadScan = (FloatingActionButton) findViewById(R.id.loadScan);
        loadActs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), LoadDept.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                activity.finish();
            }
        });
        loadScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), QRForScanning.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                activity.finish();
            }
        });
        rvMainAdapter = new RVMainAdapter(activity, mainCategories);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvMainAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("EVENTSSS", "MainActivity: onStart()");
    }


    private void makeAuth() {
        DBHelper dbHelper = new DBHelper(context, DBHelper.Users);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, site_id, name, rgu_name, rgu_id FROM Users", null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String rgu_name = cursor.getString(cursor.getColumnIndex("rgu_name"));
            dbHelper.close();
            db.close();
        } else {
            Intent intent = new Intent(context, InstructionActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (pref.getBoolean("firstrun", true)) {
            DBHelper dbHelper = new DBHelper(context, DBHelper.Users);
            dbHelper.close();
            pref.edit().putBoolean("firstrun", false).commit();
        }
        makeAuth();
        Log.d("EVENTSSS", "MainActivity: onResume()");
        mainCategories.clear();

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, date, object, vid_rabot FROM Departures WHERE isClose = 0", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String object = cursor.getString(cursor.getColumnIndex("object"));
                String vid_rabot = cursor.getString(cursor.getColumnIndex("vid_rabot"));

                String name = object + "\n" + vid_rabot;

                boolean isSearch = false;

                for (int i = 0; i < mainCategories.size(); i++) {
                    if (mainCategories.get(i).date.equals(date)) {
                        Log.d("myLOGC", id + " " + name);
                        mainCategories.get(i).secondaryCategories.add(new SecondaryCategory(id, name, date));
                        isSearch = true;
                    }
                }

                if (!isSearch) {
                    Log.d("myLOGCS", id + " " + name);
                    ArrayList<SecondaryCategory> secondaryCategories = new ArrayList<SecondaryCategory>();
                    secondaryCategories.add(new SecondaryCategory(id, name, date));

                    MainCategory mainCategory = new MainCategory(resources.getString(R.string.departuresIn) + " " + date,
                            date, secondaryCategories);

                    mainCategories.add(mainCategory);
                }
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        if (rvMainAdapter != null) {
            rvMainAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("EVENTSSS", "MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("EVENTSSS", "MainActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("EVENTSSS", "MainActivity: onDestroy()");
    }
}
