package com.gosproj.gosproject.Adapters;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gosproj.gosproject.Interfaces.RVOnClickInterface;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.MainAgentCategory;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RVSigningAdapter extends RecyclerView.Adapter<RVSigningAdapter.ViewHolder> {
    Activity activity;
    Resources resources;
    Context context;

    TextView check;

    ArrayList<MainAgentCategory> agents;

    boolean select = false;

    ArrayList<Agent> removes= new ArrayList<Agent>();

    RVOnClickInterface rvOnClickInterface;
    boolean isOpen[];
    public void showCheck(){
        check.setVisibility(TextView.VISIBLE);
    }
    public RVSigningAdapter(Activity activity, ArrayList<MainAgentCategory> agents, RVOnClickInterface rvOnClickInterface)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.agents = agents;
        this.rvOnClickInterface = rvOnClickInterface;

        isOpen = new boolean[agents.size()];

        for (int i=0; i<isOpen.length; i++)
        {
            isOpen[i] = true;
        }
    }

    @Override
    public RVSigningAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.signing_item, parent, false);

        RVSigningAdapter.ViewHolder vh = new RVSigningAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVSigningAdapter.ViewHolder holder, final int position)
    {
        if (isOpen[position])
        {
            holder.secondaryList.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.secondaryList.setVisibility(View.GONE);
        }

        if (agents.get(position).agents.size() > 0)
        {
            holder.cardView.setVisibility(View.VISIBLE);
            holder.nameCategory.setText(agents.get(position).name);
            holder.secondaryList.removeAllViews();

            for (int i=0; i<agents.get(position).agents.size(); i++)
            {
                final int finalI = i;

                View secondary = LayoutInflater.from(context).inflate(R.layout.secondary_small_sign_item, null, true);
                TextView textView = (TextView) secondary.findViewById(R.id.primaryText);
                check = (TextView) secondary.findViewById(R.id.secondaryText);
                if(agents.get(position).agents.get(i).blob == null){
                    check.setVisibility(TextView.INVISIBLE);
                }
                else{
                    check.setVisibility(TextView.VISIBLE);
                }
                textView.setText(agents.get(position).agents.get(i).nameCompany + "\n" + agents.get(position).agents.get(i).rang + "\n" + agents.get(position).agents.get(i).fio);
                if (!select)
                {
                    secondary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(agents.get(position).agents.get(finalI).blob == null){
                                rvOnClickInterface.onClick(agents.get(position).agents.get(finalI));
                            }
                        }
                    });
                }

                holder.secondaryList.addView(secondary);
            }


            /*holder.mainContent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (holder.secondaryList.getVisibility() != View.VISIBLE)
                    {
                        holder.secondaryList.setVisibility(View.VISIBLE);
                        isOpen[position] = true;

                        Animation animation = new AnimationUtils().loadAnimation(context, R.anim.open_rotate_drop);
                        holder.dropCard.startAnimation(animation);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        holder.secondaryList.measure(widthSpec, heightSpec);

                        ValueAnimator animator = ValueAnimator.ofInt(0, holder.secondaryList.getMeasuredHeight());

                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (Integer) animation.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = holder.secondaryList.getLayoutParams();
                                layoutParams.height = value;
                                holder.secondaryList.setLayoutParams(layoutParams);
                            }
                        });

                        animator.start();*//*
                    }
                    else
                    {
                        Animation animation = new AnimationUtils().loadAnimation(context, R.anim.close_rotate_drop);
                        holder.dropCard.startAnimation(animation);

                        isOpen[position] = true;

                        holder.secondaryList.setVisibility(View.GONE);

                        int finalHeight = holder.secondaryList.getHeight();
                        ValueAnimator animator = ValueAnimator.ofInt(finalHeight, 0);

                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (Integer) animation.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = holder.secondaryList.getLayoutParams();
                                layoutParams.height = value;
                                holder.secondaryList.setLayoutParams(layoutParams);
                            }
                        });

                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.secondaryList.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animator.start();*//*
                    }
                }
            });*/
        }
        else
        {
            holder.cardView.setVisibility(View.GONE);
        }
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

    public ArrayList<Agent> removeElements(ArrayList<Agent> removes)
    {
        select = false;

        for (int i = 0; i<removes.size(); i++)
        {
            if (removes.get(i).isPodryadchik)
            {
                for (int l = 0; l<agents.get(0).agents.size(); l++)
                {
                    if (removes.get(i).id == agents.get(0).agents.get(l).id)
                    {
                        agents.get(0).agents.remove(l);
                    }
                }
            }
            else if (removes.get(i).isZakazchik)
            {
                for (int l = 0; l<agents.get(1).agents.size(); l++)
                {
                    if (removes.get(i).id == agents.get(1).agents.get(l).id)
                    {
                        agents.get(1).agents.remove(l);
                    }
                }
            }
            else if (removes.get(i).isEngineeringService)
            {
                for (int l = 0; l<agents.get(2).agents.size(); l++)
                {
                    if (removes.get(i).id == agents.get(2).agents.get(l).id)
                    {
                        agents.get(2).agents.remove(l);
                    }
                }
            }
            else if (removes.get(i).isUpolnomochOrg)
            {
                for (int l = 0; l<agents.get(4).agents.size(); l++)
                {
                    if (removes.get(i).id == agents.get(4).agents.get(l).id)
                    {
                        agents.get(4).agents.remove(l);
                    }
                }
            }
            else if (removes.get(i).isAvtNadzor)
            {
                for (int l = 0; l<agents.get(5).agents.size(); l++)
                {
                    if (removes.get(i).id == agents.get(5).agents.get(l).id)
                    {
                        agents.get(5).agents.remove(l);
                    }
                }
            }
            else if (removes.get(i).isSubPodryadchik)
            {
                for (int l = 0; l<agents.get(3).agents.size(); l++)
                {
                    if (removes.get(i).id == agents.get(3).agents.get(l).id)
                    {
                        agents.get(3).agents.remove(l);
                    }
                }
            }
        }

        for (int i=0; i<agents.size(); i++)
        {
            notifyItemChanged(i);
        }

        return removes;
    }

    @Override
    public int getItemCount()
    {
        return agents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        LinearLayout mainContent;
        TextView nameCategory;
        LinearLayout secondaryList;
        View view;
        ImageView dropCard;

        public ViewHolder(View v)
        {
            super(v);
            dropCard = (ImageView) v.findViewById(R.id.dropCard);
            cardView = (CardView) v.findViewById(R.id.cardView);
            mainContent = (LinearLayout) v.findViewById(R.id.mainContent);
            nameCategory = (TextView) v.findViewById(R.id.category);
            secondaryList = (LinearLayout) v.findViewById(R.id.secondaryList);
            view = v;
        }
    }
}

