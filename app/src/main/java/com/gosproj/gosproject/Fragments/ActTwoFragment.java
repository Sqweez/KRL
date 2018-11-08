package com.gosproj.gosproject.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gosproj.gosproject.Adapters.RVAgentAdapter;
import com.gosproj.gosproject.Adapters.RVSigningAdapter;
import com.gosproj.gosproject.AgentActivity;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.SignatureActivity;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.MainAgentCategory;
import com.gosproj.gosproject.Structures.MainCategory;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ActTwoFragment extends Fragment implements RVOnClickInterface<Agent>
{
    Context context;
    Resources resources;
    Activity activity;
    RecyclerView recyclerView;
    RVSigningAdapter rvAgentAdapter;
    ArrayList<MainAgentCategory> mainAgentCategories = new ArrayList<MainAgentCategory>();
    int id;
    TextView check;
    final int REQUEST_ADD_SIGNATURE = 123;
    boolean shouldUpdate = false;
    public static ActTwoFragment getInstance(int id)
    {
        Bundle args = new Bundle();
        ActTwoFragment fragment = new ActTwoFragment();
        fragment.setArguments(args);
        fragment.id = id;
        return fragment;
    }

    public ActTwoFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_two_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();
        mainAgentCategories.clear();
        check = (TextView) view.findViewById(R.id.secondaryText);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        DBHelper dbHelper = DBHelper.getInstance(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Agents + " WHERE idDept = ?", new String[]{String.valueOf(id)});
        SharedPreferences user_info = context.getSharedPreferences("com.gosproj.gosproject", Context.MODE_PRIVATE);
        String rgu_name = user_info.getString("rgu_name", "РГУ ТЕСТ");
        mainAgentCategories.add(new MainAgentCategory("Подрядочная организация", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Заказчик", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Инженерная служба", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Субподрядчик", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Уполномоченные органы", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Авторский надзор", new ArrayList<Agent>()));
        mainAgentCategories.add(new MainAgentCategory("Сотрудники " + rgu_name, new ArrayList<Agent>()));

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
                Boolean isRGU = (cursor.getInt(cursor.getColumnIndex("isRGU")) == 1) ? true : false;
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
                    mainAgentCategories.get(4).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg,false, blob));
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
                else if(isRGU){
                    mainAgentCategories.get(6).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider, isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                }
            }

            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        rvAgentAdapter = new RVSigningAdapter(activity, mainAgentCategories, this);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvAgentAdapter);

        return view;
        }
    @Override
    public void onClick(Agent obj) {
        Intent intent = new Intent(activity, SignatureActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("agent", obj);
        activity.startActivityForResult(intent, REQUEST_ADD_SIGNATURE);

    }
    public void onResume(){
        super.onResume();
        if(shouldUpdate){
            mainAgentCategories.clear();
            DBHelper dbHelper = DBHelper.getInstance(context, DBHelper.Agents);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Agents + " WHERE idDept = ?", new String[]{String.valueOf(id)});
            SharedPreferences user_info = context.getSharedPreferences("com.gosproj.gosproject", Context.MODE_PRIVATE);
            String rgu_name = user_info.getString("rgu_name", "РГУ ТЕСТ");
            mainAgentCategories.add(new MainAgentCategory("Подрядочная организация", new ArrayList<Agent>()));
            mainAgentCategories.add(new MainAgentCategory("Заказчик", new ArrayList<Agent>()));
            mainAgentCategories.add(new MainAgentCategory("Инженерная служба", new ArrayList<Agent>()));
            mainAgentCategories.add(new MainAgentCategory("Субподрядчик", new ArrayList<Agent>()));
            mainAgentCategories.add(new MainAgentCategory("Уполномоченные органы", new ArrayList<Agent>()));
            mainAgentCategories.add(new MainAgentCategory("Авторский надзор", new ArrayList<Agent>()));
            mainAgentCategories.add(new MainAgentCategory("Сотрудники " + rgu_name, new ArrayList<Agent>()));

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
                    Boolean isRGU = (cursor.getInt(cursor.getColumnIndex("isRGU")) == 1) ? true : false;
                    Log.d("ADD_POS", id + " ");

                    if (isProvider)
                    {
                        Log.d("ADD_POS", id + " provider");
                        mainAgentCategories.get(0).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                    }
                    else if (isCustomer)
                    {
                        Log.d("ADD_POS", id + " customer");
                        mainAgentCategories.get(1).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                    }
                    else if (isEngineeringService)
                    {
                        Log.d("ADD_POS", id + " engeneiresService");
                        mainAgentCategories.get(2).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                    }
                    else if (isUorg)
                    {
                        Log.d("ADD_POS", id + " engeneiresService");
                        mainAgentCategories.get(4).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                    }
                    else if (isAvtNadzor)
                    {
                        Log.d("ADD_POS", id + " engeneiresService");
                        mainAgentCategories.get(5).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                    }
                    else if (isSubProvider)
                    {
                        Log.d("ADD_POS", id + " engeneiresService");
                        mainAgentCategories.get(3).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                    }
                    else if(isRGU){
                        mainAgentCategories.get(6).agents.add(new Agent(id, idDept, nameCompany, rang, fio, isProvider,isSubProvider, isCustomer, isEngineeringService,isAvtNadzor,isUorg, isRGU, blob));
                    }
                }

                while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            dbHelper.close();

            rvAgentAdapter = new RVSigningAdapter(activity, mainAgentCategories, this);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(rvAgentAdapter);
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        shouldUpdate = true;
    }
    @Override
    public void onPause(){
        super.onPause();
        shouldUpdate = true;
    }

    public void SetSignResult(Agent agent){
        if (agent != null)
        {
            int posChnage = -1;

            ArrayList<Agent> agents = null;

            if (agent.isPodryadchik)
            {
                agents = mainAgentCategories.get(0).agents;
            }
            else if (agent.isZakazchik)
            {
                agents = mainAgentCategories.get(1).agents;
            }
            else if (agent.isEngineeringService)
            {
                agents = mainAgentCategories.get(2).agents;
            }
            else if (agent.isUpolnomochOrg)
            {
                agents = mainAgentCategories.get(4).agents;
            }
            else if (agent.isAvtNadzor)
            {
                agents = mainAgentCategories.get(5).agents;
            }
            else if (agent.isSubPodryadchik)
            {
                agents = mainAgentCategories.get(3).agents;
            }
            else{
                agents = mainAgentCategories.get(6).agents;
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


                for (int i=0; i<7; i++)
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
                    agents.add(agent);
                }
                else
                {
                    agents.add(agent);
                }

                if (agent.isPodryadchik)
                {
                    mainAgentCategories.get(0).agents = agents;
                }
                else if (agent.isZakazchik)
                {
                    mainAgentCategories.get(1).agents = agents;
                }
                else if (agent.isEngineeringService)
                {
                    mainAgentCategories.get(2).agents = agents;
                }
                else if (agent.isUpolnomochOrg)
                {
                    mainAgentCategories.get(4).agents = agents;
                }
                else if (agent.isAvtNadzor)
                {
                    mainAgentCategories.get(5).agents = agents;
                }
                else if (agent.isSubPodryadchik)
                {
                    mainAgentCategories.get(3).agents = agents;
                }
                else{
                    mainAgentCategories.get(6).agents = agents;
                }
            }
            rvAgentAdapter.notifyDataSetChanged();
        }
    }
}
