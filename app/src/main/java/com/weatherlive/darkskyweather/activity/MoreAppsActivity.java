package com.weatherlive.darkskyweather.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.weatherlive.darkskyweather.Model.SliderModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.adapter.GridAdapter;
import com.weatherlive.darkskyweather.adapter.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MoreAppsActivity extends AppCompatActivity {

    ViewPager viewPager;
    RecyclerView grid_More_Apps;
    private ImageView[] dots;
    private int dotscount;
    LinearLayout SliderDots;
    TabLayout tabLayout;
    ImageView back;
    Runnable runnable;
    int currentPage = 0;
    private Handler handler;
    private final int delay = 4000; //milliseconds
    private int page = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        handler = new Handler();

        viewPager = findViewById(R.id.viewPager);
        /*SliderDots = findViewById(R.id.SliderDots);*/
        grid_More_Apps = findViewById(R.id.grid_More_Apps);
        tabLayout = findViewById(R.id.SliderDots);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("bannerads");
            ArrayList<SliderModel> models = new ArrayList<>();
            /*ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();*/
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String id = jo_inside.getString("id");
                String titel = jo_inside.getString("title");
                String rating = jo_inside.getString("rating");
                String download = jo_inside.getString("download");
                String imageuri = jo_inside.getString("imageuri");
                String url = jo_inside.getString("url");
                String description = jo_inside.getString("description");
                //Add your values in your `ArrayList` as below:
               /* m_li = new HashMap<String, String>();
                m_li.put("id", id);
                m_li.put("title", titel);
                m_li.put("rating", rating);
                m_li.put("download", download);
                m_li.put("url", url);
                m_li.put("description", description);
*/
                SliderModel sliderModel = new SliderModel();
                sliderModel.setId(Integer.parseInt(id));
                sliderModel.setTitle(titel);
                sliderModel.setRating(rating);
                sliderModel.setDownload(download);
                sliderModel.setImageuri(imageuri);
                sliderModel.setUrl(url);
                sliderModel.setDescription(description);
                models.add(sliderModel);
                viewpgerslider(models);

                runnable = new Runnable() {
                    int position = viewPager.getCurrentItem();

                    public void run() {

                        if (position < 3) {

                            position++;
                            viewPager.setCurrentItem(position);

                        }

                        if (position == 3 - 1) {


                        }

                        viewPager.setCurrentItem(page, true);
                        handler.postDelayed(this, delay);
                    }
                };


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        grid();

    }

    private void grid() {

        try {
            JSONObject obj = new JSONObject(loadJSONFromAssetGrid());
            JSONArray m_jArry = obj.getJSONArray("bannerads");
            ArrayList<SliderModel> models = new ArrayList<>();
            /*ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();*/
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String id = jo_inside.getString("id");
                String titel = jo_inside.getString("title");
                String rating = jo_inside.getString("rating");
                String download = jo_inside.getString("download");
                String imageuri = jo_inside.getString("imageuri");
                String url = jo_inside.getString("url");
                String description = jo_inside.getString("description");
                //Add your values in your `ArrayList` as below:
               /* m_li = new HashMap<String, String>();
                m_li.put("id", id);
                m_li.put("title", titel);
                m_li.put("rating", rating);
                m_li.put("download", download);
                m_li.put("url", url);
                m_li.put("description", description);
*/
                SliderModel sliderModel = new SliderModel();
                sliderModel.setId(Integer.parseInt(id));
                sliderModel.setTitle(titel);
                sliderModel.setRating(rating);
                sliderModel.setDownload(download);
                sliderModel.setImageuri(imageuri);
                sliderModel.setUrl(url);
                sliderModel.setDescription(description);
                models.add(sliderModel);
                grid_More_Apps.setLayoutManager(new GridLayoutManager(MoreAppsActivity.this, 3, LinearLayoutManager.VERTICAL, false));
                grid_More_Apps.setAdapter(new GridAdapter(MoreAppsActivity.this, models));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void viewpgerslider(ArrayList<SliderModel> models) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, models);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.e("position", "" + position);
                page = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



      /*  final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 5 - 1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);*/


        /*dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            SliderDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {


                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(MoreAppsActivity.this, R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(MoreAppsActivity.this, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });*/
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("sliderads.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String loadJSONFromAssetGrid() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("gridads.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
    }


}