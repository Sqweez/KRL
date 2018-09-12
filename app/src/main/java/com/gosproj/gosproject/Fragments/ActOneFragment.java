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
    String ispol_name;
    String gv1_name;
    String gv2_name;
    String gv3_name;
    TextView actText;
    TextView dateText;
    TextView uchastokText;
    TextView typeText;
    TextView ispolnitel;
    TextView gv1;
    TextView gv2;
    TextView gv3;
    LinearLayout gv1_block;
    LinearLayout gv2_block;
    LinearLayout gv3_block;
    public static ActOneFragment getInstance(String actNumber, String date, String uchastok, String type, String ispol, String gv1, String gv2, String gv3)
    {
        Bundle args = new Bundle();
        ActOneFragment fragment = new ActOneFragment();
        fragment.setArguments(args);
        fragment.actNumber = String.valueOf(actNumber);
        fragment.date = date;
        fragment.uchastok = uchastok;
        fragment.type = type;
        fragment.ispol_name = ispol;
        fragment.gv1_name = gv1;
        fragment.gv2_name = gv2;
        fragment.gv3_name = gv3;
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
        ispolnitel = (TextView) view.findViewById(R.id.ispolnitel);
        gv1 = (TextView) view.findViewById(R.id.gruppa_vyezda1);
        gv2 = (TextView) view.findViewById(R.id.gruppa_vyezda2);
        gv3 = (TextView) view.findViewById(R.id.gruppa_vyezda3);
        gv1_block = (LinearLayout) view.findViewById(R.id.gruppa_vyezda_1_block);
        gv2_block = (LinearLayout) view.findViewById(R.id.gruppa_vyezda_2_block);
        gv3_block = (LinearLayout) view.findViewById(R.id.gruppa_vyezda_3_block);
        gv1.setText(gv1_name);
        gv2.setText(gv2_name);
        gv3.setText(gv3_name);
        if (gv1_name.trim().isEmpty()){
            gv1_block.setVisibility(LinearLayout.INVISIBLE);
            gv2_block.setVisibility(LinearLayout.INVISIBLE);
            gv3_block.setVisibility(LinearLayout.INVISIBLE);
        }
        else if(gv2_name.trim().isEmpty()){
            gv2_block.setVisibility(LinearLayout.INVISIBLE);
            gv3_block.setVisibility(LinearLayout.INVISIBLE);
        }
        else if(gv3_name.trim().isEmpty()){
            gv3_block.setVisibility(LinearLayout.INVISIBLE);
        }
        ispolnitel.setText(ispol_name);
        actText.setText(resources.getString(R.string.act) + " № " + actNumber);
        dateText.setText(date);
        uchastokText.setText(uchastok);
        typeText.setText(type);

        return view;
    }
}
