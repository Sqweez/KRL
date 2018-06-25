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

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Views.CustomScrollView;
import com.gosproj.gosproject.Views.PaintView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AgentActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    PaintView paintView;

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

    CustomScrollView nestedScrollView;

    String podradchykText = "";
    String customerText = "";

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

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT podradchyk, customer FROM Departures WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            podradchykText = cursor.getString(cursor.getColumnIndex("podradchyk"));
            podradchykText = podradchykText.replace ("&quot;", "\"");
            customerText = cursor.getString(cursor.getColumnIndex("customer"));
            customerText = customerText.replace ("&quot;", "\"");
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

        erase = (LinearLayout) findViewById(R.id.buttonErase);

        paintView = (PaintView) findViewById(R.id.paint);

        provider = (RadioButton) findViewById(R.id.provider);
        customer = (RadioButton) findViewById(R.id.customer);
        engService = (RadioButton) findViewById(R.id.eng_service);

        save = (Button) findViewById(R.id.save);

        nestedScrollView = (CustomScrollView) findViewById(R.id.neasted);

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
                    nameCompany.getEditText().setText("");
                }
            }
        });

        provider.setChecked(true);
        paintView.scrollView = nestedScrollView;

        if (agent != null)
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(agent.blob, 0, agent.blob.length);

            nameCompany.getEditText().setText(agent.nameCompany);
            rang.getEditText().setText(agent.rang);
            fio.getEditText().setText(agent.fio);
            paintView.setBitmap(bmp);

            provider.setChecked(agent.isProvider);
            customer.setChecked(agent.isCustomer);
            engService.setChecked(agent.isEngineeringService);

            save.setText(resources.getString(R.string.edit));
        }

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
            }
        });

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

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Bitmap bm = paintView.getBitmap(paintView.getWidth(), paintView.getHeight());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] buffer = out.toByteArray();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("nameCompany", nameCompany.getEditText().getText().toString());
        cv.put("fio", fio.getEditText().getText().toString());
        cv.put("rang", rang.getEditText().getText().toString());
        cv.put("img", buffer);
        cv.put("isProvider", String.valueOf((provider.isChecked()) ? 1 : 0));
        cv.put("isCustomer", String.valueOf((customer.isChecked()) ? 1 : 0));
        cv.put("isEngineeringService", String.valueOf((engService.isChecked()) ? 1 : 0));

        long rowID = db.update(dbHelper.getDatabaseName(), cv, "id="+String.valueOf(agent.id), null);

        if (rowID != -1)
        {
            Log.d("ADD_POS", (int) rowID + " editable");
            agent = new Agent(agent.id, id, nameCompany.getEditText().getText().toString(), rang.getEditText().getText().toString(), fio.getEditText().getText().toString(), provider.isChecked(),
                            customer.isChecked(), engService.isChecked(), buffer);

            result = true;
        }
        else
        {
            Toast.makeText(context, getResources().getString(R.string.fail_edit_prob), Toast.LENGTH_LONG).show();

            result = false;
        }

        db.close();
        dbHelper.close();

        return result;
    }

    private boolean save()
    {
        boolean result = false;

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("nameCompany", nameCompany.getEditText().getText().toString());
        cv.put("fio", fio.getEditText().getText().toString());
        cv.put("rang", rang.getEditText().getText().toString());

        Bitmap bm = paintView.getBitmap(paintView.getWidth(), paintView.getHeight());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] buffer = out.toByteArray();

        cv.put("img", buffer);
        cv.put("isProvider", String.valueOf((provider.isChecked()) ? 1 : 0));
        cv.put("isCustomer", String.valueOf((customer.isChecked()) ? 1 : 0));
        cv.put("isEngineeringService", String.valueOf((engService.isChecked()) ? 1 : 0));

        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

        if (rowID != 0)
        {
            agent = new Agent((int) rowID, id, nameCompany.getEditText().getText().toString(), rang.getEditText().getText().toString(), fio.getEditText().getText().toString(),
                    provider.isChecked(), customer.isChecked(), engService.isChecked(), buffer);
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
