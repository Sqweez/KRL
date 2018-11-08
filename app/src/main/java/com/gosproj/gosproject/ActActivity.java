package com.gosproj.gosproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.gosproj.gosproject.Adapters.VPActAdapter;
import com.gosproj.gosproject.Fragments.ActCloseFragment;
import com.gosproj.gosproject.Fragments.ActEightFragment;
import com.gosproj.gosproject.Fragments.ActFiveFragment;
import com.gosproj.gosproject.Fragments.ActFourFragment;
import com.gosproj.gosproject.Fragments.ActFreeFragment;
import com.gosproj.gosproject.Fragments.ActOneFragment;
import com.gosproj.gosproject.Fragments.ActSevenFragment;
import com.gosproj.gosproject.Fragments.ActSixFragment;
import com.gosproj.gosproject.Fragments.ActTwoFragment;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.NavigationDrawer;
import com.gosproj.gosproject.Services.LoadScanService;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Act;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.Measurment;
import com.gosproj.gosproject.Structures.Proba;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ActActivity extends AppCompatActivity {
    int id;
    public Act act;
    Runnable runnable;
    Activity activity;
    Context context;
    Resources resources;
    Toolbar toolbar;
    LocationManager locationManager;
    LocationListener locationListener;
    VPActAdapter vpActAdapter;
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    Menu menu;
    int isNew;
    AlertDialog alert;
    Boolean dummyCondition = false;
    ActFourFragment actFourFragment = null;
    ActFiveFragment actFiveFragment = null;
    ActSixFragment actSixFragment = null;
    ActSevenFragment actSevenFragment = null;
    ActEightFragment actEightFragment = null;
    ActFreeFragment actFreeFragment = null;
    ActTwoFragment actTwoFragment = null;
    int currentPos = 0;
    final int REQUEST_ADD_MEASURMENT = 322;
    final int REQUEST_ADD_PROBA = 210;
    final int REQUEST_ADD_DEFECT = 220;
    final int REQUEST_ADD_AGENT = 230;
    final int REQUEST_ADD_PHOTO = 240;
    final int REQUEST_ADD_VIDEO = 250;
    final int REQUEST_ADD_SIGNATURE = 123;

    public FloatingActionButton fab;

    public boolean checkGpsStatus(){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    public void turnOnGps(){
        final Handler handler = new Handler();
        final int delay = 1;
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Внимание!");
                builder.setMessage("Для заполнения результатов выезда необходимо включить GPS");
                builder.setCancelable(false);
                builder.setNegativeButton("Продолжить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        handler.postDelayed(runnable, delay);
                    }
                });
                builder.setPositiveButton("Включить GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent1);
                    }
                });
                alert = builder.create();
                if(!checkGpsStatus() && !alert.isShowing()){
                    alert.show();
                }
                if(!alert.isShowing())
                    handler.postDelayed(runnable, delay);
            }
        }, delay);
    }

    public void writeFromLabToAgents(String name) {
        DBHelper dbHelper = DBHelper.getInstance(context, DBHelper.Agents);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Agents WHERE fio = ? AND idDept = ?", new String[]{name, String.valueOf(act.id)});
        if (!cursor.moveToFirst()) {
            if (!name.trim().isEmpty()) {
                ContentValues cv = new ContentValues();
                cv.put("idDept", act.id);
                cv.put("nameCompany", act.rgu);
                cv.put("fio", name);
                cv.put("rang", "Сотрудник");
                cv.put("isSubPodryadchik", 0);
                cv.put("isAvtNadzor", 0);
                cv.put("isUpolnomochOrg", 0);
                cv.put("isPodryadchik", 0);
                cv.put("isSubPodryadchik", 0);
                cv.put("isZakazchik", 0);
                cv.put("isEngineeringService", 0);
                cv.put("isRGU", 1);
                db.insert(dbHelper.getDatabaseName(), null, cv);
            }
        }
        dbHelper.close();
        db.close();
    }
    @Override
    public void onResume(){
        super.onResume();
        turnOnGps();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);

        activity = this;
        context = this;
        resources = getResources();
        id = getIntent().getIntExtra("id", 0);
        act = new Act();
        DBHelper dbHelper = new DBHelper(context, DBHelper.DEPARTURE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Departures WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            act.id = cursor.getInt(cursor.getColumnIndex("id"));
            act.idAct = cursor.getInt(cursor.getColumnIndex("idAct"));
            act.idNomer = cursor.getInt(cursor.getColumnIndex("idNomer"));
            act.date = cursor.getString(cursor.getColumnIndex("date"));
            act.object = cursor.getString(cursor.getColumnIndex("object"));
            act.vid_rabot = cursor.getString(cursor.getColumnIndex("vid_rabot"));
            act.ispolnitel = cursor.getString(cursor.getColumnIndex("ispolnitel"));
            act.gruppa_vyezda1 = cursor.getString(cursor.getColumnIndex("gruppa_vyezda1"));
            act.gruppa_vyezda2 = cursor.getString(cursor.getColumnIndex("gruppa_vyezda2"));
            act.gruppa_vyezda3 = cursor.getString(cursor.getColumnIndex("gruppa_vyezda3"));
            act.podradchyk = cursor.getString(cursor.getColumnIndex("podradchyk"));
            act.subpodradchyk = cursor.getString(cursor.getColumnIndex("subpodradchyk"));
            act.inj_sluzhby = cursor.getString(cursor.getColumnIndex("inj_sluzhby"));
            act.avt_nadzor = cursor.getString(cursor.getColumnIndex("avt_nadzor"));
            act.uorg = cursor.getString(cursor.getColumnIndex("uorg"));
            isNew = cursor.getInt(cursor.getColumnIndex("isNew"));
            if(act.podradchyk != null)
            act.podradchyk = act.podradchyk.replace("&quot;", "\"");
            act.zakazchik = cursor.getString(cursor.getColumnIndex("zakazchik"));
            act.rgu = cursor.getString(cursor.getColumnIndex("rgu_name"));
            if(act.zakazchik != null)
            act.zakazchik = act.zakazchik.replace("&quot;", "\"");
        }
        cursor.close();
        db.close();
        dbHelper.close();

        if(isNew == 1){
            LogsHelper logsHelper = new LogsHelper(LogsHelper.NEWDEPARTURE, context, activity, id);
            logsHelper.createLog(act.object + "|" + act.vid_rabot + "|" + act.ispolnitel + "|" + act.gruppa_vyezda1 + "|" + act.gruppa_vyezda2 + "|" + act.gruppa_vyezda3, "", LogsHelper.ACTION_ADD);
        }

        writeFromLabToAgents(act.ispolnitel);
        writeFromLabToAgents(act.gruppa_vyezda1);
        writeFromLabToAgents(act.gruppa_vyezda2);
        writeFromLabToAgents(act.gruppa_vyezda3);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));

        new NavigationDrawer(context, activity, toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.hide();

        final ActOneFragment actOneFragment = ActOneFragment.getInstance(String.valueOf(act.idNomer),
                act.date, act.object, act.vid_rabot, act.ispolnitel, act.gruppa_vyezda1, act.gruppa_vyezda2, act.gruppa_vyezda3);
        actTwoFragment = ActTwoFragment.getInstance(act.id);
        actFreeFragment = ActFreeFragment.getInstance(act.id, fab);
        actFourFragment = ActFourFragment.getInstance(act.id, fab);
        actFiveFragment = ActFiveFragment.getInstance(act.id, fab);
        actSixFragment = ActSixFragment.getInstance(act.id, fab);
        actSevenFragment = ActSevenFragment.getInstance(act.id, fab);
        actEightFragment = ActEightFragment.getInstance(act.id, fab);
        ActCloseFragment actCloseFragment = ActCloseFragment.getInstance(act.object + "\n" + act.vid_rabot, act.id, isNew, String.valueOf(act.idNomer), act.object, act.date, act.ispolnitel, act.gruppa_vyezda1, act.gruppa_vyezda2, act.gruppa_vyezda3, act.vid_rabot);

        fragments.add(actOneFragment);
        fragments.add(actFreeFragment);
        fragments.add(actFiveFragment);
        fragments.add(actFourFragment);
        fragments.add(actSevenFragment);
        fragments.add(actEightFragment);
        fragments.add(actSixFragment);
        fragments.add(actTwoFragment);
        fragments.add(actCloseFragment);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        circleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        vpActAdapter = new VPActAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(vpActAdapter);
        circleIndicator.setViewPager(viewPager);
        //@TODO Checking gps connection
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                currentPos = position;
                if (position == 1) {
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actFreeFragment.setFabClick();
                    fab.show();
                } else if (position == 2) {
                    actFreeFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actFiveFragment.setFabClick();
                    fab.show();
                } else if (position == 3) {
                    actFreeFragment.closeCheckUi();
                    actFourFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actFourFragment.setFabClick();
                    fab.show();
                } else if (position == 4) {
                    actFreeFragment.closeCheckUi();
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    if (menu.findItem(R.id.action_remove) != null) {
                        menu.findItem(R.id.action_remove).setVisible(true);
                        menu.findItem(R.id.action_check).setVisible(false);
                    }

                    actSevenFragment.setFabClick();
                    fab.show();
                } else if (position == 5) {
                    actFreeFragment.closeCheckUi();
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actEightFragment.setFabClick();
                    fab.show();
                } else if (position == 6) {
                    actFreeFragment.closeCheckUi();
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actSixFragment.setFabClick();
                    fab.show();
                } else if (position == 7) {
                    actFreeFragment.closeCheckUi();
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    menu.findItem(R.id.action_remove).setVisible(false);
                    fab.hide();
                } else {
                    actFreeFragment.closeCheckUi();
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(false);
                    menu.findItem(R.id.action_check).setVisible(false);

                    fab.setOnClickListener(null);
                    fab.hide();
                    //actFourFragment.closeCheckUi();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                break;
            default:
                break;
        }
    }

    private void configureButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("RE_AC", requestCode + " " + resultCode);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_ADD_PROBA:
                    ArrayList<Proba> probs = data.getParcelableArrayListExtra("probs");
                    actFourFragment.setResult(probs);
                    break;
                case REQUEST_ADD_SIGNATURE:
                    Agent sign = data.getParcelableExtra("agent");
                    actTwoFragment.SetSignResult(sign);
                    break;
                case REQUEST_ADD_DEFECT:
                    ArrayList<Defects> defectses = data.getParcelableArrayListExtra("defects");
                    actFiveFragment.setResult(defectses);
                    break;
                case REQUEST_ADD_MEASURMENT:
                    ArrayList<Measurment> measurments = data.getParcelableArrayListExtra("measurments");
                    LogsHelper logsHelper = new LogsHelper(LogsHelper.MEAS, context, activity, id);
                    int action = data.getIntExtra("action", 0);
                    String old_item = data.getStringExtra("old");
                    if(action == LogsHelper.ACTION_ADD){
                        logsHelper.createLog(old_item, "",  action);
                    }
                    else{
                        String new_item = data.getStringExtra("new");
                        logsHelper.createLog(old_item, new_item, action);
                    }
                    actFreeFragment.setResult(measurments);
                case REQUEST_ADD_AGENT:
                    Agent agent = data.getParcelableExtra("agent");
                    actSixFragment.setResult(agent);
                    break;
                case REQUEST_ADD_PHOTO:
                    actSevenFragment.SetUri();
                    break;
                case REQUEST_ADD_VIDEO:
                    actEightFragment.SetUri();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.act_menu, menu);
        this.menu = menu;
        menu.findItem(R.id.action_check).setVisible(false);
        menu.findItem(R.id.action_remove).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_remove:
                if (currentPos == 1) {
                    actFreeFragment.removeElements();
                }
                else if (currentPos == 2)
                {
                    actFiveFragment.removeElements();
                }
                else if (currentPos == 3)
                {
                    actFourFragment.removeElements();
                }
                else if (currentPos == 4)
                {
                    actSevenFragment.removeElements();
                }
                else if (currentPos == 5)
                {
                    actEightFragment.removeElements();
                }
                else if (currentPos == 6)
                {
                    actSixFragment.removeElements();
                }
                menu.findItem(R.id.action_remove).setVisible(false);
                menu.findItem(R.id.action_check).setVisible(true);
                return true;
            case R.id.action_check:
                if (currentPos == 1) {
                    actFreeFragment.removeElementsOk();
                }
                else if (currentPos == 2)
                {
                    actFiveFragment.removeElementsOk();
                }
                else if (currentPos == 3)
                {
                    actFourFragment.removeElementsOk();
                }
                else if (currentPos == 4)
                {
                    actSevenFragment.removeElementsOk();
                }
                else if (currentPos == 5)
                {
                    actEightFragment.removeElementsOk();
                }
                else if (currentPos == 6)
                {
                    actSixFragment.removeElementsOk();

                }
                menu.findItem(R.id.action_remove).setVisible(true);
                menu.findItem(R.id.action_check).setVisible(false);
                return true;
            default:
                return false;
        }
    }
}
