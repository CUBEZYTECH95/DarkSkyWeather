package com.weatherlive.darkskyweather.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.weatherlive.darkskyweather.ItemClick;
import com.weatherlive.darkskyweather.Model.LocationModel;
import com.weatherlive.darkskyweather.Model.NextModel;
import com.weatherlive.darkskyweather.Model.OneHourModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.adapter.WeatherListDataAdapter;
import com.weatherlive.darkskyweather.utils.ImageConstant;
import com.weatherlive.darkskyweather.utils.InternetConnection;
import com.weatherlive.darkskyweather.utils.NetworkUtility;
import com.weatherlive.darkskyweather.utils.PermissionUtils;
import com.weatherlive.darkskyweather.utils.SaveUserInfoUtils;
import com.weatherlive.darkskyweather.utils.SharedPrefs;
import com.weatherlive.darkskyweather.utils.SharedUtils;
import com.weatherlive.darkskyweather.utils.SpeedyLinearLayoutManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity implements /*AdListener,*/ ItemClick {

//    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private Boolean mRequestingLocationUpdates = false;
    public static boolean apponcreate = true;
    Type type = new TypeToken<List<LocationModel>>() {
    }.getType();

    RecyclerView listData;
    ArrayList<NextModel> nextModelArrayList;
    WeatherListDataAdapter nextDayDataListAdapter;
    Boolean isInternetPresent = false;
    InternetConnection cd;
    RecyclerView.LayoutManager mLayoutManager;
    LinearLayout ivNointernetCoonection;
    LottieAnimationView pd;
    private double default_lat = 35.8617;
    private double default_long = 104.1954;
    private boolean isAllow = false;
    RelativeLayout adContainer;
    private List<NativeAd> mAds = new ArrayList<>();
    private NativeAdsManager mAdsManager;
    int NUM_ADS = 5;
    List<Object> objectArrayList = new ArrayList<>();
    InterstitialAd fbinterstitialAd;
    /*public com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd;*/
    final Handler handler = new Handler(Looper.getMainLooper());
    private ProgressDialog progress;
    Date mdate15 = new Date();
    private AdManagerAdView adView;
    private AdManagerInterstitialAd interstitialAd;
    RelativeLayout rlContainer2;

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String TAG = "TEST";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*adView = findViewById(R.id.ad_view);*/
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        rlContainer2 = findViewById(R.id.adContainer);

        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    }
                });

        List<String> testDeviceIds = Collections.singletonList("965e1f03-2e7f-4a50-4660-282729f004e9");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        googlebanner();

        /*AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adView.loadAd(adRequest);*/


      /*  gadView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        gadView.loadAd(adRequest);*/

       /* if (fbinterstitialAd != null) {
            fbinterstitialAd.destroy();
            fbinterstitialAd = null;
        }

        fbinterstitialAd =
                new InterstitialAd(this, getString(R.string.fb_int));

        // Load a new interstitial.
        InterstitialAd.InterstitialLoadAdConfig loadAdConfig =
                fbinterstitialAd
                        .buildLoadAdConfig()
                        // Set a listener to get notified on changes
                        // or when the user interact with the ad.
                        .withAdListener(this)
                        .withCacheFlags(EnumSet.of(CacheFlag.VIDEO))
                        .build();
        fbinterstitialAd.loadAd(loadAdConfig);*/

        progress = new ProgressDialog(this);
        progress.setMessage("Loading....");

        loadAd();

        /*AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(HomeActivity.this);
        interstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitial.loadAd(adRequest1);

        if (!interstitial.isLoaded()) {
            interstitial.loadAd(adRequest1);
        }

        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
                startActivityForResult(intent, 11);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("LLLL_Error: ", loadAdError.getMessage());

            }


        });*/


        cd = new InternetConnection(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            /*adContainer.setVisibility(View.VISIBLE);
            bannerad();*/
        } else {
            /* adContainer.setVisibility(View.GONE);*/
        }
