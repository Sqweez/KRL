package com.gosproj.gosproject.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

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
import com.gosproj.gosproject.ActActivity;
import com.gosproj.gosproject.Adapters.GVPhotoAdapter;
import com.gosproj.gosproject.Adapters.RVAgentAdapter;
import com.gosproj.gosproject.AgentActivity;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.MainActivity;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Services.LogsHelper;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Proba;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.POWER_SERVICE;

public class ActSevenFragment extends Fragment {
    final int REQUEST_ADD_PHOTO = 240;
    int id;

    Context context;
    Resources resources;
    Activity activity;

    FloatingActionButton fab;
    LogsHelper logsHelper;
    ArrayList<Photo> photos = new ArrayList<Photo>();
    GridView gridView;
    GVPhotoAdapter gvPhotoAdapter;

    Uri imageUri;

    File fullPath = null;

    public static ActSevenFragment getInstance(int id, FloatingActionButton fab) {
        Bundle args = new Bundle();
        ActSevenFragment fragment = new ActSevenFragment();
        fragment.setArguments(args);
        fragment.id = id;
        fragment.fab = fab;
        return fragment;
    }

    public ActSevenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.act_seven_fragment, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        logsHelper = new LogsHelper(LogsHelper.PHOTO, context, activity, id);

        photos.clear();
        gridView = (GridView) view.findViewById(R.id.grid_view);

        DBHelper dbHelper = new DBHelper(context, DBHelper.Photos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Photos + " WHERE idDept = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idDept"));
                String path = cursor.getString(cursor.getColumnIndex("path"));

                //    Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

                photos.add(new Photo(id, idDept, path));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        gvPhotoAdapter = new GVPhotoAdapter(activity, photos);

        gridView.setAdapter(gvPhotoAdapter);

        return view;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        String mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void SetUri() {
        float [] latLong = new float[2];

        try {
            final ExifInterface exifInterface = new ExifInterface(fullPath.getPath());
            exifInterface.getLatLong(latLong);
            Log.d("lat", "Lat" + latLong[0]);
            Log.d("lat", "Long" + latLong[1]);
        }
        catch (IOException e){
        }
        DBHelper dbHelper = new DBHelper(context, DBHelper.Photos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("idDept", String.valueOf(id));
        cv.put("path", fullPath.getPath().toString());

        cv.put("lat", latLong[0]);
        cv.put("lon", latLong[1]);

        Log.d("POSITION", "NULL");

        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);
        logsHelper.createLog(fullPath.getName(), "", LogsHelper.ACTION_ADD);
        if (rowID != 0) {
            //photos.add(new Photo((int) rowID, id, fullPath.getPath().toString()));
            gvPhotoAdapter.addPhoto(new Photo((int) rowID, id, fullPath.getPath().toString()));
        } else {
            Toast.makeText(context, getResources().getString(R.string.fail_add_new_prob), Toast.LENGTH_LONG).show();
        }

        db.close();
        dbHelper.close();
    }

    public void setFabClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.e("ERROR_CAM", ex.getMessage().toString());
                    }

                    if (photoFile != null) {
                        fullPath = photoFile;
                        imageUri = FileProvider.getUriForFile(context, "com.gosproj.gosproject", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activity.startActivityForResult(takePictureIntent, REQUEST_ADD_PHOTO);
                    }
                }
            }
        });
    }

    public void closeCheckUi() {
        if (gvPhotoAdapter != null) {
            gvPhotoAdapter.unSelectedElements();
            fab.show();
        }
    }

    public void removeElements() {
        gvPhotoAdapter.selectedElements();
        fab.hide();
    }

    public void removeElementsOk() {
        ArrayList<Photo> removes = gvPhotoAdapter.removeElements();

        DBHelper dbHelper = new DBHelper(context, DBHelper.Photos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < removes.size(); i++) {
            String[] fileName = removes.get(i).path.split("\\/");
            logsHelper.createLog(fileName[9], "", LogsHelper.ACTION_DELETE);
            int delCount = db.delete(DBHelper.Photos, "id = ?",
                    new String[]{String.valueOf(removes.get(i).id)});
        }

        db.close();
        dbHelper.close();

        fab.show();
    }

}
