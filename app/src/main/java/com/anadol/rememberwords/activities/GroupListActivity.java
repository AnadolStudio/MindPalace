package com.anadol.rememberwords.activities;

import android.view.Menu;

import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.GroupListFragment;
import com.anadol.rememberwords.fragments.IOnBackPressed;

public class GroupListActivity extends SimpleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        Fragment  fragment = new GroupListFragment();
        return fragment;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed)fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }
}
