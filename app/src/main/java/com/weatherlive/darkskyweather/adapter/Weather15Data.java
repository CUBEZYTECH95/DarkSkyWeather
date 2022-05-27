package com.weatherlive.darkskyweather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.weatherlive.darkskyweather.Model.NextModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.ImageConstant;

import java.util.ArrayList;

public class Weather15Data extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int ITEM = 0;
    private static final int AD_TYPE = 1;
    public Activity activity;
    private static final int ITEM_FEED_COUNT = 6;
    public ArrayList<NextModel> objects;


    public Weather15Data(ArrayList<NextModel> objects, Activity activity) {

        this.activity = activity;
        this.objects = objects;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
/*
        View v1 = inflater.inflate(R.layout.fiftyn_list_litem, parent, false);
        viewHolder = new FiftenViewHolder(v1);*/

      /*  switch (viewType) {
            case ITEM:
                View v1 = inflater.inflate(R.layout.fiftyn_list_litem, parent, false);
                viewHolder = new FiftenViewHolder(v1);
                break;
            case AD_TYPE:
                View view = inflater.inflate(R.layout.native_ad, parent, false);
                viewHolder = new AdHolder(view);
                break;
        }*/
        /*return viewHolder;*/


        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        if (viewType == ITEM) {
            View view = layoutInflater.inflate(R.layout.fiftyn_list_litem, parent, false);
            return new FiftenViewHolder(view);
        } else if (viewType == AD_TYPE) {
            View view = layoutInflater.inflate(R.layout.layout_ad, parent, false);
            return new AdHolder(view);
        } else {

            return null;
        }
    }

   /* public void initNativeAds() {

        nativeAdsManager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {
                int count = nativeAdsManager.getUniqueNativeAdCount();
                for (int i = 0; i < count; i++) {
                    NativeAd ad = nativeAdsManager.nextNativeAd();
                    addNativeAds(i, ad);
                }
            }

            @Override
            public void onAdError(AdError adError) {

                Toast.makeText(context, "Ads Load Failed : Error Code - " + adError.getErrorCode() + "-" + adError.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        nativeAdsManager.loadAds();
    }

    public void addNativeAds(int i, NativeAd ad) {

        if (ad == null) {
            return;
        }
        if (this.nativeAdList.size() > i && this.nativeAdList.get(i) != null) {
            this.nativeAdList.get(i).unregisterView();
            this.objects.remove(FIRST_ADS_ITEM_POSITION + (i * ADS_FREQUENCY));
            this.nativeAdList = null;
            this.notifyDataSetChanged();
        }

        this.nativeAdList.add(i, ad);

        if (objects.size() > FIRST_ADS_ITEM_POSITION + (i * ADS_FREQUENCY)) {

            objects.add(FIRST_ADS_ITEM_POSITION + (i * ADS_FREQUENCY), ad);
            notifyItemInserted(FIRST_ADS_ITEM_POSITION + (i * ADS_FREQUENCY));
        }

    }*/

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == ITEM) {

            /*NextModel nextDayData = objects.get(position);*/

            int pos = position - Math.round(position / ITEM_FEED_COUNT);
            ((FiftenViewHolder) holder).bindData(objects.get(pos));


        } else if (holder.getItemViewType() == AD_TYPE) {

            AdHolder adHolder = (AdHolder) holder;

            AdLoader.Builder builder = new AdLoader.Builder(activity, activity.getString(R.string.ad_unit_native))
                    .forNativeAd(nativeAd -> {
                        @SuppressLint("InflateParams") NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.list_pp, null);
                        populateNativeADView(nativeAd, nativeAdView);
                        adHolder.frameLayout.removeAllViews();
                        adHolder.frameLayout.addView(nativeAdView);
                    });

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Toast.makeText(activity, loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());


        }

    }



       /* if (holder instanceof FiftenViewHolder) {

            NextModel nextDayData = objects.get(position);
            FiftenViewHolder viewholder = (FiftenViewHolder) holder;
            viewholder.tvDate.setText(nextDayData.getEpochDate());
            viewholder.tvrainp.setText("" + nextDayData.getRainProbability() + "%");
            viewholder.maxtemp.setText("" + Math.round(Float.parseFloat(nextDayData.getMaxTempValue())) + " " + "" + (char) 0x00B0 + nextDayData.getMaxTempUnit());
            viewholder.ivIcon.setBackgroundResource(ImageConstant.weatherIcon[Integer.parseInt(nextDayData.getIcon()) - 1]);
            viewholder.tvweathetext.setText("" + nextDayData.getIconPhrase());
            viewholder.tvcc.setText(nextDayData.getCloudCover() + "%");
            viewholder.tvwind.setText(nextDayData.getWindValue() + "" + nextDayData.getWindUnit());


        } else {



        }
*/


    @Override
    public int getItemCount() {

        if (objects.size() > 0) {
            return objects.size() + Math.round(objects.size() / ITEM_FEED_COUNT);
        }
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {


       /* if (objects.get(position) instanceof NativeAd) {
            return AD_TYPE;
        } else {
            return ITEM;
        }*/

        if ((position + 1) % ITEM_FEED_COUNT == 0) {
            return AD_TYPE;
        }

        return ITEM;


    }

    private void populateNativeADView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }


    class FiftenViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvweathetext, tvrainp, maxtemp, tvcc, tvwind;
        ImageView ivIcon;

        public FiftenViewHolder(View view) {
            super(view);

            tvDate = view.findViewById(R.id.tvdatetime);
            tvweathetext = view.findViewById(R.id.tvweathetext);
            tvrainp = view.findViewById(R.id.tvrainp);
            maxtemp = view.findViewById(R.id.maxtemp);
            tvcc = view.findViewById(R.id.tvcc);
            tvwind = view.findViewById(R.id.tvwind);
            ivIcon = view.findViewById(R.id.ivicon);

        }

        @SuppressLint("SetTextI18n")
        public void bindData(NextModel nextModel) {

            tvDate.setText(nextModel.getEpochDate());
            tvrainp.setText("" + nextModel.getRainProbability() + "%");
            maxtemp.setText("" + Math.round(Float.parseFloat(nextModel.getMaxTempValue())) + " " + "" + (char) 0x00B0 + nextModel.getMaxTempUnit());
            ivIcon.setBackgroundResource(ImageConstant.weatherIcon[Integer.parseInt(nextModel.getIcon()) - 1]);
            tvweathetext.setText("" + nextModel.getIconPhrase());
            tvcc.setText(nextModel.getCloudCover() + "%");
            tvwind.setText(nextModel.getWindValue() + "" + nextModel.getWindUnit());

        }
    }

    class AdHolder extends RecyclerView.ViewHolder {


        FrameLayout frameLayout;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.adLayout);
        }

    }
}

