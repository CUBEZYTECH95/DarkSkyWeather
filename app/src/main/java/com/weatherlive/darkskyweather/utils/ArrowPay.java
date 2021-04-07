package com.weatherlive.darkskyweather.utils;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OneSignal;

import java.util.Collections;
import java.util.List;

import static com.facebook.ads.BuildConfig.DEBUG;

public class ArrowPay extends Application {

    private static ArrowPay mInstance;
    private static AppOpenManager appOpenManager;
    private static final String ONESIGNAL_APP_ID = "8bc35e79-f27b-408a-8170-a05922c1e3ce";

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

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }

    public static Context getContext() {
        return mInstance;
    }

    public static synchronized ArrowPay getInstance() {
        return mInstance;
    }

    /*ad*/

}
