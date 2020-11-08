package com.anadol.rememberwords.model;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.IdRes;

import com.anadol.rememberwords.R;

import static com.anadol.rememberwords.presenter.ComparatorMaker.ORDER_ASC;
import static com.anadol.rememberwords.presenter.ComparatorMaker.TYPE_NAME;

public class SettingsPreference {
    public static final String TAG = "SettingsPreference";
    private static final String LAYOUT = "layout";
    private static final String UPDATE_DATABASE = "update_database";
    private static final String AUTO = "auto";
    private static final String TYPE_GROUP_SORT = "type_group_sort";
    private static final String ORDER_GROUP_SORT = "order_group_sort";
    private static final String TYPE_WORD_SORT = "type_word_sort";
    private static final String ORDER_WORD_SORT = "order_word_sort";

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

    public static boolean isAuto(Context context) {
        boolean isAuto = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(AUTO, true);
        return isAuto;
    }

    public static void setAuto(Context context, boolean update) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(AUTO, update)
                .apply();
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

    public static int getGroupTypeSort(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(TYPE_GROUP_SORT, TYPE_NAME);
    }

    public static void setGroupTypeSort(Context context, @IdRes int type) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(TYPE_GROUP_SORT, type)
                .apply();
    }

    public static int getGroupOrderSort(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(ORDER_GROUP_SORT, ORDER_ASC);
    }

    public static void setGroupOrderSort(Context context, @IdRes int type) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(ORDER_GROUP_SORT, type)
                .apply();
    }

    public static int getWordTypeSort(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(TYPE_WORD_SORT, TYPE_NAME);
    }

    public static void setWordTypeSort(Context context, @IdRes int type) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(TYPE_WORD_SORT, type)
                .apply();
    }

    public static int getWordOrderSort(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(ORDER_WORD_SORT, ORDER_ASC);
    }

    public static void setWordOrderSort(Context context, @IdRes int type) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(ORDER_WORD_SORT, type)
                .apply();
    }

}
