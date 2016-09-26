package com.ariefianzy.plantplaces.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ariefianzy.plantplaces.Helper.Touch;
import com.ariefianzy.plantplaces.Helper.TouchImageView;
import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.R;
import com.squareup.picasso.Picasso;

public class SliderImageActivity extends FragmentActivity {
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        Intent my = getIntent();
        Bundle data = new Bundle();
        data = my.getExtras();
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(data.getInt("position"));
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return Data.URLS.size();
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.activity_slider_image, container, false);
            TouchImageView imageView = (TouchImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            Picasso.with(getActivity())
                    .load(Data.allData.get(position).getPhoto())
                    .placeholder(R.drawable.images)
                    .fit()
                    .into(imageView);
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }
}