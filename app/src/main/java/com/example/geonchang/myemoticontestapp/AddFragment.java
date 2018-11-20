package com.example.geonchang.myemoticontestapp;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.geonchang.myemoticontestapp.databinding.FragmentAddBinding;
import com.example.geonchang.myemoticontestapp.databinding.FragmentOneBinding;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {

    FragmentAddBinding binding;
    private MainFragment parentFrag;

    private File outputFile; //파일명까지 포함한 경로
    private File path;//디렉토리경로
    int emoticonIndex;

    public AddFragment() {

    }

    public static AddFragment newInstance(int emoticonIndex) {
        AddFragment af = new AddFragment();
        /* See this code gets executed immediately on your object construction */
        Bundle args = new Bundle();
        args.putInt("emoticonIndex", emoticonIndex);
        af.setArguments(args);
        return af;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        emoticonIndex = getArguments().getInt("emoticonIndex");
        Log.e("MainActivity2", emoticonIndex + "");
        path = MainFragment.path;
        outputFile = new File(path, "emoticon/emoticon" + emoticonIndex + "/png/"); //파일명까지 포함함 경로의 File 객체 생성
        parentFrag = ((MainFragment) AddFragment.this.getParentFragment());
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false);
        init();
        return binding.getRoot();

    }

    public void init() {

        for (int i = 0; i < parentFrag.emoticonList.get(emoticonIndex).count; i++) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.view_emoticon_icon, binding.menuLayout, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);

            ImageButton img = v.findViewById(R.id.emoticon_select_btn);
            img.setImageURI(Uri.fromFile(new File(outputFile, "emoticon_" + i + ".png")));

            if (i % 4 == 0) {
                binding.layout1.addView(v);
            } else if (i % 4 == 1) {
                binding.layout2.addView(v);
            } else if (i % 4 == 2) {
                binding.layout3.addView(v);
            } else {
                binding.layout4.addView(v);
            }

            final int finalI = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), v.getId() + "클릭!!" + finalI, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
