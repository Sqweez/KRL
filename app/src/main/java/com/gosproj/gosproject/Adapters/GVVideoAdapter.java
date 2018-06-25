package com.gosproj.gosproject.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.gosproj.gosproject.Functionals.VideoRequestHandler;
import com.gosproj.gosproject.R;
import com.gosproj.gosproject.Structures.Photo;
import com.gosproj.gosproject.Structures.Videos;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GVVideoAdapter extends BaseAdapter
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Videos> videoses;
   // ArrayList<Photo> removes = new ArrayList<Photo>();
    private LayoutInflater mInflater;

    private boolean[] thumbnailsselection;

    public boolean selected = false;

    VideoRequestHandler videoRequestHandler;
    Picasso picassoInstance;

    public GVVideoAdapter(Activity activity, ArrayList<Videos> videoses)
    {
        this.activity = activity;
        this.resources = activity.getResources();
        this.context = activity.getApplicationContext();

        this.videoses = videoses;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.thumbnailsselection = new boolean[videoses.size()];

        videoRequestHandler = new VideoRequestHandler();
        picassoInstance = new Picasso.Builder(context.getApplicationContext())
                .addRequestHandler(videoRequestHandler)
                .build();
    }

    @Override
    public int getCount() {
        return videoses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addPhoto(Videos videos)
    {
        videoses.add(videos);
        this.thumbnailsselection = new boolean[videoses.size()];
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.video_item, null);
            holder.image = (ImageView) convertView.findViewById(R.id.video);
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
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // File file = new File(videoses.get(position).path);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoses.get(position).path));
                    intent.setDataAndType(Uri.parse(videoses.get(position).path), "video/mp4");
                    activity.startActivity(intent);
                }
            });
        }

        final File image = new File(videoses.get(position).path);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        holder.image.setBackgroundColor(color);

        /*
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(image.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        holder.image.setImageBitmap(thumb);*/

        picassoInstance.load("video:"+image.getPath()).into(holder.image);

        /*picassoInstance.load(VideoRequestHandler.SCHEME_VIDEO+":"+filepath).into(holder.videoThumbnailView);
        Picasso
                .with(context)
                .load(image)
                .resize(300, 300)
                .centerCrop()
                .into(holder.image); */

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
        for (int i = 0; i<videoses.size(); i++)
        {
            if (thumbnailsselection[i])
            {
                thumbnailsselection[i] = false;
            }
        }
        selected = false;
        notifyDataSetChanged();
    }

    public ArrayList<Videos> removeElements()
    {
        selected = false;

        ArrayList<Videos> removes = new ArrayList<Videos>();

        for (int i = 0; i<videoses.size(); i++)
        {
            Log.d("CURR_PHOTO", i + " " + thumbnailsselection[i] + " " + videoses.size());
            if (thumbnailsselection[i])
            {
                removes.add(videoses.get(i));
            }
        }

        for (int i = 0; i<removes.size(); i++)
        {
            for (int l = 0; l<videoses.size(); l++)
            {
                if (removes.get(i).id == videoses.get(l).id)
                {
                    File file = new File(videoses.get(l).path);
                    boolean deleted = file.delete();
                    videoses.remove(l);
                }
            }
        }

        this.thumbnailsselection = new boolean[videoses.size()];

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
