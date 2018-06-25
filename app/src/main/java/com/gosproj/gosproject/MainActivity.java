package com.gosproj.gosproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    RecyclerView recyclerView;
    RVMainAdapter rvMainAdapter;

    ArrayList<MainCategory> mainCategories = new ArrayList<MainCategory>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        context = this;
        resources = getResources();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
        }).check();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));

        new NavigationDrawer(context, activity, toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("EVENTSSS", "MainActivity: onResume()");

        mainCategories.clear();

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, date, doroga, uchastok, vid_rabot FROM Departures WHERE isClose = 0", null);

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String doroga = cursor.getString(cursor.getColumnIndex("doroga"));
                String uchastok = cursor.getString(cursor.getColumnIndex("uchastok"));
                String vid_rabot = cursor.getString(cursor.getColumnIndex("vid_rabot"));

                String name = doroga + "\n" + uchastok + ", " + vid_rabot;

                boolean isSearch = false;

                for (int i=0; i<mainCategories.size(); i++)
                {
                    if (mainCategories.get(i).date.equals(date))
                    {
                        Log.d("myLOGC", id + " " + name);
                        mainCategories.get(i).secondaryCategories.add(new SecondaryCategory(id, name, date));
                        isSearch = true;
                    }
                }

                if (!isSearch)
                {
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

        if (rvMainAdapter != null)
        {
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
