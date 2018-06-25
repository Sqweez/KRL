package com.gosproj.gosproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
import com.gosproj.gosproject.Structures.Act;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Defects;
import com.gosproj.gosproject.Structures.Proba;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ActActivity extends AppCompatActivity
{
    int id;
    public Act act;

    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    VPActAdapter vpActAdapter;
    ViewPager viewPager;
    CircleIndicator circleIndicator;

    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    Menu menu;

    ActFourFragment actFourFragment = null;
    ActFiveFragment actFiveFragment = null;
    ActSixFragment actSixFragment = null;
    ActSevenFragment actSevenFragment = null;
    ActEightFragment actEightFragment = null;

    int currentPos = 0;

    final int REQUEST_ADD_PROBA = 210;
    final int REQUEST_ADD_DEFECT = 220;
    final int REQUEST_ADD_AGENT = 230;
    final int REQUEST_ADD_PHOTO = 240;
    final int REQUEST_ADD_VIDEO = 250;

    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        if (cursor.moveToFirst())
        {
            act.id = cursor.getInt(cursor.getColumnIndex("id"));
            act.idAct = cursor.getInt(cursor.getColumnIndex("idAct"));
            act.date = cursor.getString(cursor.getColumnIndex("date"));
            act.doroga = cursor.getString(cursor.getColumnIndex("doroga"));
            act.uchastok = cursor.getString(cursor.getColumnIndex("uchastok"));
            act.vid_rabot = cursor.getString(cursor.getColumnIndex("vid_rabot"));
            act.rgu = cursor.getString(cursor.getColumnIndex("rgu"));
            act.rgu = act.rgu.replace ("&quot;", "\"");
            act.ispolnitel = cursor.getString(cursor.getColumnIndex("ispolnitel"));
            act.gruppa_vyezda = cursor.getString(cursor.getColumnIndex("gruppa_vyezda"));
            act.podradchyk = cursor.getString(cursor.getColumnIndex("podradchyk"));
            act.podradchyk = act.podradchyk.replace ("&quot;", "\"");
            act.customer = cursor.getString(cursor.getColumnIndex("customer"));
            act.customer = act.customer.replace ("&quot;", "\"");
        }

        cursor.close();
        db.close();
        dbHelper.close();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.app_name));

        new NavigationDrawer(context, activity, toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.hide();

        final ActOneFragment actOneFragment = ActOneFragment.getInstance(String.valueOf(act.idAct), act.date, act.doroga + ", " + act.uchastok, act.vid_rabot);
        final ActTwoFragment actTwoFragment = ActTwoFragment.getInstance(act.rgu, act.ispolnitel, act.gruppa_vyezda);
        final ActFreeFragment actFreeFragment = ActFreeFragment.getInstance(act.id);
        actFourFragment = ActFourFragment.getInstance(act.id, fab);
        actFiveFragment = ActFiveFragment.getInstance(act.id, fab);
        actSixFragment = ActSixFragment.getInstance(act.id, fab);
        actSevenFragment = ActSevenFragment.getInstance(act.id, fab);
        actEightFragment = ActEightFragment.getInstance(act.id, fab);
        ActCloseFragment actCloseFragment = ActCloseFragment.getInstance(act.doroga + "\n" + act.uchastok + ", " + act.vid_rabot,act.id);

        fragments.add(actOneFragment);
        fragments.add(actTwoFragment);
        fragments.add(actFreeFragment);
        fragments.add(actFourFragment);
        fragments.add(actFiveFragment);
        fragments.add(actSevenFragment);
        fragments.add(actEightFragment);
        fragments.add(actSixFragment);
        fragments.add(actCloseFragment);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        circleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        vpActAdapter = new VPActAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(vpActAdapter);
        circleIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                View view = activity.getCurrentFocus();
                if (view != null)
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                currentPos = position;

                if (position == 3)
                {
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actFourFragment.setFabClick();
                    fab.show();
                }
                else if (position == 4)
                {
                    actFourFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actFiveFragment.setFabClick();
                    fab.show();
                }
                else if (position == 5)
                {
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    if (menu.findItem(R.id.action_remove) != null)
                    {
                        menu.findItem(R.id.action_remove).setVisible(true);
                        menu.findItem(R.id.action_check).setVisible(false);
                    }

                    actSevenFragment.setFabClick();
                    fab.show();
                }
                else if (position == 6)
                {
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSixFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actEightFragment.setFabClick();
                    fab.show();
                }
                else if (position == 7)
                {
                    actFourFragment.closeCheckUi();
                    actFiveFragment.closeCheckUi();
                    actSevenFragment.closeCheckUi();
                    actEightFragment.closeCheckUi();

                    menu.findItem(R.id.action_remove).setVisible(true);
                    menu.findItem(R.id.action_check).setVisible(false);

                    actSixFragment.setFabClick();
                    fab.show();
                }
                else
                {
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
            public void onPageScrollStateChanged(int state)
            {

            }
        });
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
                case REQUEST_ADD_DEFECT:
                    ArrayList<Defects> defectses = data.getParcelableArrayListExtra("defects");
                    actFiveFragment.setResult(defectses);
                    break;
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
                if (currentPos == 3)
                {
                    actFourFragment.removeElements();
                }
                else if (currentPos == 4)
                {
                    actFiveFragment.removeElements();
                }
                else if (currentPos == 5)
                {
                    actSevenFragment.removeElements();
                }
                else if (currentPos == 6)
                {
                    actEightFragment.removeElements();
                }
                else if (currentPos == 7)
                {
                    actSixFragment.removeElements();
                }
                menu.findItem(R.id.action_remove).setVisible(false);
                menu.findItem(R.id.action_check).setVisible(true);
                return true;
            case R.id.action_check:
                if (currentPos == 3)
                {
                    actFourFragment.removeElementsOk();
                }
                else if (currentPos == 4)
                {
                    actFiveFragment.removeElementsOk();
                }
                else if (currentPos == 5)
                {
                    actSevenFragment.removeElementsOk();
                }
                else if (currentPos == 6)
                {
                    actEightFragment.removeElementsOk();
                }
                else if (currentPos == 7)
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
