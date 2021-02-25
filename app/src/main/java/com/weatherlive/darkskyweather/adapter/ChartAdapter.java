package com.weatherlive.darkskyweather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.weatherlive.darkskyweather.Model.LocationModel;
import com.weatherlive.darkskyweather.Model.OneHourModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.ImageConstant;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    ArrayList<OneHourModel> mList = new ArrayList<>();
    Activity mContext;
    Type type = new TypeToken<List<LocationModel>>() {
    }.getType();

    public ChartAdapter(Activity mContext, ArrayList<OneHourModel> mList) {

        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {

        } else if (viewType == TYPE_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_hour, parent, false);
            return new ViewHolder(view);

        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        try {

            int viewType = getItemViewType(position);
            switch (viewType) {
                case TYPE_HEADER:

                    break;
                case TYPE_ITEM:
                    final ViewHolder itemholder = (ViewHolder) holder;
                    itemholder.tvTemp.setText("" + Math.round(Float.parseFloat(mList.get(position).getTempVaule())) + " " + "" + (char) 0x00B0);
                    Glide.with(mContext).load(ImageConstant.weatherIcon[Integer.parseInt(mList.get(position).getWeatherIcon()) - 1]).into(itemholder.ivIcon);
                    /*DateTime dt = new DateTime(mList.get(position).getEpochDateTime());*/
                    itemholder.tvTime.setText(setDateFormat(mList.get(position).getEpochDateTime()));
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {

        return this.mList.size();
    }

    @Override
    public int getItemViewType(int position) {


        return TYPE_ITEM;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTemp;
        TextView tvTime;
        ImageView ivIcon;

        public ViewHolder(View v) {
            super(v);

            tvTemp = (TextView) v.findViewById(R.id.tvTemp);
            tvTime = (TextView) v.findViewById(R.id.tvTime);
            ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
        }

    }

    @SuppressLint("SimpleDateFormat")
    public String setDateFormat(String unformattedDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") Date dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(unformattedDate);
        return (new SimpleDateFormat("h a")).format(dateformat != null ? dateformat : null);
    }
}
