package com.gosproj.gosproject.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.gosproj.gosproject.Adapters.GVPhotoAdapter;
import com.gosproj.gosproject.Adapters.GVVideoAdapter;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Videos;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class ActEightFragment extends Fragment
{
    final int REQUEST_ADD_VIDEO = 250;
    int id;

    Context context;
    Resources resources;
    Activity activity;

    ArrayList<Videos> videoses = new ArrayList<Videos>();

    LogsHelper logsHelper;

    GridView gridView;
    GVVideoAdapter gvVideoAdapter;

    File fullPath;

    FloatingActionButton fab;

    public static ActEightFragment getInstance(int id, FloatingActionButton fab)
    {
        Bundle args = new Bundle();
        ActEightFragment fragment = new ActEightFragment();
        fragment.setArguments(args);
        fragment.id = id;
        fragment.fab = fab;
        return fragment;
    }

    public ActEightFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.act_eight_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();
        logsHelper = new LogsHelper(LogsHelper.VIDEO, context, activity, id);
        videoses.clear();

        gridView = (GridView) view.findViewById(R.id.grid_view);

        DBHelper dbHelper = new DBHelper(context, DBHelper.Videos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Videos + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String path = cursor.getString(cursor.getColumnIndex("path"));

            //    Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

                videoses.add(new Videos(id, idDept, path));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        gvVideoAdapter = new GVVideoAdapter(activity, videoses);

        gridView.setAdapter(gvVideoAdapter);

        return view;
    }

    public void setFabClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null)
                {
                    File videoFile = null;
                    try
                    {
                        videoFile = createImageFile();
                    }
                    catch (IOException ex)
                    {
                        Log.e("ERROR_CAM", ex.getMessage().toString());
                    }

                    if (videoFile != null)
                    {
                        fullPath = videoFile;
                        Uri imageUri = FileProvider.getUriForFile(context, "com.gosproj.gosproject", videoFile);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activity.startActivityForResult(takeVideoIntent, REQUEST_ADD_VIDEO);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 200 && resultCode == RESULT_OK)
        {
            Log.d("ADS", "sdad");
            Uri videoUri = data.getData();
            SetUri();
        }
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MP4_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        String mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void SetUri()
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.Videos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("idDept", String.valueOf(id));
        cv.put("path", fullPath.getPath().toString());

        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

        logsHelper.createLog("", "", LogsHelper.ACTION_ADD);

        if (rowID != 0)
        {
            //photos.add(new Photo((int) rowID, id, fullPath.getPath().toString()));
            gvVideoAdapter.addPhoto(new Videos((int) rowID, id, fullPath.getPath().toString()));
        }
        else
        {
            Toast.makeText(context, getResources().getString(R.string.fail_add_new_prob), Toast.LENGTH_LONG).show();
        }

        db.close();
        dbHelper.close();
    }

    public void closeCheckUi()
    {
        if (gvVideoAdapter != null)
        {
            gvVideoAdapter.unSelectedElements();
            fab.show();
        }
    }

    public void removeElements()
    {
        gvVideoAdapter.selectedElements();
       // rvAgentAdapter.selectedElements();
        fab.hide();
    }

    public void removeElementsOk()
    {
        ArrayList<Videos> removes =  gvVideoAdapter.removeElements();

        DBHelper dbHelper = new DBHelper(context, DBHelper.Videos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0; i<removes.size(); i++)
        {
            logsHelper.createLog("", "", LogsHelper.ACTION_DELETE);
            int delCount = db.delete(DBHelper.Videos, "id = ?",
                    new String[]{String.valueOf(removes.get(i).id)});
        }
        db.close();
        dbHelper.close();

        fab.show();
    }
}
