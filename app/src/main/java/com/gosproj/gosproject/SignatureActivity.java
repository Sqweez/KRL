package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Views.CustomScrollView;
import com.gosproj.gosproject.Views.PaintView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SignatureActivity extends AppCompatActivity {
    Toolbar toolbar;
    Agent agent;
    int id;
    TextView zamerName;
    TextView defectName;
    TextView probaName;
    TextView rgu;
    TextView name;
    TextView work;
    TextView infoObject;
    TextView infoZamer;
    TextView infoDefect;
    TextView infoProba;
    Resources resources;
    LinearLayout erase;
    View line1;
    View line3;
    View line2;
    View line4;
    LogsHelper logsHelper;
    String agentS;
    String oldRole;

    Activity activity;
    Context context;
    SignaturePad mSignPad;
    CustomScrollView nestedScrollView;
    Button save;

    private void getZamery(){
        String zamery = "";
        ArrayList<String>zamer = new ArrayList<String>();
        zamer.clear();
        DBHelper dbHelper = new DBHelper(context, DBHelper.MEASUREMENTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Measurements WHERE idDept = ? ", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            String value = cursor.getString(cursor.getColumnIndex("name"));
            zamer.add(value);
        }

        db.close();
        dbHelper.close();
        cursor.close();
        if(zamer.size() > 0){
            zamerName.setVisibility(View.VISIBLE);
            for(int i=0; i<zamer.size();i++){
                if(i == zamer.size() - 1){
                    zamery += "- " + zamer.get(i);
                }
                else{
                    zamery += "- " + zamer.get(i) + "\n";
                }

            }
        }
        if(!zamery.equals("")){
            infoZamer.setVisibility(View.VISIBLE);
            line1.setVisibility(View.VISIBLE);
            infoZamer.setText(zamery);
        }
    }
    private void getDefects(){
        String defects = "";
        ArrayList<String> defect = new ArrayList<String >();
        defect.clear();
        DBHelper dbHelper = new DBHelper(context, DBHelper.DEFECTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.DEFECTS + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                String name = cursor.getString(cursor.getColumnIndex("name"));

                defect.add(name);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
        if(defect.size() > 0){
            defectName.setVisibility(View.VISIBLE);
            for(int i = 0; i < defect.size(); i++){
                if (i == defect.size() - 1){
                    defects += "- " + defect.get(i);

                }
                else{
                    defects += "- " + defect.get(i) + "\n";
                }
            }
        }
        if(!defects.equals("")){
            infoDefect.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            infoDefect.setText(defects);
        }
    }
    private void getProbs(){
        String probs = "";
        ArrayList<String> proba  = new ArrayList<String>();
        proba.clear();
        DBHelper dbHelper = new DBHelper(context, DBHelper.PROBS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.PROBS + " WHERE idDept = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst())
        {
            do
            {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String place = cursor.getString(cursor.getColumnIndex("place"));

                proba.add(name + "(" + size + ")" + "\t|" + place + "|");
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
        if(proba.size() > 0){
            probaName.setVisibility(View.VISIBLE);
            for(int i=0; i<proba.size();i++){
                if(i == proba.size() - 1){
                    probs += "- " + proba.get(i);

                }
                else{
                    probs += "- " + proba.get(i) + "\n";

                }
            }
        }
        if(!probs.equals("")){
            line3.setVisibility(View.VISIBLE);
            infoProba.setVisibility(View.VISIBLE);
            infoProba.setText(probs);
        }
    }
    private void getVyezd(){
        String vyezd = "";
        String object = "";
        String date = "";
        String vid_rabot = "";
        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Departures WHERE id = ?", new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            object = cursor.getString(cursor.getColumnIndex("object"));
            date = cursor.getString(cursor.getColumnIndex("date"));
            vid_rabot = cursor.getString(cursor.getColumnIndex("vid_rabot"));
        }
        cursor.close();
        db.close();
        dbHelper.close();
        vyezd = "" + object + "\n" + date + "\n" + vid_rabot;
        infoObject.setText(vyezd);
    }
    @Override
    protected void onResume(){
        super.onResume();
        infoZamer.setVisibility(View.GONE);
        infoDefect.setVisibility(View.GONE);
        infoProba.setVisibility(View.GONE);
        line1.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);
        line3.setVisibility(View.GONE);
        if(!agent.isRgu){
            getVyezd();
            getZamery();
            getDefects();
            getProbs();
        }
        if(agent.isRgu){
            infoObject.setVisibility(View.GONE);
            line4.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        id = getIntent().getIntExtra("id", 0);
        agent = getIntent().getParcelableExtra("agent");
        activity = this;
        context = getApplicationContext();
        logsHelper = new LogsHelper(LogsHelper.SIGNATURE, context, activity, id);
        resources = getResources();
        save = (Button) findViewById(R.id.save);
        rgu = (TextView) findViewById(R.id.rgu);
        name = (TextView) findViewById(R.id.fio);
        work = (TextView) findViewById(R.id.work);

        zamerName = (TextView) findViewById(R.id.infoZameryName);
        defectName = (TextView) findViewById(R.id.infoDefectyName);
        probaName = (TextView) findViewById(R.id.infoProbyName);

        infoObject = (TextView) findViewById(R.id.infoObject);
        infoZamer = (TextView) findViewById(R.id.infoZamery);
        infoDefect = (TextView) findViewById(R.id.infoDefecty);
        infoProba = (TextView) findViewById(R.id.infoProby);

        line1 = (View) findViewById(R.id.line1);
        line2 = (View) findViewById(R.id.line2);
        line3 = (View) findViewById(R.id.line3);
        line4 = findViewById(R.id.lineObject);

        rgu.setText(agent.nameCompany);
        name.setText(agent.fio);
        work.setText(agent.rang);
        String msg = " |" + agent.rang + "|" + agent.fio;
        logsHelper.createLog(msg, "", LogsHelper.ACTION_OPEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        mSignPad = (SignaturePad) findViewById(R.id.paint);
        erase = (LinearLayout) findViewById(R.id.buttonErase);
        nestedScrollView = (CustomScrollView) findViewById(R.id.neasted);
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignPad.clear();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSave();
            }
        });

    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    private void onClickSave()
    {
        if (save())
        {
            Intent intent = new Intent();
            intent.putExtra("agent", agent);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private boolean save(){
        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Bitmap bm = mSignPad.getTransparentSignatureBitmap();
        bm = getResizedBitmap(bm, 320, 240);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] buffer = out.toByteArray();
        ContentValues cv = new ContentValues();
        cv.put("img", buffer);
        long rowID = db.update(dbHelper.getDatabaseName(), cv, "id="+String.valueOf(agent.id), null);
        if(agent.isZakazchik){
            oldRole = "заказчиком";
        }
        else if(agent.isPodryadchik){
            oldRole = "подрядчиком";
        }
        else if(agent.isEngineeringService){
            oldRole = "инженерной службой";
        }
        else if(agent.isAvtNadzor){
            oldRole = "авторским надзором";
        }
        else if(agent.isSubPodryadchik){
            oldRole = "субподрядчиком";
        }
        else if(agent.isUpolnomochOrg){
            oldRole = "уполномоченными органами";
        }
        else {
            oldRole = "сотрудником РГУ";
        }
        agentS = agent.nameCompany + "|" + agent.rang + "|" + agent.fio + "|" + oldRole;
        logsHelper.createLog(agentS, "", LogsHelper.ACTION_ADD);
        agent.blob = buffer;
        dbHelper.close();
        db.close();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("agent", agent);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            default:
                return false;
        }
    }

}
