package com.gosproj.gosproject.Services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.GPSHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LogsHelper extends Service {
    public String action;
    public String item;
    FusedLocationProviderClient client;
    Context context;
    Activity activity;
    public static String MEAS = "Meas";
    public static String PROBA = "Proba";
    public static String DEFECT = "Defect";
    public static String AGENT = "Agent";
    public static String SIGNATURE = "Signature";
    public static String PHOTO = "Photo";
    public static String VIDEO = "Video";
    public static int ACTION_ADD = 0;
    public static int ACTION_EDIT = 1;
    public static int ACTION_DELETE = 2;
    int idDept;
    public LogsHelper(String item, Context context, Activity activity, int idDept) {
        this.item = item;
        this.context = context;
        this.activity = activity;
        this.idDept = idDept;
    }

    public void onCreate() {
        super.onCreate();
    }

    public String getDateTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String dateTime = sdf.format(date) + " " + stf.format(date);
        return dateTime;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createLog(final String old_item, final String new_item, final int action) {
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnCompleteListener(activity, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                switch (item) {
                    case "Meas":
                        DBHelper dbHelperM = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbM = dbHelperM.getWritableDatabase();
                        ContentValues cvM = new ContentValues();
                        if (action == ACTION_EDIT) {
                            cvM.put("log_text", getDateTime() + ": Замер изменен со значения '" + old_item
                                    + "' на значение '" + new_item + "'");
                        } else if (action == ACTION_ADD) {
                            cvM.put("log_text", getDateTime() + ": Добавлен замер '" + old_item + "'");
                        } else if (action == ACTION_DELETE) {
                            cvM.put("log_text", getDateTime() + ": Удален замер '" + old_item + "'");
                        }
                        if (task.isSuccessful() && task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lon = task.getResult().getLongitude();
                            cvM.put("lat", lat);
                            cvM.put("long", lon);
                        }
                        cvM.put("idDept", idDept);
                        dbM.insert(dbHelperM.getDatabaseName(), null, cvM);
                        dbHelperM.close();
                        dbM.close();
                        break;
                    case "Proba":
                        String[] oldProba = old_item.split("\\|");
                        String[] newProba = new_item.split("\\|");
                        DBHelper dbHelperP = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbP = dbHelperP.getWritableDatabase();
                        ContentValues cvP = new ContentValues();
                        if (action == ACTION_EDIT) {
                            String msg = getDateTime() +
                                    ": Проба ДСМ: " + oldProba[0] +
                                    ", количество: " + oldProba[1] +
                                    ", место отбора: " + oldProba[3] +
                                    ", поставщик: " + oldProba[2] +
                                    ", для вида работ " + oldProba[4] +
                                    " ИЗМЕНЕНА на Проба ДСМ: " + newProba[0] +
                                    ", количество: " + newProba[1] +
                                    ", место отбора: " + newProba[3] +
                                    ", поставщик: " + newProba[2] +
                                    ", для вида работ " + newProba[4];
                            Log.d("log_proba", msg);
                            cvP.put("log_text", msg );
                        }
                        else if(action == ACTION_ADD){
                            String msg = getDateTime() + ": " +
                                "Добавлена проба ДСМ: " + newProba[0] +
                                ", количество: " + newProba[1] +
                                ", место отбора: " + newProba[3] +
                                ", поставщик: " + newProba[2] +
                                ", для вида работ " + newProba[4];
                            Log.d("log_proba", msg);
                            cvP.put("log_text", msg);
                        }
                        else if(action == ACTION_DELETE){
                            String msg = getDateTime() + ": " +
                                    getDateTime() + ": " +
                                    "Удалена проба ДСМ: " + oldProba[0] +
                                    ", количество: " + oldProba[1] +
                                    ", место отбора: " + oldProba[3] +
                                    ", поставщик: " + oldProba[2] +
                                    ", для вида работ " + oldProba[4];
                            Log.d("log_proba", msg);
                            cvP.put("log_text", msg);
                        }
                        if (task.isSuccessful() && task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lon = task.getResult().getLongitude();
                            cvP.put("lat", lat);
                            cvP.put("long", lon);
                        }
                        cvP.put("idDept", idDept);
                        dbP.insert(dbHelperP.getDatabaseName(), null, cvP);
                        dbHelperP.close();
                        dbP.close();
                        break;
                    case "Agent":
                        String[] oldAgent = old_item.split("\\|");
                        String[] newAgent = new_item.split("\\|");
                        DBHelper dbHelperA = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbA = dbHelperA.getWritableDatabase();
                        ContentValues cvA = new ContentValues();
                        if(action == ACTION_EDIT){
                            String msg = getDateTime() + ": Изменен присутствующий представитель " +
                                    "'" + oldAgent[0] + "', являющийся " + oldAgent[3] + " , "
                                    + oldAgent[2] + ", " + oldAgent[1] + "на '" +
                                    newAgent[0] + "', являющийся " + newAgent[3] + ", "
                                    + newAgent[2] + ", " + newAgent[1];
                            cvA.put("log_text", msg);
                        }
                        else if(action == ACTION_ADD){
                            String msg = getDateTime() + ": " +
                                    "Добавлен присутствующий представитель " +
                                    "'" + newAgent[0] + "', являющийся " + newAgent[3] + " , "
                                    + newAgent[2] + ", " + newAgent[1];
                            cvA.put("log_text", msg);
                        }
                        else if(action == ACTION_DELETE){
                            String msg = getDateTime() + ": " +
                                    "Удален присутствующий представитель " +
                                    "'" + oldAgent[0] + "', являющийся " + oldAgent[3] + " , "
                                    + oldAgent[2] + ", " + oldAgent[1];
                            cvA.put("log_text", msg);
                        }
                        if (task.isSuccessful() && task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lon = task.getResult().getLongitude();
                            cvA.put("lat", lat);
                            cvA.put("long", lon);
                        }
                        cvA.put("idDept", idDept);
                        dbA.insert(dbHelperA.getDatabaseName(), null, cvA);
                        dbHelperA.close();
                        dbA.close();
                        break;
                    case "Defect":
                        DBHelper dbHelperD = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbD = dbHelperD.getWritableDatabase();
                        ContentValues cvD = new ContentValues();
                        if(action == ACTION_EDIT){
                            String msg = getDateTime() + ": дефект '" + old_item + "' ИЗМЕНЕН на '" +
                                    new_item;
                            cvD.put("log_text", msg);
                        }
                        else if(action == ACTION_ADD){
                            String msg = getDateTime() + ": добавлен дефект '" + new_item + "'";
                            cvD.put("log_text", msg);
                        }
                        else if(action == ACTION_DELETE){
                            String msg = getDateTime() + ": удален дефект '" + old_item + "'";
                            cvD.put("log_text", msg);
                        }
                        if (task.isSuccessful() && task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lon = task.getResult().getLongitude();
                            cvD.put("lat", lat);
                            cvD.put("long", lon);
                        }
                        cvD.put("idDept", idDept);
                        dbD.insert(dbHelperD.getDatabaseName(), null, cvD);
                        dbHelperD.close();
                        dbD.close();
                        break;
                    case "Signature":
                        String[] oldAgentS = old_item.split("\\|");
                        DBHelper dbHelperS = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbS = dbHelperS.getWritableDatabase();
                        ContentValues cvS = new ContentValues();
                        if(action == ACTION_ADD){
                            String msg = getDateTime() + ": добавлена подпись присутствующего представителя " +
                                    "'" + oldAgentS[0] + "', являющегося " + oldAgentS[3] + " , "
                                    + oldAgentS[2] + ", " + oldAgentS[1];
                            cvS.put("log_text", msg);
                        }
                        if (task.isSuccessful() && task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lon = task.getResult().getLongitude();
                            cvS.put("lat", lat);
                            cvS.put("long", lon);
                        }
                        cvS.put("idDept", idDept);
                        dbS.insert(dbHelperS.getDatabaseName(), null, cvS);
                        dbHelperS.close();
                        dbS.close();
                        break;
                    case "Photo":
                        DBHelper dbHelperPh = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbPh = dbHelperPh.getWritableDatabase();
                        ContentValues cvPh = new ContentValues();
                        if(action == ACTION_ADD){
                            String msg = getDateTime() + ": ДОБАВЛЕНА фотография";
                            cvPh.put("log_text", msg);
                        }
                        else if(action == ACTION_DELETE){
                            String msg = getDateTime() + ": УДАЛЕНА фотография";
                            cvPh.put("log_text", msg);
                        }
                        if (task.isSuccessful() && task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lon = task.getResult().getLongitude();
                            cvPh.put("lat", lat);
                            cvPh.put("long", lon);
                        }
                        cvPh.put("idDept", idDept);
                        dbPh.insert(dbHelperPh.getDatabaseName(), null, cvPh);
                        dbHelperPh.close();
                        dbPh.close();
                        break;
                    case "Video":
                        DBHelper dbHelperV = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbV = dbHelperV.getWritableDatabase();
                        ContentValues cvV = new ContentValues();
                        if(action == ACTION_ADD){
                            String msg = getDateTime() + ": ДОБАВЛЕНО видео";
                            cvV.put("log_text", msg);
                        }
                        else if(action == ACTION_DELETE){
                            String msg = getDateTime() + ": УДАЛЕНО видео";
                            cvV.put("log_text", msg);
                        }
                        if (task.isSuccessful() && task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lon = task.getResult().getLongitude();
                            cvV.put("lat", lat);
                            cvV.put("long", lon);
                        }
                        cvV.put("idDept", idDept);
                        dbV.insert(dbHelperV.getDatabaseName(), null, cvV);
                        dbHelperV.close();
                        dbV.close();
                        break;
                    default:
                        break;

                }
            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
