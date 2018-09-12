package com.gosproj.gosproject.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.telephony.TelephonyManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.ServerApi;
import com.gosproj.gosproject.MainActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Scan;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.EventListener;

import static com.gosproj.gosproject.Services.CreateAndLoadService.getToken;

public class LoadScanService extends Service {
    final String LOG_TAG = "myLogs";
    Context context;
    int id;
    String rgu_id;
    NotificationManager nm;

    public LoadScanService() {
        super();
    }


    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        id = Integer.parseInt(intent.getStringExtra("id"));
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    String onClose() {
        String scanFile = createScanFile(id);

        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/archives/");
        sdPath.mkdir();

        File rootFolder = Environment.getExternalStorageDirectory();
        rootFolder = new File(rootFolder.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/" + String.valueOf(id) + "/scans");
        rootFolder.mkdirs();

        String token = getToken(3);

        rootFolder.renameTo(new File(rootFolder.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/" + token));
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String date = df.format(Calendar.getInstance().getTime());
            String pathZip = sdPath.getAbsolutePath() + "/" + String.valueOf(id) + "-" + date + "-" + token + ".zip";
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
        } catch (ZipException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void stopService() {
        this.stopSelf();
    }

    private String createScanFile(int id) {
        ArrayList<Scan> scans = new ArrayList<Scan>();
        DBHelper dbHelper = new DBHelper(context, DBHelper.Scans);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Scans + " WHERE idVyezda = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            do {
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idVyezda"));
                int docType = cursor.getInt(cursor.getColumnIndex("docType"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                int rgu_id = cursor.getInt(cursor.getColumnIndex("rgu_id"));
                scans.add(new Scan(ids, idDept, path, docType, rgu_id));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        dbHelper.close();
        //TODO Попытка поместить номер телефона
        JSONArray scan = new JSONArray();
        JSONObject obj = new JSONObject();

        try {
            obj.put("id", scans.get(0).idDept);
            obj.put("rgu_id", scans.get(0).rgu_id);
            obj.put("type", scans.get(0).docType);
            scan.put(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < scans.size(); i++) {
            String pathImg = "null";
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/" + String.valueOf(id) + "/scans/images");
            sdPath.mkdirs();

            File scanImage = new File(scans.get(i).path);

            try {
                FileUtils.copyFileToDirectory(scanImage, sdPath);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/Android/data/com.gosproj.gosproject/" + String.valueOf(id) + "/scans");
        if (!scan.toString().equals("")) {
            File sdFile = new File(sdPath, "info_scans.ini");

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(scan.toString());
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        if (!scan.toString().equals("")) {
            File sdFile = new File(sdPath, "info_scans.ini");

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(scan.toString());
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return sdPath.getAbsolutePath();
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    void someTask() {
        Log.d("ARCHIVEEE", "START");

        CreateArhive createArhive = new CreateArhive(context);
        createArhive.execute();

    }

    class CreateArhive extends AsyncTask<Void, Void, String> {
        Context context;

        public CreateArhive(Context context) {
            this.context = context;

            sendNotif("Сбор документов");
        }

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected String doInBackground(Void... unused) {
            return onClose();
        }

        @Override
        protected void onPostExecute(String unused) {
            if (unused.equals("")) {

            } else {
                SendZip sendZip = new SendZip(unused);
                sendZip.execute();
            }
        }
    }

    private void addPath(String path) {
        Log.d("Offline", "ОФФЛАЙН");
        DBHelper dbHelperOFF = new DBHelper(context, DBHelper.OfflineZip);
        SQLiteDatabase dbOFF = dbHelperOFF.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("path", path);

        long rowID = dbOFF.insert(dbHelperOFF.getDatabaseName(), null, cv);

        dbHelperOFF.close();
        dbOFF.close();
    }

    class SendZip extends AsyncTask<Void, Void, String> {
        String pathZip;

        public SendZip(String pathZip) {
            this.pathZip = pathZip;
            sendNotif("Отправка данных на сервер");
        }

        @Override
        protected void onPostExecute(String unused) {
            stopService();
            nm.cancel(200);
        }

        @Override
        protected String doInBackground(Void... voids) {
            DBHelper dbHelper = new DBHelper(context, DBHelper.Scans);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete(DBHelper.Scans, "idVyezda = " + id, null);

            dbHelper.close();
            db.close();

            if (isOnline()) {
                try {
                    File file = new File(pathZip);
                    boolean result = new ServerApi().UpLoadFile(file);

                    if (!result) {
                        addPath(pathZip);
                        Log.d("ARCHIVEEE", "NOT RESULT");

                    } else {
                        file.delete();
                        Log.d("ARCHIVEEE", "RESULT");

                    }

                    Log.d("ARCHIVEEE", "FINISH");
                } catch (IOException e) {
                    addPath(pathZip);

                    Log.d("ARCHIVEEE", "START2");
                }
            }

            return "";
        }
    }

    public boolean isOnline() {
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
