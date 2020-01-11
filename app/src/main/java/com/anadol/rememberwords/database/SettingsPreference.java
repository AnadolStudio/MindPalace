package com.anadol.rememberwords.database;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsPreference {
    public static final String TAG = "SettingsPreference";
    private static final String LAYOUT = "layout";

    public static void setLayoutPreference(Context context, int layout){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(LAYOUT,layout)
                .apply();
    }
    public static int getLayoutPreference(Context context){
        int i = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(LAYOUT, 0);
        return i;
    }
}
