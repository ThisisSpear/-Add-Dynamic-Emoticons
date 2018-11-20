package com.example.geonchang.myemoticontestapp;

import android.support.v4.app.Fragment;

public class FragmentInfo {

    private int iconResId;
    private String iconResUrl;
    private String text;
    private Fragment fragment;

    public FragmentInfo(String iconResUrl, String text, Fragment fragment) {
        this.iconResUrl = iconResUrl;
        this.text = text;
        this.fragment = fragment;
    }

    public FragmentInfo(int iconResId, String text, Fragment fragment) {
        this.iconResId = iconResId;
        this.text = text;
        this.fragment = fragment;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getIconResUrl() {
        return iconResUrl;
    }

    public Fragment getFragment() {
        return fragment;
    }
}