package com.gosproj.gosproject.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gosproj.gosproject.R;

public class ActOneFragment extends Fragment
{
    Context context;
    Resources resources;
    Activity activity;

    String actNumber;
    String date;
    String uchastok;
    String type;

    TextView actText;
    TextView dateText;
    TextView uchastokText;
    TextView typeText;

    public static ActOneFragment getInstance(String actNumber, String date, String uchastok, String type)
    {
        Bundle args = new Bundle();
        ActOneFragment fragment = new ActOneFragment();
        fragment.setArguments(args);
        fragment.actNumber = actNumber;
        fragment.date = date;
        fragment.uchastok = uchastok;
        fragment.type = type;
        return fragment;
    }

    public ActOneFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_one_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        actText = (TextView) view.findViewById(R.id.act);
        dateText = (TextView) view.findViewById(R.id.date);
        uchastokText = (TextView) view.findViewById(R.id.uchastok);
        typeText = (TextView) view.findViewById(R.id.vid_rabot);

        actText.setText(resources.getString(R.string.act) + " № " + actNumber);
        dateText.setText(date);
        uchastokText.setText(uchastok);
        typeText.setText(type);

        return view;
    }
}
