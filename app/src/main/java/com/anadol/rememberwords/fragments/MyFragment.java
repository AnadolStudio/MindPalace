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
    public static final String MODE = "mode";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SEARCH = 1;
    public static final int MODE_SELECT = 2;

    protected MyRecyclerAdapter adapter;
    protected ArrayList<Integer> selectedList;
    protected boolean selectMode = false;
    protected int mode = MODE_NORMAL;


    public abstract void updateUI();

    protected void menuSelected(){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.invalidateOptionsMenu();
        adapter.notifyDataSetChanged();
        if (!selectMode) {
            selectedList.clear();
        }
        updateActionBarTitle();
    }
    public boolean isSelectMode() {
        return selectMode;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODE,mode);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!= null){
            mode = savedInstanceState.getInt(MODE);
        }
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        menuSelected();
    }

    public void updateActionBarTitle(){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (!selectMode) {
            activity.getSupportActionBar().setTitle(getString(R.string.app_name));
            activity.getSupportActionBar().setSubtitle(null);
        }else {
            activity.getSupportActionBar().setTitle(String.valueOf(selectedList.size()));
        }
    }

    protected void cancel(){
        setSelectMode(false);
    }
}
