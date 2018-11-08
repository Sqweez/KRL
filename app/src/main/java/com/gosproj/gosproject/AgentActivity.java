package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Views.CustomScrollView;
import com.gosproj.gosproject.Views.PaintView;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;

public class AgentActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout nameCompany;
    TextInputLayout rang;
    TextInputLayout fio;
    LinearLayout erase;

    Button save;

    int id;
    Agent agent;

    RadioButton provider;
    RadioButton customer;
    RadioButton engService;
    RadioButton subprovider;
    RadioButton uorg;
    RadioButton avt_nadz;
    LogsHelper logsHelper;
    CustomScrollView nestedScrollView;
    String oldAgent;
    String newAgent;
    String podradchykText = "";
    String customerText = "";
    String subpodrText = "";
    String avtnadzText = "";
    String uorgText = "";
    String injsluzhbText = "";
    String oldRole;
    String newRole;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);

        id = getIntent().getIntExtra("id", 0);
        agent = getIntent().getParcelableExtra("agent");
        activity = this;
        context = this;
        resources = getResources();

        logsHelper = new LogsHelper(LogsHelper.AGENT, context, activity, id);

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Departures WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            uorgText = cursor.getString(cursor.getColumnIndex("uorg"));
            podradchykText = cursor.getString(cursor.getColumnIndex("podradchyk"));
            podradchykText = podradchykText.replace ("&quot;", "\"");
            customerText = cursor.getString(cursor.getColumnIndex("zakazchik"));
            customerText = customerText.replace ("&quot;", "\"");
            subpodrText = cursor.getString(cursor.getColumnIndex("subpodradchyk"));
            avtnadzText = cursor.getString(cursor.getColumnIndex("avt_nadzor"));
            injsluzhbText = cursor.getString(cursor.getColumnIndex("inj_sluzhby"));
            uorgText = cursor.getString(cursor.getColumnIndex("uorg"));
        }

        cursor.close();
        db.close();
        dbHelper.close();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);

        nameCompany = (TextInputLayout) findViewById(R.id.name_company);
        rang = (TextInputLayout) findViewById(R.id.rang);
        fio = (TextInputLayout) findViewById(R.id.fio);

        customer = (RadioButton) findViewById(R.id.customer);
        provider = (RadioButton) findViewById(R.id.provider);
        engService = (RadioButton) findViewById(R.id.eng_service);
        subprovider = (RadioButton) findViewById(R.id.subprovider);
        avt_nadz = (RadioButton) findViewById(R.id.avt_nadz);
        uorg = (RadioButton) findViewById(R.id.uorg);
        save = (Button) findViewById(R.id.save);
        nestedScrollView = (CustomScrollView) findViewById(R.id.neasted);

        uorg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    nameCompany.getEditText().setText(uorgText);
                }
            }
        });
        subprovider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    nameCompany.getEditText().setText(subpodrText);
                }
            }
        });
        avt_nadz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    nameCompany.getEditText().setText(avtnadzText);
                }
            }
        });
        provider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    nameCompany.getEditText().setText(podradchykText);
                }
            }
        });

        customer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    nameCompany.getEditText().setText(customerText);
                }
            }
        });

        engService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && agent == null)
                {
                    nameCompany.getEditText().setText(injsluzhbText);
                }
            }
        });

        customer.setChecked(true);
        if (agent != null)
        {
            rang.getEditText().setText(agent.rang);
            fio.getEditText().setText(agent.fio);
            provider.setChecked(agent.isPodryadchik);
            customer.setChecked(agent.isZakazchik);
            engService.setChecked(agent.isEngineeringService);
            avt_nadz.setChecked(agent.isAvtNadzor);
            subprovider.setChecked(agent.isSubPodryadchik);
            uorg.setChecked(agent.isUpolnomochOrg);
            nameCompany.getEditText().setText(agent.nameCompany);
            if(agent.isZakazchik){
                oldRole = "заказчика";
            }
            else if(agent.isPodryadchik){
                oldRole = "подрядчика";
            }
            else if(agent.isEngineeringService){
                oldRole = "инженерной службы";
            }
            else if(agent.isAvtNadzor){
                oldRole = "авторского надзора";
            }
            else if(agent.isSubPodryadchik){
                oldRole = "субподрядчика";
            }
            else if(agent.isUpolnomochOrg){
                oldRole = "уполномоченных органов";
            }
            oldAgent = agent.nameCompany + "|" + agent.rang + "|" + agent.fio + "|" + oldRole;
            save.setText(resources.getString(R.string.edit));
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });
    }
    private boolean checkFields()
    {
        boolean isT = false;

        if (!nameCompany.getEditText().getText().toString().equals(""))
        {
            if (!rang.getEditText().getText().toString().equals(""))
            {
                if (!fio.getEditText().getText().toString().equals(""))
                {
                    isT = true;
                }
            }
        }

        return isT;
    }

    private void onClickSave()
    {
        if (clickSave())
        {
            Intent intent = new Intent();
            intent.putExtra("agent", agent);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    public boolean clickSave()
    {
        if (checkFields())
        {
            if (agent == null)
            {
                if (save())
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (edit())
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.field_filds), Toast.LENGTH_LONG).show();

            return false;
        }
    }

    private boolean edit()
    {
        boolean result = false;

        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext(), DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("nameCompany", nameCompany.getEditText().getText().toString());
        cv.put("fio", fio.getEditText().getText().toString());
        cv.put("rang", rang.getEditText().getText().toString());
        cv.put("isPodryadchik", String.valueOf((provider.isChecked()) ? 1 : 0));
        cv.put("isZakazchik", String.valueOf((customer.isChecked()) ? 1 : 0));
        cv.put("isSubPodryadchik", String.valueOf((subprovider.isChecked()) ? 1 : 0));
        cv.put("isUpolnomochOrg", String.valueOf((uorg.isChecked()) ? 1 : 0));
        cv.put("isAvtNadzor", String.valueOf((avt_nadz.isChecked()) ? 1 : 0));
        cv.put("isEngineeringService", String.valueOf((engService.isChecked()) ? 1 : 0));

        if(customer.isChecked()){
            newRole = "заказчика";
        }
        else if(provider.isChecked())
        {
            newRole = "подрядчика";
        }
        else if(engService.isChecked()){
            newRole = "инженерной службы";
        }
        else if(avt_nadz.isChecked()){
            newRole = "авторского надзора";
        }
        else if(subprovider.isChecked()){
            newRole = "субподрядчика";
        }
        else if(uorg.isChecked()){
            newRole = "уполномоченных органов";
        }
        long rowID = db.update(dbHelper.getDatabaseName(), cv, "id="+String.valueOf(agent.id), null);

        if (rowID != -1)
        {

            Log.d("ADD_POS", (int) rowID + " editable");
            agent = new Agent(agent.id, id, nameCompany.getEditText().getText().toString(), rang.getEditText().getText().toString(), fio.getEditText().getText().toString(), provider.isChecked(),
                    subprovider.isChecked(), customer.isChecked(), engService.isChecked(), avt_nadz.isChecked(), uorg.isChecked(), false, null);
            result = true;
        }
        else
        {
            Toast.makeText(context, getResources().getString(R.string.fail_edit_prob), Toast.LENGTH_LONG).show();

            result = false;
        }

        db.close();
        dbHelper.close();
        newAgent = nameCompany.getEditText().getText().toString() + "|" + rang.getEditText().getText().toString() + "|" + fio.getEditText().getText().toString() + "|" + newRole;
        logsHelper.createLog(oldAgent, newAgent, LogsHelper.ACTION_EDIT);
        return result;
    }
    private boolean save()
    {
        boolean result = false;

        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext(), DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("nameCompany", nameCompany.getEditText().getText().toString());
        cv.put("fio", fio.getEditText().getText().toString());
        cv.put("rang", rang.getEditText().getText().toString());
        cv.put("isPodryadchik", String.valueOf((provider.isChecked()) ? 1 : 0));
        cv.put("isZakazchik", String.valueOf((customer.isChecked()) ? 1 : 0));
        cv.put("isSubPodryadchik", String.valueOf((subprovider.isChecked()) ? 1 : 0));
        cv.put("isUpolnomochOrg", String.valueOf((uorg.isChecked()) ? 1 : 0));
        cv.put("isAvtNadzor", String.valueOf((avt_nadz.isChecked()) ? 1 : 0));
        cv.put("isEngineeringService", String.valueOf((engService.isChecked()) ? 1 : 0));

        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);
        if(customer.isChecked()){
            newRole = "заказчика";
        }
        else if(provider.isChecked())
        {
            newRole = "подрядчика";
        }
        else if(engService.isChecked()){
            newRole = "инженерной службы";
        }
        else if(avt_nadz.isChecked()){
            newRole = "авторского надзора";
        }
        else if(subprovider.isChecked()){
            newRole = "субподрядчика";
        }
        else if(uorg.isChecked()){
            newRole = "уполномоченных органов";
        }
        if (rowID != 0)
        {

            agent = new Agent((int) rowID, id, nameCompany.getEditText().getText().toString(), rang.getEditText().getText().toString(), fio.getEditText().getText().toString(), provider.isChecked(),
                    subprovider.isChecked(), customer.isChecked(), engService.isChecked(), avt_nadz.isChecked(), uorg.isChecked(),false, null);
            result = true;
            Log.d("ADD_POS", (int) rowID + " ");
        }
        else
        {
            Toast.makeText(context, getResources().getString(R.string.fail_add_new_prob), Toast.LENGTH_LONG).show();

            result = false;
        }


        db.close();
        dbHelper.close();
        newAgent = nameCompany.getEditText().getText().toString() + "|" + rang.getEditText().getText().toString() + "|" + fio.getEditText().getText().toString() + "|" + newRole;

        logsHelper.createLog("", newAgent, LogsHelper.ACTION_ADD);
        return result;
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
