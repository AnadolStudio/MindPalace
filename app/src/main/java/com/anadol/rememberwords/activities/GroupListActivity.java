package com.anadol.rememberwords.activities;

import android.support.v4.app.Fragment;

import com.anadol.rememberwords.fragments.GroupListFragment;

public class GroupListActivity extends SimpleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        Fragment  fragment = new GroupListFragment();
        return fragment;
    }

}
