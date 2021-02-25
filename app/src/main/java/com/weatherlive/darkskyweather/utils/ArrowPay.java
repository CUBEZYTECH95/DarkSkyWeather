package com.weatherlive.darkskyweather.utils;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import static com.facebook.ads.BuildConfig.DEBUG;

public class ArrowPay extends Application {

    private static ArrowPay mInstance;

    public ArrowPay() {
        mInstance = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        AudienceNetworkInitializeHelper.initialize(this);

        if (DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

    }

    public static Context getContext() {
        return mInstance;
    }

    public static synchronized ArrowPay getInstance() {
        return mInstance;
    }

}
