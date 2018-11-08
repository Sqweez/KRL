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
import android.widget.Toast;

import com.gosproj.gosproject.Adapters.RVAgentAdapter;
import com.gosproj.gosproject.Adapters.RVDefectAdapter;
import com.gosproj.gosproject.AgentActivity;
import com.gosproj.gosproject.DefectActivity;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Helper.HelperActivity;
import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Services.LogsHelper;
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
    LogsHelper logsHelper;
    String oldRole;
    ActTwoFragment actTwoFragment;

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

        logsHelper = new LogsHelper(LogsHelper.AGENT, context, activity, id);
        mainAgentCategories.clear();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        DBHelper dbHelper = DBHelper.getInstance(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Agents + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        mainAgentCategories.add(new MainAgentCategory("Подрядочная организация", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Заказчик", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Инженерная служба", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Субподрядчик", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Уполномоченные органы", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Авторский надзор", new ArrayList<Agent>()));

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
                Boolean isProvider = (cursor.getInt(cursor.getColumnIndex("isPodryadchik")) == 1)? true : false;
                Boolean isUorg = (cursor.getInt(cursor.getColumnIndex("isUpolnomochOrg")) == 1)? true : false;
                Boolean isCustomer = (cursor.getInt(cursor.getColumnIndex("isZakazchik")) == 1)? true : false;
                Boolean isSubProvider = (cursor.getInt(cursor.getColumnIndex("isSubPodryadchik")) == 1)? true : false;
                Boolean isAvtNadzor = (cursor.getInt(cursor.getColumnIndex("isAvtNadzor")) == 1)? true : false;
                Boolean isEngineeringService = (cursor.getInt(cursor.getColumnIndex("isEngineeringService")) == 1)? true : false;

                Log.d("ADD_POS", id + " ");

                if (isProvider)
                {
                    Log.d("ADD_POS", id + " provider");
                    mainAgentCategories.get(0).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isCustomer)
                {
                    Log.d("ADD_POS", id + " customer");
                    mainAgentCategories.get(1).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isEngineeringService)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(2).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isUorg)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(4).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isAvtNadzor)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(5).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isSubProvider)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(3).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
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

    @Override
    public void onResume(){
        super.onResume();
        mainAgentCategories.clear();
        DBHelper dbHelper = new DBHelper(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Agents + " WHERE idDept = ?", new String[]{String.valueOf(id)});
        mainAgentCategories.add(new MainAgentCategory("Подрядочная организация", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Заказчик", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Инженерная служба", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Субподрядчик", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Уполномоченные органы", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Авторский надзор", new ArrayList<Agent>()));
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
                Boolean isProvider = (cursor.getInt(cursor.getColumnIndex("isPodryadchik")) == 1)? true : false;
                Boolean isUorg = (cursor.getInt(cursor.getColumnIndex("isUpolnomochOrg")) == 1)? true : false;
                Boolean isCustomer = (cursor.getInt(cursor.getColumnIndex("isZakazchik")) == 1)? true : false;
                Boolean isSubProvider = (cursor.getInt(cursor.getColumnIndex("isSubPodryadchik")) == 1)? true : false;
                Boolean isAvtNadzor = (cursor.getInt(cursor.getColumnIndex("isAvtNadzor")) == 1)? true : false;
                Boolean isEngineeringService = (cursor.getInt(cursor.getColumnIndex("isEngineeringService")) == 1)? true : false;
                Log.d("ADD_POS", id + " ");

                if (isProvider)
                {
                    Log.d("ADD_POS", id + " provider");
                    mainAgentCategories.get(0).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isCustomer)
                {
                    Log.d("ADD_POS", id + " customer");
                    mainAgentCategories.get(1).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isEngineeringService)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(2).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isUorg)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(4).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));;
                }
                else if (isAvtNadzor)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(5).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
                }
                else if (isSubProvider)
                {
                    Log.d("ADD_POS", id + " engeneiresService");
                    mainAgentCategories.get(3).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, false, blob));
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
    }
    public void setFabClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AgentActivity.class);
                intent.putExtra("id", id);
                activity.startActivityForResult(intent, REQUEST_ADD_AGENT);
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
    public void setResult(Agent agent)
    {
        if (agent != null)
        {
            int posChnage = -1;

            ArrayList<Agent> agentsM = null;

            if (agent.isPodryadchik)
            {
                agentsM = mainAgentCategories.get(0).agents;
            }
            else if (agent.isZakazchik)
            {
                agentsM = mainAgentCategories.get(1).agents;
            }
            else if (agent.isEngineeringService)
            {
                agentsM = mainAgentCategories.get(2).agents;
            }
            else if (agent.isUpolnomochOrg)
            {
                agentsM = mainAgentCategories.get(4).agents;
            }
            else if (agent.isAvtNadzor)
            {
                agentsM = mainAgentCategories.get(5).agents;
            }
            else if (agent.isSubPodryadchik)
            {
                agentsM = mainAgentCategories.get(3).agents;
            }
            if (agentsM != null)
            {
                for (int i =0; i<agentsM.size(); i++)
                {
                    if (agentsM.get(i).id == agent.id)
                    {
                        posChnage = i;

                        break;
                    }
                }




                if (posChnage != -1)
                {
                    Log.d("Agents", "" +  agentsM.size());
                    agentsM.set(posChnage, agent);

                }
                else
                {
                    agentsM.add(agent);
                }

                if (agent.isPodryadchik)
                {
                    mainAgentCategories.get(0).agents = agentsM;
                }
                else if (agent.isZakazchik)
                {
                    mainAgentCategories.get(1).agents = agentsM;
                }
                else if (agent.isEngineeringService)
                {
                    mainAgentCategories.get(2).agents = agentsM;
                }
                else if (agent.isUpolnomochOrg)
                {
                    mainAgentCategories.get(4).agents = agentsM;
                }
                else if (agent.isAvtNadzor)
                {
                    mainAgentCategories.get(5).agents = agentsM;
                }
                else if (agent.isSubPodryadchik)
                {
                    mainAgentCategories.get(3).agents = agentsM;
                }
            }
            rvAgentAdapter.notifyDataSetChanged();
        }
    }
    public void removeElementsOk()
    {
        ArrayList<Agent> removes =  rvAgentAdapter.removeElements();
        DBHelper dbHelper = DBHelper.getInstance(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i=0; i<removes.size(); i++)
        {
            if(removes.get(i).isZakazchik){
                oldRole = "заказчика";
            }
            else if(removes.get(i).isPodryadchik){
                oldRole = "подрядчика";
            }
            else if(removes.get(i).isEngineeringService){
                oldRole = "инженерной службы";
            }
            else if(removes.get(i).isAvtNadzor){
                oldRole = "авторского надзора";
            }
            else if(removes.get(i).isSubPodryadchik){
                oldRole = "субподрядчика";
            }
            else if(removes.get(i).isUpolnomochOrg){
                oldRole = "уполномоченных органов";
            }
            String agent = removes.get(i).nameCompany + "|" + removes.get(i).rang + "|" + removes.get(i).fio + "|" + oldRole;
            logsHelper.createLog(agent, "", LogsHelper.ACTION_DELETE);
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
        if(obj.blob == null){
            Intent intent = new Intent(activity, AgentActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("agent", obj);
            activity.startActivityForResult(intent, REQUEST_ADD_AGENT);
        }
    }
}