//        runtimePermission();
        init();
        restoreValuesFromBundle(savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    private void googlebanner() {

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        AdManagerAdView adView = new AdManagerAdView(this);
        adView.setAdSizes(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.ad_unit_id_banner));
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("TAG", "onAdLoaded: ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Toast.makeText(HomeActivity.this, "" + adError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        RelativeLayout.LayoutParams bannerParameters = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        rlContainer2.addView(adView, bannerParameters);
    }

    public void loadAd() {

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        AdManagerInterstitialAd.load(
                this,
                getString(R.string.ad_unit_int),
                adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        HomeActivity.this.interstitialAd = interstitialAd;

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        HomeActivity.this.interstitialAd = null;
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 5000ms
                                                /* progress.dismiss();*/
                                                pd.setVisibility(View.GONE);
                                            }
                                        }, 2000);
                                        finish();

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        HomeActivity.this.interstitialAd = null;
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {

                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        interstitialAd = null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 5000ms
                                pd.setVisibility(View.GONE);
                            }
                        }, 2000);

                    }
                });

       /* com.google.android.gms.ads.interstitial.InterstitialAd.load(
                this,
                getString(R.string.google_int),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        HomeActivity.this.interstitialAd = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        HomeActivity.this.interstitialAd = null;
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 5000ms
                                                progress.dismiss();
                                            }
                                        }, 2000);
                                        finish();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        Log.e("onAdFailedToShow", "onAdFailedToLoad: " + adError.getDomain());
                                        Log.e("onAdFailedToShowget", "onAdFailedToLoad: " + adError.getCode());
                                        HomeActivity.this.interstitialAd = null;
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.

                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        // Handle the error
                        Log.e("getMessage", "onAdFailedToLoad: " + loadAdError.getDomain());
                        Log.e("getCode", "onAdFailedToLoad: " + loadAdError.getCode());

                        interstitialAd = null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 5000ms
                                progress.dismiss();
                            }
                        }, 2000);

                    }

                });*/

    }


    public void init() {

        apponcreate = true;
        pd = findViewById(R.id.progressBar);
        pd.enableMergePathsForKitKatAndAbove(true);
        pd.playAnimation();
        pd.cancelAnimation();
        pd.setVisibility(View.VISIBLE);
        /*ProgressDialog.getInstance().showProgress(this);*/

        ivNointernetCoonection = (LinearLayout) findViewById(R.id.ivNointernetCoonection);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();


//        if (Build.VERSION.SDK_INT >= 23) {
//            Dexter.withActivity(this)
//                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                    .withListener(new PermissionListener() {
//                        @Override
//                        public void onPermissionGranted(PermissionGrantedResponse response) {
//                            mRequestingLocationUpdates = true;
//                            startLocationUpdates();
//                            isAllow = true;
//                            default_lat = 0;
//                            default_long = 0;
//                        }
//
//                        @Override
//                        public void onPermissionDenied(PermissionDeniedResponse response) {
//
//
//
//                            if (response.isPermanentlyDenied()) {
//                                // open device settings when the permission is
//                                // denied permanently
//                                openSettings();
//
//                            }
//                            else {
//                                isAllow = false;
//                                updateLocationUI();
//
//                            }
//                        }
//
//                        @Override
//                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                            token.continuePermissionRequest();
//                        }
//                    }).check();
//        } else {
//
//            mRequestingLocationUpdates = true;
//            startLocationUpdates();
//        }

        listData = (RecyclerView) findViewById(R.id.listData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listData.setLayoutManager(linearLayoutManager);
        nextModelArrayList = new ArrayList<>();

        isInternetPresent = cd.isConnectingToInternet();
    }


    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            if (apponcreate) {
                apponcreate = false;

                SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + mCurrentLocation.getLatitude());
                SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE, "" + mCurrentLocation.getLongitude());

                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    if (SaveUserInfoUtils.getUnitFromDefaults(getApplicationContext(), ImageConstant.IS_DEAFAULT_LOCATION) == 0) {
                        getData();
                    } else {
                        ArrayList<LocationModel> locationModelArrayList;
                        if (!SharedUtils.getDefaultLocationListInfo(getApplicationContext()).equals("")) {
                            locationModelArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getApplicationContext()), type);
                            LocationModel locationModel = new LocationModel();
                            locationModel.setKey(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY));
                            if (locationModelArrayList.contains(locationModel)) {

                                if (locationModelArrayList.indexOf(locationModel) != -1) {

                                    int pos = locationModelArrayList.indexOf(locationModel);
                                    address = locationModelArrayList.get(pos).getCity() + ", " + locationModelArrayList.get(pos).getCountry();
                                    getDataCurrent();

                                }
                            }
                        }
                    }

                } else {

                    ivNointernetCoonection.setVisibility(View.VISIBLE);
                    listData.setVisibility(View.GONE);
                    pd.setVisibility(View.GONE);
                    /*ProgressDialog.getInstance().hideProgress();*/
                }
            } else {
                if (SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE).equals("") &&
                        SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE).equals("")) {

                    SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + mCurrentLocation.getLatitude());
                    SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE, "" + mCurrentLocation.getLongitude());

                } else {

                    Location startPoint = new Location("locationA");
                    startPoint.setLatitude(Double.parseDouble(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE)));
                    startPoint.setLongitude(Double.parseDouble(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE)));
                    Location endPoint = new Location("locationA");
                    endPoint.setLatitude(mCurrentLocation.getLatitude());
                    endPoint.setLongitude(mCurrentLocation.getLongitude());
                    double distance = startPoint.distanceTo(endPoint) / 1000;

                    if (distance >= 3.00) {

                        SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + mCurrentLocation.getLatitude());
                        SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE, "" + mCurrentLocation.getLongitude());
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            if (SaveUserInfoUtils.getUnitFromDefaults(getApplicationContext(), ImageConstant.IS_DEAFAULT_LOCATION) == 0) {
                                getData();
                            } else {
                                ArrayList<LocationModel> locationModelArrayList;
                                if (!SharedUtils.getDefaultLocationListInfo(getApplicationContext()).equals("")) {

                                    locationModelArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getApplicationContext()), type);
                                    LocationModel locationModel = new LocationModel();
                                    locationModel.setKey(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY));
                                    if (locationModelArrayList.contains(locationModel)) {

                                        if (locationModelArrayList.indexOf(locationModel) != -1) {

                                            int pos = locationModelArrayList.indexOf(locationModel);
                                            address = locationModelArrayList.get(pos).getCity() + ", " + locationModelArrayList.get(pos).getCountry();
                                            getDataCurrent();
                                        }
                                    }
                                }
                            }

                        } else {
                            ivNointernetCoonection.setVisibility(View.VISIBLE);
                            listData.setVisibility(View.GONE);
                            /*ProgressDialog.getInstance().hideProgress();*/
                            pd.setVisibility(View.GONE);
                        }
                    }


                }
            }
        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {
                if (isAllow) {
                    if (apponcreate) {
                        apponcreate = false;
                        SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + default_lat);
                        SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + default_long);
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            if (SaveUserInfoUtils.getUnitFromDefaults(getApplicationContext(), ImageConstant.IS_DEAFAULT_LOCATION) == 0) {
                                getData();
                            } else {
                                ArrayList<LocationModel> addLocationDataArrayList;
                                if (!SharedUtils.getDefaultLocationListInfo(getApplicationContext()).equals("")) {
                                    addLocationDataArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getApplicationContext()), type);
                                    LocationModel addLocationData = new LocationModel();
                                    addLocationData.setKey(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY));
                                    if (addLocationDataArrayList.contains(addLocationData)) {
                                        if (addLocationDataArrayList.indexOf(addLocationData) != -1) {
                                            int pos = addLocationDataArrayList.indexOf(addLocationData);
                                            address = addLocationDataArrayList.get(pos).getCity() + ", " + addLocationDataArrayList.get(pos).getCountry();
                                            getDataCurrent();
                                        }
                                    }
                                }
                            }

                        } else {

                            ivNointernetCoonection.setVisibility(View.VISIBLE);
                            listData.setVisibility(View.GONE);
                            pd.setVisibility(View.GONE);
                        }
                    } else {

                        if (SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE).equals("") &&
                                SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE).equals("")) {

                            SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + default_lat);
                            SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE, "" + default_long);

                        } else {
                            Location startPoint = new Location("locationA");

                            String lat = SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE);
                            String lon = SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE);
                            /*Log.e("latlong", "updateLocationUI: " + lat);
                            Log.e("latlong", "updateLocationUI: " + lon);*/

                            /*startPoint.setLatitude(Double.parseDouble(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE)));
                            startPoint.setLongitude(Double.parseDouble(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE)));*/

                            Location endPoint = new Location("locationA");
                            endPoint.setLatitude(default_lat);
                            endPoint.setLongitude(default_long);
                            double distance = startPoint.distanceTo(endPoint) / 1000;

                            if (distance >= 3.00) {
                                SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + default_lat);
                                SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE, "" + default_long);
                                isInternetPresent = cd.isConnectingToInternet();
                                if (isInternetPresent) {
                                    if (SaveUserInfoUtils.getUnitFromDefaults(getApplicationContext(), ImageConstant.IS_DEAFAULT_LOCATION) == 0) {
                                        getData();
                                    } else {
                                        ArrayList<LocationModel> addLocationDataArrayList;
                                        if (!SharedUtils.getDefaultLocationListInfo(getApplicationContext()).equals("")) {
                                            addLocationDataArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getApplicationContext()), type);
                                            LocationModel addLocationData = new LocationModel();
                                            addLocationData.setKey(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY));
                                            if (addLocationDataArrayList.contains(addLocationData)) {

                                                if (addLocationDataArrayList.indexOf(addLocationData) != -1) {
                                                    int pos = addLocationDataArrayList.indexOf(addLocationData);
                                                    address = addLocationDataArrayList.get(pos).getCity() + ", " + addLocationDataArrayList.get(pos).getCountry();
                                                    getDataCurrent();
                                                }
                                            }
                                        }
                                    }

                                } else {

                                    ivNointernetCoonection.setVisibility(View.VISIBLE);
                                    listData.setVisibility(View.GONE);
                                    /*ProgressDialog.getInstance().hideProgress();*/
                                    pd.setVisibility(View.GONE);
                                }
                            }

                        }
                    }
                }
            }
        }
    }


    private void openSettings() {

        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                getPackageName(), null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    GetKey getKey;

    private void getData() {

        /*ProgressDialog.getInstance().showProgress(this);*/
        pd.setVisibility(View.VISIBLE);
        String url = "https://api.accuweather.com/locations/v1/cities/geoposition/search.json?apikey=d7e795ae6a0d44aaa8abb1a0a7ac19e4&q=" +
                SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE) + "%2C" +
                SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE) + "&language=en-gb&details=true&toplevel=false";

        Log.e("url", "" + url);
        getKey = new GetKey();
        //noinspection deprecation
        getKey.execute(url);
    }

    @Override
    public void click(int pos) {

        onSettingClick();

    }


    @SuppressLint("StaticFieldLeak")
    private class GetKey extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            StringBuilder response = new StringBuilder();
            try {
                try {
                    String url = params[0].toString();

                    NetworkUtility.sendGetRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] data = NetworkUtility.readMultipleLinesRespone();
                for (String line : data) {
                    response.append(line);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            NetworkUtility.disconnect();
            return response.toString();
        }


        protected void onPostExecute(String res) {
            JSONObject json_data = null;
            try {
                if (res.length() > 0) {
                    json_data = new JSONObject(res);
                    if (json_data != null) {
                        Calendar calendar = Calendar.getInstance();
                        /*if (SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY).equals("")) {
                            new AlarmReceiver().setRepeatAlarm(getApplicationContext(), 1001, calendar);
                        }*/
                        SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY, json_data.getString("Key"));
                        SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_CURRENT_KEY, json_data.getString("Key"));

                        address = getCompleteAddressString(Double.parseDouble(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE)),
                                Double.parseDouble(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE)));

                        ArrayList<LocationModel> locationModelArrayList;
                        if (!SharedUtils.getDefaultLocationListInfo(getApplicationContext()).equals("")) {
                            locationModelArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getApplicationContext()), type);
                        } else {

                            locationModelArrayList = new ArrayList<>();
                        }
                        LocationModel locationModel = new LocationModel();
                        locationModel.setKey(json_data.getString("Key"));
                        locationModel.setCity(city);
                        locationModel.setCountry(country);
                        locationModel.setArea(area);
                        locationModel.setDaynight(0);
                        if (locationModelArrayList.size() > 0) {
                            locationModelArrayList.remove(0);
                            locationModelArrayList.add(0, locationModel);
                        } else {
                            locationModelArrayList.add(locationModel);
                        }
                        SharedUtils.setDefaultLocationInfo(getApplicationContext(), new Gson().toJson(locationModelArrayList));
                        if (isInternetPresent) {
                            getDataCurrent();
                        } else {
                            ivNointernetCoonection.setVisibility(View.VISIBLE);
                            listData.setVisibility(View.GONE);
                            /*ProgressDialog.getInstance().hideProgress();*/
                            pd.setVisibility(View.GONE);
                        }


                    } else {

                    }


                } else {
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_data_available), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    GetCurrentWeatherData getCurrentWeatherData;

    String WeatherText = "";
    String date = "";
    String Temp = "";
    String Unit = "";
    String humidity = "";
    String cloudCover = "";
    String minmax = "";
    String pressure = "";
    String dewPoint = "";
    String wind = "";
    String WeatherIcon;
    String address = "";
    String area = "";
    String country = "";
    String city = "";

    private void getDataCurrent() {

        String url = "https://api.accuweather.com/currentconditions/v1/" + SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY) +
                ".json?apikey=srRLeAmTroxPinDG8Aus3Ikl6tLGJd94&language=en-gb&details=true&getphotos=false";
        Log.e("url", "" + url);

        getCurrentWeatherData = new GetCurrentWeatherData();
        getCurrentWeatherData.execute(url);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCurrentWeatherData extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            StringBuilder response = new StringBuilder();
            try {
                try {
                    String url = params[0].toString();

                    NetworkUtility.sendGetRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] data = NetworkUtility.readMultipleLinesRespone();
                for (String line : data) {
                    response.append(line);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            NetworkUtility.disconnect();
            return response.toString();
        }


        protected void onPostExecute(String res) {
            try {
                if (res.length() > 0) {
                    JSONArray jsonArr = new JSONArray(res);
                    if (jsonArr.length() > 0 && jsonArr != null) {
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObject = jsonArr.getJSONObject(i);
                            WeatherText = jsonObject.getString("WeatherText");
                            date = jsonObject.getString("LocalObservationDateTime");
                            JSONObject jsonObject1 = jsonObject.getJSONObject("RealFeelTemperature");
                            JSONObject jsonObjectmatrix = jsonObject1.getJSONObject(SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT));
                            Temp = "" + Math.round(Float.parseFloat(jsonObjectmatrix.getString("Value")));
                            Unit = "" + (char) 0x00B0 + jsonObjectmatrix.getString("Unit");
                            humidity = jsonObject.getString("RelativeHumidity") + "%";
                            cloudCover = jsonObject.getString("CloudCover") + "%";
                            WeatherIcon = jsonObject.getString("WeatherIcon");

//                            HomeActivity mainActivity = (HomeActivity) getActivity();
//                            if (pos == 0) {
                            if (jsonObject.getBoolean("IsDayTime")) {
                                SaveUserInfoUtils.saveUnitToDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_NIGHT_DAY, 0);
                            } else {
                                SaveUserInfoUtils.saveUnitToDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_NIGHT_DAY, 1);
                            }
//                            } else {
//                                Type type = new TypeToken<List<LocationModel>>() {
//                                }.getType();
//                                ArrayList<LocationModel> locationModelArrayList;
//                                if (!SharedUtils.getDefaultLocationListInfo(getActivity()).equals("")) {
//                                    locationModelArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getActivity()), type);
//                                    if (jsonObject.getBoolean("IsDayTime")) {
//                                        locationModelArrayList.get(pos - 1).setDaynight(0);
//                                        SharedUtils.setDefaultLocationInfo(getActivity(), new Gson().toJson(locationModelArrayList));
//                                        mainActivity.changeBack(0);
//
//                                    } else {
//                                        locationModelArrayList.get(pos - 1).setDaynight(1);
//                                        SharedUtils.setDefaultLocationInfo(getActivity(), new Gson().toJson(locationModelArrayList));
//                                        mainActivity.changeBack(1);
//                                    }
//                                }
//
//                            }


                            JSONObject jsonObjectTemSum = jsonObject.getJSONObject("TemperatureSummary");
                            JSONObject jsonObjectPast6Hour = jsonObjectTemSum.getJSONObject("Past6HourRange");
                            JSONObject jsonObjectMin = jsonObjectPast6Hour.getJSONObject("Minimum");
                            JSONObject jsonObjectMax = jsonObjectPast6Hour.getJSONObject("Maximum");
                            JSONObject jsonObjectmaxMetrix = jsonObjectMax.getJSONObject(SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT));
                            JSONObject jsonObjectminMetrix = jsonObjectMin.getJSONObject(SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT));
                            minmax = jsonObjectminMetrix.getString("Value") + " " + "" + (char) 0x00B0 + "|" + jsonObjectmaxMetrix.getString("Value") + " " + "" + (char) 0x00B0;

                            JSONObject jsonObjectPressure = jsonObject.getJSONObject("Pressure");
                            JSONObject jsonObjectPreMetric = jsonObjectPressure.getJSONObject(SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT));
                            pressure = jsonObjectPreMetric.getString("Value") + " " + jsonObjectPreMetric.getString("Unit");

                            JSONObject jsonObjectDew = jsonObject.getJSONObject("DewPoint");
                            JSONObject jsonObjectDewMetric = jsonObjectDew.getJSONObject(SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT));
                            dewPoint = jsonObjectDewMetric.getString("Value") + " " + "" + (char) 0x00B0 + jsonObjectDewMetric.getString("Unit");


                            JSONObject jsonObjectWind = jsonObject.getJSONObject("Wind");
                            JSONObject jsonObjectDirection = jsonObjectWind.getJSONObject("Direction");
                            JSONObject jsonObjectSpeed = jsonObjectWind.getJSONObject("Speed");
                            JSONObject jsonObjectWindmatrix = jsonObjectSpeed.getJSONObject(SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT));
                            wind = jsonObjectDirection.getString("Localized") +
                                    "  " + jsonObjectWindmatrix.getString("Value") + "" + jsonObjectWindmatrix.getString("Unit");

                            /*if (SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_WIDGET_ALARM).equals("")) {
                                updateAllWidgets(HomeActivity.this, new Intent(HomeActivity.this, WeatherWidget.class));
                                SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_WIDGET_ALARM, "1");

                            }*/

                        }
                        if (isInternetPresent) {
                            getNext24HourData();
                        } else {
                            ivNointernetCoonection.setVisibility(View.VISIBLE);
                            listData.setVisibility(View.GONE);
                            /*ProgressDialog.getInstance().hideProgress();*/
                            pd.setVisibility(View.GONE);
                        }


                    } else {

                    }

                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    ArrayList<OneHourModel> ChartDatas;

    public void getNext24HourData() {

        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60 * 1000);
        String url = "";
        if (SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT).equals("Metric")) {
            url = "http://api.accuweather.com/forecasts/v1/hourly/24hour/" + SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY) + "?apikey=srRLeAmTroxPinDG8Aus3Ikl6tLGJd94&language=en-GB&details=true&metric=true";
        } else {
            url = "http://api.accuweather.com/forecasts/v1/hourly/24hour/" + SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY) + "?apikey=srRLeAmTroxPinDG8Aus3Ikl6tLGJd94&language=en-GB&details=true";
        }
        client.get(url, params, new RegisterResponseHandler());
    }

    public class RegisterResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            super.onFinish();

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            String response = new String(responseBody);
            ChartDatas = new ArrayList<>();
            try {
                if (response.length() > 0) {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0 && jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            OneHourModel oneHourModel = new OneHourModel();
                            oneHourModel.setEpochDateTime(jsonObject.getString("DateTime"));
                            oneHourModel.setWeatherIcon(jsonObject.getString("WeatherIcon"));
                            Log.e("***weatherlive icon", "**" + jsonObject.getString("WeatherIcon"));
                            oneHourModel.setIconPhrase(jsonObject.getString("IconPhrase"));
                            oneHourModel.setIsDaylight(jsonObject.getString("IsDaylight"));
                            oneHourModel.setRelativeHumidity(jsonObject.getString("RelativeHumidity"));
                            oneHourModel.setUVIndex(jsonObject.getString("UVIndex"));
                            oneHourModel.setPrecipitationProbability(jsonObject.getString("PrecipitationProbability"));
                            oneHourModel.setRainProbability(jsonObject.getString("RainProbability"));
                            oneHourModel.setSnowProbability(jsonObject.getString("SnowProbability"));
                            oneHourModel.setIceProbability(jsonObject.getString("IceProbability"));
                            oneHourModel.setCloudCover(jsonObject.getString("CloudCover"));


                            JSONObject jsonObjectTemp = jsonObject.getJSONObject("RealFeelTemperature");
                            oneHourModel.setTempVaule(jsonObjectTemp.getString("Value"));
                            oneHourModel.setTempUnit(jsonObjectTemp.getString("Unit"));

                            JSONObject jsonObjectDew = jsonObject.getJSONObject("DewPoint");
                            oneHourModel.setDewVaule(jsonObjectDew.getString("Value"));
                            oneHourModel.setDewUnit(jsonObjectDew.getString("Unit"));

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("Wind");
                            JSONObject jsonObjectDirection = jsonObjectWind.getJSONObject("Direction");
                            JSONObject jsonObjectSpeed = jsonObjectWind.getJSONObject("Speed");
                            oneHourModel.setWindSpeedValue(jsonObjectSpeed.getString("Value"));
                            oneHourModel.setWindSpeedUnit(jsonObjectSpeed.getString("Unit"));
                            oneHourModel.setWindDegree(jsonObjectDirection.getString("Degrees"));
                            oneHourModel.setWindDirection(jsonObjectDirection.getString("Localized"));

                            JSONObject jsonObjectLiquid = jsonObject.getJSONObject("TotalLiquid");
                            oneHourModel.setLiquidValue(jsonObjectLiquid.getString("Value"));
                            oneHourModel.setLiquidUnit(jsonObjectLiquid.getString("Unit"));

                            JSONObject jsonObjectRain = jsonObject.getJSONObject("Rain");
                            oneHourModel.setRainValue(jsonObjectRain.getString("Value"));
                            oneHourModel.setRainUnit(jsonObjectRain.getString("Unit"));


                            JSONObject jsonObjectSnow = jsonObject.getJSONObject("Snow");
                            oneHourModel.setSnowVaule(jsonObjectSnow.getString("Value"));
                            oneHourModel.setSnowUnit(jsonObjectSnow.getString("Unit"));

                            JSONObject jsonObjectIce = jsonObject.getJSONObject("Ice");
                            oneHourModel.setIceVaule(jsonObjectIce.getString("Value"));
                            oneHourModel.setIceUnit(jsonObjectIce.getString("Unit"));
                            ChartDatas.add(oneHourModel);

                        }
                        getNextData();

                    } else {

                    }


                } else {


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            ChartDatas = new ArrayList<>();
            getNextData();
        }


    }

    GetNext10DaysWeatherData getNext10DaysWeatherData;


    private void getNextData() {

        String url = "";
        if (SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT).equals("Metric")) {
            url = "https://api.accuweather.com/forecasts/v1/daily/15day/" + SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY) +
                    ".json?apikey=srRLeAmTroxPinDG8Aus3Ikl6tLGJd94&language=en-gb&details=true&metric=true";
        } else {
            url = "https://api.accuweather.com/forecasts/v1/daily/15day/" + SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY) +
                    ".json?apikey=srRLeAmTroxPinDG8Aus3Ikl6tLGJd94&language=en-gb&details=true";
        }


        Log.e("url", "" + url);
        getNext10DaysWeatherData = new GetNext10DaysWeatherData();
        getNext10DaysWeatherData.execute(url);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    private class GetNext10DaysWeatherData extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
