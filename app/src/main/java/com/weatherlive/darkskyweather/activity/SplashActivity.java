package com.weatherlive.darkskyweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.InternetConnection;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    Boolean isInternetPresent = false;
    InternetConnection cd;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.enableMergePathsForKitKatAndAbove(true);
        animationView.playAnimation();
        animationView.cancelAnimation();

        cd = new InternetConnection(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        new Handler().postDelayed(new Runnable() {
            //
            @Override
            public void run() {

                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivityForResult(i, 1);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}