package com.ariefianzy.plantplaces.Helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public final class GridAdapterCat extends BaseAdapter {
    private final Context context;
    private List<String> urls = new ArrayList<>();

    public GridAdapterCat(final Context context,String cat) {
        this.context = context;
        urls.clear();
        /**
         * Mengisi urls dari class Data
         */
        for(int i=0; i<Data.allData.size();i++){
            if(Data.allData.get(i).getCategory().equals(cat))
                urls.add(Data.allData.get(i).getPhoto());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .fit() //
                .tag(context) //
                .into(view);
        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
