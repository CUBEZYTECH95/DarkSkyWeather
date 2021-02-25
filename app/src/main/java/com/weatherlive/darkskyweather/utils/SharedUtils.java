package com.weatherlive.darkskyweather.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedUtils {

    public static final String PREF_DEFAULT_LOCATION_LIST = "Pref_default_location_list";
    public static final String PREF_ADS_DATA = "Pref_ads_data_loaded";

    public static String getDefaultLocationListInfo(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_DEFAULT_LOCATION_LIST, "");
    }

    public static void getDefaultLocationListRemove(Context context) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor spreferencesEditor = sp.edit();
        spreferencesEditor.remove(PREF_DEFAULT_LOCATION_LIST).clear().apply();
    }

    public static void setDefaultLocationInfo(Context context, String bytes) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_DEFAULT_LOCATION_LIST, bytes).apply();
    }

    public static String getAdsData(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_ADS_DATA, "");
    }

    public static void setAdsData(Context context, String bytes) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_ADS_DATA, bytes).apply();
    }


}