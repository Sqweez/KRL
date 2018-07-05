package com.gosproj.gosproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.graphics.BitmapFactory.Options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.pqpo.smartcropperlib.SmartCropper;
import me.pqpo.smartcropperlib.view.CropImageView;

public class CropActivity extends AppCompatActivity {
    Options options;
    CropImageView ivCrop;
    Button btnOk;
    Button btnCancel;
    File tempFile;
    Uri FileUri;
    Activity activity;
    Bitmap selectedBitmap = null;
    Button btnBW;
    Button btnOrig;
    Bitmap original;
    File newFile;
    int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        activity = this;
        ivCrop = (CropImageView) findViewById(R.id.iv_crop);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnBW = (Button) findViewById(R.id.btnSetBW);
        btnOrig = (Button) findViewById(R.id.btnSetOrig);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        tempFile = (File) getIntent().getSerializableExtra("file");
        newFile = new File(tempFile, "" + count + ".jpg");
        FileUri = FileProvider.getUriForFile(getApplicationContext(), "com.gosproj.gosproject", tempFile);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap crop = ivCrop.crop();
                if (crop != null) {
                    Bitmap resized = getResizedBitmap(crop, 1191, 1684);
                    crop = resized;
                    savePhoto(crop, tempFile);
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempFile.delete();
                setResult(0);
                finish();
            }
        });
        btnBW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBitmap = toGrayscale(selectedBitmap);
                ivCrop.setImageToCrop(selectedBitmap);
            }
        });
        btnOrig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBitmap = original;
                ivCrop.setImageToCrop(selectedBitmap);

            }
        });
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhoto.resolveActivity(activity.getPackageManager()) != null) {
            takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, FileUri);
            activity.startActivityForResult(takePhoto, 2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(this.tempFile.getPath());
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(options);
            selectedBitmap = BitmapFactory.decodeFile(this.tempFile.getPath(), options);
        } else {
            tempFile.delete();
            setResult(0);
            finish();
        }
        if (selectedBitmap != null) {
            this.ivCrop.setImageToCrop(selectedBitmap);
            original = selectedBitmap;
        }
    }

    private void savePhoto(Bitmap bitmap, File saveFile) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int calculateSampleSize(Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        if (outHeight > 1000 || outWidth > 1000) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / 1000;
            } else {
                sampleSize = outWidth / 1000;
            }
        }
        if (sampleSize < 1) {
            return 1;
        }
        return sampleSize;
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
