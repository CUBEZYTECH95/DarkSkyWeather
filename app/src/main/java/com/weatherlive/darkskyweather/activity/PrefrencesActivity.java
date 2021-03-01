package com.weatherlive.darkskyweather.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.weatherlive.darkskyweather.BuildConfig;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.ImageConstant;
import com.weatherlive.darkskyweather.utils.InternetConnection;
import com.weatherlive.darkskyweather.utils.SaveUserInfoUtils;

import java.util.EnumSet;

public class PrefrencesActivity extends AppCompatActivity {


    ImageView ivtem, ivshare, ivrate, ivback, ivabouts, ivnotification, ivfeed, ivprivacy, ivmore;
    TextView tvbottomtemp;
    Boolean isInternetPresent = false;
    InternetConnection cd;
    private InterstitialAd interstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefrences);

        init();

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
        ivtem = (ImageView) findViewById(R.id.ivtem);
        tvbottomtemp = findViewById(R.id.tvbottomtemp);
        ivshare = findViewById(R.id.ivshare);
        ivrate = findViewById(R.id.ivrate);
        ivback = findViewById(R.id.ivback);
        ivabouts = findViewById(R.id.ivabouts);
        ivnotification = findViewById(R.id.ivnotification);
        ivfeed = findViewById(R.id.ivfeed);
        ivprivacy = findViewById(R.id.ivprivacy);
        ivmore = findViewById(R.id.ivmore);

        ivmore.setOnClickListener(new View.OnClickListener() {
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



   /* @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }*/
}