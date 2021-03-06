package com.gosproj.gosproject.Fragments;

import android.app.Activity;
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
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.ServerApi;
import com.gosproj.gosproject.MainActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Services.CreateAndLoadService;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Act;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Proba;
import com.gosproj.gosproject.Structures.Videos;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActCloseFragment extends Fragment
{
    Context context;
    Resources resources;
    Activity activity;
    LogsHelper logsHelper;
    String name;
    int id;
    int actID;
    int isNew;
    String actNumber;
    String object;
    String date;
    String ispolnitel;
    String gv1;
    String gv2;
    String gv3;
    String vid_rabot;
    TextView text;
    TextView act;
    Button buttonClose;
    Button buttonCloseWS;
    byte[] img;
    private static final Random random = new Random();
    private static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ";

    public static ActCloseFragment getInstance(String name, int id, int isNew, String actNumber, String object, String date, String ispolnitel, String gv1, String gv2, String gv3, String vid_rabot)
    {
        Bundle args = new Bundle();
        ActCloseFragment fragment = new ActCloseFragment();
        fragment.setArguments(args);
        fragment.name = name;
        fragment.id = id;
        fragment.object = object;
        fragment.actNumber = actNumber;
        fragment.isNew = isNew;
        fragment.date = date;
        fragment.ispolnitel = ispolnitel;
        fragment.gv1 = gv1;
        fragment.gv2 = gv2;
        fragment.gv3 = gv3;
        fragment.vid_rabot = vid_rabot;
        return fragment;
    }

    public ActCloseFragment()
    {}
    public void checkSigns(){
        boolean isAllSigned = false;
        DBHelper dbHelper = new DBHelper(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Agents + " WHERE idDept = ?", new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()) {
            do{
               img = cursor.getBlob(cursor.getColumnIndex("img"));
               if(img == null){
                   isAllSigned = false;
                   break;
               }
               else{
                   isAllSigned = true;
               }
            }
            while (cursor.moveToNext());
        }
        if(isAllSigned){
            text.setVisibility(TextView.INVISIBLE);
            buttonClose.setEnabled(true);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        checkSigns();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_close_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();
        text = (TextView) view.findViewById(R.id.warningAboutButton);

        logsHelper = new LogsHelper(LogsHelper.DEPARTURE, context, activity, id);

        text.setText("Кнопка закрыть акт будет недоступна, пока все присутствующие не поставят подпись");
        act = (TextView) view.findViewById(R.id.act);
        buttonClose = (Button) view.findViewById(R.id.closeAct);
        buttonCloseWS = (Button) view.findViewById(R.id.closeActWithoutSigning);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String actNameAndDate = name + "\n" + format.format(date);
        act.setText(actNameAndDate);
        buttonCloseWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAct();
            }
        });
        buttonClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeAct();
            }
        });

        return view;
    }
    private void closeAct(){
        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("UPDATE Departures SET isClose = ?" +
                "WHERE id = ?", new String[]{String.valueOf(1) ,String.valueOf(id)});

        cursor.moveToFirst();
        cursor.close();

        db.close();
        dbHelper.close();
        if(isNew == 0){
            logsHelper.createLog(actNumber, "", LogsHelper.ACTION_CLOSE);
        }
        else{
            LogsHelper logs = new LogsHelper(LogsHelper.NEWDEPARTURE, context, activity, id);
            logs.createLog(object + "|" + vid_rabot + "|" + ispolnitel + "|" + gv1 + "|" + gv2 + "|" + gv3, "", LogsHelper.ACTION_CLOSE);
        }
        Intent intentService = new Intent(activity, CreateAndLoadService.class);
        intentService.putExtra("id", id);
        intentService.putExtra("isNew", isNew);

        if(isOnline()){
            SharedPreferences sharedPref = context.getSharedPreferences(resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            DateFormat df = new SimpleDateFormat("Время: HH:mm:ss dd.MM.yyyy");
            String date = df.format(Calendar.getInstance().getTime());
            editor.putString("timeSync", date);
            editor.commit();
        }

        activity.startService(intentService);
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
