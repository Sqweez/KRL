package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Measurment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MeasurmentActivity extends AppCompatActivity {

    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout name;
    String old_measur_name;
    Button save;
    Button saveAndAddNew;

    int id;
    Measurment measurment;
    LogsHelper logHelper;
    ArrayList<Measurment> returnMeasurment = new ArrayList<Measurment>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurment);

        id = getIntent().getIntExtra("id", 0);
        measurment = getIntent().getParcelableExtra("measurment");

        activity = this;
        context = this;
        resources = getResources();
        logHelper = new LogsHelper(LogsHelper.MEAS, context, activity, id);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);

        name = (TextInputLayout) findViewById(R.id.name_dcm);

        save = (Button) findViewById(R.id.save);
        saveAndAddNew = (Button) findViewById(R.id.saveAndAddNew);

        if (measurment != null)
        {
            name.getEditText().setText(measurment.name);
            old_measur_name = measurment.name;
            save.setText(resources.getString(R.string.edit));
            saveAndAddNew.setText(resources.getString(R.string.editAddNew));

            saveAndAddNew.setVisibility(View.GONE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });

        saveAndAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveAndAddNew();
            }
        });
    }

    private boolean checkFields()
    {
        boolean isT = false;

        if (!name.getEditText().getText().toString().equals(""))
        {
            if (!name.getEditText().getText().toString().equals(""))
            {
                isT = true;
            }
        }

        return isT;
    }

    private void onClickSave()
    {
        if (clickSave())
        {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("measurments", returnMeasurment);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void onClickSaveAndAddNew()
    {
        if (clickSave())
        {
            name.getEditText().setText("");
            /*killo.getEditText().setText("");
            comment.getEditText().setText("");*/

            measurment = null;
        }
    }

    public boolean clickSave()
    {
        if (checkFields())
        {
            if (measurment == null)
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

    public String getDateTime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String dateTime = sdf.format(date) + " " + stf.format(date);
        return dateTime;
    }
    private boolean edit()
    {
        boolean result = false;

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.MEASUREMENTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("UPDATE Measurements SET name = ?" +
                "WHERE id = ?", new String[]{ name.getEditText().getText().toString(), String.valueOf(measurment.id)});

        if (cursor != null)
        {
            returnMeasurment.add(new Measurment(measurment.id, id, name.getEditText().getText().toString()));

            result = true;
        }
        else
        {
            Toast.makeText(context, getResources().getString(R.string.fail_edit_prob), Toast.LENGTH_LONG).show();

            result = false;
        }

        cursor.moveToFirst();
        cursor.close();

        db.close();
        dbHelper.close();
        logHelper.createLog(old_measur_name, name.getEditText().getText().toString(), logHelper.ACTION_EDIT);
       /* DBHelper dbHelper1 = new DBHelper(getApplicationContext(), DBHelper.Logs);
        SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("log_text", getDateTime() + ": Замер изменен со значения '" + old_measur_name + "' на значение '" + name.getEditText().getText().toString() + "'");
        db1.insert(dbHelper1.getDatabaseName(), null, cv);
        dbHelper1.close();
        db1.close();*/
        return result;
    }

    private boolean save()
    {
        boolean result = false;

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.MEASUREMENTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("name", name.getEditText().getText().toString());

        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);
        logHelper.createLog(name.getEditText().getText().toString(), "", logHelper.ACTION_ADD);

        if (rowID != 0)
        {
            returnMeasurment.add(new Measurment((int) rowID, id, name.getEditText().getText().toString()));

            result = true;
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
                intent.putParcelableArrayListExtra("measurments", returnMeasurment);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("measurments", returnMeasurment);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
