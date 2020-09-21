package com.anadol.rememberwords.model;

import android.content.Context;
import android.preference.PreferenceManager;

public class SettingsPreference {
    public static final String TAG = "SettingsPreference";
    private static final String LAYOUT = "layout";
    private static final String UPDATE_DATABASE = "update_database";

    public static void setLayoutPreference(Context context, int layout) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(LAYOUT, layout)
                .apply();
    }

    public static int getLayoutPreference(Context context) {
        int i = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(LAYOUT, 0);
        return i;
    }

    public static boolean isUpdated(Context context) {
        boolean isUpdate = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(UPDATE_DATABASE, false);
        return isUpdate;
    }

    public static void setUpdate(Context context, boolean update) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(UPDATE_DATABASE, update)
                .apply();
    }
}
