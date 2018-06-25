package com.gosproj.gosproject.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VPActAdapter extends FragmentPagerAdapter
{
    public ArrayList<Fragment> fragments;

    public VPActAdapter (FragmentManager fm, ArrayList<Fragment> fragments)
    {
        super(fm);

        this.fragments = fragments;
    }



    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
