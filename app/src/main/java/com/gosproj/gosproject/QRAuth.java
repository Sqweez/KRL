package com.gosproj.gosproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gosproj.gosproject.Functionals.NavigationDrawer;
import com.gosproj.gosproject.Structures.Act;

public class QRAuth extends AppCompatActivity {

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

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));
        new NavigationDrawer(context, activity, toolbar);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setBeepEnabled(false);
        integrator.setPrompt(getResources().getString(R.string.qr_auth_msg) + "\n\n\n\n");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }
    public static boolean isNumeric(String str)
    {
        try
        {
            int i = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public boolean checkAuthQR(String[] strings){
        if(strings.length == 4 && isNumeric(strings[1]) && isNumeric(strings[2])){
            return true;
        }
        else{
            return false;
        }
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
                if(checkAuthQR(strings)){
                    Intent intent = new Intent(activity, InstructionActivity.class);
                    intent.putExtra("data", strings);
                    setResult(RESULT_OK, intent);
                    finish();
                }
               else{
                    Toast.makeText(this, resources.getString(R.string.qr_scan_error), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}
