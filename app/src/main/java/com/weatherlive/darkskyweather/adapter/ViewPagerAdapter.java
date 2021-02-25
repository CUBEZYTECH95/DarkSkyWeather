package com.weatherlive.darkskyweather.adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.weatherlive.darkskyweather.Model.SliderModel;
import com.weatherlive.darkskyweather.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<SliderModel> arrayList;


    public ViewPagerAdapter(Context context, ArrayList<SliderModel> arrayList) {

        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {


        View view = layoutInflater.inflate(R.layout.custom_lay, container, false);


        final TextView tvtitel = view.findViewById(R.id.tvtitel);
        final TextView tvdesc = view.findViewById(R.id.tvdesc);
        final TextView tvrating = view.findViewById(R.id.tvrating);
        final TextView tvtotaldownload = view.findViewById(R.id.tvtotaldownload);
        final ImageView ivimage = view.findViewById(R.id.ivimage);
        RatingBar ratingBar = view.findViewById(R.id.rating);
        tvtitel.setText(arrayList.get(position).getTitle());
        tvtitel.setSelected(true);
        ratingBar.setRating(Float.parseFloat(arrayList.get(position).getRating()));
        tvdesc.setText(arrayList.get(position).getDescription());
        tvrating.setText(arrayList.get(position).getRating());
        tvtotaldownload.setText(String.valueOf(arrayList.get(position).getDownload()) + " " + "total");
        loadImage(context, ivimage, arrayList.get(position).getImageuri());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(arrayList.get(position).getUrl());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {

                    Toast.makeText(context, "You don't have Google Play installed", Toast.LENGTH_LONG).show();
                }
            }
        });

        container.addView(view);

        return view;


    }

    @Override
    public int getCount() {
        return arrayList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

    public static void loadImage(Context context, ImageView imageView, String url) {
        Glide.with(context).load(url).into(imageView);
    }
}
