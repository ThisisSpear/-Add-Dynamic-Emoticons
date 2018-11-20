package com.example.geonchang.myemoticontestapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<FragmentInfo> mData = new ArrayList<>();

    public void addFragment(String iconResId, String title, Fragment fragment) {
        FragmentInfo info = new FragmentInfo(iconResId, title, fragment);
        mData.add(info);
    }

    public void addFragment(int iconResId, String title, Fragment fragment) {
        FragmentInfo info = new FragmentInfo(iconResId, title, fragment);
        mData.add(info);
    }

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public FragmentInfo getFragmentInfo(int position) {
        return mData.get(position);
    }

}
