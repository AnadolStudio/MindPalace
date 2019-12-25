package com.anadol.rememberwords.fragments;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.myList.MyRecyclerAdapter;

import java.util.ArrayList;

public abstract class MyFragment extends Fragment {
    protected MyRecyclerAdapter adapter;
    protected ArrayList<Integer> selectedList;
    protected boolean selectMode = false;

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
