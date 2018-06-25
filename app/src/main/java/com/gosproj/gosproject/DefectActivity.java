package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.Proba;

import java.util.ArrayList;

public class DefectActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout name;
    TextInputLayout killo;
    TextInputLayout comment;

    Button save;
    Button saveAndAddNew;

    int id;
    Defects defects;

    ArrayList<Defects> returnDefects = new ArrayList<Defects>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect);

        id = getIntent().getIntExtra("id", 0);
        defects = getIntent().getParcelableExtra("defect");

        activity = this;
        context = this;
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);

        name = (TextInputLayout) findViewById(R.id.name_dcm);
        killo = (TextInputLayout) findViewById(R.id.killo);
        comment = (TextInputLayout) findViewById(R.id.comment);

        save = (Button) findViewById(R.id.save);
        saveAndAddNew = (Button) findViewById(R.id.saveAndAddNew);

        if (defects != null)
        {
            name.getEditText().setText(defects.name);
            killo.getEditText().setText(defects.kilometr);
            comment.getEditText().setText(defects.comment);

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
            if (!killo.getEditText().getText().toString().equals(""))
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
            intent.putParcelableArrayListExtra("defects", returnDefects);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void onClickSaveAndAddNew()
    {
        if (clickSave())
        {
            name.getEditText().setText("");
            killo.getEditText().setText("");
            comment.getEditText().setText("");

            defects = null;
        }
    }

    public boolean clickSave()
    {
        if (checkFields())
        {
            if (defects == null)
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

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.DEFECTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("UPDATE Defects SET name = ?, kilometr = ?, comment = ?" +
                        "WHERE id = ?", new String[]{ name.getEditText().getText().toString(), killo.getEditText().getText().toString(), comment.getEditText().getText().toString(), String.valueOf(defects.id)});

        if (cursor != null)
        {
            returnDefects.add(new Defects(defects.id, id, name.getEditText().getText().toString(), killo.getEditText().getText().toString(), comment.getEditText().getText().toString()));

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

        return result;
    }

    private boolean save()
    {
        boolean result = false;

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.DEFECTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("name", name.getEditText().getText().toString());
        cv.put("kilometr", killo.getEditText().getText().toString());
        cv.put("comment", comment.getEditText().getText().toString());

        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

        if (rowID != 0)
        {
            returnDefects.add(new Defects((int) rowID, id, name.getEditText().getText().toString(), killo.getEditText().getText().toString(), comment.getEditText().getText().toString()));

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
                intent.putParcelableArrayListExtra("defects", returnDefects);
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
        intent.putParcelableArrayListExtra("defects", returnDefects);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
