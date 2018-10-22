package com.gosproj.gosproject.Adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gosproj.gosproject.ActActivity;
import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.MainCategory;
import com.gosproj.gosproject.Structures.Proba;
import com.gosproj.gosproject.Structures.SecondaryCategory;

import java.util.ArrayList;

public class RVOtborProbAdapter extends RecyclerView.Adapter<RVOtborProbAdapter.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Proba> probs;

    public boolean select = false;

    ArrayList<Proba> removes= new ArrayList<Proba>();

    RVOnClickInterface<Proba> rvOnClickInterface;

    public RVOtborProbAdapter(Activity activity, ArrayList<Proba> probs, RVOnClickInterface<Proba> rvOnClickInterface)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.probs = probs;
        this.rvOnClickInterface = rvOnClickInterface;
    }

    @Override
    public RVOtborProbAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.prob_item, parent, false);

        RVOtborProbAdapter.ViewHolder vh = new RVOtborProbAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVOtborProbAdapter.ViewHolder holder, final int position)
    {
        String value = probs.get(position).name + " (" + String.valueOf(probs.get(position).size) + ")";
        holder.name.setText(value);
        holder.value.setText(probs.get(position).place);
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
                    removes.add(probs.get(position));
                }
                else
                {
                    removes.remove(probs.get(position));
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
                    rvOnClickInterface.onClick(probs.get(position));
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

    public ArrayList<Proba> removeElements()
    {
        select = false;

        for (int i = 0; i<removes.size(); i++)
        {
            for (int l = 0; l<probs.size(); l++)
            {
                if (removes.get(i).id == probs.get(l).id)
                {
                    probs.remove(l);
                    notifyItemRemoved(l);
                }
            }
        }

        for (int l = 0; l<probs.size(); l++)
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
        return probs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;

        TextView name;
        TextView value;
        CheckBox checkBox;

        public ViewHolder(View v)
        {
            super(v);

            name = (TextView) v.findViewById(R.id.name);
            value = (TextView) v.findViewById(R.id.value);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox);

            view = v;
        }
    }
}
