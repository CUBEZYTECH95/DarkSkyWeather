package com.weatherlive.darkskyweather.utils;

import android.content.Context;

import com.facebook.ads.AudienceNetworkAds;

public class AudienceNetworkInitializeHelper implements AudienceNetworkAds.InitListener {


    private final Context mContext;

    private AudienceNetworkInitializeHelper(Context context) {
        mContext = context;
    }

    /**
     * It's recommended to call this method from Application.onCreate(). Otherwise you can call it
     * from all Activity.onCreate() methods for Activities that contain ads.
     *
     * @param context Application or Activity.
     */
    static void initialize(Context context) {


        AudienceNetworkAds.buildInitSettings(context)
                .withInitListener(new AudienceNetworkInitializeHelper(context))
                .initialize();

    }

    @Override
    public void onInitialized(AudienceNetworkAds.InitResult result) {

    }
}
