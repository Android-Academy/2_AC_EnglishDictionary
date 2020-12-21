package com.vullnetlimani.englishdictionary.util;

import android.transition.Fade;

import androidx.appcompat.app.AppCompatActivity;

public class Helper {

    public static void FixFlicker(AppCompatActivity appCompatActivity, int id) {
        Fade fade = new Fade();
        fade.excludeTarget(id, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        appCompatActivity.getWindow().setEnterTransition(fade);
        appCompatActivity.getWindow().setExitTransition(fade);
    }

}
