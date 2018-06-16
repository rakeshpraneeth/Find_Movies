package com.krp.findmovies.utilities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public final class FragmentUtils {

    private FragmentUtils(){

    }

    public static void replaceFragment(Context context, int containerId, Fragment fragment){
        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        if(fragment !=null) {
            appCompatActivity.getSupportFragmentManager().beginTransaction()
                    .replace(containerId, fragment)
                    .commit();
        }
    }
}