//            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response = new StringBuilder();
            try {
                try {
                    String url = params[0].toString();

                    NetworkUtility.sendGetRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] data = NetworkUtility.readMultipleLinesRespone();
                for (String line : data) {
                    response.append(line);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            NetworkUtility.disconnect();
            return response.toString();
        }


        protected void onPostExecute(String res) {
            /*ProgressDialog.getInstance().hideProgress();*/
            pd.setVisibility(View.GONE);
            listData.setVisibility(View.VISIBLE);
            int day = 0;
            JSONObject json_data = null;
            nextModelArrayList = new ArrayList<>();
            objectArrayList = new ArrayList<>();
            try {
                if (res.length() > 0) {
                    json_data = new JSONObject(res);
                    JSONArray jsonObjectForcast = json_data.getJSONArray("DailyForecasts");
                    if (jsonObjectForcast.length() > 0 && jsonObjectForcast != null) {

                        for (int i = 0; i < jsonObjectForcast.length(); i++) {

                            NextModel nextModel = new NextModel();
                            JSONObject jsonObject = jsonObjectForcast.getJSONObject(i);

                            nextModel.setDate(jsonObject.getString("Date"));
                            DateTime dt = new DateTime(jsonObject.getString("Date"));
                            String date = "";
                            date = weekOfDay[dt.getDayOfWeek() - 1];

                            nextModel.setEpochDate(date);
                            JSONObject jsonObjectsun = jsonObject.getJSONObject("Sun");
                            nextModel.setSunRise(jsonObjectsun.getString("Rise"));
                            nextModel.setSunSet(jsonObjectsun.getString("Set"));
                            JSONObject jsonObject1 = jsonObject.getJSONObject("RealFeelTemperature");
                            JSONObject jsonObjecMinTemp = jsonObject1.getJSONObject("Minimum");
                            JSONObject jsonObjectMaxTemp = jsonObject1.getJSONObject("Maximum");
                            nextModel.setMinTemValue(jsonObjecMinTemp.getString("Value"));
                            nextModel.setMinTempUnit(jsonObjecMinTemp.getString("Unit"));

                            nextModel.setMaxTempValue(jsonObjectMaxTemp.getString("Value"));
                            nextModel.setMaxTempUnit(jsonObjectMaxTemp.getString("Unit"));

                            DateTime dtsunset = new DateTime(jsonObjectsun.getString("Set"));
                            DateTime dtsunrise = new DateTime(jsonObjectsun.getString("Rise"));

                            if (i == 0) {
                                if ((System.currentTimeMillis() > dtsunrise.getMillis()) &&
                                        (System.currentTimeMillis() < dtsunset.getMillis())) {

                                    day = 0;

                                } else {

                                    day = 1;
                                }
                            }

                            JSONObject jsonObjectday = null;
                            JSONObject jsonObjectnight = null;
                            jsonObjectday = jsonObject.getJSONObject("Day");
                            jsonObjectnight = jsonObject.getJSONObject("Night");

                            nextModel.setNightIcon(jsonObjectnight.getString("Icon"));

                            nextModel.setIcon(jsonObjectday.getString("Icon"));
                            nextModel.setIconPhrase(jsonObjectday.getString("IconPhrase"));
                            nextModel.setShortPhrase(jsonObjectday.getString("ShortPhrase"));
                            nextModel.setLongPhrase(jsonObjectday.getString("LongPhrase"));
                            nextModel.setPrecipitationProbability(jsonObjectday.getString("PrecipitationProbability"));
                            nextModel.setThunderstormProbability(jsonObjectday.getString("ThunderstormProbability"));
                            nextModel.setRainProbability(jsonObjectday.getString("RainProbability"));
                            nextModel.setSnowProbability(jsonObjectday.getString("SnowProbability"));
                            nextModel.setIceProbability(jsonObjectday.getString("IceProbability"));

                            JSONObject jsonObjectWind = jsonObjectday.getJSONObject("Wind");
                            JSONObject jsonObjectDirection = jsonObjectWind.getJSONObject("Direction");
                            JSONObject jsonObjectSpeed = jsonObjectWind.getJSONObject("Speed");
                            nextModel.setWindValue(jsonObjectSpeed.getString("Value"));
                            nextModel.setWindUnit(jsonObjectSpeed.getString("Unit"));
                            nextModel.setWindDegree(jsonObjectDirection.getString("Degrees"));
                            nextModel.setWindLocalized(jsonObjectDirection.getString("Localized"));


                            JSONObject jsonObjectRain = jsonObjectday.getJSONObject("Rain");
                            nextModel.setRainValue(jsonObjectRain.getString("Value"));
                            nextModel.setRainUnit(jsonObjectRain.getString("Unit"));

                            JSONObject jsonObjectSnow = jsonObjectday.getJSONObject("Snow");
                            nextModel.setSnowValue(jsonObjectSnow.getString("Value"));
                            nextModel.setSnowUnit(jsonObjectSnow.getString("Unit"));

                            JSONObject jsonObjectIce = jsonObjectday.getJSONObject("Ice");
                            nextModel.setIceValue(jsonObjectIce.getString("Value"));
                            nextModel.setIceUnit(jsonObjectIce.getString("Unit"));

                            nextModel.setHoursOfPrecipitation(jsonObjectday.getString("HoursOfPrecipitation"));
                            nextModel.setHoursOfRain(jsonObjectday.getString("HoursOfRain"));
                            nextModel.setHoursOfSnow(jsonObjectday.getString("HoursOfSnow"));
                            nextModel.setHoursOfIce(jsonObjectday.getString("HoursOfIce"));
                            nextModel.setCloudCover(jsonObjectday.getString("CloudCover"));

                            nextModelArrayList.add(nextModel);
                            objectArrayList.add(nextModel);

                        }


                       /* NextModel nextModel = new NextModel();
                        nextModelArrayList.add(0, nextModel);*/

                        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);*/
                        listData.setLayoutManager(new SpeedyLinearLayoutManager(HomeActivity.this, SpeedyLinearLayoutManager.VERTICAL, false));
                        nextDayDataListAdapter = new WeatherListDataAdapter(HomeActivity.this, nextModelArrayList,nextModelArrayList, ChartDatas, WeatherText, date, Temp,
                                Unit, humidity, cloudCover, minmax, pressure, dewPoint, wind, WeatherIcon, address, HomeActivity.this);
                        listData.setAdapter(nextDayDataListAdapter);
                        listData.setHasFixedSize(true);


                    } else {

                    }


                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    String[] weekOfDay = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

    @Override
    public void
    onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                city = returnedAddress.getLocality();
                area = strReturnedAddress.toString();
                country = returnedAddress.getCountryName();

                strAdd = returnedAddress.getLocality() + "," + returnedAddress.getCountryName();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private void startLocationUpdates() {

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("tag", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("tag", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("tag", errorMessage);

                        }

                        updateLocationUI();
                    }
                });
    }

    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
                Log.e("lat", "restoreValuesFromBundle: " + mCurrentLocation.getLatitude());
                Log.e("long", "restoreValuesFromBundle: " + mCurrentLocation.getLongitude());
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        if ( ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ( PermissionUtils.neverAskAgainSelected (this, Manifest.permission.ACCESS_FINE_LOCATION )) {
                    displayNeverAskAgainDialog();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                            ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE );
                }
            }

        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
            updateLocationUI();
        }

        //Heloo
        // Resuming location updates depending on button state and
        // allowed permissions
