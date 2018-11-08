package com.gosproj.gosproject.Functionals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{
    public static DBHelper mInstance = null;
    public static final int DATABASE_VERSION = 48;
    public static String DATABASE_NAME = "";

    Context context;

    public static String DEPARTURE = "Departures";
    public static String MEASUREMENTS = "Measurements";
    public static String PROBS = "Probs";
    public static String DEFECTS = "Defects";
    public static String Agents = "Agents";
    public static String Photos = "Photos";
    public static String Scans = "Scans";
    public static String Videos = "Videos";
    public static String OfflineZip = "OfflineZip";
    public static String Users = "Users";
    public static String Logs = "Logs";

    public static DBHelper getInstance(Context context, String name){
        if(mInstance == null){
            mInstance = new DBHelper(context.getApplicationContext(), name);
        }
        return mInstance;
    }
    public DBHelper(Context context, String name)
    {
        super(context, name, null, DATABASE_VERSION);

        this.context = context;
        this.DATABASE_NAME = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        switch (DATABASE_NAME)
        {
            case "Users":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "site_id integer,"
                        + "name text,"
                        + "rgu_name text,"
                        + "rgu_id integer"+ ");");
                break;
            case "Logs":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "lat double default 0,"
                        + "long double default 0,"
                        + "log_text text"+ ");");
                break;
            case "VidRabot":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "vid_rabot_id integer,"
                        + "name text"+  ");");
                break;
            case "Departures":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idAct integer default 0,"
                        + "idNomer integer default 0,"
                        + "date text,"
                        + "object text,"
                        + "isNew integer default 0,"
                        + "id_rabot integer default 0,"
                        + "vid_rabot text,"
                        + "ispolnitel text,"
                        + "gruppa_vyezda1 text default '',"
                        + "gruppa_vyezda2 text default '',"
                        + "gruppa_vyezda3 text default '',"
                        + "podradchyk text default '',"
                        + "subpodradchyk text default '',"
                        + "avt_nadzor text default '',"
                        + "inj_sluzhby text default '',"
                        + "rgu_name text,"
                        + "uorg text,"
                        + "zakazchik text,"
                        + "isClose integer"+ ");");
                break;
            case "Measurements":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "name text" + ");");
                break;
            case "Probs":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "name text,"
                        + "size text,"
                        + "place text,"
                        + "provider text,"
                        + "typeWork text"+ ");");
                break;
            case "Defects":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "name text" + ");");
                break;
            case "Agents":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "nameCompany text,"
                        + "fio text,"
                        + "rang text,"
                        + "img blob,"
                        + "isSubPodryadchik integer,"
                        + "isAvtNadzor integer,"
                        + "isUpolnomochOrg integer,"
                        + "isPodryadchik integer,"
                        + "isZakazchik integer,"
                        + "isRGU integer default 0,"
                        + "isEngineeringService integer" + ");");
                break;
            case "Photos":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "path text,"
                        + "lat real,"
                        + "lon real"+ ");");
                break;
            case "Scans":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idVyezda integer,"
                        + "docType integer,"
                        + "rgu_id integer,"
                        + "path text"  + ");");
                break;
            case "Videos":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "path text,"
                        + "lat real,"
                        + "lon real"+ ");");
                break;
            case "OfflineZip":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "path text"+ ");");
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
