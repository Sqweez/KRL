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
import com.gosproj.gosproject.Structures.Measurment;

import java.util.ArrayList;

public class RVMeasAdapter extends RecyclerView.Adapter<RVMeasAdapter.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Measurment> measurments;

    boolean select = false;

    ArrayList<Measurment> removes= new ArrayList<Measurment>();

    RVOnClickInterface<Measurment> rvOnClickInterface;

    public RVMeasAdapter(Activity activity, ArrayList<Measurment> measurments, RVOnClickInterface<Measurment> rvOnClickInterface)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.measurments = measurments;
        this.rvOnClickInterface = rvOnClickInterface;
    }

    @Override
    public RVMeasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.defect_item, parent, false);

        RVMeasAdapter.ViewHolder vh = new RVMeasAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVMeasAdapter.ViewHolder holder, final int position)
    {
        holder.name.setText(measurments.get(position).name);
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
                    removes.add(measurments.get(position));
                }
                else
                {
                    removes.remove(measurments.get(position));
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
                    rvOnClickInterface.onClick(measurments.get(position));
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

    public ArrayList<Measurment> removeElements()
    {
        select = false;

        for (int i = 0; i<removes.size(); i++)
        {
            for (int l = 0; l<measurments.size(); l++)
            {
                if (removes.get(i).id == measurments.get(l).id)
                {
                    measurments.remove(l);
                    notifyItemRemoved(l);
                }
            }
        }

        for (int l = 0; l<measurments.size(); l++)
        {
            notifyItemChanged(l);
        }

        // unSelectedElements();

        select = false;

        return removes;
    }

    @Override
    public int getItemCount()
    {
        return measurments.size();
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