//        if (mRequestingLocationUpdates && checkPermissions()) {
////            if (count == 0) {
////                if (isInternetPresent) {
////                    final Handler handler = new Handler();
////                    handler.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            showAdDailog();
////                            count = 1;
////                        }
////                    }, 3000);
////
////                }
////            }
//            startLocationUpdates();
//        }
////        apponcreate = true;
////
//        updateLocationUI();
    }
    private void displayNeverAskAgainDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder (this );
        builder.setMessage( "For know weather forecast you must need to access the location for performing necessary task. Please permit the permission through "
                + "Settings screen.\n\nSelect Permissions -> Enable permission" );
        builder.setCancelable(false);
        builder.setPositiveButton ("Permit Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialogInterface, int i ) {
                finish();

            }
        });
        builder.show();

        isAllow = false;
        updateLocationUI();
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mRequestingLocationUpdates = true;
                startLocationUpdates();
                isAllow = true;
                default_lat = 0;
                default_long = 0;
                Log.i(TAG, "Permission granted successfully");
            } else {
                PermissionUtils.setShouldShowStatus(this, Manifest.permission.ACCESS_FINE_LOCATION);

            }

        }
    }


        public void onSettingClick() {

        Intent intent = new Intent(HomeActivity.this, PrefrencesActivity.class);
        startActivityForResult(intent, 12);

    }

    public void OnCLickSearch() {

//        if (SharedPrefs.isAdFirstTime(SharedPrefs.userSharedPrefData.user_id)) {

//            if (SharedPrefs.getAdsTime(SharedPrefs.userSharedPrefData.wifi) <= mdate15.getTime()) {

        pd.setVisibility(View.VISIBLE);

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        AdManagerInterstitialAd.load(
                this,
                getString(R.string.ad_unit_int),
                adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        pd.setVisibility(View.GONE);

                        interstitialAd.show(HomeActivity.this);

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.

                                        Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
                                        startActivityForResult(intent, 11);

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.

                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
                                        startActivityForResult(intent, 11);

                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        pd.setVisibility(View.GONE);

                                        Date date1 = new Date();
                                        SharedPrefs.saveAdsTime(SharedPrefs.userSharedPrefData.wifi, date1.getTime() + (15601000));


                                    }

                                });


                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        pd.setVisibility(View.GONE);

                        Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
                        startActivityForResult(intent, 11);

                    }
                });

           /* } else {

                Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
                startActivityForResult(intent, 11);
            }*/
