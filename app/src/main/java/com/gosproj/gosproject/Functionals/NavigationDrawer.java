package com.gosproj.gosproject.Functionals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gosproj.gosproject.CreateActActivity;
import com.gosproj.gosproject.LoadDept;
import com.gosproj.gosproject.MainActivity;
import com.gosproj.gosproject.QRForScanning;
import com.gosproj.gosproject.R;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class NavigationDrawer {
    Context context;
    Activity activity;
    Resources resources;
    Toolbar toolbar;
    String name;
    String rgu_name;
    static Drawer result;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public NavigationDrawer(final Context context, final Activity activity, final Toolbar toolbar) {
        this.context = context;
        this.activity = activity;
        this.resources = activity.getResources();
        this.toolbar = toolbar;
        SharedPreferences user_info = context.getSharedPreferences("com.gosproj.gosproject", Context.MODE_PRIVATE);
        name = user_info.getString("name", "ИМЯ ТЕСТ");
        rgu_name = user_info.getString("rgu_name", "РГУ ТЕСТ");
        SharedPreferences sharedPref = context.getSharedPreferences(resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        PrimaryDrawerItem Main = new PrimaryDrawerItem().withIdentifier(2)
                .withName(resources.getString(R.string.active_task))
                .withIcon(R.drawable.ic_list_bulleted)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        activity.finish();

                        return true;
                    }
                });
        PrimaryDrawerItem newAct = new PrimaryDrawerItem().withIdentifier(10)
                .withName(resources.getString(R.string.create_new_act))
                .withIcon(R.drawable.file_plus)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), CreateActActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });
        PrimaryDrawerItem newDeparture = new PrimaryDrawerItem().withIdentifier(3)
                .withName(resources.getString(R.string.load_departure))
                .withIcon(R.drawable.file_download)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), LoadDept.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        activity.finish();

                        return true;
                    }
                });
        PrimaryDrawerItem update = new PrimaryDrawerItem().withIdentifier(7)
                .withName(R.string.update)
                .withIcon(R.drawable.ic_sync)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        final String appPackageName = activity.getPackageName();
                        try{
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        }catch (android.content.ActivityNotFoundException e){
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        return true;
                    }
                });
        PrimaryDrawerItem scanning = new PrimaryDrawerItem().withIdentifier(5)
                .withName(resources.getString(R.string.scanning))
                .withIcon(R.drawable.icon_scanner)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), QRForScanning.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        activity.finish();

                        return true;
                    }
                });
        // TODO Вернуть как будет готово
        PrimaryDrawerItem synchronization = new PrimaryDrawerItem().withIdentifier(4)
                .withName(resources.getString(R.string.synchronization))
                .withIcon(R.drawable.ic_sync)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Sync sync = new Sync(activity);
                        sync.execute();

                        if (result != null) {
                            result.closeDrawer();
                        }
                        return true;

                    }
                });
        PrimaryDrawerItem logout = new PrimaryDrawerItem().withIdentifier(4)
                .withName(resources.getString(R.string.logout))
                .withIcon(R.drawable.ic_logout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        DBHelper dbHelper = new DBHelper(context, DBHelper.Users);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("Users", null, null);
                        dbHelper.close();
                        db.close();
                        DBHelper dbHelperL = new DBHelper(context, DBHelper.Logs);
                        SQLiteDatabase dbL = dbHelperL.getWritableDatabase();

                        dbL.delete(DBHelper.Logs, null, null);

                        dbHelperL.close();
                        dbL.close();

                        DBHelper dbHelperDp = new DBHelper(context, DBHelper.DEPARTURE);
                        SQLiteDatabase dbDp = dbHelperDp.getWritableDatabase();

                        dbDp.delete(DBHelper.DEPARTURE, null, null);

                        dbHelperDp.close();
                        dbDp.close();

                        DBHelper dbHelpeM = new DBHelper(context, DBHelper.MEASUREMENTS);
                        SQLiteDatabase dbM = dbHelpeM.getWritableDatabase();

                        dbM.delete(DBHelper.MEASUREMENTS, null, null);

                        dbHelpeM.close();
                        dbM.close();

                        DBHelper dbHelpeP = new DBHelper(context, DBHelper.PROBS);
                        SQLiteDatabase dbP = dbHelpeP.getWritableDatabase();

                        dbP.delete(DBHelper.PROBS, null, null);

                        dbHelpeP.close();
                        dbP.close();

                        DBHelper dbHelpeD = new DBHelper(context, DBHelper.DEFECTS);
                        SQLiteDatabase dbD = dbHelpeD.getWritableDatabase();

                        dbD.delete(DBHelper.DEFECTS, null, null);

                        dbHelpeD.close();
                        dbD.close();

                        DBHelper dbHelpeA = new DBHelper(context, DBHelper.Agents);
                        SQLiteDatabase dbA = dbHelpeA.getWritableDatabase();

                        dbA.delete(DBHelper.Agents, null, null);

                        dbHelpeA.close();
                        dbA.close();

                        DBHelper dbHelpePh = new DBHelper(context, DBHelper.Photos);
                        SQLiteDatabase dbPh = dbHelpePh.getWritableDatabase();

                        dbPh.delete(DBHelper.Photos, null, null);

                        dbHelpePh.close();
                        dbPh.close();

                        DBHelper dbHelpeV = new DBHelper(context, DBHelper.Videos);
                        SQLiteDatabase dbV = dbHelpeV.getWritableDatabase();

                        dbV.delete(DBHelper.Videos, null, null);

                        dbHelpeV.close();
                        dbV.close();
                        SharedPreferences user_info = context.getSharedPreferences("com.gosproj.gosproject", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = user_info.edit();
                        editor.clear();
                        editor.apply();
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                        activity.finish();
                        return true;
                    }
                });
        PrimaryDrawerItem exit = new PrimaryDrawerItem().withIdentifier(5)
                .withName(resources.getString(R.string.exit))
                .withIcon(R.drawable.ic_exit_to_app)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        activity.finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        return true;
                    }
                });

        String highScore = sharedPref.getString("timeSync", "Синхронизация не производилась");

        SecondaryDrawerItem secondaryDrawerItem = new SecondaryDrawerItem();
        secondaryDrawerItem.withSelectable(false);
        secondaryDrawerItem.withName(highScore);

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .withCompactStyle(true)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesVisible(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(rgu_name).withTextColor(Color.WHITE)
                )
                .build();

        result = new DrawerBuilder()
                .withAccountHeader(header)
                .withSelectedItem(-1)
                .withActivity(activity)
                .withToolbar(toolbar)
                .addDrawerItems(
                        Main,
                        newDeparture,
                        newAct,
                        new DividerDrawerItem(),
                        scanning,
                        new DividerDrawerItem(),
                        synchronization,
                        logout,
                        exit,
                        new DividerDrawerItem(),
                        update,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(resources.getString(R.string.last_time_sync) + ":").withSelectable(false),
                        secondaryDrawerItem
                )
                .build();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onClickSync() {
        DBHelper dbHelper = new DBHelper(context, DBHelper.OfflineZip);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT path FROM OfflineZip", null);

        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex("path"));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
    }

    class Sync extends AsyncTask<Void, Void, String> {
        Activity activity;
        SweetAlertDialog sDialog;
        int res = 0;

        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        public Sync(Activity activity) {
            this.activity = activity;

            sharedPref = context.getSharedPreferences(resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#535c69"));
            sDialog.setTitleText("Идет синхронизация");
            sDialog.setCancelable(false);
            sDialog.show();
        }

        @Override
        protected String doInBackground(Void... unused) {
            DBHelper dbHelper = new DBHelper(context, DBHelper.OfflineZip);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM OfflineZip", null);
            if (cursor.moveToFirst()) {
                do {
                    if (isOnline()) {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        String path = cursor.getString(cursor.getColumnIndex("path"));

                        try {
                            File file = new File(path);
                            boolean result = new ServerApi(ServerApi.ACTION_LOAD_ACT).UpLoadFile(file);

                            if (result) {
                                file.delete();
                                db.delete(DBHelper.OfflineZip, "id = " + id, null);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        DateFormat df = new SimpleDateFormat("Время: HH:mm:ss dd.MM.yyyy");
                        String date = df.format(Calendar.getInstance().getTime());

                        editor.putString("timeSync", date);
                        editor.commit();
                    }
                }
                while (cursor.moveToNext());
            }
            else{
                res = 1;
            }

            cursor.close();
            db.close();
            dbHelper.close();

            return "";
        }

        @Override
        protected void onPostExecute(String unused) {
            sDialog.hide();
            if(res == 0 && isOnline()){
                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Синхронизация выполненена успешно")
                        .setConfirmText("OK")
                        .show();
            }
            else if(!isOnline()){
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Отсутствует подключение к интернету")
                        .setConfirmText("OK")
                        .show();
            }
            else if(res == 1){
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Нечего синхронизировать")
                        .setConfirmText("OK")
                        .show();
            }

        }
    }
}
