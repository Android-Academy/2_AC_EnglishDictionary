package com.vullnetlimani.englishdictionary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.vullnetlimani.englishdictionary.R;
import com.vullnetlimani.englishdictionary.WordMeaningActivity;

public class FragmentAntonyms extends Fragment {

    private final AppCompatActivity appCompatActivity;

    public FragmentAntonyms(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_global_layout, container, false);

        ImageView imageView = view.findViewById(R.id.imageView_icon);

        TextView textView = view.findViewById(R.id.textView_id);

        String getText = ((WordMeaningActivity) appCompatActivity).antonyms;

        if (((WordMeaningActivity) appCompatActivity).checkDataEmpty(getText)) {
            imageView.setVisibility(View.VISIBLE);
            textView.setText(R.string.not_text_founded);
        } else {
            textView.setText(getText);
        }

        return view;
    }
}
