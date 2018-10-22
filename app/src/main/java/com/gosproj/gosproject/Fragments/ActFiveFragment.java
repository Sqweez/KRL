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

import com.gosproj.gosproject.Adapters.RVDefectAdapter;
import com.gosproj.gosproject.DefectActivity;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.Proba;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ActFiveFragment extends Fragment implements RVOnClickInterface<Defects>
{
    final int REQUEST_ADD_DEFECT = 220;
    int id;

    Context context;
    Resources resources;
    Activity activity;

    RecyclerView recyclerView;
    FloatingActionButton fab;

    LogsHelper logsHelper;

    ArrayList<Defects> def = new ArrayList<Defects>();
    RVDefectAdapter rvDefectAdapter;

    public static ActFiveFragment getInstance(int id, FloatingActionButton fab)
    {
        Bundle args = new Bundle();
        ActFiveFragment fragment = new ActFiveFragment();
        fragment.setArguments(args);
        fragment.id = id;
        fragment.fab = fab;
        return fragment;
    }

    public ActFiveFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_five_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        def.clear();

        logsHelper = new LogsHelper(LogsHelper.DEFECT, context, activity, id);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEFECTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.DEFECTS + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                def.add(new Defects(id, idDept, name));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        rvDefectAdapter = new RVDefectAdapter(activity, def, this);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvDefectAdapter);

        return view;
    }

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

    public void setResult(ArrayList<Defects> defectses)
    {
        for (int i = 0; i<defectses.size(); i++)
        {
            int posChanged = -1;
            for (int l=0; l<this.def.size(); l++)
            {
                if (defectses.get(i).id == this.def.get(l).id)
                {
                    posChanged = l;
                    break;
                }
            }

            if (posChanged != -1)
            {
                this.def.set(posChanged, defectses.get(i));
            }
            else
            {
                this.def.add(defectses.get(i));
            }

            rvDefectAdapter.notifyDataSetChanged();
        }
    }

    public void closeCheckUi()
    {
        if (rvDefectAdapter != null)
        {
            rvDefectAdapter.unSelectedElements();
            fab.show();
        }
    }

    public void removeElements()
    {
        rvDefectAdapter.selectedElements();
        fab.hide();
    }

    public void removeElementsOk()
    {
        ArrayList<Defects> removes =  rvDefectAdapter.removeElements();

        DBHelper dbHelper = new DBHelper(context, DBHelper.DEFECTS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0; i<removes.size(); i++)
        {
            String defect = removes.get(i).name;
            logsHelper.createLog(defect, "", LogsHelper.ACTION_DELETE);
            int delCount = db.delete(DBHelper.DEFECTS, "id = ?",
                    new String[]{String.valueOf(removes.get(i).id)});
        }

        db.close();
        dbHelper.close();

        fab.show();
    }

    @Override
    public void onClick(Defects obj)
    {
        Intent intent = new Intent(activity, DefectActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("defect", obj);
        activity.startActivityForResult(intent, REQUEST_ADD_DEFECT);
    }
}
