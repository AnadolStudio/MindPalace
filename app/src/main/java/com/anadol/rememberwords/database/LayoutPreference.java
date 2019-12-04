package com.anadol.rememberwords.database;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class LayoutPreference {
    public static final String TAG = "LayoutPreference";
    public static final String LAYOUT = "layout";

    public static void setLayoutPreference(Context context, int layout){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(LAYOUT,layout)
                .apply();
    }
    public static int getLayoutPreference(Context context){
        int i = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(LAYOUT, 1);
        return i;
    }
}
