package com.weatherlive.darkskyweather.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks {

    private static boolean isShowingAd = false;
    private static final String LOG_TAG = "AppOpenManager";
    private static final String AD_UNIT_ID = "ca-app-pub-4293491867572780/4009494595";
    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    private final ArrowPay arrowPay;
    private Activity currentActivity = null;

    private long loadTime = 0;


    /**
     * Constructor
     */
    public AppOpenManager(ArrowPay arrowPay) {
        this.arrowPay = arrowPay;
        this.arrowPay.registerActivityLifecycleCallbacks(this);

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAppOpenAdLoaded(AppOpenAd ad) {
                        AppOpenManager.this.appOpenAd = ad;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        AppOpenManager.this.appOpenAd = appOpenAd;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                        showAdIfAvailable();
                        currentActivity = null;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e("TAG", "onAdFailedToLoad: "+loadAdError.getMessage());
                        AdRequest request = getAdRequest();
                        AppOpenAd.load(
                                arrowPay, AD_UNIT_ID, request,
                                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    @Override
                    public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle the error.
                    }
                };

        AdRequest request = getAdRequest();
        AppOpenAd.load(
                arrowPay, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
//        showAdIfAvailable();
        Log.d(LOG_TAG, "onStart");
    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        // We will implement this below.
        if (isAdAvailable()) {
            return;
        }

//        loadCallback =
//                new AppOpenAd.AppOpenAdLoadCallback() {
//                    /**
//                     * Called when an app open ad has loaded.
//                     *
//                     * @param ad the loaded app open ad.
//                     */
//                    @Override
//                    public void onAppOpenAdLoaded(AppOpenAd ad) {
//                        AppOpenManager.this.appOpenAd = ad;
//                        AppOpenManager.this.loadTime = (new Date()).getTime();
//                    }
//
//                    @Override
//                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
//                        super.onAdLoaded(appOpenAd);
//                        AppOpenManager.this.appOpenAd = appOpenAd;
//                        AppOpenManager.this.loadTime = (new Date()).getTime();
//                        showAdIfAvailable();
//                        currentActivity = null;
//                    }
//
//                    /**
//                     * Called when an app open ad has failed to load.
//                     *
//                     * @param loadAdError the error.
//                     */
//                    @Override
//                    public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
//                        // Handle the error.
//                    }
//                };

//        AdRequest request = getAdRequest();
//        AppOpenAd.load(
//                myApplication, AD_UNIT_ID, request,
//                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    /**
     * ActivityLifecycleCallback methods
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        currentActivity = activity;
        showAdIfAvailable();
    }

    @Override
    public void onActivityStarted(Activity activity) {

        currentActivity = activity;

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;


//                            fetchAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.show(currentActivity, fullScreenContentCallback);

        }
//        else {
//            Log.d(LOG_TAG, "Can not show ad.");
//            fetchAd();
//        }
    }

}
