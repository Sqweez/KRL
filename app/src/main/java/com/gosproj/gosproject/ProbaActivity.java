package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Proba;

import java.util.ArrayList;

public class ProbaActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout name;
    TextInputLayout size;
    TextInputLayout place;
    TextInputLayout provider;
    TextInputLayout typeWork;
    String old_proba;
    String new_proba;
    Button save;
    Button saveAndAddNew;

    int id;
    LogsHelper logsHelper;
    Proba proba;

    ArrayList<Proba> returnProbs = new ArrayList<Proba>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proba);

        id = getIntent().getIntExtra("id", 0);
        proba = getIntent().getParcelableExtra("proba");

        activity = this;
        context = this;
        resources = getResources();

        logsHelper = new LogsHelper(LogsHelper.PROBA, context, activity, id);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);

        name = (TextInputLayout) findViewById(R.id.name);
        size = (TextInputLayout) findViewById(R.id.size);
        place = (TextInputLayout) findViewById(R.id.place);
        provider = (TextInputLayout) findViewById(R.id.provider);
        typeWork = (TextInputLayout) findViewById(R.id.typeWork);

        save = (Button) findViewById(R.id.save);
        saveAndAddNew = (Button) findViewById(R.id.saveAndAddNew);
        if (proba != null)
        {
            name.getEditText().setText(proba.name);
            size.getEditText().setText(String.valueOf(proba.size));
            place.getEditText().setText(proba.place);
            provider.getEditText().setText(proba.provider);
            typeWork.getEditText().setText(proba.typeWork);
            old_proba = proba.name + "|" + proba.size + "|" + proba.place + "|" + proba.provider + "|" + proba.typeWork;
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

                if (!size.getEditText().getText().toString().equals(""))
                {
                    if (!place.getEditText().getText().toString().equals(""))
                    {
                        if (!provider.getEditText().getText().toString().equals(""))
                        {
                            if (!typeWork.getEditText().getText().toString().equals(""))
                            {
                                isT = true;
                            }
                        }
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
            intent.putParcelableArrayListExtra("probs", returnProbs);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void onClickSaveAndAddNew()
    {
        if (clickSave())
        {
            name.getEditText().setText("");
            size.getEditText().setText("");
            place.getEditText().setText("");
            provider.getEditText().setText("");
            typeWork.getEditText().setText("");

            proba = null;
        }
    }

    public boolean clickSave()
    {
        if (checkFields())
        {
            if (proba == null)
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

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.PROBS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("UPDATE Probs SET name = ?, size = ?, place = ?,  provider = ?, typeWork = ?" +
                        "WHERE id = ?", new String[]{ name.getEditText().getText().toString(), size.getEditText().getText().toString(),
                        place.getEditText().getText().toString(), provider.getEditText().getText().toString(), typeWork.getEditText().getText().toString(), String.valueOf(proba.id)});

        if (cursor != null)
        {
            returnProbs.add(new Proba(proba.id, id, name.getEditText().getText().toString(), size.getEditText().getText().toString(),
                    place.getEditText().getText().toString(), provider.getEditText().getText().toString(), typeWork.getEditText().getText().toString()));

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
        new_proba = name.getEditText().getText().toString() + "|" + size.getEditText().getText().toString() + "|" +
                place.getEditText().getText().toString() + "|" + provider.getEditText().getText().toString() + "|" + typeWork.getEditText().getText().toString();
        logsHelper.createLog(old_proba, new_proba, LogsHelper.ACTION_EDIT);

        return result;
    }

    private boolean save()
    {
        boolean result = false;

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.PROBS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("name", name.getEditText().getText().toString());
        cv.put("size", size.getEditText().getText().toString());
        cv.put("place", place.getEditText().getText().toString());
        cv.put("provider", provider.getEditText().getText().toString());
        cv.put("typeWork", typeWork.getEditText().getText().toString());

        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

        if (rowID != 0)
        {
            returnProbs.add(new Proba((int) rowID, id, name.getEditText().getText().toString(), size.getEditText().getText().toString(),
                    place.getEditText().getText().toString(), provider.getEditText().getText().toString(), typeWork.getEditText().getText().toString()));

            result = true;
        }
        else
        {
            Toast.makeText(context, getResources().getString(R.string.fail_add_new_prob), Toast.LENGTH_LONG).show();

            result = false;
        }

        db.close();
        dbHelper.close();
        new_proba = name.getEditText().getText().toString() + "|" + size.getEditText().getText().toString() + "|" +
                place.getEditText().getText().toString() + "|" + provider.getEditText().getText().toString() + "|" + typeWork.getEditText().getText().toString();
        logsHelper.createLog("", new_proba, LogsHelper.ACTION_ADD);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("probs", returnProbs);
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
        intent.putParcelableArrayListExtra("probs", returnProbs);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
