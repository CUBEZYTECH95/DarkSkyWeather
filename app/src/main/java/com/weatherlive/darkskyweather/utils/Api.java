package com.weatherlive.darkskyweather.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class Api {
    @SuppressLint("StaticFieldLeak")
    private static Api mInstance;
    private RequestQueue mRequestQueue;
    @SuppressLint("StaticFieldLeak")
    private static Context mCtx;

    public Api(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized Api getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Api(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = "https://api.accuweather.com/locations/v1/cities/autocomplete?q=" + query + "&apikey=srRLeAmTroxPinDG8Aus3Ikl6tLGJd94&language=en-us&get_param=value";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        Api.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
