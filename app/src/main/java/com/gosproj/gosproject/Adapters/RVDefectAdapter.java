package com.gosproj.gosproject.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.Proba;

import java.util.ArrayList;

public class RVDefectAdapter extends RecyclerView.Adapter<RVDefectAdapter.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Defects> def;

    boolean select = false;

    ArrayList<Defects> removes= new ArrayList<Defects>();

    RVOnClickInterface<Defects> rvOnClickInterface;

    public RVDefectAdapter(Activity activity, ArrayList<Defects> defectses, RVOnClickInterface<Defects> rvOnClickInterface)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.def = defectses;
        this.rvOnClickInterface = rvOnClickInterface;
    }

    @Override
    public RVDefectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.defect_item, parent, false);

        RVDefectAdapter.ViewHolder vh = new RVDefectAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVDefectAdapter.ViewHolder holder, final int position)
    {
        holder.name.setText(def.get(position).name);
        holder.checkBox.setChecked(false);

        if (!select)
        {
            holder.checkBox.setVisibility(View.GONE);
        }
        else
        {
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    removes.add(def.get(position));
                }
                else
                {
                    removes.remove(def.get(position));
                }
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!select)
                {
                    rvOnClickInterface.onClick(def.get(position));
                }
                else{
                    boolean isChecked = holder.checkBox.isChecked();
                    if(isChecked){
                        holder.checkBox.setChecked(false);
                        removes.remove(def.get(position));
                    }
                    else{
                        holder.checkBox.setChecked(true);
                        removes.add(def.get(position));
                    }
                }
            }
        });
    }

    public void selectedElements()
    {
        select = true;
        notifyDataSetChanged();
    }

    public void unSelectedElements()
    {
        select = false;
        notifyDataSetChanged();
    }

    public ArrayList<Defects> removeElements()
    {
        select = false;

        for (int i = 0; i<removes.size(); i++)
        {
            for (int l = 0; l<def.size(); l++)
            {
                if (removes.get(i).id == def.get(l).id)
                {
                    def.remove(l);
                    notifyItemRemoved(l);
                }
            }
        }

        for (int l = 0; l<def.size(); l++)
        {
            notifyItemChanged(l);
        }


        select = false;

        return removes;
    }

    @Override
    public int getItemCount()
    {
        return def.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        CheckBox checkBox;
        View view;

        public ViewHolder(View v)
        {
            super(v);

            name = (TextView) v.findViewById(R.id.name);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox);
            view = v;
        }
    }
}
