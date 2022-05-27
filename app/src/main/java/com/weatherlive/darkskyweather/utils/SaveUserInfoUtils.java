package com.weatherlive.darkskyweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SaveUserInfoUtils {

    public static void saveToUserDefaults(Context context, String key,
                                          String value) {

        Log.d("SaveUserInfoUtils", "Saving:" + key + ":" + value);
        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static String getFromUserDefaults(Context context, String key) {
        Log.d("SaveUserInfoUtils", "Get:" + key);
        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }


    public static void saveUnitToUserDefaults(Context context, String key,
                                              String value) {

        Log.d("SaveUserInfoUtils", "Saving:" + key + ":" + value);
        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public static String getUnitFromUserDefaults(Context context, String key) {
        Log.d("SaveUserInfoUtils", "Get:" + key);
        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "Metric");
    }


    public static void saveUnitToDefaults(Context context, String key,
                                          int value) {

        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    public static int getUnitFromDefaults(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }


    public static void saveToUserDenny(Context context, String key,
                                       String value) {

        Log.d("SaveUserInfoUtils", "Saving:" + key + ":" + value);
        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();

    }


    public static String getFromUserDenny(Context context, String key) {
        Log.d("SaveUserInfoUtils", "Get:" + key);
        SharedPreferences preferences = context.getSharedPreferences(
                ImageConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }


    


}
