package com.ariefianzy.plantplaces.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ariefianzy.plantplaces.Activity.ShowImageActivity;
import com.ariefianzy.plantplaces.Activity.SliderImageActivity;
import com.ariefianzy.plantplaces.Activity.SliderImageCatActivity;
import com.ariefianzy.plantplaces.Helper.GridAdapterCat;
import com.ariefianzy.plantplaces.Helper.SampleGridViewAdapter;
import com.ariefianzy.plantplaces.Helper.SampleScrollListener;
import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.R;

public class CatAFragment extends Fragment {
    private GridView gv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cat_a, container, false);
        /**
         * Membuat GridView untuk menampilkan gambar
         */
        gv = (GridView) rootView.findViewById(R.id.gridview);
        gv.setAdapter(new GridAdapterCat(getActivity(),"A"));
        gv.setOnScrollListener(new SampleScrollListener(getActivity()));

        /**
         * Jika elemen dari gridview di click
         */
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(getActivity(), SliderImageCatActivity.class);
                Bundle data = new Bundle();
                data.putString("url", Data.URLS.get(i));
                data.putInt("position", i);
                data.putString("category", "A");
                myIntent.putExtras(data);
                startActivity(myIntent);
            }
        });
        return rootView;
    }
}