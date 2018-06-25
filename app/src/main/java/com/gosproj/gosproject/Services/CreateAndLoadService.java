package com.gosproj.gosproject.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.ServerApi;
import com.gosproj.gosproject.MainActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Act;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
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

public class CreateAndLoadService extends Service
{
    final String LOG_TAG = "ServiceLoad";

    private static final Random random = new Random();
    private static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ";

    Context context;
    int actID;
    int id;

    NotificationManager nm;

    public CreateAndLoadService()
    {
        super();
    }

    public void onCreate()
    {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");

        context = getApplicationContext();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LOG_TAG, "onStartCommand");
        id = intent.getIntExtra("id", 0);

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

        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/archives/");
        sdPath.mkdir();

        File rootFolder = Environment.getExternalStorageDirectory();
        rootFolder = new File(rootFolder.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+String.valueOf(id));
        rootFolder.mkdirs();

        String token = getToken(3);

        rootFolder.renameTo(new File(rootFolder.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/"+token));

        try
        {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String date = df.format(Calendar.getInstance().getTime());

            String pathZip = sdPath.getAbsolutePath() + "/"+String.valueOf(actID)+"-"+date+"-"+token+".zip";

            ZipFile zipFile = new ZipFile(pathZip);
            Log.d("MYLOGAWESOME", "KEKS " + zipFile);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            parameters.setEncryptFiles(true);

            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

            parameters.setPassword("123");

            zipFile.addFolder(rootFolder, parameters);
            Log.d("MYLOGAWESOME", "SHMEKS " + zipFile);
            deleteRecursive(rootFolder);

            return pathZip;
        }
        catch (ZipException e)
        {
            e.printStackTrace();
            Log.d("MYLOGAWESOME", "OSHIBKA " + e);
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
            act.date = cursor.getString(cursor.getColumnIndex("date"));
            act.doroga = cursor.getString(cursor.getColumnIndex("doroga"));
            act.uchastok = cursor.getString(cursor.getColumnIndex("uchastok"));
            act.vid_rabot = cursor.getString(cursor.getColumnIndex("vid_rabot"));
            act.rgu = cursor.getString(cursor.getColumnIndex("rgu"));
            act.rgu = act.rgu.replace ("&quot;", "\"");
            act.ispolnitel = cursor.getString(cursor.getColumnIndex("ispolnitel"));
            act.gruppa_vyezda = cursor.getString(cursor.getColumnIndex("gruppa_vyezda"));
            act.podradchyk = cursor.getString(cursor.getColumnIndex("podradchyk"));

            actID = act.idAct;
        }

        cursor.close();
        db.close();
        dbHelper.close();

        String zamery = "";

        DBHelper dbHelperM = new DBHelper(context, DBHelper.MEASUREMENTS);
        SQLiteDatabase dbM = dbHelperM.getWritableDatabase();

        Cursor cursorM = dbM.rawQuery("SELECT * FROM Measurements WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursorM.moveToFirst())
        {
            zamery = cursorM.getString(cursorM.getColumnIndex("value"));
        }

        cursorM.close();
        dbM.close();
        dbHelperM.close();

        JSONObject obj = new JSONObject();

        zamery = StringEscapeUtils.unescapeHtml4(zamery);
        zamery = zamery.replaceAll("\\n", "");

        try
        {
            obj.put("idAct", act.idAct);
            obj.put("date", act.date);
            obj.put("doroga", act.doroga);
            obj.put("uchastok", act.uchastok);
            obj.put("vid_rabot", act.vid_rabot);
            obj.put("rgu", act.rgu);
            obj.put("ispolnitel", act.ispolnitel);
            obj.put("gruppa_vyezda", act.gruppa_vyezda);
            obj.put("podradchyk", act.podradchyk);
            obj.put("measurements", zamery);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
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
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String place = cursor.getString(cursor.getColumnIndex("place"));
                String provider = cursor.getString(cursor.getColumnIndex("provider"));
                String typeWork = cursor.getString(cursor.getColumnIndex("typeWork"));

                probas.add(new Proba(ids, idDept, name, count, size, place, provider, typeWork));
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
                jsonObject.put("count", probas.get(i).count);
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
                String kilometr = cursor.getString(cursor.getColumnIndex("kilometr"));
                String comment = cursor.getString(cursor.getColumnIndex("comment"));

                defectses.add(new Defects(ids, idDept, name, kilometr, comment));
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
                jsonObject.put("kilometr", defectses.get(i).kilometr);
                jsonObject.put("comment", defectses.get(i).comment);

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
                Boolean isProvider = (cursor.getInt(cursor.getColumnIndex("isProvider")) == 1)? true : false;
                Boolean isCustomer = (cursor.getInt(cursor.getColumnIndex("isCustomer")) == 1)? true : false;
                Boolean isEngineeringService = (cursor.getInt(cursor.getColumnIndex("isEngineeringService")) == 1)? true : false;

                //    Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

                agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider, isCustomer, isEngineeringService, blob));
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

                Bitmap bitmap = BitmapFactory.decodeByteArray(agents.get(i).blob, 0, agents.get(i).blob.length);
                OutputStream stream = new FileOutputStream(sdPath.getAbsolutePath() + "/" + String.valueOf(i) + ".png");

                if (bitmap != null)
                {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    pathImg = "/signatures/" + String.valueOf(i) + ".png";
                }

                stream.close();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nameCompany", agents.get(i).nameCompany);
                jsonObject.put("fio", agents.get(i).fio);
                jsonObject.put("rang", agents.get(i).rang);
                jsonObject.put("signature", pathImg);
                jsonObject.put("isProvider", String.valueOf(agents.get(i).isProvider));
                jsonObject.put("isCustomer", String.valueOf(agents.get(i).isCustomer));
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
                    boolean result = new ServerApi().UpLoadFile(file);

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
        Log.d("Offline", "ОФФЛАЙН");
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
}
