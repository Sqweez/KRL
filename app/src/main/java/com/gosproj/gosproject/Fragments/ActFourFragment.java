package com.gosproj.gosproject.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gosproj.gosproject.Adapters.RVMainAdapter;
import com.gosproj.gosproject.Adapters.RVOtborProbAdapter;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.ProbaActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.MainCategory;
import com.gosproj.gosproject.Structures.Proba;
import com.gosproj.gosproject.Structures.SecondaryCategory;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ActFourFragment extends Fragment implements RVOnClickInterface<Proba>
{
    final int REQUEST_ADD_PROBA = 210;
    int id;

    Context context;
    Resources resources;
    Activity activity;

    LogsHelper logsHelper;

    RecyclerView recyclerView;

    ArrayList<Proba> probs = new ArrayList<Proba>();
    RVOtborProbAdapter rvOtborProbAdapter;

    FloatingActionButton fab;

    public static ActFourFragment getInstance(int id, FloatingActionButton fab)
    {
        Bundle args = new Bundle();
        ActFourFragment fragment = new ActFourFragment();
        fragment.setArguments(args);
        fragment.id = id;
        fragment.fab = fab;
        return fragment;
    }

    public ActFourFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_four_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        logsHelper = new LogsHelper(LogsHelper.PROBA, context, activity, id);

        probs.clear();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        DBHelper dbHelper = new DBHelper(context, DBHelper.PROBS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.PROBS + " WHERE idDept = ?", new String[]{String.valueOf(id)});


        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String place = cursor.getString(cursor.getColumnIndex("place"));
                String provider = cursor.getString(cursor.getColumnIndex("provider"));
                String typeWork = cursor.getString(cursor.getColumnIndex("typeWork"));

                probs.add(new Proba(id, idDept, name, size, place, provider, typeWork));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        rvOtborProbAdapter = new RVOtborProbAdapter(activity, probs, this);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvOtborProbAdapter);

        return view;
    }

    public void setFabClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProbaActivity.class);
                intent.putExtra("id", id);
                activity.startActivityForResult(intent, REQUEST_ADD_PROBA);
                //intent.putExtra("proba", new Proba());
            }
        });

    }

    public void setResult(ArrayList<Proba> probs)
    {
        for (int i = 0; i<probs.size(); i++)
        {
            int posChanged = -1;
            for (int l=0; l<this.probs.size(); l++)
            {
                if (probs.get(i).id == this.probs.get(l).id)
                {
                    posChanged = l;
                    break;
                }
            }

            if (posChanged != -1)
            {
                this.probs.set(posChanged, probs.get(i));
            }
            else
            {
                this.probs.add(probs.get(i));
            }

            rvOtborProbAdapter.notifyDataSetChanged();
        }
    }

    public void closeCheckUi()
    {
        if (rvOtborProbAdapter != null)
        {
            rvOtborProbAdapter.unSelectedElements();
            fab.show();
        }
    }

    public void removeElements()
    {
        rvOtborProbAdapter.selectedElements();
        fab.hide();
    }

    public void removeElementsOk()
    {
        ArrayList<Proba> removes =  rvOtborProbAdapter.removeElements();

        DBHelper dbHelper = new DBHelper(context, DBHelper.PROBS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0; i<removes.size(); i++)
        {
            String old_proba = removes.get(i).name + "|" + removes.get(i).size
                    + "|" + removes.get(i).place + "|" + removes.get(i).provider + "|" + removes.get(i).typeWork;
            logsHelper.createLog(old_proba, "", LogsHelper.ACTION_DELETE);
            int delCount = db.delete(DBHelper.PROBS, "id = ?",
                    new String[]{String.valueOf(removes.get(i).id)});
        }

        db.close();
        dbHelper.close();

        fab.show();
    }

    @Override
    public void onClick(Proba obj)
    {
        Log.d("MYLOG", obj.name);
        Intent intent = new Intent(activity, ProbaActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("proba", obj);
        activity.startActivityForResult(intent, REQUEST_ADD_PROBA);
    }
}
