package com.weatherlive.darkskyweather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.weatherlive.darkskyweather.Model.NextModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.ImageConstant;

import java.util.ArrayList;
import java.util.List;

public class Weather15Data extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int ITEM = 0;
    private static final int AD_TYPE = 1;
    Context context;
    public List<Object> objects;
    private int FIRST_ADS_ITEM_POSITION = 4;
    private int ADS_FREQUENCY = 5;
    private NativeAdsManager nativeAdsManager;
    private List<NativeAd> nativeAdList = new ArrayList<>();


    public Weather15Data(List<Object> objects, int size, Context context) {

        this.context = context;
        this.objects = objects;
        int no_of_ad_request = size / (ADS_FREQUENCY - 1);
        nativeAdsManager = new NativeAdsManager(context, "365449667893959_365450307893895", no_of_ad_request);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View v1 = inflater.inflate(R.layout.fiftyn_list_litem, parent, false);
                viewHolder = new FiftenViewHolder(v1);
                break;
            case AD_TYPE:
                View view = inflater.inflate(R.layout.native_ad, parent, false);
                viewHolder = new AdHolder(view);
                break;
        }
        return viewHolder;

    }

    public void initNativeAds() {

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

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof FiftenViewHolder) {

            NextModel nextDayData = (NextModel) objects.get(position);
            FiftenViewHolder viewholder = (FiftenViewHolder) holder;
            viewholder.tvDate.setText(nextDayData.getEpochDate());
            viewholder.tvrainp.setText("" + nextDayData.getRainProbability() + "%");
            viewholder.maxtemp.setText("" + Math.round(Float.parseFloat(nextDayData.getMaxTempValue())) + " " + "" + (char) 0x00B0 + nextDayData.getMaxTempUnit());
            viewholder.ivIcon.setBackgroundResource(ImageConstant.weatherIcon[Integer.parseInt(nextDayData.getIcon()) - 1]);
            viewholder.tvweathetext.setText("" + nextDayData.getIconPhrase());
            viewholder.tvcc.setText(nextDayData.getCloudCover() + "%");
            viewholder.tvwind.setText(nextDayData.getWindValue() + "" + nextDayData.getWindUnit());


        } else {

            NativeAd nativeAd = (NativeAd) objects.get(position);
            AdHolder adsViewHolder = (AdHolder) holder;
            adsViewHolder.nativeAdTitle.setText(nativeAd.getAdvertiserName());
            adsViewHolder.nativeAdBody.setText(nativeAd.getAdBodyText());
            adsViewHolder.nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            adsViewHolder.nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            adsViewHolder.nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            adsViewHolder.sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(adsViewHolder.nativeAdTitle);
            clickableViews.add(adsViewHolder.nativeAdCallToAction);
            nativeAd.registerViewForInteraction(adsViewHolder.itemView, adsViewHolder.nativeAdMedia, adsViewHolder.nativeAdIcon, clickableViews);
        }

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (objects.get(position) instanceof NativeAd) {
            return AD_TYPE;
        } else {
            return ITEM;
        }
    }

    private static class FiftenViewHolder extends RecyclerView.ViewHolder {

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

    }

    private static class AdHolder extends RecyclerView.ViewHolder {

        public MediaView nativeAdIcon;
        public TextView nativeAdTitle;
        public MediaView nativeAdMedia;
        public TextView nativeAdSocialContext;
        public TextView nativeAdBody;
        public TextView sponsoredLabel;
        public Button nativeAdCallToAction;


        public AdHolder(@NonNull View itemView) {
            super(itemView);

            nativeAdIcon = itemView.findViewById(R.id.native_ad_icon);
            nativeAdTitle = itemView.findViewById(R.id.native_ad_title);
            nativeAdMedia = itemView.findViewById(R.id.native_ad_media);
            nativeAdSocialContext = itemView.findViewById(R.id.native_ad_social_context);
            nativeAdBody = itemView.findViewById(R.id.native_ad_body);
            sponsoredLabel = itemView.findViewById(R.id.native_ad_sponsored_label);
            nativeAdCallToAction = itemView.findViewById(R.id.native_ad_call_to_action);
        }


    }
}
