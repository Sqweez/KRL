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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gosproj.gosproject.Adapters.RVMeasAdapter;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.MeasurmentActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Measurment;

import java.util.ArrayList;

public class ActFreeFragment extends Fragment implements RVOnClickInterface<Measurment>
{
    Context context;
    Resources resources;
    Activity activity;
    FloatingActionButton fab;
    final int REQUEST_ADD_MEASURMENT = 322;
    ArrayList<Measurment> measurments = new ArrayList<Measurment>();
    RecyclerView recyclerView;
    RVMeasAdapter rvAdapter;
    int id;
    LogsHelper logsHelper;
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
    public void setFabClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MeasurmentActivity.class);
                intent.putExtra("id", id);
                activity.startActivityForResult(intent, REQUEST_ADD_MEASURMENT);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_free_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();
        measurments.clear();
        logsHelper = new LogsHelper(LogsHelper.MEAS, context, activity, id);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        DBHelper dbHelper = new DBHelper(context, DBHelper.MEASUREMENTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Measurements WHERE idDept = ? ", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
            String value = cursor.getString(cursor.getColumnIndex("name"));
            measurments.add(new Measurment(id, idDept, value));
        }

        db.close();
        dbHelper.close();
        cursor.close();

        rvAdapter = new RVMeasAdapter(activity, measurments, this);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvAdapter);

        return view;
    }
    public void setResult(ArrayList<Measurment> measurments)
    {
        for (int i = 0; i<measurments.size(); i++)
        {
            int posChanged = -1;
            for (int l=0; l<this.measurments.size(); l++)
            {
                if (measurments.get(i).id == this.measurments.get(l).id)
                {
                    posChanged = l;
                    break;
                }
            }

            if (posChanged != -1)
            {
                this.measurments.set(posChanged, measurments.get(i));
            }
            else
            {
                this.measurments.add(measurments.get(i));
            }

            rvAdapter.notifyDataSetChanged();
        }
    }

    public void closeCheckUi()
    {
        if (rvAdapter != null)
        {
            rvAdapter.unSelectedElements();
            fab.show();
        }
    }

    public void removeElements()
    {
        rvAdapter.selectedElements();
        fab.hide();
    }

    public void removeElementsOk()
    {
        ArrayList<Measurment> removes =  rvAdapter.removeElements();
        DBHelper dbHelper = new DBHelper(context, DBHelper.MEASUREMENTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0; i<removes.size(); i++)
        {
            logsHelper.createLog(removes.get(i).name, "", LogsHelper.ACTION_DELETE);
            int delCount = db.delete(DBHelper.MEASUREMENTS, "id = ?",
                    new String[]{String.valueOf(removes.get(i).id)});
        }

        db.close();
        dbHelper.close();

        fab.show();
    }

    @Override
    public void onClick(Measurment obj) {
        Intent intent = new Intent(activity, MeasurmentActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("measurment", obj);
        activity.startActivityForResult(intent, REQUEST_ADD_MEASURMENT);
    }
}
