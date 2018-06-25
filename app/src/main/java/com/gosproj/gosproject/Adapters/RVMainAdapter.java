package com.gosproj.gosproject.Adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gosproj.gosproject.ActActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.MainCategory;
import com.gosproj.gosproject.Structures.SecondaryCategory;

import java.util.ArrayList;

public class RVMainAdapter extends RecyclerView.Adapter<RVMainAdapter.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<MainCategory> mainCategories;

    public RVMainAdapter(Activity activity, ArrayList<MainCategory> mainCategories)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.mainCategories = mainCategories;
    }

    @Override
    public RVMainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);

        RVMainAdapter.ViewHolder vh = new RVMainAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVMainAdapter.ViewHolder holder, final int position)
    {
        holder.nameCategory.setText(mainCategories.get(position).name);

        for (int i=0; i<mainCategories.get(position).secondaryCategories.size(); i++)
        {
            //Log.d("myLOGCREA",i + " " + mainCategories.get(position).secondaryCategories.get(i).name);
            holder.secondaryList.addView(createSecondaryItem(mainCategories.get(position).secondaryCategories.get(i)));
        }

        final int[] finalHeight = {0};
        holder.secondaryList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (finalHeight[0] == 0)
                {
                    finalHeight[0] = holder.secondaryList.getHeight();
                    holder.secondaryList.setVisibility(View.GONE);
                }
            }
        });

        holder.mainContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (holder.secondaryList.getVisibility() != View.VISIBLE)
                {
                    holder.secondaryList.setVisibility(View.VISIBLE);

                    Animation animation = new AnimationUtils().loadAnimation(context, R.anim.open_rotate_drop);
                    holder.dropCard.startAnimation(animation);

                    /*
                    final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    holder.secondaryList.measure(widthSpec, heightSpec);*/
                    ValueAnimator animator = ValueAnimator.ofInt(0, finalHeight[0]);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (Integer) animation.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = holder.secondaryList.getLayoutParams();
                            layoutParams.height = value;
                            holder.secondaryList.setLayoutParams(layoutParams);
                        }
                    });

                    animator.start();
                }
                else
                {
                    Animation animation = new AnimationUtils().loadAnimation(context, R.anim.close_rotate_drop);
                    holder.dropCard.startAnimation(animation);

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
                    animator.start();
                }
            }
        });
    }

    private LinearLayout createSecondaryItem(final SecondaryCategory secondaryCategory)
    {
        LinearLayout linLayout = new LinearLayout(context);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayout.setLayoutParams(lpView);

        TextView tv = new TextView(context);
        tv.setText(secondaryCategory.name);
        tv.setTextColor(resources.getColor(R.color.md_black_1000));
        tv.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, resources.getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, resources.getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, resources.getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, resources.getDisplayMetrics()));
        tv.setLayoutParams(lpView);

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics());
        LinearLayout.LayoutParams lpView2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        lpView2.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, resources.getDisplayMetrics()), 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, resources.getDisplayMetrics()),0);

        View view = new View(context);
        view.setBackgroundColor(resources.getColor(R.color.md_black_1000));
        view.setLayoutParams(lpView2);

        linLayout.addView(tv);
        linLayout.addView(view);

        linLayout.setClickable(true);
        linLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ActActivity.class);
                intent.putExtra("id", secondaryCategory.id);
                activity.startActivity(intent);
            }
        });

        return linLayout;
    }

    @Override
    public int getItemCount()
    {
        return mainCategories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        LinearLayout mainContent;
        TextView nameCategory;
        ImageView dropCard;
        LinearLayout secondaryList;

        public ViewHolder(View v)
        {
            super(v);

            cardView = (CardView) v.findViewById(R.id.cardView);
            mainContent = (LinearLayout) v.findViewById(R.id.mainContent);
            nameCategory = (TextView) v.findViewById(R.id.category);
            dropCard = (ImageView) v.findViewById(R.id.dropCard);
            secondaryList = (LinearLayout) v.findViewById(R.id.secondaryList);
        }
    }
}
