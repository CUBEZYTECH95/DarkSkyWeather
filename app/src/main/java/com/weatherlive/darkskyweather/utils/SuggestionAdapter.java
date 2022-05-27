package com.weatherlive.darkskyweather.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.weatherlive.darkskyweather.R;
import com.weatherlive.darkskyweather.Model.SearchModel;


import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class SuggestionAdapter extends ArrayAdapter<SearchModel> implements Filterable {


    private ArrayList<SearchModel> mlistData, tempItems, suggestions;
    Context context;

    public SuggestionAdapter(Context context, int resource, int textViewResourceId, ArrayList<SearchModel> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        mlistData = items;
        tempItems = new ArrayList<SearchModel>(items); // this makes the difference.
        suggestions = new ArrayList<SearchModel>();
    }


    public void setData(ArrayList<SearchModel> list) {
        mlistData.clear();
        mlistData.addAll(list);
    }

    @Override
    public int getCount() {
        return mlistData.size();
    }

    @Nullable
    @Override
    public SearchModel getItem(int position) {
        return mlistData.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_autocomplete_search, parent, false);
        }
        SearchModel people = mlistData.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.text1);
            if (lblName != null)
                lblName.setText(people.getCity() + "," + people.getCountry());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((SearchModel) resultValue).getCity();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (SearchModel people : tempItems) {
                    String name = people.getCity();
                    if (name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            try {
                ArrayList<SearchModel> filterList = (ArrayList<SearchModel>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (SearchModel people : filterList) {
                        add(people);
                        notifyDataSetChanged();
                    }
                }

            } catch (ConcurrentModificationException e) {
            }

        }
    };
}
