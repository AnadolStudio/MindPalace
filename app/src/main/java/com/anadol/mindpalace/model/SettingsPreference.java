package com.anadol.mindpalace.model;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.IdRes;

import static com.anadol.mindpalace.presenter.ComparatorMaker.ORDER_ASC;
import static com.anadol.mindpalace.presenter.ComparatorMaker.TYPE_NAME;

public class SettingsPreference {
    public static final String TAG = "SettingsPreference";

    private static final String AUTO = "auto";
    private static final String TYPE_GROUP_SORT = "type_group_sort";
    private static final String ORDER_GROUP_SORT = "order_group_sort";
    private static final String TYPE_WORD_SORT = "type_word_sort";
    private static final String ORDER_WORD_SORT = "order_word_sort";

    public static boolean isAutoCreatorLearningTest(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(AUTO, true);
    }

    public static void setAuto(Context context, boolean update) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(AUTO, update)
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
