package com.example.geonchang.myemoticontestapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MainFragment extends Fragment {

    public View view;
    public ArrayList<EmoticonList> emoticonList; // 이모티콘 리스트

    private ViewPager emoticonViewPager;
    private MyPagerAdapter emoticonAdapter;

    public static File path;//디렉토리경로
    private File outputFile; //파일명까지 포함한 경로
    private int emoticonCount; // 이모티콘 종류의 가지 수

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        getEmoticonList();
        // Inflate the layout for this fragment

        return view;
    }

    public void emoticonInit(View view) {

        emoticonCount = emoticonList.size();

        emoticonViewPager = view.findViewById(R.id.pager);
        emoticonAdapter = new MyPagerAdapter(getChildFragmentManager());
        emoticonAdapter.addFragment(R.drawable.btn_upload_emoticon, "Page1", new OneFragment());
        for (int i = 0; i < emoticonCount; i++) {
            outputFile = new File(path, "emoticon/emoticon" + i + "/"); //파일명까지 포함함 경로의 File 객체 생성
            if (outputFile.exists()) { // 이모티콘 파일이 있는 경우
                emoticonAdapter.addFragment(String.valueOf(outputFile + "/icon.png"), "Page" + i, AddFragment.newInstance(i));
            }
        }

        emoticonAdapter.addFragment(R.drawable.btn_add_box, "Page1", new EmoticonDownloadFragment());
        //}
        emoticonViewPager.setAdapter(emoticonAdapter);
        final TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(emoticonViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.btn_upload_emoticon);
        for (int i = 1; i < emoticonViewPager.getAdapter().getCount(); i++) {
            if (i == emoticonViewPager.getAdapter().getCount() - 1) {
                tabLayout.getTabAt(i).setIcon(emoticonAdapter.getFragmentInfo(i).getIconResId());
                break;
            }
            Log.e("MainActivity2", "adapter.getFragmentInfo(i).getIconResUrl() : " + emoticonAdapter.getFragmentInfo(i).getIconResUrl());
            File imgFile = new File(emoticonAdapter.getFragmentInfo(i).getIconResUrl()); //파일명까지 포함함 경로의 File 객체 생성
            if (imgFile.exists()) {
                Log.e("MainActivity2", "icon 있음");
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(getResources(), myBitmap);
                tabLayout.getTabAt(i).setIcon(mBitmapDrawable);
            }
        }
    }

    public void getEmoticonList() {
        Log.e("getEmoticonList", "getEmoticonList()");
        final RequestQueue queue = VolleyClass.getInstance(getContext()).getRequestQueue();
        JsonObjectRequest req = new JsonObjectRequest("http://mirpsck2.cafe24.com/artin/value.php",
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        emoticonList = new ArrayList<>();
                        try {
                            if (response.has("list")) {
                                JSONArray jsonArray = response.getJSONArray("list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int index = jsonObject.getInt("index");
                                    String name = jsonObject.getString("name");
                                    int count = jsonObject.getInt("count");

                                    EmoticonList emoticon = new EmoticonList(index, name, count);

                                    emoticonList.add(emoticon);

                                }
                                emoticonInit(view);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        int socketTimeout = 15000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        queue.add(req);

    }

}
