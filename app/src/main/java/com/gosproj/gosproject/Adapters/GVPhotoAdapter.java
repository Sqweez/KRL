package com.gosproj.gosproject.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.gosproj.gosproject.Functionals.PhotoRequestHandler;
import com.gosproj.gosproject.Functionals.VideoRequestHandler;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Agent;
import com.gosproj.gosproject.Structures.Photo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GVPhotoAdapter extends BaseAdapter
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Photo> photos;
   // ArrayList<Photo> removes = new ArrayList<Photo>();
    private LayoutInflater mInflater;

    private boolean[] thumbnailsselection;

    public boolean selected = false;

    PhotoRequestHandler photoRequestHandler;
    Picasso picassoInstance;

    public GVPhotoAdapter (Activity activity, ArrayList<Photo> photos)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.photos = photos;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.thumbnailsselection = new boolean[photos.size()];

        photoRequestHandler = new PhotoRequestHandler();
        picassoInstance = new Picasso.Builder(context.getApplicationContext())
                .addRequestHandler(photoRequestHandler)
                .build();
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addPhoto(Photo photo)
    {
        photos.add(photo);
        this.thumbnailsselection = new boolean[photos.size()];
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.photo_item, null);
            holder.image = (ImageView) convertView.findViewById(R.id.photo);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(holder);
        }
        else
            {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkBox.setId(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                if (thumbnailsselection[id])
                {
                    cb.setChecked(false);
                    thumbnailsselection[id] = false;
                } else {
                    cb.setChecked(true);
                    thumbnailsselection[id] = true;
                }
            }
        });

        if (selected)
        {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkBox.setVisibility(View.GONE);
        }

        final File image = new File(photos.get(position).path);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        holder.image.setBackgroundColor(color);

        /*
        Picasso.with(activity).load(image).fit().centerCrop().into(holder.image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("PICASSO", "IMAGE IN LOAD");
                    }

                    @Override
                    public void onError() {
                        Log.d("PICASSO", "IMAGE FARAL ERROR");
                    }
                });

        Picasso.Builder builder = new Picasso.Builder(activity);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.d("PICASSO", exception.getMessage());
            }
        });*/

        picassoInstance.load("image:"+image.getPath()).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File videoFile2Play2 = new File(photos.get(position).path);
                Intent i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(context, "com.gosproj.gosproject", videoFile2Play2));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setDataAndType(FileProvider.getUriForFile(context, "com.gosproj.gosproject", videoFile2Play2), "image/jpg");
                activity.startActivity(i);
            }
        });

        holder.checkBox.setChecked(thumbnailsselection[position]);
        holder.id = position;
        return convertView;
    }

    public void selectedElements()
    {
        selected = true;
        notifyDataSetChanged();
    }

    public void unSelectedElements()
    {
        for (int i = 0; i<photos.size(); i++)
        {
            if (thumbnailsselection[i])
            {
                thumbnailsselection[i] = false;
            }
        }
        selected = false;
        notifyDataSetChanged();
    }

    public ArrayList<Photo> removeElements()
    {
        selected = false;

        ArrayList<Photo> removes = new ArrayList<Photo>();

        for (int i = 0; i<photos.size(); i++)
        {
            Log.d("CURR_PHOTO", i + " " + thumbnailsselection[i] + " " + photos.size());
            if (thumbnailsselection[i])
            {
                removes.add(photos.get(i));
            }
        }

        for (int i = 0; i<removes.size(); i++)
        {
            for (int l = 0; l<photos.size(); l++)
            {
                if (removes.get(i).id == photos.get(l).id)
                {
                    File file = new File(photos.get(l).path);
                    boolean deleted = file.delete();
                    photos.remove(l);
                }
            }
        }

        this.thumbnailsselection = new boolean[photos.size()];

        selected = false;

        notifyDataSetChanged();

        return removes;
    }

    class ViewHolder
    {
        public ImageView image;
        public CheckBox checkBox;
        int id;
    }
}
