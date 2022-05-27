package com.weatherlive.darkskyweather.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.ads.BuildConfig;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.ImageConstant;
import com.weatherlive.darkskyweather.utils.InternetConnection;
import com.weatherlive.darkskyweather.utils.SaveUserInfoUtils;

public class PrefrencesActivity extends AppCompatActivity {


    ImageView ivtem, ivshare, ivrate, ivback, ivabouts, ivnotification, ivfeed, ivprivacy, ivblank;
    TextView tvbottomtemp, tvad;
    Boolean isInternetPresent = false;
    InternetConnection cd;
    private InterstitialAd interstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    private NativeAd nativeAd;
    NativeAdLayout native_ad_container;
    CardView adView;
    public boolean isLoading;
    FrameLayout frameLayout;
    private AdManagerAdView adView1;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prefrences);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        /*frameLayout = findViewById(R.id.native_ad_container);*/
        native_ad_container = findViewById(R.id.native_ad_container);
        adView1 = findViewById(R.id.ad_view);
        tvad = findViewById(R.id.tvad);
        ivtem = (ImageView) findViewById(R.id.ivtem);
        tvbottomtemp = findViewById(R.id.tvbottomtemp);
        ivrate = findViewById(R.id.ivrate);
        ivback = findViewById(R.id.ivback);
        ivabouts = findViewById(R.id.ivabouts);
        ivnotification = findViewById(R.id.ivnotification);
        ivfeed = findViewById(R.id.ivfeed);
        ivprivacy = findViewById(R.id.ivprivacy);
        ivblank = findViewById(R.id.ivblank);

        /*native_ad_container = findViewById(R.id.native_ad_container);*/
        ivshare = findViewById(R.id.ivshare);

        init();

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        // Start loading the ad in the background.
        adView1.loadAd(adRequest);


        /*gadView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        gadView.loadAd(adRequest);*/


        if (SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_NOTIFICATION_ON_OFF).equals("")) {

            ivnotification.setBackgroundResource(R.drawable.ic_notification);

        } else {

            ivnotification.setBackgroundResource(R.drawable.ic_off_notification);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ivtem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireAnalytics("ChangeTempUnit", "text");
                selectTempratureUnit();
            }
        });

        ivfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireAnalytics("FeedBack", "send");
                sendEmail();
            }
        });

        ivprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fireAnalytics("PrivacyPolicy", "text");
                Intent intent = new Intent(PrefrencesActivity.this, PrivacyPolicesActivity.class);
                startActivityForResult(intent, 1);

            }
        });

        ivshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fireAnalytics("ShareApp", "share");

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                /*String shareMessage = "\nLet me recommend you this application\n\n";*/
                String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            }
        });

        ivrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fireAnalytics("RateUs", "text");
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName() + "");
                    Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goMarket);
                } catch (ActivityNotFoundException e) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName() + "");
                    Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goMarket);
                }
            }
        });

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivabouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fireAnalytics("AboutUsPage", "text");
                startActivity(new Intent(PrefrencesActivity.this, AboutUsActivity.class));

            }
        });

        ivnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SaveUserInfoUtils.getFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_NOTIFICATION_ON_OFF).equals("")) {
                    SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_NOTIFICATION_ON_OFF, "1");
                    ivnotification.setBackgroundResource(R.drawable.ic_off_notification);

                } else {
                    SaveUserInfoUtils.saveToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_NOTIFICATION_ON_OFF, "");
                    ivnotification.setBackgroundResource(R.drawable.ic_notification);

                }

            }
        });

    }

    /*private void loadgoogle() {

        AdLoader.Builder builder = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110");
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                frameLayout.setVisibility(View.VISIBLE);
                tvad.setVisibility(View.VISIBLE);
                if (glNativeAd != null) {
                    glNativeAd.destroy();
                }
                glNativeAd = unifiedNativeAd;

                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.gnt_medium_template_view, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }
        });
        NativeAdOptions adOptions = new NativeAdOptions.Builder().build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                frameLayout.setVisibility(View.GONE);
                tvad.setVisibility(View.GONE);
            }


        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
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
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
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
        adView.setNativeAd(nativeAd);
    }*/


    private void init() {


        ivblank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fireAnalytics("MoreAppPage", "text");
                startActivity(new Intent(PrefrencesActivity.this, MoreAppsActivity.class));
            }
        });

        if (SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT).equals("Metric")) {
            tvbottomtemp.setText(values[0]);
        } else {
            tvbottomtemp.setText(values[1]);
        }


    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + Uri.encode(getResources().getString(R.string.mail_id))));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email via..."));
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(getApplicationContext(),
                    "There are no email clients installed.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    CharSequence[] values = {"Celsius (\u2103)", "Fahrenheit (\u2109)"};

    public void selectTempratureUnit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PrefrencesActivity.this, R.style.MaterialThemeDialog);

        builder.setTitle("Select Unit");
        int selectedPos = 0;
        if (SaveUserInfoUtils.getUnitFromUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT).equals("Metric")) {
            selectedPos = 0;
        } else {
            selectedPos = 1;
        }

        builder.setSingleChoiceItems(values, selectedPos, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        SaveUserInfoUtils.saveUnitToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT, "Metric");
                        tvbottomtemp.setText(values[item]);
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case 1:
                        SaveUserInfoUtils.saveUnitToUserDefaults(getApplicationContext(), ImageConstant.PARAM_VALID_TEMPARATURE_UNIT, "Imperial");
                        tvbottomtemp.setText(values[item]);
                        setResult(RESULT_OK);
                        finish();
                        break;

                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog1 = builder.create();
        alertDialog1.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == 12) {
                Intent intent = new Intent();
                intent.putExtra("weatherlive", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SettingActivity");
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

    /*private void loadNativeAd() {

        nativeAd = new NativeAd(this, getString(R.string.fb_native));
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

                tvad.setVisibility(View.GONE);
                native_ad_container.setVisibility(View.GONE);


            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                native_ad_container.setVisibility(View.VISIBLE);
                tvad.setVisibility(View.VISIBLE);
                isLoading = true;
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
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

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();
        LayoutInflater inflater = LayoutInflater.from(this);

        adView = (CardView) inflater.inflate(R.layout.native_ad_unit, native_ad_container, false);
        native_ad_container.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, native_ad_container);
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
    }*/


}

