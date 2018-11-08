package com.gosproj.gosproject.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gosproj.gosproject.Functionals.PhotoRequestHandler;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Scan;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GVScanAdapter extends BaseAdapter
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Scan> scans;

    private LayoutInflater mInflater;

    private boolean[] thumbnailsselection;

    public boolean selected = false;

    PhotoRequestHandler photoRequestHandler;
    Picasso picassoInstance;
    public GVScanAdapter(Activity activity, ArrayList<Scan> scans)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.scans = scans;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.thumbnailsselection = new boolean[scans.size()];

        photoRequestHandler = new PhotoRequestHandler();
        picassoInstance = new Picasso.Builder(context.getApplicationContext())
                .addRequestHandler(photoRequestHandler)
                .build();
    }

    @Override
    public int getCount() {
        return scans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addPhoto(Scan scan)
    {
        scans.add(scan);
        this.thumbnailsselection = new boolean[scans.size()];
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.photo_item, null);
            holder.image = (ImageView) convertView.findViewById(R.id.photo);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.textView = (TextView) convertView.findViewById(R.id.countOfScanTv);
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

        final File image = new File(scans.get(position).path);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        holder.image.setBackgroundColor(color);
        picassoInstance.load("image:"+image.getPath()).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected){
                    File videoFile2Play2 = new File(scans.get(position).path);
                    Intent i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(context, "com.gosproj.gosproject", videoFile2Play2));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.setDataAndType(FileProvider.getUriForFile(context, "com.gosproj.gosproject", videoFile2Play2), "image/jpg");
                    activity.startActivity(i);
                }
               else {
                    boolean isChecked = holder.checkBox.isChecked();
                    if(isChecked){
                        holder.checkBox.setChecked(false);
                        thumbnailsselection[holder.id] = false;
                    }
                    else{
                        holder.checkBox.setChecked(true);
                        thumbnailsselection[holder.id] = true;
                    }
                }
            }
        });

        holder.checkBox.setChecked(thumbnailsselection[position]);
        holder.id = position;
        String numberOfImg = String.valueOf(holder.id + 1);
        holder.textView.setText(numberOfImg);
        return convertView;
    }

    public void selectedElements()
    {
        selected = true;
        notifyDataSetChanged();
    }

    public void unSelectedElements()
    {
        for (int i = 0; i<scans.size(); i++)
        {
            if (thumbnailsselection[i])
            {
                thumbnailsselection[i] = false;
            }
        }
        selected = false;
        notifyDataSetChanged();
    }

    public ArrayList<Scan> removeElements()
    {
        selected = false;

        ArrayList<Scan> removes = new ArrayList<Scan>();

        for (int i = 0; i<scans.size(); i++)
        {
            Log.d("CURR_PHOTO", i + " " + thumbnailsselection[i] + " " + scans.size());
            if (thumbnailsselection[i])
            {
                removes.add(scans.get(i));
            }
        }

        for (int i = 0; i<removes.size(); i++)
        {
            for (int l = 0; l<scans.size(); l++)
            {
                if (removes.get(i).id == scans.get(l).id)
                {
                    File file = new File(scans.get(l).path);
                    boolean deleted = file.delete();
                    scans.remove(l);
                }
            }
        }

        this.thumbnailsselection = new boolean[scans.size()];

        selected = false;

        notifyDataSetChanged();

        return removes;
    }

    class ViewHolder
    {
        public ImageView image;
        public CheckBox checkBox;
        public TextView textView;
        int id;
    }
}
