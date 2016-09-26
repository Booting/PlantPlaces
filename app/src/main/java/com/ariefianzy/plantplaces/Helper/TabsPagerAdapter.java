package com.ariefianzy.plantplaces.Helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ariefianzy.plantplaces.Fragment.CatAFragment;
import com.ariefianzy.plantplaces.Fragment.CatBFragment;
import com.ariefianzy.plantplaces.Fragment.CatCFragment;
import com.ariefianzy.plantplaces.Fragment.CatDFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new CatAFragment();
            case 1:
                return new CatBFragment();
            case 2:
                return new CatCFragment();
            case 3:
                return new CatDFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }

}