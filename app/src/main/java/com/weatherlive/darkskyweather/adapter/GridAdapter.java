package com.weatherlive.darkskyweather.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.weatherlive.darkskyweather.Model.SliderModel;
import com.weatherlive.darkskyweather.R;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.Holder> {

    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<SliderModel> arrayList;

    public GridAdapter(Context context, ArrayList<SliderModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public GridAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.grid_lay, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridAdapter.Holder holder, int position) {

        holder.tvtitel.setText(arrayList.get(position).getTitle());
        holder.tvtitel.setSelected(true);
        holder.rbrating.setRating(Float.parseFloat(arrayList.get(position).getRating()));
        loadImage(context, holder.imglogo, arrayList.get(position).getImageuri());
        Log.e("imageuri", "onBindViewHolder: " + arrayList.get(position).getImageuri());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public int getItemCount() {
        Log.e("grid", "getItemCount: " + arrayList.size());
        return arrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView tvtitel;
        RatingBar rbrating;
        ImageView imglogo;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvtitel = itemView.findViewById(R.id.tvtitel);
            rbrating = itemView.findViewById(R.id.rbrating);
            imglogo = itemView.findViewById(R.id.imglogo);
        }
    }

    public static void loadImage(Context context, ImageView imageView, String url) {
        Glide.with(context).load(url).into(imageView);
    }
}
