package com.weatherlive.darkskyweather.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.weatherlive.darkskyweather.R;


public class SharedPrefs {

    private static String sharedPrefenceName = ArrowPay.getContext().getResources().getString(R.string.app_name);

    public static SharedPreferences getSharedPref() {
        return ArrowPay.getContext().getSharedPreferences(sharedPrefenceName, Context.MODE_PRIVATE);
    }

    public interface userSharedPrefData {

        String user_id = "user_id";
        String location_ads_time = "location_ads_time";
        String user_phone_number = "user_phone_number";
        String user_referral_code = "user_referral_code";
        String user_device_id = "user_device_id";
        String user_imei_number = "user_imei_number";
        String user_status = "user_status";
        String user_create_date = "user_create_date";
        String total_points = "total_points";
        String day_count = "day_count";
        String is_date = "is_date";
        String package_name = "package_name";
        String facebook_url = "facebook_url";
        String twitter_url = "twitter_url";
        String instagram_url = "instagram_url";
        String youtube_url = "youtube_url";
        String telegram_url = "telegram_url";
        String whatsapp_number = "whatsapp_number";
        String wifi = "wifi";
        String seatAvaTo = "seatAvaTo";
        String seatAvaFrom = "seatAvaFrom";
        String seatAvaDate = "seatAvaDate";
        String scheduledTo = "scheduledTo";
        String scheduledFrom = "scheduledFrom";
        String scheduledDate = "scheduledDate";
        String json = "json";
        String json1 = "json1";
    }

    public static void putString(String key, String value) {
        getSharedPref().edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getSharedPref().getString(key, "");
    }

    public static void putInteger(String key, int value) {
        getSharedPref().edit().putInt(key, value).apply();
    }

    public static int getInteger(String key) {
        return getSharedPref().getInt(key, 0);
    }

    public static void removeKey(String key) {
        getSharedPref().edit().remove(key).apply();
    }

    public static void addPoints(int value) {
        int total = getSharedPref().getInt(userSharedPrefData.total_points, 0) + value;
        getSharedPref().edit().putInt(userSharedPrefData.total_points, total).apply();
    }

    public static void setPoints(int value) {
        getSharedPref().edit().putInt(userSharedPrefData.total_points, value).apply();
    }

    public static int getPoints() {
        return getSharedPref().getInt(userSharedPrefData.total_points, 0);
    }



    public static long getAdsTime(String key) {
        return getSharedPref().getLong(key, 0);
    }

    public static void saveAdsTime(String key, long time) {
        getSharedPref().edit().putLong(key, time).apply();
    }

    public static boolean isAdFirstTime(String key) {
        return getSharedPref().getBoolean(key, false);
    }

    public static void setAdFirstTime(String key, boolean val) {
        getSharedPref().edit().putBoolean(key, val).apply();
    }
}
