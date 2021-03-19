package com.weatherlive.darkskyweather.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.weatherlive.darkskyweather.BuildConfig;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.ImageConstant;
import com.weatherlive.darkskyweather.utils.InternetConnection;
import com.weatherlive.darkskyweather.utils.SaveUserInfoUtils;

import java.util.ArrayList;
import java.util.List;

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
    private boolean isLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prefrences);

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

        native_ad_container = findViewById(R.id.native_ad_container);
        ivshare = findViewById(R.id.ivshare);

        init();

        loadNativeAd();


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


    private void loadNativeAd() {


        nativeAd = new NativeAd(this, "IMG_16_9_APP_INSTALL#365449667893959_365450307893895");
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
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
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
    }

   /* @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }*/
}