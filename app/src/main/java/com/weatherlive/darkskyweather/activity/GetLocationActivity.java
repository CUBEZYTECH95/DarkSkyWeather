package com.weatherlive.darkskyweather.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weatherlive.darkskyweather.Model.LocationModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.adapter.GetLocationsAdapter;
import com.weatherlive.darkskyweather.utils.InternetConnection;
import com.weatherlive.darkskyweather.utils.SharedUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetLocationActivity extends AppCompatActivity {

    Boolean isInternetPresent = false;
    InternetConnection cd;
    ArrayList<LocationModel> locationModelArrayList;
    Type type = new TypeToken<List<LocationModel>>() {
    }.getType();
    LinearLayoutManager mLayoutManager;
    GetLocationsAdapter locationListAdapter;
    RecyclerView listLocation;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlocation);

        cd = new InternetConnection(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        init();






    }


    private void init() {


        listLocation = (RecyclerView) findViewById(R.id.listLocation);
        imageView = findViewById(R.id.ivback);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listLocation.setLayoutManager(mLayoutManager);


        if (!SharedUtils.getDefaultLocationListInfo(getApplicationContext()).equals("")) {

            locationModelArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(getApplicationContext()), type);
            LocationModel locationModel = new LocationModel();
            locationModelArrayList.add(0, locationModel);

        } else {

            locationModelArrayList = new ArrayList<>();
            LocationModel locationModel = new LocationModel();
            locationModelArrayList.add(0, locationModel);

        }
        locationListAdapter = new GetLocationsAdapter(GetLocationActivity.this, locationModelArrayList);
        listLocation.setAdapter(locationListAdapter);


    }

    @Override
    public void onBackPressed() {

        finish();

    }
}