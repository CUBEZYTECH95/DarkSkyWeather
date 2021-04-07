package com.weatherlive.darkskyweather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdsManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.weatherlive.darkskyweather.ItemClick;
import com.weatherlive.darkskyweather.Model.NextModel;
import com.weatherlive.darkskyweather.Model.OneHourModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.activity.HomeActivity;
import com.weatherlive.darkskyweather.utils.ImageConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class WeatherListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private HashSet<Integer> expandedPositionSet;
    public Activity context;
    private static final int TYPE_SEARCH = 0;
    private static final int TYPE_ITEM = 1;
    ArrayList<NextModel> data;
    ArrayList<OneHourModel> chartDatas;
    private NativeAdsManager nativeAdsManager;
    private List<NativeAd> nativeAdList = new ArrayList<>();
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
    String WeatherIcon = "";
    String address = "";
    private CardView adView;
    private NativeAd nativeAd;
    ItemClick itemClick;
    FirebaseAnalytics mFirebaseAnalytics;
    List<Object> objectArrayList = new ArrayList<>();
    Weather15Data weather15Days;


    public WeatherListDataAdapter(Activity context, ArrayList<NextModel> data, List<Object> objectArrayList, ArrayList<OneHourModel> chartDatas, String WeatherText, String date, String Temp, String Unit, String humidity,
                                  String cloudCover, String minmax, String pressure, String dewPoint, String wind, String WeatherIcon, String address, ItemClick itemClick) {

        this.layoutInflater = LayoutInflater.from(context);
        expandedPositionSet = new HashSet<>();
        this.context = context;
        this.data = data;
        this.objectArrayList = objectArrayList;
        this.WeatherText = WeatherText;
        this.date = date;
        this.Temp = Temp;
        this.Unit = Unit;
        this.humidity = humidity;
        this.cloudCover = cloudCover;
        this.minmax = minmax;
        this.pressure = pressure;
        this.dewPoint = dewPoint;
        this.wind = wind;
        this.WeatherIcon = WeatherIcon;
        this.address = address;
        this.chartDatas = chartDatas;
        this.itemClick = itemClick;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        weather15Days = new Weather15Data(objectArrayList, objectArrayList.size(), context);
        weather15Days.initNativeAds();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.e("view", "onCreateViewHolder: " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_list, parent, false);
        return new HEaderViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HEaderViewHolder searchViewHolder = (HEaderViewHolder) holder;

        searchViewHolder.WeatherText.setText(WeatherText);
        searchViewHolder.tvLocation.setText(address);
        searchViewHolder.tvTemp.setText(Temp);
        searchViewHolder.tvUnit.setText(Unit);
        searchViewHolder.tvtempdata.setText(minmax);
        searchViewHolder.tvpredata.setText(pressure);
        searchViewHolder.tvdewdata.setText(dewPoint);
        searchViewHolder.tvhumiditydata.setText(humidity);
        searchViewHolder.tvwinddata.setText(wind);
        searchViewHolder.tvccdata.setText(cloudCover);

        String i = WeatherIcon;
        int iconID_1 = context.getResources().getIdentifier("iv_" + i, "drawable", context.getPackageName());
        searchViewHolder.ivcurrentimg.setImageResource(iconID_1);
        try {
            searchViewHolder.tvcurrentdate.setText(setDateFormat(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        searchViewHolder.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireAnalytics("SettingPage", "text");
                itemClick.click(position);

            }
        });
        searchViewHolder.ivAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireAnalytics("SearchNewLocation", "text");
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.OnCLickSearch();


            }
        });
        searchViewHolder.getcurrentlocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireAnalytics("CurrentLocationSet", "location");
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.getCurrentLocation();

            }
        });

        searchViewHolder.rv_15days.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        searchViewHolder.rv_15days.setAdapter(weather15Days);

        loadNativeAd(searchViewHolder);


    }

    private void loadNativeAd(HEaderViewHolder searchViewHolder) {

        nativeAd = new NativeAd(context, "365449667893959_365450307893895");
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {


            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                searchViewHolder.nativeAdLayout.setVisibility(View.VISIBLE);
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd, searchViewHolder);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        };

        // Request an ad
        nativeAd.loadAd(nativeAd.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                .build());


    }

    private void inflateAd(NativeAd nativeAd, HEaderViewHolder searchViewHolder) {

        nativeAd.unregisterView();
        LayoutInflater inflater = LayoutInflater.from(context);

        adView = (CardView) inflater.inflate(R.layout.ncpl_native_ad_unit, searchViewHolder.nativeAdLayout, false);
        searchViewHolder.nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, searchViewHolder.nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);


        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);


    }

    @Override
    public int getItemCount() {

        return 1;
    }

    private void fireAnalytics(String arg1, String arg2) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "WeatherListDataAdapter");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg1);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    private void fireDetailAnalytics(String arg0, String arg1) {
        Bundle params = new Bundle();
        params.putString("image_path", arg0);
        params.putString("select_from", arg1);
        mFirebaseAnalytics.logEvent("select_image", params);
    }


    class HEaderViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout lout_ads;

        LinearLayout ivAddLocation;
        LinearLayout ivSetting;
        ImageView ivCurrentLocation;
        LinearLayout getcurrentlocations;
        RecyclerView listHourly;

        ImageView main_bg;
        ImageView ivcurrentimg;

        TextView WeatherText;
        TextView tvLocation;
        TextView tvTemp;
        TextView tvUnit;
        TextView tvcurrentdate;

        TextView tvtempdata;
        TextView tvpredata;
        TextView tvdewdata;
        TextView tvhumiditydata;
        TextView tvwinddata;
        TextView tvccdata;

        View layout2;
        LinearLayout lout_bar;
        LinearLayout lout_child_bar;
        ImageView ivBar;
        ImageView ivIcon, ivnighticon;
        TextView tvMinChart;
        TextView tvMaxChart;
        TextView tvDay;
        TextView tvweek, tvdaymonth;

        FrameLayout fl_adplaceholder;

        LinearLayout listdailyChart;
        ArrayList<NextModel> nextModelArrayList;
        NativeAdLayout nativeAdLayout;
        RecyclerView rv_15days;

        @SuppressLint("SetTextI18n")
        public HEaderViewHolder(View view) {
            super(view);

            nativeAdLayout = view.findViewById(R.id.native_ad_container);
            ivAddLocation = (LinearLayout) view.findViewById(R.id.ivAddLocation);
            ivSetting = (LinearLayout) view.findViewById(R.id.ivSetting);
            listHourly = (RecyclerView) view.findViewById(R.id.listHourly);
            getcurrentlocations = (LinearLayout) view.findViewById(R.id.getcurrentlocations);
            tvcurrentdate = view.findViewById(R.id.tvcurrentdt);
            ivcurrentimg = view.findViewById(R.id.ivcurrentimg);

            WeatherText = (TextView) view.findViewById(R.id.WeatherText);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvTemp = (TextView) view.findViewById(R.id.tvTemp);
            tvUnit = (TextView) view.findViewById(R.id.tvUnit);
            rv_15days = view.findViewById(R.id.rv_15days);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            listHourly.setLayoutManager(mLayoutManager);
            listHourly.setAdapter(new ChartAdapter(context, chartDatas));
            listHourly.setHasFixedSize(true);
            listHourly.setNestedScrollingEnabled(false);


            listdailyChart = (LinearLayout) view.findViewById(R.id.listdailyChart);
            nextModelArrayList = new ArrayList<>();

            int[] minarray = new int[7];
            int[] maxarray = new int[7];

            if (data.size() >= 8) {

                for (int i = 1; i < 8; i++) {

                    nextModelArrayList.add(data.get(i));
                    minarray[i - 1] = Math.round(Float.parseFloat(data.get(i).getMinTemValue()));
                    maxarray[i - 1] = Math.round(Float.parseFloat(data.get(i).getMaxTempValue()));

                }

                int maxtemp = getMaxValue(maxarray);
                int mintemp = getMinValue(minarray);

                for (int i = 1; i < 8; i++) {

                    layout2 = LayoutInflater.from(context).inflate(R.layout.list_item_graph_daily, listdailyChart, false);
                    lout_bar = (LinearLayout) layout2.findViewById(R.id.lout_bar);
                    lout_child_bar = (LinearLayout) layout2.findViewById(R.id.lout_child_bar);
                    ivBar = (ImageView) layout2.findViewById(R.id.ivBar);
                    tvMinChart = (TextView) layout2.findViewById(R.id.tvMinChart);
                    tvMaxChart = (TextView) layout2.findViewById(R.id.tvMaxChart);
                    /*tvDay = (TextView) layout2.findViewById(R.id.tvDay);*/
                    ivIcon = (ImageView) layout2.findViewById(R.id.ivIcon);
                    ivnighticon = (ImageView) layout2.findViewById(R.id.ivnighticon);
                    tvweek = layout2.findViewById(R.id.tvweek);
                    tvdaymonth = layout2.findViewById(R.id.tvdaymonth);


                    tvMinChart.setText("" + Math.round(Float.parseFloat(data.get(i).getMinTemValue())) + " " + "" + (char) 0x00B0);
                    tvMaxChart.setText("" + Math.round(Float.parseFloat(data.get(i).getMaxTempValue())) + " " + "" + (char) 0x00B0);

                    String date = data.get(i).getEpochDate();
                    tvweek.setText(date.substring(0,3));
                    try {
                        tvdaymonth.setText(setdaymonthFormat(data.get(i).getDate()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /*tvweek.setText("" + date[1].substring(0, 1).toUpperCase() + "" + date[0].substring(1, 3).toLowerCase());*/

                    ivnighticon.setBackgroundResource(ImageConstant.weatherIcon[Integer.parseInt(data.get(i).getNightIcon()) - 1]);
                    Log.e("nighticon-", "HEaderViewHolder: " + data.get(i).getNightIcon());
                    ivIcon.setBackgroundResource(ImageConstant.weatherIcon[Integer.parseInt(data.get(i).getIcon()) - 1]);


                    tvMaxChart.measure(0, 0);
                    tvMinChart.measure(0, 0);

                    int heighttext = (tvMaxChart.getMeasuredHeight()) + (tvMinChart.getMeasuredHeight());
                    int height = (context.getResources().getDimensionPixelSize(R.dimen.loutbar)) - (heighttext);

                    int maindiff = (maxtemp) - (mintemp);
                    int gap = Math.round(height / maindiff);
                    int eachDiff = Math.round(Float.parseFloat(data.get(i).getMaxTempValue())) - Math.round(Float.parseFloat(data.get(i).getMinTemValue()));
                    int newheight = (eachDiff * gap);

                    lout_bar.getLayoutParams().height = (context.getResources().getDimensionPixelSize(R.dimen.loutbar));
                    lout_bar.requestLayout();
                    ivBar.getLayoutParams().height = newheight;
                    ivBar.requestLayout();

                    int min = Math.round(Float.parseFloat(data.get(i).getMinTemValue()));
                    if (min != mintemp) {

                        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(lout_child_bar.getLayoutParams());
                        float density = context.getResources().getDisplayMetrics().density;
//                        float dp = (Math.round(Float.parseFloat(data.get(i).getMinTemValue()) - mintemp) * gap) / density;
                        marginParams.setMargins(0, 0, 0, (Math.round(Float.parseFloat(data.get(i).getMinTemValue()) - mintemp) * gap));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(marginParams);
                        lout_child_bar.setLayoutParams(layoutParams);
                    }

                    LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    childParam2.weight = 1f;
                    layout2.setLayoutParams(childParam2);
                    layout2.requestLayout();
                    listdailyChart.addView(layout2);


                }

            } else {

                listdailyChart.setVisibility(View.GONE);
            }

            tvtempdata = (TextView) view.findViewById(R.id.tvtempdata);
            tvpredata = (TextView) view.findViewById(R.id.tvpredata);
            tvdewdata = (TextView) view.findViewById(R.id.tvdewdata);
            tvhumiditydata = (TextView) view.findViewById(R.id.tvhumiditydata);
            tvwinddata = (TextView) view.findViewById(R.id.tvwinddata);
            tvccdata = (TextView) view.findViewById(R.id.tvccdata);


        }
    }

    public static int getMinValue(int[] numbers) {
        int minValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < minValue) {
                minValue = numbers[i];
            }
        }
        return minValue;
    }

    public static int getMaxValue(int[] numbers) {
        int maxValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maxValue) {
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }

    private static class ReyclerViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvweathetext, tvrainp, maxtemp, tvcc, tvwind;
        ImageView ivIcon;

        public ReyclerViewHolder(View view) {
            super(view);

            tvDate = view.findViewById(R.id.tvdatetime);
            tvweathetext = view.findViewById(R.id.tvweathetext);
            tvrainp = view.findViewById(R.id.tvrainp);
            maxtemp = view.findViewById(R.id.maxtemp);
            tvcc = view.findViewById(R.id.tvcc);
            tvwind = view.findViewById(R.id.tvwind);
            ivIcon = view.findViewById(R.id.ivicon);

        }

    }

    @SuppressLint("SimpleDateFormat")
    public String setDateFormat(String unformattedDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") Date dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(unformattedDate);
        return (new SimpleDateFormat("MMM dd ,yyyy")).format(dateformat != null ? dateformat : null);
    }

    @SuppressLint("SimpleDateFormat")
    public String setdaymonthFormat(String unformattedDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") Date dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(unformattedDate);
        return (new SimpleDateFormat("dd/MM")).format(dateformat != null ? dateformat : null);
    }

    static class AdHolder extends RecyclerView.ViewHolder {

        public AdHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