/*
        } else {

            SharedPrefs.setAdFirstTime(SharedPrefs.userSharedPrefData.user_id, true);
            Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
            startActivityForResult(intent, 11);
        }*/

       /* if (fbinterstitialAd == null || !fbinterstitialAd.isAdLoaded()) {

            Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
            startActivityForResult(intent, 11);

            return;
        }

        if (fbinterstitialAd.isAdInvalidated()) {
            fbinterstitialAd.loadAd();
            Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
            startActivityForResult(intent, 11);
            return;
        }
        fbinterstitialAd.show();*/



       /* if (!interstitial.isLoaded()) {



        } else {

            interstitial.show();
        }*/


    }

    public void getCurrentLocation() {
        Date date = new Date();
        if (SharedPrefs.getAdsTime(SharedPrefs.userSharedPrefData.location_ads_time) <= date.getTime()) {

            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            AdManagerInterstitialAd.load(
                    this,
                    getString(R.string.ad_unit_int),
                    adRequest,
                    new AdManagerInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.

                            interstitialAd.show(HomeActivity.this);

                            interstitialAd.setFullScreenContentCallback(
                                    new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            // Called when fullscreen content is dismissed.
                                            // Make sure to set your reference to null so you don't
                                            // show it a second time.

                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                                            // Called when fullscreen content failed to show.

                                            // Make sure to set your reference to null so you don't
                                            // show it a second time.

                                        }

                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            // Called when fullscreen content is shown.
                                            Date date1 = new Date();
                                            SharedPrefs.saveAdsTime(SharedPrefs.userSharedPrefData.location_ads_time, date1.getTime() + (720 * 60 * 1000));

                                        }

                                    });
                        }
                    });
        }

        if (mCurrentLocation != null) {
            SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LATITUDE, "" + mCurrentLocation.getLatitude());
            SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_LONGITUDE, "" + mCurrentLocation.getLongitude());
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                ivNointernetCoonection.setVisibility(View.GONE);
                listData.setVisibility(View.GONE);
                SaveUserInfoUtils.saveUnitToDefaults(getApplicationContext(), ImageConstant.IS_DEAFAULT_LOCATION, 0);
                getData();
            } else {
                ivNointernetCoonection.setVisibility(View.VISIBLE);
                listData.setVisibility(View.GONE);
                /*ProgressDialog.getInstance().hideProgress();*/
                pd.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.e("result", "" + requestCode);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("tag", "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e("tag", "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
            case 11:
                if (resultCode == RESULT_OK) {
                    if (data != null) {

                        SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY, data.getStringExtra("key"));
                        address = data.getStringExtra("name");
                        getDataCurrent();

                    }
                }
                break;
            case 12:

                isInternetPresent = cd.isConnectingToInternet();
                if (data != null) {
                }
                if (isInternetPresent) {
                    ArrayList<LocationModel> locationModelArrayList;
                    if (!SharedUtils.getDefaultLocationListInfo(getApplicationContext()).equals("")) {

                        locationModelArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getApplicationContext()), type);

                        LocationModel locationModel = new LocationModel();
                        locationModel.setKey(SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_KEY));
                        if (locationModelArrayList.contains(locationModel)) {

                            if (locationModelArrayList.indexOf(locationModel) != -1) {

                                int pos = locationModelArrayList.indexOf(locationModel);
                                address = locationModelArrayList.get(pos).getCity() + ", " + locationModelArrayList.get(pos).getCountry();
                                getDataCurrent();
                            }
                        }
                    }

                } else {
                    ivNointernetCoonection.setVisibility(View.VISIBLE);
                    listData.setVisibility(View.GONE);
                    /*ProgressDialog.getInstance().hideProgress();*/
                    pd.setVisibility(View.GONE);
                }
                break;
            case 55:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);
        pd.setVisibility(View.VISIBLE);

        if (interstitialAd != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 5000ms
                    interstitialAd.show(HomeActivity.this);
                }
            }, 2000);

        } else {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 5000ms
                    finish();
                }
            }, 2000);


        }

    }

    @Override
    public void onDestroy() {
       /* if (fbinterstitialAd != null) {
            fbinterstitialAd.destroy();
            fbinterstitialAd = null;
        }*/
        super.onDestroy();
    }


   /* private void bannerad() {


        bannerAdView =
                new AdView(
                        this,
                        "365449667893959_365451134560479",
                        AdSize.BANNER_HEIGHT_50);

        adContainer.addView(bannerAdView);
        bannerAdView.loadAd(bannerAdView.buildLoadAdConfig().withAdListener(this).build());

    }

    @Override
    public void onError(Ad ad, AdError adError) {

        adContainer.setVisibility(View.GONE);
        Log.e("facebookad", "onError: " + adError.getErrorMessage());
    }

    @Override
    public void onAdLoaded(Ad ad) {
        adContainer.setVisibility(View.VISIBLE);
        Log.e("facebookad", "onAdLoaded: " + ad.getPlacementId());

    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    protected void onDestroy() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }

        super.onDestroy();
    }*/


  /*  private void InterstitialAd() {

        interstitialAd =
                new InterstitialAd(this, "365449667893959_365451851227074");
        InterstitialAd.InterstitialLoadAdConfig loadAdConfig =
                interstitialAd
                        .buildLoadAdConfig()
                        .withAdListener(new AbstractAdListener() {
                            @Override
                            public void onError(Ad ad, AdError error) {
                                super.onError(ad, error);

                                onSettingClick();
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                super.onAdLoaded(ad);

                                Log.e("int", "onAdLoaded: " + ad.isAdInvalidated());
                                Log.e("int", "onAdLoaded: " + ad.getPlacementId());
                                if (interstitialAd.isAdLoaded()) {
                                    interstitialAd.show();
                                }


                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                super.onAdClicked(ad);


                            }

                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                super.onInterstitialDisplayed(ad);
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                super.onInterstitialDismissed(ad);

                                onSettingClick();

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                super.onLoggingImpression(ad);
                            }
                        })
                        .withCacheFlags(EnumSet.of(CacheFlag.VIDEO))
                        .build();
        interstitialAd.loadAd(loadAdConfig);

    }*/



   /* public void loadfull() {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.

                if (minterstitialAd != null) {
                    minterstitialAd.show(HomeActivity.this);
                }

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error

            }




        });

    }*/


}