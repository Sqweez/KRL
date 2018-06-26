package com.gosproj.gosproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.gosproj.gosproject.Adapters.GVPhotoAdapter;
import com.gosproj.gosproject.Adapters.GVScanAdapter;
import com.gosproj.gosproject.Functionals.DBHelper;
import com.gosproj.gosproject.Functionals.NavigationDrawer;
import com.gosproj.gosproject.Services.LoadScanService;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Scan;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScanActivity extends AppCompatActivity {
    final int REQUEST_ADD_SCAN = 777;
    Activity activity;
    Context context;
    GridView gridView;
    GVScanAdapter adapter;
    File fullPath = null;
    Uri imageUri;
    int vyezdId;
    int docType;
    ArrayList<Scan> scans = new ArrayList<Scan>();
    FloatingActionButton fab;
    Menu menu;
    Button button;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        scans.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        activity = this;
        context = this;
        button = (Button) findViewById(R.id.buttonLoadScans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Сканирование");
        new NavigationDrawer(context, activity, toolbar);
        scans.clear();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        gridView = (GridView) findViewById(R.id.grid_view);
        DBHelper dbHelper = new DBHelper(context, DBHelper.Scans);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] strings = getIntent().getStringArrayExtra("data");
        vyezdId = Integer.parseInt(strings[0]);
        docType = Integer.parseInt(strings[1]);
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Scans + " WHERE idVyezda = ?", new String[]{String.valueOf(vyezdId)});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDept = cursor.getInt(cursor.getColumnIndex("idVyezda"));
                int type_of_doc = cursor.getInt(cursor.getColumnIndex("docType"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                scans.add(new Scan(id, idDept, path, type_of_doc));
            }
            while (cursor.moveToNext());
        }
        if(scans.isEmpty()){
            button.setVisibility(View.GONE);
        }
        cursor.close();
        db.close();
        dbHelper.close();
        adapter = new GVScanAdapter(activity, scans);
        gridView.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startService(new Intent(activity, LoadScanService.class).putExtra("id", vyezdId));
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        activity.startActivityForResult(takePictureIntent, REQUEST_ADD_SCAN);
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act_menu, menu);
        this.menu = menu;
        menu.findItem(R.id.action_check).setVisible(false);
        if(scans.isEmpty()){
            menu.findItem(R.id.action_remove).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.action_remove:
                removeElements();
                menu.findItem(R.id.action_remove).setVisible(false);
                menu.findItem(R.id.action_check).setVisible(true);
                return true;
            case R.id.action_check:
                removeElementsOk();
                menu.findItem(R.id.action_remove).setVisible(true);
                menu.findItem(R.id.action_check).setVisible(false);
                removeElementsOk();
                return true;
            default:
                return false;
        }
    }
    public void removeElements() {
        adapter.selectedElements();
        fab.hide();
    }
    public void removeElementsOk() {

        ArrayList<Scan> removes = adapter.removeElements();
        DBHelper dbHelper = new DBHelper(context, DBHelper.Scans);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < removes.size(); i++) {
            int delCount = db.delete(DBHelper.Scans, "id = ?",
                    new String[]{String.valueOf(removes.get(i).id)});
        }
        if(scans.isEmpty()){
            button.setVisibility(View.GONE);
            menu.findItem(R.id.action_remove).setVisible(false);
        }
        db.close();
        dbHelper.close();

        fab.show();
    }
    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        String mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_ADD_SCAN) {
            setUri();
        }
    }

    private void setUri() {
        DBHelper dbHelper = new DBHelper(context, DBHelper.Scans);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("idVyezda", vyezdId);
        cv.put("path", fullPath.getPath().toString());
        cv.put("docType", docType);
        long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

        if (rowID != 0) {
            adapter.addPhoto(new Scan((int) rowID, vyezdId, fullPath.getPath().toString(), docType ));
            button.setVisibility(View.VISIBLE);
            menu.findItem(R.id.action_remove).setVisible(true);
        } else {
            Toast.makeText(context, "Не удалось добавить фотографию", Toast.LENGTH_SHORT).show();
        }
        db.close();
        dbHelper.close();
    }


}
