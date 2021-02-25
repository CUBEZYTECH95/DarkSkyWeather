package com.weatherlive.darkskyweather.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.weatherlive.darkskyweather.BuildConfig;
import com.weatherlive.darkskyweather.R;

public class AboutUsActivity extends AppCompatActivity {

    TextView tvappname;
    ImageView ivback;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        tvappname = findViewById(R.id.tvappname);
        ivback = findViewById(R.id.ivback);
        tvappname.setText(getString(R.string.app_name) + "" + "V" + BuildConfig.VERSION_NAME);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}