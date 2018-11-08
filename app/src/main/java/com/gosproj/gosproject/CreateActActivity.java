package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Services.LogsHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateActActivity extends AppCompatActivity {
    Activity activity;
    Context context;
    Resources resources;
    EditText object_name;
    EditText gv1_ed;
    EditText gv2_ed;
    EditText gv3_ed;

    TextView actText;
    TextView dateText;
    TextView ispolnitel;

    String ispol_name;
    String date;
    int user_id;
    String rgu_name;
    String object;
    String typeOfWork;
    String gv_1;
    String gv_2;
    String gv_3;

    Spinner vid_rabot_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        context = getApplicationContext();
        resources = getResources();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        object_name = (EditText) findViewById(R.id.uchastok_edit);
        gv1_ed = (EditText) findViewById(R.id.gruppa_vyezda1_edit);
        gv2_ed = (EditText) findViewById(R.id.gruppa_vyezda2_edit);
        gv3_ed = (EditText) findViewById(R.id.gruppa_vyezda3_edit);

        actText = (TextView) findViewById(R.id.act);
        dateText = (TextView) findViewById(R.id.date);
        ispolnitel = (TextView) findViewById(R.id.ispolnitel);

        SharedPreferences sf = context.getSharedPreferences("com.gosproj.gosproject", Context.MODE_PRIVATE);
        ispol_name = sf.getString("name", "ИМЯ ФАМИЛИЯ");
        user_id = sf.getInt("user_id", 0);
        rgu_name = sf.getString("rgu_name", "РГУ ТЕСТ");

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        date = df.format(Calendar.getInstance().getTime());

        actText.setText("Новый акт выполненных работ");
        ispolnitel.setText(ispol_name);
        dateText.setText(date);

        final String[] typeOfWorks = new String[]{
                "Выберите вид работ",
                "Реконструкция",
                "Капитальный ремонт",
                "Средний ремонт",
                "Содержание, текущий ремонт",
                "Гарантийные осмотры",
                "Средний ремонт методом холодного ресайклирования",
                "Строительство",
                "Инструментальный осмотр в рамках СУДА",
                "Выезды с уполномоченными органами",
                };
        vid_rabot_spinner = (Spinner) findViewById(R.id.vid_rabot_edit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateActActivity.this, android.R.layout.simple_spinner_dropdown_item, typeOfWorks);
        vid_rabot_spinner.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                object = object_name.getText().toString();
                typeOfWork = vid_rabot_spinner.getSelectedItem().toString();
                gv_1 = gv1_ed.getText().toString();
                gv_2 = gv2_ed.getText().toString();
                gv_3 = gv3_ed.getText().toString();
                if(!typeOfWork.equals("") && !object.equals("") && !typeOfWork.equals("Выберите вид работ")){
                    ContentValues cv = new ContentValues();
                    cv.put("object", object);
                    cv.put("vid_rabot", typeOfWork);
                    cv.put("gruppa_vyezda1", gv_1);
                    cv.put("gruppa_vyezda2", gv_2);
                    cv.put("gruppa_vyezda3", gv_3);
                    switch (typeOfWork){
                        case "Выезды с уполномоченными органами":
                            cv.put("id_rabot", 181);
                            break;
                        case "Гарантийные осмотры":
                            cv.put("id_rabot", 180);
                            break;
                        case "Инструментальный осмотр в рамках СУДА":
                            cv.put("id_rabot", 11249);
                            break;
                        case "Капитальный ремонт":
                            cv.put("id_rabot", 176);
                            break;
                        case "Реконструкция":
                            cv.put("id_rabot", 175);
                            break;
                        case "Содержание, текущий ремонт":
                            cv.put("id_rabot", 179);
                            break;
                        case "Средний ремонт":
                            cv.put("id_rabot", 177);
                            break;
                        case "Средний ремонт методом холодного ресайклирования":
                            cv.put("id_rabot", 687);
                            break;
                        case "Строительство":
                            cv.put("id_rabot", 4983);
                            break;
                    }
                    cv.put("ispolnitel", ispol_name);
                    cv.put("isNew", 1);
                    cv.put("date", date);
                    cv.put("rgu_name", rgu_name);
                    cv.put("zakazchik", "");
                    cv.put("inj_sluzhby", "");
                    cv.put("podradchyk", "");
                    cv.put("subpodradchyk", "");
                    cv.put("avt_nadzor", "");
                    cv.put("uorg", "");
                    cv.put("isClose", "0");
                    DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    long rowID = db.insert(DBHelper.DEPARTURE, null, cv);
                    db.close();
                    dbHelper.close();

                    Intent intent = new Intent(context, ActActivity.class);
                    intent.putExtra("id", (int)rowID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
                else{
                    if(object.equals("")){
                        Toast.makeText(context, "Заполните поле объект", Toast.LENGTH_SHORT).show();
                    }
                    if(typeOfWork.equals("") || typeOfWork.equals("Выберите вид работ")){
                        Toast.makeText(context, "Заполните поле вид работ", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

}
