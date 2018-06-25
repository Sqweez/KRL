package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.NavigationDrawer;

public class LoadDept extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_dept);

        activity = this;
        context = this;
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));

        new NavigationDrawer(context, activity, toolbar);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getResources().getString(R.string.text_qr_activity) + "\n\n\n\n");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
        {
            if(result.getContents() == null)
            {
                Toast.makeText(this, getResources().getString(R.string.fail_load_new_departure), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                finish();
            }
            else
            {
                String rQR = result.getContents();
                String[] strings = rQR.split("\\|");

                for (int i=0; i<strings.length; i++)
                {
                    Log.d("DEXSTER", strings[i]);
                }

                DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.DEPARTURE);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM Departures WHERE idAct= ? AND date = ? AND doroga = ? AND uchastok = ? AND vid_rabot = ? AND rgu = ? AND ispolnitel = ? AND gruppa_vyezda = ? AND podradchyk = ?" +
                                "AND customer = ?",
                                            strings);

                if (!cursor.moveToFirst())
                {
                    ContentValues cv = new ContentValues();

                    cv.put("idAct", strings[0]);
                    cv.put("date", strings[1]);
                    cv.put("doroga", strings[2]);
                    cv.put("uchastok", strings[3]);
                    cv.put("vid_rabot", strings[4]);
                    cv.put("rgu", strings[5]);
                    cv.put("ispolnitel", strings[6]);
                    cv.put("gruppa_vyezda", strings[7]);
                    cv.put("podradchyk", strings[8]);
                    cv.put("customer", strings[9]);
                    cv.put("isClose", "0");

                    long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

                    if (rowID != 0)
                    {
                        Toast.makeText(this, getResources().getString(R.string.load_new_departure), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(this, getResources().getString(R.string.fail_load_new_departure), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(this, getResources().getString(R.string.fail_load_new_departure_is_true), Toast.LENGTH_LONG).show();
                }

                db.close();
                dbHelper.close();
                cursor.close();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);

            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
