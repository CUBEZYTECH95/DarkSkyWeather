package com.weatherlive.darkskyweather.utils;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Collections;
import java.util.List;

import static com.facebook.ads.BuildConfig.DEBUG;

public class ArrowPay extends Application {

    private static ArrowPay mInstance;
    private static AppOpenManager appOpenManager;

    public ArrowPay() {
        mInstance = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });

        List<String> testDeviceIds = Collections.singletonList("b1558c4a-f475-48f3-9bc1-646391224a37");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        appOpenManager = new AppOpenManager(this);


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

    /*ad*/

}
