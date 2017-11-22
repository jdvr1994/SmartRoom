package com.apps.ing3ns.smartroomapp;

import android.app.Application;
import android.os.SystemClock;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by julia on 30/06/2017.
 */

public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        SystemClock.sleep(1000);
    }
}
