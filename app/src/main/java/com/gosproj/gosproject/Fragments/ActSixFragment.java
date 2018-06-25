package com.gosproj.gosproject.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gosproj.gosproject.Adapters.RVAgentAdapter;
import com.gosproj.gosproject.Adapters.RVDefectAdapter;
import com.gosproj.gosproject.AgentActivity;
import com.gosproj.gosproject.DefectActivity;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.MainAgentCategory;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ActSixFragment extends Fragment implements RVOnClickInterface<Agent>
{
    final int REQUEST_ADD_AGENT = 230;
    int id;

    Context context;
    Resources resources;
    Activity activity;

    RecyclerView recyclerView;
    FloatingActionButton fab;

    ArrayList<MainAgentCategory> mainAgentCategories = new ArrayList<MainAgentCategory>();
    RVAgentAdapter rvAgentAdapter;

    public static ActSixFragment getInstance(int id, FloatingActionButton fab)
    {
        Bundle args = new Bundle();
        ActSixFragment fragment = new ActSixFragment();
        fragment.setArguments(args);
        fragment.id = id;
        fragment.fab = fab;
        return fragment;
    }

    public ActSixFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_six_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        mainAgentCategories.clear();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        DBHelper dbHelper = new DBHelper(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Agents + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        mainAgentCategories.add(new MainAgentCategory("Подрядочная организация", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Заказчик", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Инженерная служба", new ArrayList<Agent>()));

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String nameCompany = cursor.getString(cursor.getColumnIndex("nameCompany"));
                String fio = cursor.getString(cursor.getColumnIndex("fio"));
                String rang = cursor.getString(cursor.getColumnIndex("rang"));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex("img"));
                Boolean isProvider = (cursor.getInt(cursor.getColumnIndex("isProvider")) == 1)? true : false;
                Boolean isCustomer = (cursor.getInt(cursor.getColumnIndex("isCustomer")) == 1)? true : false;
                Boolean isEngineeringService = (cursor.getInt(cursor.getColumnIndex("isEngineeringService")) == 1)? true : false;

                Log.d("ADD_POS", id + " ");

                if (isProvider)
                {
                    Log.d("ADD_POS", id + " provider");
                    mainAgentCategories.get(0).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider, isCustomer, isEngineeringService, blob));
                }
                else if (isCustomer)
                {
                    Log.d("ADD_POS", id + " customer");
                    mainAgentCategories.get(1).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider, isCustomer, isEngineeringService, blob));
                }
                else if (isEngineeringService)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(2).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider, isCustomer, isEngineeringService, blob));
                }
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        rvAgentAdapter = new RVAgentAdapter(activity, mainAgentCategories, this);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvAgentAdapter);

        return view;
    }

    public void setResult(Agent agent)
    {
        if (agent != null)
        {
            int posChnage = -1;

            ArrayList<Agent> agents = null;

            if (agent.isProvider)
            {
                agents = mainAgentCategories.get(0).agents;
            }
            else if (agent.isCustomer)
            {
                agents = mainAgentCategories.get(1).agents;
            }
            else if (agent.isEngineeringService)
            {
                agents = mainAgentCategories.get(2).agents;
            }

            if (agents != null)
            {
                for (int i =0; i<agents.size(); i++)
                {
                    if (agents.get(i).id == agent.id)
                    {
                        posChnage = i;

                        break;
                    }
                }


                for (int i=0; i<3; i++)
                {
                    boolean result = false;

                    for (int l=0; l<mainAgentCategories.get(i).agents.size(); l++)
                    {
                        if (mainAgentCategories.get(i).agents.get(l).id == agent.id)
                        {
                            mainAgentCategories.get(i).agents.remove(l);

                            result = true;

                            break;
                        }
                    }

                    if (result)
                    {
                        break;
                    }
                }

                if (posChnage != -1)
                {
                    agents.set(posChnage, agent);
                }
                else
                {
                    agents.add(agent);
                }

                if (agent.isProvider)
                {
                    mainAgentCategories.get(0).agents = agents;
                }
                else if (agent.isCustomer)
                {
                    mainAgentCategories.get(1).agents = agents;
                }
                else if (agent.isEngineeringService)
                {
                    mainAgentCategories.get(2).agents = agents;
                }
            }
            rvAgentAdapter.notifyDataSetChanged();
        }
    }

    public void setFabClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AgentActivity.class);
                intent.putExtra("id", id);
                activity.startActivityForResult(intent, REQUEST_ADD_AGENT);
                //intent.putExtra("proba", new Proba());
            }
        });
    }

    public void closeCheckUi()
    {
        if (rvAgentAdapter != null)
        {
            rvAgentAdapter.unSelectedElements();
            fab.show();
        }
    }

    public void removeElements()
    {
        rvAgentAdapter.selectedElements();
        fab.hide();
    }

    public void removeElementsOk()
    {
        ArrayList<Agent> removes =  rvAgentAdapter.removeElements();

        DBHelper dbHelper = new DBHelper(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0; i<removes.size(); i++)
        {
            int delCount = db.delete(DBHelper.Agents, "id = ?",
                    new String[]{String.valueOf(removes.get(i).id)});
        }

        db.close();
        dbHelper.close();

        fab.show();
    }

    @Override
    public void onClick(Agent obj)
    {
        Intent intent = new Intent(activity, AgentActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("agent", obj);
        activity.startActivityForResult(intent, REQUEST_ADD_AGENT);
    }
}
