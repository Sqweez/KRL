package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gosproj.gosproject.Functionals.DBHelper;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InstructionActivity extends AppCompatActivity {
    Context context;
    Button btnContinue;
    Activity activity;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String[] strings = data.getStringArrayExtra("data");
            String name = strings[0];
            int site_id = Integer.parseInt(strings[1]);
            int rgu_id = Integer.parseInt(strings[2]);
            String rgu_name = strings[3];
            ContentValues cv = new ContentValues();
            cv.put("site_id", site_id);
            cv.put("name", name);
            cv.put("rgu_name", rgu_name);
            cv.put("rgu_id", rgu_id);
            SharedPreferences user_info = context.getSharedPreferences("com.gosproj.gosproject", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = user_info.edit();
            editor.putInt("user_id", site_id);
            editor.putInt("rgu_id", rgu_id);
            editor.putString("rgu_name", rgu_name);
            editor.putString("name", name);
            editor.commit();
            DBHelper dbHelper = new DBHelper(context, DBHelper.Users);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);
            dbHelper.close();
            db.close();
            startActivity(new Intent(context, MainActivity.class));
        }
    }
    @Override
    public void onBackPressed(){
        activity.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        setContentView(R.layout.activity_instruction);
        btnContinue = (Button) findViewById(R.id.btnConfirmInstruction);
        Button btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, QRAuth.class), 11111);
            }
        });
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText("Внимание!")
                .setContentText("Для работы в мобильном приложении нужно отсканировать QR-код с личными данным")
                .setConfirmText("Продолжить")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
        pDialog.show();
    }
}
