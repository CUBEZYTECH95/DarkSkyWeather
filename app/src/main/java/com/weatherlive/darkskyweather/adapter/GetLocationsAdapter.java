package com.weatherlive.darkskyweather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weatherlive.darkskyweather.Model.LocationModel;
import com.weatherlive.darkskyweather.Model.SearchModel;
import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.utils.Api;
import com.weatherlive.darkskyweather.utils.ImageConstant;
import com.weatherlive.darkskyweather.utils.SaveUserInfoUtils;
import com.weatherlive.darkskyweather.utils.SharedUtils;
import com.weatherlive.darkskyweather.utils.SuggestionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetLocationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    /*private static final int TYPE_ITEM_Add_LOCATION = 1;*/
    private static final int TYPE_ITEM = 1;

    ArrayList<LocationModel> mList = new ArrayList<>();
    Activity mContext;
    Type type = new TypeToken<List<LocationModel>>() {
    }.getType();
    FirebaseAnalytics mFirebaseAnalytics;


    public GetLocationsAdapter(Activity mContext, ArrayList<LocationModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }


    @Override
    public int getItemViewType(int position) {


        if (position == 0) {

            return TYPE_HEADER;
        } else {

            return TYPE_ITEM;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType == TYPE_HEADER) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_search_item_header, parent, false);
            return new HeaderViewHolder(view);

        } else if (viewType == TYPE_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
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
                    final HeaderViewHolder searchViewHolder = (HeaderViewHolder) holder;
                    serarchDatas = new ArrayList<>();
                    suggestionAdapter = new SuggestionAdapter(mContext, R.layout.item_autocomplete_search, R.id.text1, serarchDatas);
                    searchViewHolder.autoCompleteTextView.setThreshold(2);

                    searchViewHolder.autoCompleteTextView.setAdapter(suggestionAdapter);

                    searchViewHolder.autoCompleteTextView.setOnItemClickListener(

                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int pos, long id) {

                                    ArrayList<LocationModel> locationModelArrayList;
                                    if (!SharedUtils.getDefaultLocationListInfo(mContext).equals("")) {

                                        locationModelArrayList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(mContext), type);

                                    } else {

                                        locationModelArrayList = new ArrayList<>();
                                    }

                                    LocationModel locationModel = new LocationModel();
                                    locationModel.setKey(serarchDatas.get(pos).getKey());
                                    locationModel.setCity(serarchDatas.get(pos).getCity());
                                    locationModel.setCountry(serarchDatas.get(pos).getCountry());
                                    locationModel.setArea(serarchDatas.get(pos).getArea());
                                    locationModel.setDaynight(0);
                                    locationModelArrayList.add(1, locationModel);
                                    SharedUtils.setDefaultLocationInfo(mContext, new Gson().toJson(locationModelArrayList));
                                    mList.add(2, locationModel);
                                    InputMethodManager in = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                                    notifyDataSetChanged();
                                    Log.d("SearchBar", "On Response:-----" + serarchDatas.get(0) + "," + serarchDatas.get(0) + "\n");

                                }
                            });

                    searchViewHolder.autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int
                                count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            if (s.length() > 2) {

                                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                                        AUTO_COMPLETE_DELAY);
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            if (msg.what == TRIGGER_AUTO_COMPLETE) {

                                if (!TextUtils.isEmpty(searchViewHolder.autoCompleteTextView.getText())) {
                                    fireAnalytics("SearchLocation", "text");
                                    fireAnalytics("SearchLocation", searchViewHolder.autoCompleteTextView.getText().toString());

                                    makeApiCall(searchViewHolder.autoCompleteTextView.getText().toString());
                                }
                            }
                            return false;
                        }
                    });

                    break;


                case TYPE_ITEM:

                    ViewHolder itemholder = (ViewHolder) holder;


                  /*  if (position == 1) {
                        itemholder.ivdelete.setVisibility(View.GONE);
                    } else {

                        itemholder.ivdelete.setVisibility(View.VISIBLE);
                        itemholder.ivdelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                mList.remove(position);
                                SharedUtils.setDefaultLocationInfo(mContext, new Gson().toJson(mList));
                                mList.clear();
                                mList = new ArrayList<>();
                                if (!SharedUtils.getDefaultLocationListInfo(mContext).equals("")) {

                                    LocationModel addLocationData = new LocationModel();
                                    mList = new Gson().fromJson(SharedUtils.getDefaultLocationListInfo(mContext), type);
                                    mList.remove(0);
                                    mList.add(0, addLocationData);

                                } else {

                                    mList = new ArrayList<>();
                                    LocationModel addLocationData = new LocationModel();
                                    mList.add(0, addLocationData);
                                }

                                notifyDataSetChanged();
                            }
                        });

                    }*/

                    if (position == 1) {

                        itemholder.tvlocationName.setText("Current location");
                        itemholder.tvarea.setText(mList.get(position).getArea());

                    } else {

                        itemholder.tvlocationName.setText(mList.get(position).getCity());
                        itemholder.tvarea.setText(mList.get(position).getArea() + ", " + mList.get(position).getCountry());
                    }

                    itemholder.lout_main.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if (position == 1) {

                                SaveUserInfoUtils.saveUnitToDefaults(mContext, ImageConstant.IS_DEAFAULT_LOCATION, 0);

                            } else {

                                SaveUserInfoUtils.saveUnitToDefaults(mContext, ImageConstant.IS_DEAFAULT_LOCATION, 1);
                            }

                            Intent intent = new Intent();
                            intent.putExtra("key", mList.get(position).getKey());
                            intent.putExtra("name", mList.get(position).getCity() + "," + mList.get(position).getCountry());
                            mContext.setResult(mContext.RESULT_OK, intent);
                            mContext.finish();

                        }
                    });

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    ArrayList<SearchModel> serarchDatas;
    private SuggestionAdapter suggestionAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

    private void makeApiCall(String text) {
        Api.make(mContext, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("responce", "" + response);
                JSONArray jsonArray = null;
                List<String> stringList = new ArrayList<>();
                serarchDatas = new ArrayList<>();
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0 && jsonArray != null) {

                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            SearchModel searchModel = new SearchModel();
                            searchModel.setKey(jsonObject.getString("Key"));
                            searchModel.setCity(jsonObject.getString("LocalizedName"));
                            JSONObject object = jsonObject.getJSONObject("Country");
                            searchModel.setCountry(object.getString("LocalizedName"));
                            JSONObject jsonObject1 = jsonObject.getJSONObject("AdministrativeArea");
                            searchModel.setArea(jsonObject1.getString("LocalizedName"));
                            serarchDatas.add(searchModel);
                            stringList.add(jsonObject.getString("LocalizedName") + ", " + object.getString("LocalizedName"));

                        }

                        suggestionAdapter.setData(serarchDatas);
                        suggestionAdapter.notifyDataSetChanged();
//                        adapter = new SearchLocationAdpater(mContext, R.layout.item_autocomplete_search, R.id.text1, serarchDatas);
//                        etBrowseLocation.setAdapter(adapter);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "LocationAdapter");
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        AppCompatAutoCompleteTextView autoCompleteTextView;

        public HeaderViewHolder(View view) {
            super(view);

            autoCompleteTextView = itemView.findViewById(R.id.auto_complete_edit_text);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvlocationName;
        TextView tvarea;
        LinearLayout lout_main;
        ImageView ivdelete;

        public ViewHolder(View view) {
            super(view);
            lout_main = (LinearLayout) view.findViewById(R.id.lout_main);
            tvlocationName = (TextView) view.findViewById(R.id.tvlocationName);
            tvarea = (TextView) view.findViewById(R.id.tvarea);
            /*ivdelete = view.findViewById(R.id.ivdelete);*/
        }
    }


}
