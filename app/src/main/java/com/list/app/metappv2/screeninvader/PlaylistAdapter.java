package com.list.app.metappv2.screeninvader;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.list.app.metappv2.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.ArrayList;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by meks on 21.07.2016.
 */
public class PlaylistAdapter extends ArrayAdapter<PlaylistItem> {

    ArrayList<PlaylistItem> playlistItems;
    Context context;
    ScreeninvaderAPI SIAPI;
    int layoutID;

    ImageLoader imageLoader = ImageLoader.getInstance();

    private final static String YOUTUBE_THUMBNAIL_LINK = "http://img.youtube.com/vi/%1$s/default.jpg";

    public PlaylistAdapter( Context c, int l, ArrayList<PlaylistItem> items, ScreeninvaderAPI api){
        super(c, l, items);

        layoutID = l;
        context = c;
        playlistItems = items;
        SIAPI = api;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .build();

        imageLoader.init(config);
    }

    @Override
    public int getCount() {
        return playlistItems.size();
    }

    @Override
    public PlaylistItem getItem(int position) {
        playlistItems.get(position);
        return playlistItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return Long.valueOf(position);
    }


    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();
        LayoutInflater li = LayoutInflater.from(context);

        PlaylistItem current = playlistItems.get(position);

        View itemView = li.inflate(R.layout.listitem_screeninvader, parent, false);
        holder.title = (TextView) itemView.findViewById(R.id.view_title);
        holder.timestamp = (TextView) itemView.findViewById(R.id.view_timestamp);
        holder.thumbnail = (ImageView) itemView.findViewById(R.id.view_thumbnail);

        holder.title.setText(current.title);
        if(current.category == "youtube") {holder.timestamp.setText(getDuration(current.source_url));}


        current.id = current.url.substring(current.url.length() - 11);
        String thumbnailUrl = String.format(YOUTUBE_THUMBNAIL_LINK, current.id);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_issues)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, dpToPx(71), dpToPx(40), false);
                    }
                })
                .build();

        imageLoader.displayImage(thumbnailUrl, holder.thumbnail, options);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SIAPI.sendSICommand("/playlist/index", Integer.toString(position));
            }
        });
        return itemView;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static String getDuration(String url){
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(url);

        String time =  mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong( time );
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        return minutes+":"+seconds;
    }

    public class Holder{
        TextView title;
        TextView timestamp;
        ImageView thumbnail;
    }
}
