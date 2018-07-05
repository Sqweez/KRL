package com.gosproj.gosproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gosproj.gosproject.Functionals.NavigationDrawer;

public class QRForScanning extends AppCompatActivity {

    Activity activity;
    Context context;
    Resources resources;
    Toolbar toolbar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrfor_scanning);
        activity = this;
        context = this;
        resources = getResources();

        activity = this;
        context = this;
        resources = getResources();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));

        new NavigationDrawer(context, activity, toolbar);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getResources().getString(R.string.qr_code_load_acts_msg) + "\n\n\n\n");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, resources.getString(R.string.qr_scan_fail), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                String dataFromQr = result.getContents();
                String[] strings = dataFromQr.split("\\|");
                Log.d("DEBUG_QR", strings[0]);
                Log.d("DEBUG_QR", strings[1]);
                Log.d("DEBUG_QR", strings[2]);
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra("data", strings);
                startActivity(intent);
            }
        }
    }
}
