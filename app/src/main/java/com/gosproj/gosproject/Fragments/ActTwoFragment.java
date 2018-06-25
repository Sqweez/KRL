package com.gosproj.gosproject.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gosproj.gosproject.R;

public class ActTwoFragment extends Fragment
{
    Context context;
    Resources resources;
    Activity activity;

    String rgu;
    String ispolnitel;
    String gruppaVyezda;

    TextView rguText;
    TextView ispolnitelText;
    TextView gruppaVyezdaText;

    public static ActTwoFragment getInstance(String rgu, String ispolnitelm, String gruppaVyezda)
    {
        Bundle args = new Bundle();
        ActTwoFragment fragment = new ActTwoFragment();
        fragment.setArguments(args);
        fragment.rgu = rgu;
        fragment.ispolnitel = ispolnitelm;
        fragment.gruppaVyezda = gruppaVyezda;
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

        rguText = (TextView) view.findViewById(R.id.rgu);
        rguText.setText(rgu);

        ispolnitelText = (TextView) view.findViewById(R.id.ispolnitel);
        ispolnitelText.setText(ispolnitel);

        gruppaVyezdaText = (TextView) view.findViewById(R.id.gruppa_vyezda);
        gruppaVyezdaText.setText(gruppaVyezda);

        return view;
    }
}
