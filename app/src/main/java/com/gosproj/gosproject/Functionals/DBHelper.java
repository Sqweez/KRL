package com.gosproj.gosproject.Functionals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 21;
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
    public static String VidRabot = "VidRabot";

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
            case "VidRabot":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "vid_rabot_id integer,"
                        + "name text"+  ");");
                break;
            case "Departures":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idAct integer,"
                        + "idNomer integer,"
                        + "date text,"
                        + "object text,"
                        + "id_rabot integer,"
                        + "vid_rabot text,"
                        + "ispolnitel text,"
                        + "gruppa_vyezda1 text,"
                        + "gruppa_vyezda2 text,"
                        + "gruppa_vyezda3 text,"
                        + "podradchyk text,"
                        + "subpodradchyk text,"
                        + "avt_nadzor text,"
                        + "inj_sluzhby text,"
                        + "rgu_name text,"
                        + "uorg text,"
                        + "zakazchik text,"
                        + "isClose integer"+ ");");
                break;
            case "Measurements":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "value text" + ");");
                break;
            case "Probs":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "name text,"
                        + "count integer,"
                        + "size text,"
                        + "place text,"
                        + "provider text,"
                        + "typeWork text"+ ");");
                break;
            case "Defects":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "name text,"
                        + "kilometr text,"
                        + "comment text" + ");");
                break;
            case "Agents":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDept integer,"
                        + "nameCompany text,"
                        + "fio text,"
                        + "rang text,"
                        + "img blob,"
                        + "isProvider integer,"
                        + "isCustomer integer,"
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
