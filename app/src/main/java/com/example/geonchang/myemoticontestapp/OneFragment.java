package com.example.geonchang.myemoticontestapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.geonchang.myemoticontestapp.databinding.FragmentOneBinding;

public class OneFragment extends Fragment {

    FragmentOneBinding binding;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_one, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        int emoticon[] = {R.raw.emoticon_gif_1, R.raw.emoticon_gif_2, R.raw.emoticon_gif_3, R.raw.emoticon_gif_4, R.raw.emoticon_gif_5, R.raw.emoticon_gif_6,
                R.raw.emoticon_gif_7, R.raw.emoticon_gif_8, R.raw.emoticon_gif_9, R.raw.emoticon_gif_10, R.raw.emoticon_gif_11, R.raw.emoticon_gif_12};
        for (int i = 0; i < emoticon.length; i++) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.view_emoticon_icon, binding.menuLayout, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);

            ImageButton img = v.findViewById(R.id.emoticon_select_btn);
            img.setImageResource(emoticon[i]);

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
