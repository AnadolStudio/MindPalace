package com.anadol.rememberwords.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.myList.MyRecyclerAdapter;

import java.util.ArrayList;

public abstract class MyFragment extends Fragment {
    protected static final String KEY_SELECT_ALL = "select_all";
    protected static final String KEY_SELECT_COUNT = "select_count";
    protected static final String KEY_SELECT_LIST = "select_list";
    public static final String MODE = "mode";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SEARCH = 1;
    public static final int MODE_SELECT = 2;

    protected int mode = MODE_NORMAL;


    public abstract void updateUI();

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODE,mode);
    }

    public int getMode() {
        return mode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!= null){
            mode = savedInstanceState.getInt(MODE);
        }
    }
}
