package com.gosproj.gosproject.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.NavigationDrawer;
import com.gosproj.gosproject.Functionals.ServerApi;
import com.gosproj.gosproject.MainActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Act;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.LogStruct;
import com.gosproj.gosproject.Structures.Measurment;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Proba;
import com.gosproj.gosproject.Structures.Videos;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CreateAndLoadService extends Service
{
    final String LOG_TAG = "ServiceLoad";

    private static final Random random = new Random();
    private static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ";

    Resources resources;
    Activity activity;
    Context context;

    SharedPreferences sharedPref;

    int actID;
    int id;
    int isNew;
    NotificationManager nm;

    public CreateAndLoadService()
    {
        super();
    }

    public void onCreate()
    {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        resources = getResources();
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LOG_TAG, "onStartCommand");
        id = intent.getIntExtra("id", 0);
        isNew = intent.getIntExtra("isNew", 0);
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy()
    {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void someTask()
    {
        Log.d("ARCHIVEEE", "START");

        CreateArhive createArhive = new CreateArhive(context);
        createArhive.execute();
    }

    public String onClose()
    {
        String pathActInfo = createActFile(id);
        String pathProbInfo = createProbFile(id);
        String pathDefectsInfo = createDefectFile(id);
        String pathAgentInfo = createAgentFile(id);
        String pathPhotos = createPhotoFile(id);
        String pathVideos = createVideoFile(id);
        String pathLogs = createLogFile(id);
        String pathZamety = createZamerFile(id);

        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/archives/");
        sdPath.mkdir();

        File rootFolder = Environment.getExternalStorageDirectory();
        rootFolder = new File(rootFolder.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id) + "");
        rootFolder.mkdirs();
        String token = getToken(3);
        rootFolder.renameTo(new File(rootFolder.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+token));
        try
        {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String date = df.format(Calendar.getInstance().getTime());

            String pathZip = sdPath.getAbsolutePath() + "/"+String.valueOf(actID)+"-"+date+"-"+token+".zip";

            ZipFile zipFile = new ZipFile(pathZip);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            parameters.setEncryptFiles(true);

            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

            parameters.setPassword("123");
            parameters.setIncludeRootFolder(false);
            zipFile.createZipFileFromFolder(rootFolder, parameters, false, 0);
            deleteRecursive(rootFolder);

            return pathZip;
        }
        catch (ZipException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public void stopService()
    {
        this.stopSelf();
    }

    public static String getToken(int length)
    {
        StringBuilder token = new StringBuilder(length);

        for (int i = 0; i < length; i++)
        {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public String createZamerFile(int id){
        ArrayList<Measurment>zamery = new ArrayList<Measurment>();
        DBHelper dbHelper = new DBHelper(context, DBHelper.MEASUREMENTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Measurements WHERE idDept = ?", new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            do{
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                zamery.add(new Measurment(ids, idDept, name));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        dbHelper.close();

        JSONArray zamer = new JSONArray();

        try
        {
            for (int i=0; i<zamery.size(); i++)
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", zamery.get(i).name);
                zamer.put(jsonObject);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();

            return "";
        }

        if(!zamer.toString().equals("")){
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id));
            sdPath.mkdirs();
            File sdFile = new File(sdPath, "info_zamery.ini");
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(zamer.toString());
                bw.close();

                return sdFile.getAbsolutePath();
            }catch (IOException e){
                e.printStackTrace();
                return "";
            }
        }
        else{
            return "";
        }

    }
    public String createActFile(int id)
    {
        Act act = new Act();

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Departures WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            act.id = cursor.getInt(cursor.getColumnIndex("id"));
            act.idAct = cursor.getInt(cursor.getColumnIndex("idAct"));
            act.idNomer = cursor.getInt(cursor.getColumnIndex("idNomer"));
            act.date = cursor.getString(cursor.getColumnIndex("date"));
            act.object = cursor.getString(cursor.getColumnIndex("object"));
            act.id_rabot = cursor.getInt(cursor.getColumnIndex("id_rabot"));
            act.vid_rabot = cursor.getString(cursor.getColumnIndex("vid_rabot"));
            act.ispolnitel = cursor.getString(cursor.getColumnIndex("ispolnitel"));
            act.gruppa_vyezda1 = cursor.getString(cursor.getColumnIndex("gruppa_vyezda1"));
            act.gruppa_vyezda2 = cursor.getString(cursor.getColumnIndex("gruppa_vyezda2"));
            act.gruppa_vyezda3 = cursor.getString(cursor.getColumnIndex("gruppa_vyezda3"));
            act.podradchyk = cursor.getString(cursor.getColumnIndex("podradchyk"));
            act.subpodradchyk = cursor.getString(cursor.getColumnIndex("subpodradchyk"));
            act.avt_nadzor = cursor.getString(cursor.getColumnIndex("avt_nadzor"));
            act.inj_sluzhby = cursor.getString(cursor.getColumnIndex("inj_sluzhby"));
            act.uorg = cursor.getString(cursor.getColumnIndex("uorg"));
            act.zakazchik = cursor.getString(cursor.getColumnIndex("zakazchik"));
            act.rgu = cursor.getString(cursor.getColumnIndex("rgu_name"));

            actID = act.idAct;
        }

        cursor.close();
        db.close();
        dbHelper.close();

        JSONObject obj = new JSONObject();

        if(isNew == 0){
            try
            {
                obj.put("idAct", act.idAct);
                obj.put("date", act.date);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else{
            SharedPreferences sf = context.getSharedPreferences("com.gosproj.gosproject", Context.MODE_PRIVATE);
            int user_id = sf.getInt("user_id", 0);
            int rgu_id = sf.getInt("rgu_id", 0);
            String rguID = String.valueOf(rgu_id);
            if(rgu_id < 10){
                rguID = "0" + rgu_id;
            }
            try
            {
                obj.put("user_id", user_id);
                obj.put("rgu_id", rguID);
                obj.put("date", act.date);
                obj.put("object", act.object);
                obj.put("id_rabot", act.id_rabot);
                if(!act.gruppa_vyezda1.equals(""))
                obj.put("gv_1", act.gruppa_vyezda1);
                if(!act.gruppa_vyezda2.equals(""))
                obj.put("gv_2", act.gruppa_vyezda2);
                if(!act.gruppa_vyezda3.equals(""))
                obj.put("gv_3", act.gruppa_vyezda3);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if (!obj.toString().equals(""))
        {
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id));
            sdPath.mkdirs();
            File sdFile = new File(sdPath, "info_act.ini");
            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(obj.toString());
                bw.close();
                Log.d("myLog", "Файл записан на SD: " + sdFile.getAbsolutePath());

                return sdFile.getAbsolutePath();
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return "";
            }
        }
        else
        {
            return "";
        }
    }

    public String createProbFile(int id)
    {
        ArrayList<Proba> probas = new ArrayList<Proba>();

        DBHelper dbHelper = new DBHelper(context, DBHelper.PROBS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.PROBS + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String place = cursor.getString(cursor.getColumnIndex("place"));
                String provider = cursor.getString(cursor.getColumnIndex("provider"));
                String typeWork = cursor.getString(cursor.getColumnIndex("typeWork"));

                probas.add(new Proba(ids, idDept, name, size, place, provider, typeWork));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        JSONArray probs = new JSONArray();

        try
        {
            for (int i=0; i<probas.size(); i++)
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", probas.get(i).name);
                jsonObject.put("size", probas.get(i).size);
                jsonObject.put("place", probas.get(i).place);
                jsonObject.put("provider", probas.get(i).provider);
                jsonObject.put("typeWork", probas.get(i).typeWork);

                probs.put(jsonObject);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();

            return "";
        }

        if (!probs.toString().equals(""))
        {
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id));
            sdPath.mkdirs();
            File sdFile = new File(sdPath, "info_probs.ini");
            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(probs.toString());
                bw.close();
                Log.d("myLog", "Файл записан на SD: " + sdFile.getAbsolutePath());

                return sdFile.getAbsolutePath();
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return "";
            }
        }
        else
        {
            return "";
        }
    }

    public String createDefectFile(int id)
    {
        ArrayList<Defects> defectses = new ArrayList<Defects>();

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEFECTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.DEFECTS + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                defectses.add(new Defects(ids, idDept, name));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
        JSONArray defects = new JSONArray();

        try
        {
            for (int i=0; i<defectses.size(); i++)
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", defectses.get(i).name);

                defects.put(jsonObject);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();

            return "";
        }

        if (!defects.toString().equals(""))
        {
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id));
            sdPath.mkdirs();
            File sdFile = new File(sdPath, "info_defects.ini");
            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(defects.toString());
                bw.close();
                Log.d("myLog", "Файл записан на SD: " + sdFile.getAbsolutePath());

                return sdFile.getAbsolutePath();
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return "";
            }
        }
        else
        {
            return "";
        }
    }
    public String createLogFile(int id){

        ArrayList<LogStruct> logs = new ArrayList<LogStruct>();

        DBHelper dbHelper = new DBHelper(context, DBHelper.Logs);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Logs + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if(cursor.moveToFirst()){
            do
            {
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lon = cursor.getDouble(cursor.getColumnIndex("long"));
                String text = cursor.getString(cursor.getColumnIndex("log_text"));

                logs.add(new LogStruct(ids, idDept, lat, lon, text));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        dbHelper.close();
        JSONArray loges = new JSONArray();

        try {
            for (int i = 0; i < logs.size(); i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lat", logs.get(i).lat);
                jsonObject.put("lon", logs.get(i).lon);
                jsonObject.put("log_text", logs.get(i).text);
                loges.put(jsonObject);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            return "";
        }
        if(!loges.toString().equals(""))
            {
                File sdPath = Environment.getExternalStorageDirectory();
                sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id));
                sdPath.mkdirs();
                File sdFile = new File(sdPath, "logs.ini");
                try
                {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                    bw.write(loges.toString());
                    bw.close();
                    Log.d("myLog", "Файл записан на SD: " + sdFile.getAbsolutePath());

                    return sdFile.getAbsolutePath();
                }
                catch (IOException e)
                {
                    e.printStackTrace();

                    return "";
                }
            }
        else
        {
            return "";
        }
        }

    public String createAgentFile(int id)
    {
        ArrayList<Agent> agents = new ArrayList<Agent>();

        DBHelper dbHelper = new DBHelper(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Agents + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String nameCompany = cursor.getString(cursor.getColumnIndex("nameCompany"));
                String fio = cursor.getString(cursor.getColumnIndex("fio"));
                String rang = cursor.getString(cursor.getColumnIndex("rang"));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex("img"));
                Boolean isProvider = (cursor.getInt(cursor.getColumnIndex("isPodryadchik")) == 1)? true : false;
                Boolean isUorg = (cursor.getInt(cursor.getColumnIndex("isUpolnomochOrg")) == 1)? true : false;
                Boolean isCustomer = (cursor.getInt(cursor.getColumnIndex("isZakazchik")) == 1)? true : false;
                Boolean isSubProvider = (cursor.getInt(cursor.getColumnIndex("isSubPodryadchik")) == 1)? true : false;
                Boolean isAvtNadzor = (cursor.getInt(cursor.getColumnIndex("isAvtNadzor")) == 1)? true : false;
                Boolean isEngineeringService = (cursor.getInt(cursor.getColumnIndex("isEngineeringService")) == 1)? true : false;
                agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider, isSubProvider, isCustomer, isEngineeringService, isAvtNadzor, isUorg, false, blob));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        JSONArray obj = new JSONArray();

        try
        {
            for (int i=0; i<agents.size(); i++)
            {
                String pathImg = "null";

                File sdPath = Environment.getExternalStorageDirectory();
                sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id)+"/signatures");
                sdPath.mkdirs();
                if(agents.get(i).blob != null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(agents.get(i).blob, 0, agents.get(i).blob.length);
                    OutputStream stream = new FileOutputStream(sdPath.getAbsolutePath() + "/" + String.valueOf(i) + ".png");

                    if (bitmap != null)
                    {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        pathImg = "/signatures/" + String.valueOf(i) + ".png";
                    }

                    stream.close();
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nameCompany", agents.get(i).nameCompany);
                jsonObject.put("fio", agents.get(i).fio);
                jsonObject.put("rang", agents.get(i).rang);
                jsonObject.put("signature", pathImg);
                jsonObject.put("isProvider", String.valueOf(agents.get(i).isPodryadchik));
                jsonObject.put("isCustomer", String.valueOf(agents.get(i).isZakazchik));
                jsonObject.put("isSubProvider", String.valueOf(agents.get(i).isSubPodryadchik));
                jsonObject.put("isAvtNadz", String.valueOf(agents.get(i).isAvtNadzor));
                jsonObject.put("isUpolnomoch", String.valueOf(agents.get(i).isUpolnomochOrg));
                jsonObject.put("isEngineeringService", String.valueOf(agents.get(i).isEngineeringService));

                obj.put(jsonObject);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();

            return "";
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();

            return "";
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return "";
        }

        if (!obj.toString().equals(""))
        {
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id));
            sdPath.mkdirs();
            File sdFile = new File(sdPath, "info_providers.ini");
            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(obj.toString());
                bw.close();
                Log.d("myLog", "Файл записан на SD: " + sdFile.getAbsolutePath());



                return sdFile.getAbsolutePath();
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return "";
            }
        }
        else
        {
            return "";
        }
    }

    public String createPhotoFile(int id)
    {
        ArrayList<Photo> photos = new ArrayList<Photo>();

        DBHelper dbHelper = new DBHelper(context, DBHelper.Photos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Photos + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                photos.add(new Photo(ids, idDept, path));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        for (int i=0; i<photos.size(); i++)
        {
            String pathImg = "null";

            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id)+"/photo");
            sdPath.mkdirs();

            File photo = new File(photos.get(i).path);

            try
            {
                FileUtils.copyFileToDirectory(photo, sdPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();

                break;
            }
        }
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id)+"/photo");
        return sdPath.getAbsolutePath();
    }

    public String createVideoFile(int id)
    {
        ArrayList<Videos> videoses = new ArrayList<Videos>();

        DBHelper dbHelper = new DBHelper(context, DBHelper.Videos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Videos + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String path = cursor.getString(cursor.getColumnIndex("path"));

                //    Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

                videoses.add(new Videos(ids, idDept, path));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        for (int i=0; i<videoses.size(); i++)
        {
            String pathImg = "null";

            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id)+"/video");
            sdPath.mkdirs();

            File photo = new File(videoses.get(i).path);

            try
            {
                FileUtils.copyFileToDirectory(photo, sdPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();

                break;
            }
        }

        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id)+"/video");
        return sdPath.getAbsolutePath();
    }

    class CreateArhive extends AsyncTask<Void, Void, String>
    {
        Context context;

        public CreateArhive(Context context)
        {
            this.context = context;

            sendNotif("Сбор данных об акте");
        }

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected String doInBackground(Void... unused) {
            return onClose();
        }

        @Override
        protected void onPostExecute(String unused)
        {
            if (unused.equals(""))
            {

            }
            else
            {
                SendZip sendZip = new SendZip(unused);
                sendZip.execute();
                if(isOnline()){
                    Sync sync = new Sync();
                    sync.execute();
                }

            }
        }
    }

    class SendZip extends AsyncTask<Void, Void, String>
    {
        String pathZip;

        public SendZip(String pathZip)
        {
            this.pathZip = pathZip;

            sendNotif("Отправка данных об акте");
        }

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected String doInBackground(Void... unused)
        {
            DBHelper dbHelperL = new DBHelper(context, DBHelper.Logs);
            SQLiteDatabase dbL = dbHelperL.getWritableDatabase();

            dbL.delete(DBHelper.Logs, "idDept = " + id, null);

            dbHelperL.close();
            dbL.close();

            DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete(DBHelper.DEPARTURE, "id = " + id, null);

            dbHelper.close();
            db.close();

            DBHelper dbHelpeM = new DBHelper(context, DBHelper.MEASUREMENTS);
            SQLiteDatabase dbM = dbHelpeM.getWritableDatabase();

            dbM.delete(DBHelper.MEASUREMENTS, "idDept = " + id, null);

            dbHelpeM.close();
            dbM.close();

            DBHelper dbHelpeP = new DBHelper(context, DBHelper.PROBS);
            SQLiteDatabase dbP = dbHelpeP.getWritableDatabase();

            dbP.delete(DBHelper.PROBS, "idDept = " + id, null);

            dbHelpeP.close();
            dbP.close();

            DBHelper dbHelpeD = new DBHelper(context, DBHelper.DEFECTS);
            SQLiteDatabase dbD = dbHelpeD.getWritableDatabase();

            dbD.delete(DBHelper.DEFECTS, "idDept = " + id, null);

            dbHelpeD.close();
            dbD.close();

            DBHelper dbHelpeA = new DBHelper(context, DBHelper.Agents);
            SQLiteDatabase dbA = dbHelpeA.getWritableDatabase();

            dbA.delete(DBHelper.Agents, "idDept = " + id, null);

            dbHelpeA.close();
            dbA.close();

            DBHelper dbHelpePh = new DBHelper(context, DBHelper.Photos);
            SQLiteDatabase dbPh = dbHelpePh.getWritableDatabase();

            dbPh.delete(DBHelper.Photos, "idDept = " + id, null);

            dbHelpePh.close();
            dbPh.close();

            DBHelper dbHelpeV = new DBHelper(context, DBHelper.Videos);
            SQLiteDatabase dbV = dbHelpeV.getWritableDatabase();

            dbV.delete(DBHelper.Videos, "idDept = " + id, null);

            dbHelpeV.close();
            dbV.close();


            if (isOnline())
            {
                try
                {
                    File file = new File(pathZip);
                    boolean result = new ServerApi(ServerApi.ACTION_LOAD_ACT).UpLoadFile(file);

                    if(!result)
                    {
                        addPath(pathZip);
                        Log.d("ARCHIVEEE", "NOT RESULT");

                    }
                    else
                    {
                        file.delete();
                        Log.d("ARCHIVEEE", "RESULT");

                    }

                    Log.d("ARCHIVEEE", "FINISH");
                }
                catch (IOException e)
                {
                    addPath(pathZip);

                    Log.d("ARCHIVEEE", "START2");
                }
            }
            else
            {
                addPath(pathZip);
                Log.d("ARCHIVEEE", "START3");
            }

            return "";
        }

        @Override
        protected void onPostExecute(String unused)
        {
            stopService();
            nm.cancel(200);
        }
    }

    private void addPath(String path)
    {
        DBHelper dbHelperOFF = new DBHelper(context, DBHelper.OfflineZip);
        SQLiteDatabase dbOFF = dbHelperOFF.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("path", path);

        long rowID = dbOFF.insert(dbHelperOFF.getDatabaseName(), null, cv);

        dbHelperOFF.close();
        dbOFF.close();
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    void sendNotif(String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Синхронизация")
                        .setContentText(text);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        nm.notify(200, notification);
    }

    class Sync extends AsyncTask<Void, Void, String> {
        int res = 0;

        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        public Sync() {
            sharedPref = context.getSharedPreferences(resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected String doInBackground(Void... unused) {
            DBHelper dbHelper = new DBHelper(context, DBHelper.OfflineZip);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM OfflineZip", null);
            if (cursor.moveToFirst()) {
                do {
                    if (isOnline()) {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        String path = cursor.getString(cursor.getColumnIndex("path"));

                        try {
                            File file = new File(path);
                            boolean result = new ServerApi(ServerApi.ACTION_LOAD_ACT).UpLoadFile(file);

                            if (result) {
                                file.delete();
                                db.delete(DBHelper.OfflineZip, "id = " + id, null);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                       /* DateFormat df = new SimpleDateFormat("Время: HH:mm:ss dd.MM.yyyy");
                        String date = df.format(Calendar.getInstance().getTime());
                        editor.putString("timeSync", date);
                        editor.commit();*/
                    }
                }
                while (cursor.moveToNext());
            }
            else{
                res = 1;
            }

            cursor.close();
            db.close();
            dbHelper.close();

            return "";
        }
        @Override
        protected void onPostExecute(String unused) {
        }

    }
}
