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
import com.gosproj.gosproject.Services.LogsHelper;

import java.util.HashMap;
import java.util.Map;

public class LoadDept extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;
    String name;
    String rgu;
    Toolbar toolbar;
    Map<Integer, String> vid_rabot = new HashMap<Integer, String>();
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
        vid_rabot.put(181, "Выезды с уполномоченными органами");
        vid_rabot.put(180, "Гарантийные осмотры");
        vid_rabot.put(11249, "Инструментальный осмотр в рамках СУДА");
        vid_rabot.put(176, "Капитальный ремонт");
        vid_rabot.put(175, "Реконструкция");
        vid_rabot.put(179, "Содержание, текущий ремонт");
        vid_rabot.put(177, "Средний ремонт");
        vid_rabot.put(687, "Средний ремонт методом холодного ресайклирования");
        vid_rabot.put(4983, "Строительство");
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setBeepEnabled(false);
        integrator.setPrompt(getResources().getString(R.string.text_qr_activity) + "\n\n\n\n");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            int i = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static boolean checkQR(String[] strings){
        if(strings.length == 14){
            if(isNumeric(strings[0]) && isNumeric(strings[1]) && isNumeric(strings[4])){
                return true;
            }
        }
        return false;
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
            else {
                String rQR = result.getContents();
                String[] strings = rQR.split("\\|");
                if (checkQR(strings)) {

                    for (int i = 0; i < strings.length; i++) {
                        Log.d("DEXSTER", strings[i]);
                    }
                    DBHelper dbHelper1 = new DBHelper(context, DBHelper.Users);
                    SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
                    Cursor cursor1 = db1.rawQuery("SELECT id, site_id, name, rgu_name, rgu_id FROM Users", null);
                    if (cursor1.moveToFirst()) {
                        name = cursor1.getString(cursor1.getColumnIndex("name"));
                        rgu = cursor1.getString(cursor1.getColumnIndex("rgu_name"));
                    }
                    dbHelper1.close();
                    db1.close();

                    DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.DEPARTURE);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    Cursor cursor = db.rawQuery("SELECT * FROM Departures WHERE idAct= ? AND idNomer = ? AND date = ? AND object = ? AND id_rabot = ? AND vid_rabot = ? AND ispolnitel = ? AND gruppa_vyezda1 = ? AND gruppa_vyezda2 = ? AND gruppa_vyezda3 = ? AND podradchyk = ?" +
                                    "AND subpodradchyk = ? AND avt_nadzor = ? AND zakazchik = ? AND inj_sluzhby = ? AND rgu_name = ? AND uorg = ?",
                            strings);

                    if (!cursor.moveToFirst()) {
                        ContentValues cv = new ContentValues();

                        cv.put("idAct", strings[0]);
                        cv.put("idNomer", Integer.parseInt(strings[1]));
                        cv.put("object", strings[2]);
                        cv.put("date", strings[3]);
                        cv.put("id_rabot", strings[4]);
                        cv.put("vid_rabot", vid_rabot.get(Integer.parseInt(strings[4])));
                        cv.put("gruppa_vyezda1", strings[5]);
                        cv.put("gruppa_vyezda2", strings[6]);
                        cv.put("gruppa_vyezda3", strings[7]);
                        cv.put("ispolnitel", name);
                        cv.put("rgu_name", rgu);
                        cv.put("zakazchik", strings[8]);
                        cv.put("inj_sluzhby", strings[9]);
                        cv.put("podradchyk", strings[10]);
                        cv.put("subpodradchyk", strings[11]);
                        cv.put("avt_nadzor", strings[12]);
                        cv.put("uorg", strings[13]);
                        cv.put("isClose", "0");

                        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

                        if (rowID != 0) {
                            LogsHelper logsHelper = new LogsHelper(LogsHelper.DEPARTURE, context, activity, (int)rowID);
                            logsHelper.createLog(strings[1], "", LogsHelper.ACTION_ADD);

                            Toast.makeText(this, getResources().getString(R.string.load_new_departure), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.fail_load_new_departure), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.fail_load_new_departure_is_true), Toast.LENGTH_LONG).show();
                    }

                    db.close();
                    dbHelper.close();
                    cursor.close();


                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);

                    finish();

                }
                else{
                    Toast.makeText(activity, "Отсканирован некорректный QR-код", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
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
