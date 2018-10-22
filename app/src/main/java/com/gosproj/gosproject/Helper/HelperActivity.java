package com.gosproj.gosproject.Helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Agent;

import java.util.ArrayList;

public class HelperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Agent> removes = getIntent().getParcelableArrayListExtra("datas");
        Intent intent = new Intent();
        intent.putExtra("removes", removes);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

}
