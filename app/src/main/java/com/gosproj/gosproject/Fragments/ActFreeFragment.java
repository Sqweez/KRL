package com.gosproj.gosproject.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gosproj.gosproject.DefectActivity;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.R;

public class ActFreeFragment extends Fragment
{
    Context context;
    Resources resources;
    Activity activity;
    FloatingActionButton fab;
    final int REQUEST_ADD_DEFECT = 220;

    String rgu;

    EditText editText;

    int id;
    public void setFabClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DefectActivity.class);
                intent.putExtra("id", id);
                activity.startActivityForResult(intent, REQUEST_ADD_DEFECT);
                //intent.putExtra("proba", new Proba());
            }
        });
    }
    public static ActFreeFragment getInstance(int id, FloatingActionButton fab)
    {
        Bundle args = new Bundle();
        ActFreeFragment fragment = new ActFreeFragment();
        fragment.setArguments(args);
        fragment.id = id;
        fragment.fab = fab;
        return fragment;
    }

    public ActFreeFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_free_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        editText = (EditText) view.findViewById(R.id.editText);

        DBHelper dbHelper = new DBHelper(context, DBHelper.MEASUREMENTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Measurements WHERE idDept = ? ", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            String value = cursor.getString(cursor.getColumnIndex("value"));
            editText.setText(Html.fromHtml(value));
        }

        db.close();
        dbHelper.close();
        cursor.close();

        /*
        editText.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    text += "|";

                    Log.d("TEXT_EDI", text);
                }
                else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL)
                {
                    if (!text.equals(""))
                    {
                        text = text.substring(0, text.length() - 1);
                    }

                    Log.d("TEXT_EDI", text);
                }
                else
                {
                    text += editText.getText().toString();

                    Log.d("TEXT_EDI", text);
                }
                return false;
            }
        });*/

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    if (!editText.getText().toString().equals(""))
                    {
                        DBHelper dbHelper = new DBHelper(context, DBHelper.MEASUREMENTS);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        Cursor cursor = db.rawQuery("SELECT * FROM Measurements WHERE idDept = ? ", new String[]{String.valueOf(id)});

                        String text = Html.toHtml(editText.getText());
                        text = text.replaceAll("</p>", "");
                        text = text.replaceAll("<p dir=\"ltr\">", "");

                        Log.d("myLog_TEXT", text);

                        if (!cursor.moveToFirst())
                        {
                            ContentValues cv = new ContentValues();

                            cv.put("idDept", String.valueOf(id));
                            cv.put("value", text);

                            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);
                        }
                        else
                        {
                            Cursor cursorUpdate = db.rawQuery("UPDATE Measurements SET value = ?" +
                                    "WHERE idDept = ?", new String[]{ text, String.valueOf(id)});

                            cursorUpdate.moveToFirst();
                            cursorUpdate.close();
                        }

                        db.close();
                        dbHelper.close();
                        cursor.close();
                    }
                }
            }
        });

        return view;
    }
}
